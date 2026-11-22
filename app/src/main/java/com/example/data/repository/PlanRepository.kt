package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.*
import org.json.JSONArray
import org.json.JSONObject

class PlanRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("wealthflow_user_plans", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SAVED_PLANS = "saved_financial_plans_json"
        private const val KEY_THEME_MODE = "user_theme_mode" // "DARK", "LIGHT", "SYSTEM"
        private const val KEY_SOUND_ENABLED = "user_sound_enabled"
        private const val KEY_SOUND_PROFILE = "user_sound_profile"
        private const val KEY_DEFAULT_INFLATION = "default_inflation_rate"
        private const val KEY_DEFAULT_RETURN = "default_expected_return"
    }

    fun getSavedPlans(): List<SavedPlan> {
        val raw = prefs.getString(KEY_SAVED_PLANS, null) ?: return emptyList()
        val list = mutableListOf<SavedPlan>()
        try {
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val type = try {
                    PlanType.valueOf(obj.getString("type"))
                } catch (_: Exception) {
                    PlanType.SIP
                }

                val sipInput = if (obj.has("sipInput")) {
                    val sObj = obj.getJSONObject("sipInput")
                    SipInput(
                        initialLumpsum = sObj.optDouble("initialLumpsum", 0.0),
                        periodicInvestment = sObj.optDouble("periodicInvestment", 15000.0),
                        durationYears = sObj.optInt("durationYears", 15),
                        expectedReturnAnnual = sObj.optDouble("expectedReturnAnnual", 12.0),
                        enableStepUp = sObj.optBoolean("enableStepUp", true),
                        stepUpValue = sObj.optDouble("stepUpValue", 10.0)
                    )
                } else null

                val swpInput = if (obj.has("swpInput")) {
                    val wObj = obj.getJSONObject("swpInput")
                    SwpInput(
                        startingCorpus = wObj.optDouble("startingCorpus", 5000000.0),
                        periodicWithdrawal = wObj.optDouble("periodicWithdrawal", 35000.0),
                        durationYears = wObj.optInt("durationYears", 20),
                        expectedReturnAnnual = wObj.optDouble("expectedReturnAnnual", 8.5),
                        enableAnnualIncrease = wObj.optBoolean("enableAnnualIncrease", true),
                        annualWithdrawalIncreasePercent = wObj.optDouble("annualWithdrawalIncreasePercent", 5.0)
                    )
                } else null

                val sipToSwpInput = if (obj.has("sipToSwpInput")) {
                    val jObj = obj.getJSONObject("sipToSwpInput")
                    SipToSwpInput(
                        sipInvestment = jObj.optDouble("sipInvestment", 20000.0),
                        sipStepUpPercent = jObj.optDouble("sipStepUpPercent", 10.0),
                        sipDurationYears = jObj.optInt("sipDurationYears", 15),
                        sipExpectedReturn = jObj.optDouble("sipExpectedReturn", 12.0),
                        sipLumpsum = jObj.optDouble("sipLumpsum", 100000.0),
                        swpExpectedReturn = jObj.optDouble("swpExpectedReturn", 8.0),
                        swpMonthlyWithdrawal = jObj.optDouble("swpMonthlyWithdrawal", 60000.0),
                        swpDurationYears = jObj.optInt("swpDurationYears", 20),
                        swpAnnualIncreasePercent = jObj.optDouble("swpAnnualIncreasePercent", 5.0)
                    )
                } else null

                val goalInput = if (obj.has("goalInput")) {
                    val gObj = obj.getJSONObject("goalInput")
                    GoalInput(
                        targetAmountToday = gObj.optDouble("targetAmountToday", 5000000.0),
                        inflationRateAnnual = gObj.optDouble("inflationRateAnnual", 6.0),
                        yearsUntilGoal = gObj.optInt("yearsUntilGoal", 15),
                        expectedReturnAnnual = gObj.optDouble("expectedReturnAnnual", 12.0),
                        stepUpPercent = gObj.optDouble("stepUpPercent", 10.0)
                    )
                } else null

                list.add(
                    SavedPlan(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        type = type,
                        dateCreatedMillis = obj.optLong("dateCreatedMillis", System.currentTimeMillis()),
                        primaryMetricLabel = obj.optString("primaryMetricLabel", "Corpus"),
                        primaryMetricValue = obj.optDouble("primaryMetricValue", 0.0),
                        secondaryMetricLabel = obj.optString("secondaryMetricLabel", "Invested"),
                        secondaryMetricValue = obj.optDouble("secondaryMetricValue", 0.0),
                        tenureYears = obj.optInt("tenureYears", 15),
                        returnRateAnnual = obj.optDouble("returnRateAnnual", 12.0),
                        monthlyAmount = obj.optDouble("monthlyAmount", 10000.0),
                        sipInput = sipInput,
                        swpInput = swpInput,
                        sipToSwpInput = sipToSwpInput,
                        goalInput = goalInput
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun savePlan(plan: SavedPlan) {
        val existing = getSavedPlans().toMutableList()
        existing.removeAll { it.id == plan.id }
        existing.add(0, plan) // Newest first
        persistList(existing)
    }

    fun deletePlan(id: String) {
        val existing = getSavedPlans().toMutableList()
        existing.removeAll { it.id == id }
        persistList(existing)
    }

    fun clearAllPlans() {
        prefs.edit().remove(KEY_SAVED_PLANS).apply()
    }

    private fun persistList(plans: List<SavedPlan>) {
        val array = JSONArray()
        for (p in plans) {
            val obj = JSONObject()
            obj.put("id", p.id)
            obj.put("name", p.name)
            obj.put("type", p.type.name)
            obj.put("dateCreatedMillis", p.dateCreatedMillis)
            obj.put("primaryMetricLabel", p.primaryMetricLabel)
            obj.put("primaryMetricValue", p.primaryMetricValue)
            obj.put("secondaryMetricLabel", p.secondaryMetricLabel)
            obj.put("secondaryMetricValue", p.secondaryMetricValue)
            obj.put("tenureYears", p.tenureYears)
            obj.put("returnRateAnnual", p.returnRateAnnual)
            obj.put("monthlyAmount", p.monthlyAmount)

            p.sipInput?.let { s ->
                val sObj = JSONObject()
                sObj.put("initialLumpsum", s.initialLumpsum)
                sObj.put("periodicInvestment", s.periodicInvestment)
                sObj.put("durationYears", s.durationYears)
                sObj.put("expectedReturnAnnual", s.expectedReturnAnnual)
                sObj.put("enableStepUp", s.enableStepUp)
                sObj.put("stepUpValue", s.stepUpValue)
                obj.put("sipInput", sObj)
            }

            p.swpInput?.let { w ->
                val wObj = JSONObject()
                wObj.put("startingCorpus", w.startingCorpus)
                wObj.put("periodicWithdrawal", w.periodicWithdrawal)
                wObj.put("durationYears", w.durationYears)
                wObj.put("expectedReturnAnnual", w.expectedReturnAnnual)
                wObj.put("enableAnnualIncrease", w.enableAnnualIncrease)
                wObj.put("annualWithdrawalIncreasePercent", w.annualWithdrawalIncreasePercent)
                obj.put("swpInput", wObj)
            }

            p.sipToSwpInput?.let { j ->
                val jObj = JSONObject()
                jObj.put("sipInvestment", j.sipInvestment)
                jObj.put("sipStepUpPercent", j.sipStepUpPercent)
                jObj.put("sipDurationYears", j.sipDurationYears)
                jObj.put("sipExpectedReturn", j.sipExpectedReturn)
                jObj.put("sipLumpsum", j.sipLumpsum)
                jObj.put("swpExpectedReturn", j.swpExpectedReturn)
                jObj.put("swpMonthlyWithdrawal", j.swpMonthlyWithdrawal)
                jObj.put("swpDurationYears", j.swpDurationYears)
                jObj.put("swpAnnualIncreasePercent", j.swpAnnualIncreasePercent)
                obj.put("sipToSwpInput", jObj)
            }

            p.goalInput?.let { g ->
                val gObj = JSONObject()
                gObj.put("targetAmountToday", g.targetAmountToday)
                gObj.put("inflationRateAnnual", g.inflationRateAnnual)
                gObj.put("yearsUntilGoal", g.yearsUntilGoal)
                gObj.put("expectedReturnAnnual", g.expectedReturnAnnual)
                gObj.put("stepUpPercent", g.stepUpPercent)
                obj.put("goalInput", gObj)
            }

            array.put(obj)
        }
        prefs.edit().putString(KEY_SAVED_PLANS, array.toString()).apply()
    }

    // Settings Persistence
    fun getThemeMode(): String = prefs.getString(KEY_THEME_MODE, "DARK") ?: "DARK"
    fun setThemeMode(mode: String) = prefs.edit().putString(KEY_THEME_MODE, mode).apply()

    fun isSoundEnabled(): Boolean = prefs.getBoolean(KEY_SOUND_ENABLED, true) // Enabled by default
    fun setSoundEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()

    fun getSoundProfile(): String = prefs.getString(KEY_SOUND_PROFILE, "HAPTIC_CLICK") ?: "HAPTIC_CLICK"
    fun setSoundProfile(profile: String) = prefs.edit().putString(KEY_SOUND_PROFILE, profile).apply()

    fun getDefaultInflation(): Double = prefs.getFloat(KEY_DEFAULT_INFLATION, 6.0f).toDouble()
    fun setDefaultInflation(rate: Double) = prefs.edit().putFloat(KEY_DEFAULT_INFLATION, rate.toFloat()).apply()

    fun getDefaultReturn(): Double = prefs.getFloat(KEY_DEFAULT_RETURN, 12.0f).toDouble()
    fun setDefaultReturn(rate: Double) = prefs.edit().putFloat(KEY_DEFAULT_RETURN, rate.toFloat()).apply()
}
