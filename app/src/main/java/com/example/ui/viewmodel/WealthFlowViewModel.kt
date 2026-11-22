package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.calculations.FinancialMathEngine
import com.example.data.fundfinder.*
import com.example.data.learning.LearningHubConfig
import com.example.data.model.*
import com.example.data.repository.PlanRepository
import com.example.utils.SoundManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class MainDestination(val title: String) {
    HOME("Home"),
    PLAN("Plan"),
    ANALYZE("Analyze"),
    MY_PLANS("My Plans"),
    SETTINGS("Settings"),
    LEARNING_HUB("Learning Hub")
}

enum class PlanSubTab(val title: String) {
    SIP("SIP Calculator"),
    SWP("SWP Pension"),
    SIP_TO_SWP("SIP → SWP Journey"),
    GOAL("Goal Planner"),
    FUND_FINDER("Fund Finder")
}

enum class AnalyzeSubTab(val title: String) {
    COMPARE("Compare A vs B"),
    WHAT_IF("What-If Sandbox"),
    SCENARIOS("Stress Test"),
    INFLATION("Inflation Impact"),
    INSIGHTS("SIP Insights")
}

enum class ThemeMode(val displayName: String) {
    DARK("Dark Theme"),
    LIGHT("Light Theme"),
    SYSTEM("System Default")
}

// Deprecated legacy alias for backward compatibility with existing tests
enum class AppTab(val title: String) {
    SIP("SIP"),
    SWP("SWP"),
    SIP_TO_SWP("SIP → SWP"),
    GOAL("Goal"),
    SIMULATOR("Compare")
}

data class WealthFlowUiState(
    val currentDestination: MainDestination = MainDestination.HOME,
    val currentPlanSubTab: PlanSubTab = PlanSubTab.SIP,
    val currentAnalyzeSubTab: AnalyzeSubTab = AnalyzeSubTab.COMPARE,
    val currentTab: AppTab = AppTab.SIP, // backward compat

    val themeMode: ThemeMode = ThemeMode.DARK,
    val isDarkTheme: Boolean = true,
    val isSoundEnabled: Boolean = true,
    val soundProfile: SoundManager.SoundProfile = SoundManager.SoundProfile.HAPTIC_CLICK,

    val defaultInflation: Double = 6.0,
    val defaultExpectedReturn: Double = 12.0,
    val savedPlans: List<SavedPlan> = emptyList(),
    val snackbarMessage: String? = null,

    // SIP Mode
    val sipInput: SipInput = SipInput(
        initialLumpsum = 0.0,
        periodicInvestment = 15000.0,
        frequency = Frequency.MONTHLY,
        durationYears = 15,
        expectedReturnAnnual = 12.0,
        enableStepUp = true,
        stepUpType = StepUpType.PERCENTAGE,
        stepUpValue = 10.0,
        stepUpFrequencyYears = 1
    ),
    val sipResult: SipResult = FinancialMathEngine.calculateSIP(
        SipInput(
            initialLumpsum = 0.0,
            periodicInvestment = 15000.0,
            frequency = Frequency.MONTHLY,
            durationYears = 15,
            expectedReturnAnnual = 12.0,
            enableStepUp = true,
            stepUpType = StepUpType.PERCENTAGE,
            stepUpValue = 10.0,
            stepUpFrequencyYears = 1
        )
    ),

    // SWP Mode
    val swpInput: SwpInput = SwpInput(
        startingCorpus = 7500000.0, // ₹75 Lakhs
        periodicWithdrawal = 45000.0, // ₹45,000/mo
        frequency = Frequency.MONTHLY,
        durationYears = 20,
        expectedReturnAnnual = 8.5,
        enableAnnualIncrease = true,
        annualWithdrawalIncreasePercent = 5.0
    ),
    val swpResult: SwpResult = FinancialMathEngine.calculateSWP(
        SwpInput(
            startingCorpus = 7500000.0,
            periodicWithdrawal = 45000.0,
            frequency = Frequency.MONTHLY,
            durationYears = 20,
            expectedReturnAnnual = 8.5,
            enableAnnualIncrease = true,
            annualWithdrawalIncreasePercent = 5.0
        )
    ),

    // SIP to SWP Mode
    val sipToSwpInput: SipToSwpInput = SipToSwpInput(
        sipInvestment = 20000.0,
        sipStepUpPercent = 10.0,
        sipDurationYears = 15,
        sipExpectedReturn = 12.0,
        sipLumpsum = 100000.0,
        swpExpectedReturn = 8.0,
        swpMonthlyWithdrawal = 60000.0,
        swpDurationYears = 20,
        swpAnnualIncreasePercent = 5.0
    ),
    val sipToSwpResult: SipToSwpResult = FinancialMathEngine.calculateSIPToSWP(
        SipToSwpInput(
            sipInvestment = 20000.0,
            sipStepUpPercent = 10.0,
            sipDurationYears = 15,
            sipExpectedReturn = 12.0,
            sipLumpsum = 100000.0,
            swpExpectedReturn = 8.0,
            swpMonthlyWithdrawal = 60000.0,
            swpDurationYears = 20,
            swpAnnualIncreasePercent = 5.0
        )
    ),

    // Goal Planner Mode
    val goalInput: GoalInput = GoalInput(
        targetAmountToday = 5000000.0, // ₹50 Lakhs
        inflationRateAnnual = 6.0,
        yearsUntilGoal = 15,
        expectedReturnAnnual = 12.0,
        stepUpPercent = 10.0
    ),
    val goalResult: GoalResult = FinancialMathEngine.calculateGoalSIP(
        GoalInput(
            targetAmountToday = 5000000.0,
            inflationRateAnnual = 6.0,
            yearsUntilGoal = 15,
            expectedReturnAnnual = 12.0,
            stepUpPercent = 10.0
        )
    ),

    // Reverse SWP (Required Corpus)
    val reverseSwpWithdrawal: Double = 50000.0,
    val reverseSwpYears: Int = 20,
    val reverseSwpReturn: Double = 8.5,
    val reverseSwpAnnualInc: Double = 5.0,
    val requiredCorpusForSwp: Double = FinancialMathEngine.calculateRequiredCorpusForSwp(
        monthlyWithdrawal = 50000.0,
        durationYears = 20,
        expectedReturnAnnual = 8.5,
        annualIncreasePercent = 5.0
    ),
    val maxSustainableMonthlyWithdrawal: Double = FinancialMathEngine.calculateSustainableMonthlyWithdrawal(
        corpus = 7500000.0,
        expectedReturnAnnual = 8.5
    ),

    // Fund Finder State
    val fundFinderStep: Int = 0,
    val investorProfile: InvestorProfile = InvestorProfile(),
    val fundFinderGoal: FundFinderGoal = FundFinderGoal(),
    val riskAnswers: Map<String, Int> = emptyMap(),
    val riskResult: RiskAssessmentResult = RiskScoringEngine.calculateScores(emptyMap()),
    val primaryCategoryGuidance: PrimaryCategoryGuidance = CategoryMatchingEngine.matchPrimaryCategory(
        FundFinderGoal(),
        RiskScoringEngine.calculateScores(emptyMap()),
        InvestorProfile()
    ),
    val categoryRecommendations: List<CategoryRecommendation> = CategoryMatchingEngine.matchCategories(
        FundFinderGoal(),
        RiskScoringEngine.calculateScores(emptyMap()),
        InvestorProfile()
    ),
    val selectedCategoryForFilter: SebiCategory? = null,
    val fundSearchQuery: String = "",
    val comparedSchemeAmfiCodes: List<String> = emptyList(),
    val illustrativePlan: IllustrativeSipEngine.SipPortfolioPlan = IllustrativeSipEngine.buildPortfolioPlan(
        monthlyBudget = 15000.0,
        horizonYears = 10,
        recommendations = CategoryMatchingEngine.matchCategories(
            FundFinderGoal(),
            RiskScoringEngine.calculateScores(emptyMap()),
            InvestorProfile()
        )
    ),
    val showGlossaryDialog: Boolean = false,
    val selectedLearningLanguage: LearningHubConfig.VideoLanguage? = null
)

class WealthFlowViewModel(application: Application? = null) : ViewModel() {

    private val repository: PlanRepository? = application?.let { PlanRepository(it) }
    private val _uiState = MutableStateFlow(WealthFlowUiState())
    val uiState: StateFlow<WealthFlowUiState> = _uiState.asStateFlow()

    init {
        // Hydrate persisted state if repository available
        repository?.let { repo ->
            val saved = repo.getSavedPlans()
            val themeStr = repo.getThemeMode()
            val themeMode = try { ThemeMode.valueOf(themeStr) } catch (_: Exception) { ThemeMode.DARK }
            val soundEnabled = repo.isSoundEnabled()
            val profileStr = repo.getSoundProfile()
            val profile = try { SoundManager.SoundProfile.valueOf(profileStr) } catch (_: Exception) { SoundManager.SoundProfile.HAPTIC_CLICK }
            val inflation = repo.getDefaultInflation()
            val defReturn = repo.getDefaultReturn()

            SoundManager.isSoundEnabled = soundEnabled
            SoundManager.currentProfile = profile

            _uiState.update { current ->
                current.copy(
                    savedPlans = saved,
                    themeMode = themeMode,
                    isDarkTheme = themeMode != ThemeMode.LIGHT,
                    isSoundEnabled = soundEnabled,
                    soundProfile = profile,
                    defaultInflation = inflation,
                    defaultExpectedReturn = defReturn
                )
            }
        }
    }

    // Navigation Destinations
    fun selectDestination(dest: MainDestination) {
        _uiState.update { it.copy(currentDestination = dest) }
    }

    fun selectPlanSubTab(subTab: PlanSubTab) {
        _uiState.update { it.copy(currentDestination = MainDestination.PLAN, currentPlanSubTab = subTab) }
    }

    fun selectAnalyzeSubTab(subTab: AnalyzeSubTab) {
        _uiState.update { it.copy(currentDestination = MainDestination.ANALYZE, currentAnalyzeSubTab = subTab) }
    }

    // Legacy method for backward compatibility
    fun selectTab(tab: AppTab) {
        val dest = when (tab) {
            AppTab.SIP, AppTab.SWP, AppTab.SIP_TO_SWP, AppTab.GOAL -> MainDestination.PLAN
            AppTab.SIMULATOR -> MainDestination.ANALYZE
        }
        val planSub = when (tab) {
            AppTab.SIP -> PlanSubTab.SIP
            AppTab.SWP -> PlanSubTab.SWP
            AppTab.SIP_TO_SWP -> PlanSubTab.SIP_TO_SWP
            AppTab.GOAL -> PlanSubTab.GOAL
            else -> _uiState.value.currentPlanSubTab
        }
        _uiState.update { it.copy(currentTab = tab, currentDestination = dest, currentPlanSubTab = planSub) }
    }

    // Theme & Appearance
    fun setThemeMode(mode: ThemeMode) {
        repository?.setThemeMode(mode.name)
        _uiState.update { it.copy(themeMode = mode, isDarkTheme = mode != ThemeMode.LIGHT) }
    }

    fun toggleTheme() {
        val next = if (_uiState.value.isDarkTheme) ThemeMode.LIGHT else ThemeMode.DARK
        setThemeMode(next)
    }

    // Sound Management
    fun toggleSound() {
        _uiState.update { current ->
            val next = !current.isSoundEnabled
            SoundManager.isSoundEnabled = next
            repository?.setSoundEnabled(next)
            if (next) {
                SoundManager.play(SoundManager.SoundType.TOGGLE_ON)
            }
            current.copy(isSoundEnabled = next)
        }
    }

    fun setSoundProfile(profile: SoundManager.SoundProfile) {
        SoundManager.currentProfile = profile
        SoundManager.isSoundEnabled = true
        repository?.setSoundEnabled(true)
        repository?.setSoundProfile(profile.name)
        SoundManager.play(SoundManager.SoundType.POP)
        _uiState.update { it.copy(soundProfile = profile, isSoundEnabled = true) }
    }

    // Plan Management (Save, Load, Delete)
    fun saveCurrentPlan(customName: String, planType: PlanType) {
        val state = _uiState.value
        val plan = when (planType) {
            PlanType.SIP -> SavedPlan(
                name = customName.ifBlank { "SIP Investment Plan" },
                type = PlanType.SIP,
                primaryMetricLabel = "Estimated Corpus",
                primaryMetricValue = state.sipResult.finalCorpus,
                secondaryMetricLabel = "Total Invested",
                secondaryMetricValue = state.sipResult.totalInvested,
                tenureYears = state.sipInput.durationYears,
                returnRateAnnual = state.sipInput.expectedReturnAnnual,
                monthlyAmount = state.sipInput.periodicInvestment,
                sipInput = state.sipInput
            )
            PlanType.SWP -> SavedPlan(
                name = customName.ifBlank { "Retirement SWP Pension" },
                type = PlanType.SWP,
                primaryMetricLabel = "Total Withdrawn",
                primaryMetricValue = state.swpResult.totalWithdrawn,
                secondaryMetricLabel = "Starting Nest Egg",
                secondaryMetricValue = state.swpInput.startingCorpus,
                tenureYears = state.swpInput.durationYears,
                returnRateAnnual = state.swpInput.expectedReturnAnnual,
                monthlyAmount = state.swpInput.periodicWithdrawal,
                swpInput = state.swpInput
            )
            PlanType.SIP_TO_SWP -> SavedPlan(
                name = customName.ifBlank { "Accumulation & Retirement Lifecycle" },
                type = PlanType.SIP_TO_SWP,
                primaryMetricLabel = "Retirement Corpus",
                primaryMetricValue = state.sipToSwpResult.accumulatedCorpus,
                secondaryMetricLabel = "Lifetime Withdrawn",
                secondaryMetricValue = state.sipToSwpResult.totalWithdrawn,
                tenureYears = state.sipToSwpInput.sipDurationYears + state.sipToSwpInput.swpDurationYears,
                returnRateAnnual = state.sipToSwpInput.sipExpectedReturn,
                monthlyAmount = state.sipToSwpInput.sipInvestment,
                sipToSwpInput = state.sipToSwpInput
            )
            PlanType.GOAL -> SavedPlan(
                name = customName.ifBlank { "Target Financial Milestone" },
                type = PlanType.GOAL,
                primaryMetricLabel = "Future Target",
                primaryMetricValue = state.goalResult.futureTargetAmount,
                secondaryMetricLabel = "Required Flat SIP",
                secondaryMetricValue = state.goalResult.requiredFlatMonthlySip,
                tenureYears = state.goalInput.yearsUntilGoal,
                returnRateAnnual = state.goalInput.expectedReturnAnnual,
                monthlyAmount = state.goalResult.requiredFlatMonthlySip,
                goalInput = state.goalInput
            )
        }

        repository?.savePlan(plan)
        val updatedList = repository?.getSavedPlans() ?: (_uiState.value.savedPlans + plan)
        SoundManager.play(SoundManager.SoundType.SUCCESS)
        _uiState.update { it.copy(savedPlans = updatedList, snackbarMessage = "Plan \"${plan.name}\" saved to My Plans") }
    }

    fun deletePlan(id: String) {
        repository?.deletePlan(id)
        val updated = repository?.getSavedPlans() ?: _uiState.value.savedPlans.filter { it.id != id }
        SoundManager.play(SoundManager.SoundType.POP)
        _uiState.update { it.copy(savedPlans = updated, snackbarMessage = "Plan removed") }
    }

    fun clearAllSavedPlans() {
        repository?.clearAllPlans()
        _uiState.update { it.copy(savedPlans = emptyList(), snackbarMessage = "All saved plans cleared") }
    }

    fun loadPlanIntoCalculator(plan: SavedPlan) {
        when (plan.type) {
            PlanType.SIP -> plan.sipInput?.let { inp ->
                _uiState.update {
                    it.copy(
                        sipInput = inp,
                        sipResult = FinancialMathEngine.calculateSIP(inp),
                        currentDestination = MainDestination.PLAN,
                        currentPlanSubTab = PlanSubTab.SIP,
                        snackbarMessage = "Loaded plan \"${plan.name}\""
                    )
                }
            }
            PlanType.SWP -> plan.swpInput?.let { inp ->
                _uiState.update {
                    it.copy(
                        swpInput = inp,
                        swpResult = FinancialMathEngine.calculateSWP(inp),
                        currentDestination = MainDestination.PLAN,
                        currentPlanSubTab = PlanSubTab.SWP,
                        snackbarMessage = "Loaded plan \"${plan.name}\""
                    )
                }
            }
            PlanType.SIP_TO_SWP -> plan.sipToSwpInput?.let { inp ->
                _uiState.update {
                    it.copy(
                        sipToSwpInput = inp,
                        sipToSwpResult = FinancialMathEngine.calculateSIPToSWP(inp),
                        currentDestination = MainDestination.PLAN,
                        currentPlanSubTab = PlanSubTab.SIP_TO_SWP,
                        snackbarMessage = "Loaded plan \"${plan.name}\""
                    )
                }
            }
            PlanType.GOAL -> plan.goalInput?.let { inp ->
                _uiState.update {
                    it.copy(
                        goalInput = inp,
                        goalResult = FinancialMathEngine.calculateGoalSIP(inp),
                        currentDestination = MainDestination.PLAN,
                        currentPlanSubTab = PlanSubTab.GOAL,
                        snackbarMessage = "Loaded goal \"${plan.name}\""
                    )
                }
            }
        }
        SoundManager.play(SoundManager.SoundType.SUCCESS)
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun setDefaultInflation(rate: Double) {
        repository?.setDefaultInflation(rate)
        _uiState.update { state ->
            val newGoal = state.goalInput.copy(inflationRateAnnual = rate)
            state.copy(
                defaultInflation = rate,
                goalInput = newGoal,
                goalResult = FinancialMathEngine.calculateGoalSIP(newGoal)
            )
        }
    }

    fun setDefaultExpectedReturn(rate: Double) {
        repository?.setDefaultReturn(rate)
        _uiState.update { state ->
            val newSip = state.sipInput.copy(expectedReturnAnnual = rate)
            state.copy(
                defaultExpectedReturn = rate,
                sipInput = newSip,
                sipResult = FinancialMathEngine.calculateSIP(newSip)
            )
        }
    }

    // SIP Updates
    fun updateSipInvestment(amount: Double) {
        _uiState.update { state ->
            val newInput = state.sipInput.copy(periodicInvestment = amount)
            state.copy(sipInput = newInput, sipResult = FinancialMathEngine.calculateSIP(newInput))
        }
    }

    fun updateSipDuration(years: Int) {
        _uiState.update { state ->
            val newInput = state.sipInput.copy(durationYears = years)
            state.copy(sipInput = newInput, sipResult = FinancialMathEngine.calculateSIP(newInput))
        }
    }

    fun updateSipReturn(rate: Double) {
        _uiState.update { state ->
            val newInput = state.sipInput.copy(expectedReturnAnnual = rate)
            state.copy(sipInput = newInput, sipResult = FinancialMathEngine.calculateSIP(newInput))
        }
    }

    fun updateSipFrequency(frequency: Frequency) {
        _uiState.update { state ->
            val newInput = state.sipInput.copy(frequency = frequency)
            state.copy(sipInput = newInput, sipResult = FinancialMathEngine.calculateSIP(newInput))
        }
    }

    fun toggleStepUp(enabled: Boolean) {
        _uiState.update { state ->
            val newInput = state.sipInput.copy(enableStepUp = enabled)
            state.copy(sipInput = newInput, sipResult = FinancialMathEngine.calculateSIP(newInput))
        }
    }

    fun updateStepUpValue(value: Double) {
        _uiState.update { state ->
            val newInput = state.sipInput.copy(stepUpValue = value)
            state.copy(sipInput = newInput, sipResult = FinancialMathEngine.calculateSIP(newInput))
        }
    }

    fun updateStepUpType(type: StepUpType) {
        _uiState.update { state ->
            val defaultVal = if (type == StepUpType.PERCENTAGE) 10.0 else 1000.0
            val newInput = state.sipInput.copy(stepUpType = type, stepUpValue = defaultVal)
            state.copy(sipInput = newInput, sipResult = FinancialMathEngine.calculateSIP(newInput))
        }
    }

    fun updateInitialLumpsum(amount: Double) {
        _uiState.update { state ->
            val newInput = state.sipInput.copy(initialLumpsum = amount)
            state.copy(sipInput = newInput, sipResult = FinancialMathEngine.calculateSIP(newInput))
        }
    }

    // SWP Updates
    fun updateSwpCorpus(amount: Double) {
        _uiState.update { state ->
            val newInput = state.swpInput.copy(startingCorpus = amount)
            state.copy(
                swpInput = newInput,
                swpResult = FinancialMathEngine.calculateSWP(newInput),
                maxSustainableMonthlyWithdrawal = FinancialMathEngine.calculateSustainableMonthlyWithdrawal(amount, state.swpInput.expectedReturnAnnual)
            )
        }
    }

    fun updateSwpWithdrawal(amount: Double) {
        _uiState.update { state ->
            val newInput = state.swpInput.copy(periodicWithdrawal = amount)
            state.copy(swpInput = newInput, swpResult = FinancialMathEngine.calculateSWP(newInput))
        }
    }

    fun updateSwpDuration(years: Int) {
        _uiState.update { state ->
            val newInput = state.swpInput.copy(durationYears = years)
            state.copy(swpInput = newInput, swpResult = FinancialMathEngine.calculateSWP(newInput))
        }
    }

    fun updateSwpReturn(rate: Double) {
        _uiState.update { state ->
            val newInput = state.swpInput.copy(expectedReturnAnnual = rate)
            state.copy(
                swpInput = newInput,
                swpResult = FinancialMathEngine.calculateSWP(newInput),
                maxSustainableMonthlyWithdrawal = FinancialMathEngine.calculateSustainableMonthlyWithdrawal(state.swpInput.startingCorpus, rate)
            )
        }
    }

    fun toggleSwpAnnualIncrease(enabled: Boolean) {
        _uiState.update { state ->
            val newInput = state.swpInput.copy(enableAnnualIncrease = enabled)
            state.copy(swpInput = newInput, swpResult = FinancialMathEngine.calculateSWP(newInput))
        }
    }

    fun updateSwpAnnualIncreasePercent(rate: Double) {
        _uiState.update { state ->
            val newInput = state.swpInput.copy(annualWithdrawalIncreasePercent = rate)
            state.copy(swpInput = newInput, swpResult = FinancialMathEngine.calculateSWP(newInput))
        }
    }

    // SIP to SWP Updates
    fun updateSipToSwpSip(amount: Double) {
        _uiState.update { state ->
            val newInput = state.sipToSwpInput.copy(sipInvestment = amount)
            state.copy(sipToSwpInput = newInput, sipToSwpResult = FinancialMathEngine.calculateSIPToSWP(newInput))
        }
    }

    fun updateSipToSwpAccumulationYears(years: Int) {
        _uiState.update { state ->
            val newInput = state.sipToSwpInput.copy(sipDurationYears = years)
            state.copy(sipToSwpInput = newInput, sipToSwpResult = FinancialMathEngine.calculateSIPToSWP(newInput))
        }
    }

    fun updateSipToSwpWithdrawal(monthlyWithdrawal: Double) {
        _uiState.update { state ->
            val newInput = state.sipToSwpInput.copy(swpMonthlyWithdrawal = monthlyWithdrawal)
            state.copy(sipToSwpInput = newInput, sipToSwpResult = FinancialMathEngine.calculateSIPToSWP(newInput))
        }
    }

    fun updateSipToSwpWithdrawalYears(years: Int) {
        _uiState.update { state ->
            val newInput = state.sipToSwpInput.copy(swpDurationYears = years)
            state.copy(sipToSwpInput = newInput, sipToSwpResult = FinancialMathEngine.calculateSIPToSWP(newInput))
        }
    }

    fun updateSipToSwpStepUp(stepUp: Double) {
        _uiState.update { state ->
            val newInput = state.sipToSwpInput.copy(sipStepUpPercent = stepUp)
            state.copy(sipToSwpInput = newInput, sipToSwpResult = FinancialMathEngine.calculateSIPToSWP(newInput))
        }
    }

    // Goal Updates
    fun updateGoalTargetToday(amount: Double) {
        _uiState.update { state ->
            val newInput = state.goalInput.copy(targetAmountToday = amount)
            state.copy(goalInput = newInput, goalResult = FinancialMathEngine.calculateGoalSIP(newInput))
        }
    }

    fun updateGoalYears(years: Int) {
        _uiState.update { state ->
            val newInput = state.goalInput.copy(yearsUntilGoal = years)
            state.copy(goalInput = newInput, goalResult = FinancialMathEngine.calculateGoalSIP(newInput))
        }
    }

    fun updateGoalInflation(rate: Double) {
        _uiState.update { state ->
            val newInput = state.goalInput.copy(inflationRateAnnual = rate)
            state.copy(goalInput = newInput, goalResult = FinancialMathEngine.calculateGoalSIP(newInput))
        }
    }

    fun updateGoalReturn(rate: Double) {
        _uiState.update { state ->
            val newInput = state.goalInput.copy(expectedReturnAnnual = rate)
            state.copy(goalInput = newInput, goalResult = FinancialMathEngine.calculateGoalSIP(newInput))
        }
    }

    fun updateGoalStepUp(rate: Double) {
        _uiState.update { state ->
            val newInput = state.goalInput.copy(stepUpPercent = rate)
            state.copy(goalInput = newInput, goalResult = FinancialMathEngine.calculateGoalSIP(newInput))
        }
    }

    // Reverse SWP (Required Corpus)
    fun updateReverseSwpInputs(monthlyWithdrawal: Double, years: Int, returnRate: Double, annualInc: Double) {
        val requiredCorpus = FinancialMathEngine.calculateRequiredCorpusForSwp(
            monthlyWithdrawal = monthlyWithdrawal,
            durationYears = years,
            expectedReturnAnnual = returnRate,
            annualIncreasePercent = annualInc
        )
        _uiState.update { state ->
            state.copy(
                reverseSwpWithdrawal = monthlyWithdrawal,
                reverseSwpYears = years,
                reverseSwpReturn = returnRate,
                reverseSwpAnnualInc = annualInc,
                requiredCorpusForSwp = requiredCorpus
            )
        }
    }

    fun updateDefaultReturn(rate: Double) {
        repository?.setDefaultReturn(rate)
        _uiState.update { it.copy(defaultExpectedReturn = rate) }
    }

    fun updateDefaultInflation(rate: Double) {
        repository?.setDefaultInflation(rate)
        _uiState.update { it.copy(defaultInflation = rate) }
    }

    // ==========================================
    // FUND FINDER ACTIONS
    // ==========================================

    fun setFundFinderStep(step: Int) {
        val bounded = Math.max(0, Math.min(7, step))
        _uiState.update { it.copy(fundFinderStep = bounded) }
    }

    fun updateInvestorProfile(profile: InvestorProfile) {
        _uiState.update { state ->
            val updated = state.copy(
                investorProfile = profile,
                fundFinderGoal = state.fundFinderGoal.copy(monthlyBudget = profile.monthlyBudgetInr)
            )
            recalculateFundFinder(updated)
        }
    }

    fun updateFundFinderGoal(goal: FundFinderGoal) {
        _uiState.update { state ->
            val updated = state.copy(fundFinderGoal = goal)
            recalculateFundFinder(updated)
        }
    }

    fun answerRiskQuestion(questionId: String, scoreValue: Int) {
        _uiState.update { state ->
            val updatedAnswers = state.riskAnswers.toMutableMap().apply {
                put(questionId, scoreValue)
            }
            val newScore = RiskScoringEngine.calculateScores(updatedAnswers)
            val updated = state.copy(
                riskAnswers = updatedAnswers,
                riskResult = newScore
            )
            recalculateFundFinder(updated)
        }
    }

    private fun recalculateFundFinder(state: WealthFlowUiState): WealthFlowUiState {
        val score = RiskScoringEngine.calculateScores(state.riskAnswers)
        val primaryGuidance = CategoryMatchingEngine.matchPrimaryCategory(
            goal = state.fundFinderGoal,
            profileResult = score,
            investorProfile = state.investorProfile
        )
        val categories = CategoryMatchingEngine.matchCategories(
            goal = state.fundFinderGoal,
            profileResult = score,
            investorProfile = state.investorProfile
        )
        val plan = IllustrativeSipEngine.buildPortfolioPlan(
            monthlyBudget = state.investorProfile.monthlyBudgetInr,
            horizonYears = state.fundFinderGoal.durationYears,
            recommendations = categories
        )
        return state.copy(
            riskResult = score,
            primaryCategoryGuidance = primaryGuidance,
            categoryRecommendations = categories,
            illustrativePlan = plan
        )
    }

    fun loadPrimaryCategoryIntoCalculator() {
        val guidance = _uiState.value.primaryCategoryGuidance
        val newSipInput = SipInput(
            initialLumpsum = 0.0,
            periodicInvestment = guidance.suggestedSipAmount,
            frequency = Frequency.MONTHLY,
            durationYears = guidance.horizonYears,
            expectedReturnAnnual = guidance.expectedReturnAnnual,
            enableStepUp = true,
            stepUpType = StepUpType.PERCENTAGE,
            stepUpValue = 10.0,
            stepUpFrequencyYears = 1
        )
        _uiState.update { state ->
            state.copy(
                sipInput = newSipInput,
                sipResult = FinancialMathEngine.calculateSIP(newSipInput),
                currentDestination = MainDestination.PLAN,
                currentPlanSubTab = PlanSubTab.SIP,
                snackbarMessage = "Loaded ${guidance.primaryCategory.displayName} (₹${guidance.suggestedSipAmount.toInt()}/mo, ${guidance.horizonYears}Y) into SIP Calculator"
            )
        }
        SoundManager.play(SoundManager.SoundType.SUCCESS)
    }

    fun filterFundExplorerByCategory(category: SebiCategory?) {
        _uiState.update { it.copy(selectedCategoryForFilter = category) }
    }

    fun updateFundSearchQuery(query: String) {
        _uiState.update { it.copy(fundSearchQuery = query) }
    }

    fun toggleFundComparison(amfiCode: String) {
        _uiState.update { state ->
            val current = state.comparedSchemeAmfiCodes.toMutableList()
            if (current.contains(amfiCode)) {
                current.remove(amfiCode)
                SoundManager.play(SoundManager.SoundType.POP)
            } else {
                if (current.size < 4) {
                    current.add(amfiCode)
                    SoundManager.play(SoundManager.SoundType.POP)
                } else {
                    SoundManager.play(SoundManager.SoundType.POP)
                    return@update state.copy(snackbarMessage = "Maximum 4 schemes can be compared simultaneously.")
                }
            }
            state.copy(comparedSchemeAmfiCodes = current)
        }
    }

    fun clearFundComparison() {
        _uiState.update { it.copy(comparedSchemeAmfiCodes = emptyList()) }
        SoundManager.play(SoundManager.SoundType.POP)
    }

    fun loadIllustrativeSipIntoCalculator() {
        val plan = _uiState.value.illustrativePlan
        _uiState.update { state ->
            state.copy(
                sipInput = plan.sipInputForCalculator,
                sipResult = FinancialMathEngine.calculateSIP(plan.sipInputForCalculator),
                currentDestination = MainDestination.PLAN,
                currentPlanSubTab = PlanSubTab.SIP,
                snackbarMessage = "Loaded Fund Finder SIP (${plan.allocations.size} Categories, ₹${plan.totalMonthlyBudget.toInt()}/mo) into SIP Calculator"
            )
        }
        SoundManager.play(SoundManager.SoundType.SUCCESS)
    }

    fun resetFundFinder() {
        _uiState.update { state ->
            state.copy(
                fundFinderStep = 0,
                riskAnswers = emptyMap(),
                comparedSchemeAmfiCodes = emptyList(),
                fundSearchQuery = "",
                selectedCategoryForFilter = null
            )
        }
        SoundManager.play(SoundManager.SoundType.POP)
    }

    fun toggleGlossaryDialog(show: Boolean) {
        _uiState.update { it.copy(showGlossaryDialog = show) }
        SoundManager.play(SoundManager.SoundType.TAB)
    }

    // ==========================================
    // LEARNING HUB ACTIONS
    // ==========================================

    fun selectLearningLanguage(language: LearningHubConfig.VideoLanguage?) {
        _uiState.update { it.copy(selectedLearningLanguage = language) }
        SoundManager.play(SoundManager.SoundType.TAB)
    }

    fun clearLearningLanguage() {
        _uiState.update { it.copy(selectedLearningLanguage = null) }
        SoundManager.play(SoundManager.SoundType.TAB)
    }
}
