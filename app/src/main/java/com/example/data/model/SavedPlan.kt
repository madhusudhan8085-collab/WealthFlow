package com.example.data.model

import java.util.UUID

enum class PlanType(val displayName: String) {
    SIP("SIP Investment"),
    SWP("SWP Pension"),
    SIP_TO_SWP("Accumulate → Retire"),
    GOAL("Goal Target")
}

data class SavedPlan(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: PlanType,
    val dateCreatedMillis: Long = System.currentTimeMillis(),
    val primaryMetricLabel: String,
    val primaryMetricValue: Double,
    val secondaryMetricLabel: String,
    val secondaryMetricValue: Double,
    val tenureYears: Int,
    val returnRateAnnual: Double,
    val monthlyAmount: Double,
    // Serialized payloads to rehydrate calculator states
    val sipInput: SipInput? = null,
    val swpInput: SwpInput? = null,
    val sipToSwpInput: SipToSwpInput? = null,
    val goalInput: GoalInput? = null
)
