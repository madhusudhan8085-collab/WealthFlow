package com.example.data.model

enum class Frequency(val displayName: String, val periodsPerYear: Int) {
    MONTHLY("Monthly", 12),
    QUARTERLY("Quarterly", 4),
    YEARLY("Yearly", 1)
}

enum class StepUpType(val displayName: String) {
    PERCENTAGE("Percentage (%)"),
    FIXED_AMOUNT("Fixed Amount (₹)")
}

data class SipInput(
    val initialLumpsum: Double = 0.0,
    val periodicInvestment: Double = 15000.0,
    val frequency: Frequency = Frequency.MONTHLY,
    val durationYears: Int = 15,
    val expectedReturnAnnual: Double = 12.0,
    val enableStepUp: Boolean = true,
    val stepUpType: StepUpType = StepUpType.PERCENTAGE,
    val stepUpValue: Double = 10.0, // 10% or ₹1,000
    val stepUpFrequencyYears: Int = 1,
    val targetCorpus: Double? = null
)

data class YearlyRecord(
    val year: Int,
    val openingBalance: Double,
    val yearlyContribution: Double,
    val periodicContribution: Double,
    val investmentGains: Double,
    val closingBalance: Double,
    val cumulativeInvested: Double,
    val cumulativeGains: Double
)

data class SipResult(
    val totalInvested: Double,
    val totalGains: Double,
    val finalCorpus: Double,
    val growthMultiplier: Double,
    val gainsPercentage: Double,
    val yearlyBreakdown: List<YearlyRecord>,
    val targetAchieved: Boolean = false,
    val targetShortfall: Double = 0.0
)

data class SwpInput(
    val startingCorpus: Double = 5000000.0, // ₹50 Lakhs
    val periodicWithdrawal: Double = 35000.0, // ₹35,000/mo
    val frequency: Frequency = Frequency.MONTHLY,
    val durationYears: Int = 20,
    val expectedReturnAnnual: Double = 9.0,
    val annualWithdrawalIncreasePercent: Double = 5.0, // inflation-linked
    val enableAnnualIncrease: Boolean = true
)

data class SwpYearlyRecord(
    val year: Int,
    val openingCorpus: Double,
    val annualWithdrawal: Double,
    val periodicWithdrawal: Double,
    val growthEarned: Double,
    val closingCorpus: Double,
    val cumulativeWithdrawn: Double,
    val isDepleted: Boolean = false
)

data class SwpResult(
    val totalWithdrawn: Double,
    val totalGrowthEarned: Double,
    val remainingCorpus: Double,
    val survivesFullDuration: Boolean,
    val depletionMonth: Int?, // month index if depleted, null if survived
    val depletionYear: Double?,
    val safeWithdrawalRatePercent: Double,
    val yearlyBreakdown: List<SwpYearlyRecord>
)

data class SipToSwpInput(
    // Phase 1 - Accumulation
    val sipInvestment: Double = 20000.0,
    val sipStepUpPercent: Double = 10.0,
    val sipDurationYears: Int = 15,
    val sipExpectedReturn: Double = 12.0,
    val sipLumpsum: Double = 100000.0,
    // Phase 2 - Distribution
    val swpExpectedReturn: Double = 8.5,
    val swpMonthlyWithdrawal: Double = 60000.0,
    val swpDurationYears: Int = 20,
    val swpAnnualIncreasePercent: Double = 6.0
)

data class SipToSwpResult(
    val accumulationInvested: Double,
    val accumulatedCorpus: Double,
    val accumulationGains: Double,
    val totalWithdrawn: Double,
    val finalRemainingCorpus: Double,
    val totalGrowthEarnedInSwp: Double,
    val swpSurvives: Boolean,
    val swpDepletionMonth: Int?,
    val accumulationBreakdown: List<YearlyRecord>,
    val withdrawalBreakdown: List<SwpYearlyRecord>
)

data class GoalInput(
    val targetAmountToday: Double = 5000000.0, // ₹50 Lakhs in today's money
    val inflationRateAnnual: Double = 6.0,
    val yearsUntilGoal: Int = 15,
    val expectedReturnAnnual: Double = 12.0,
    val stepUpPercent: Double = 10.0
)

data class GoalResult(
    val futureTargetAmount: Double,
    val inflationMultiplier: Double,
    val requiredFlatMonthlySip: Double,
    val requiredStepUpStartingSip: Double,
    val totalInvestedFlat: Double,
    val totalInvestedStepUp: Double,
    val expectedGainsFlat: Double,
    val expectedGainsStepUp: Double
)

data class ScenarioComparison(
    val conservativeReturn: Double = 8.0,
    val baseReturn: Double = 12.0,
    val optimisticReturn: Double = 15.0,
    val conservativeCorpus: Double,
    val baseCorpus: Double,
    val optimisticCorpus: Double,
    val totalInvested: Double
)

data class SensitivityPoint(
    val returnRate: Double,
    val durationYears: Int,
    val finalCorpus: Double
)

data class WhatIfDelta(
    val baseCorpus: Double,
    val newCorpus: Double,
    val differenceCorpus: Double,
    val baseInvested: Double,
    val newInvested: Double,
    val differenceInvested: Double,
    val differenceGains: Double
)
