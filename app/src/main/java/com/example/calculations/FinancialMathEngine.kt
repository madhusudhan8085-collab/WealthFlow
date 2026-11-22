package com.example.calculations

import com.example.data.model.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

object FinancialMathEngine {

    /**
     * Calculates SIP with step-up support, periodic compounding, and year-by-year schedule.
     * Treats payments at beginning of period (annuity due).
     */
    fun calculateSIP(input: SipInput): SipResult {
        val periodsPerYear = input.frequency.periodsPerYear
        val totalYears = max(1, input.durationYears)
        val totalPeriods = totalYears * periodsPerYear
        val ratePerPeriod = (input.expectedReturnAnnual / 100.0) / periodsPerYear

        var currentBalance = max(0.0, input.initialLumpsum)
        var cumulativeInvested = currentBalance
        val yearlyRecords = ArrayList<YearlyRecord>(totalYears)

        val basePeriodic = max(0.0, input.periodicInvestment)
        val stepUpValue = max(0.0, input.stepUpValue)
        val stepUpFreqYears = max(1, input.stepUpFrequencyYears)

        var cumulativeGains = 0.0

        for (year in 1..totalYears) {
            val yearOpening = currentBalance
            var yearContribution = 0.0
            var yearGains = 0.0

            // Determine periodic contribution for this year
            val yearIndex = (year - 1) / stepUpFreqYears
            val periodicForYear = if (input.enableStepUp) {
                when (input.stepUpType) {
                    StepUpType.PERCENTAGE -> {
                        basePeriodic * (1.0 + stepUpValue / 100.0).pow(yearIndex.toDouble())
                    }
                    StepUpType.FIXED_AMOUNT -> {
                        basePeriodic + (yearIndex * stepUpValue)
                    }
                }
            } else {
                basePeriodic
            }

            for (p in 1..periodsPerYear) {
                // Beginning of period deposit
                currentBalance += periodicForYear
                yearContribution += periodicForYear
                cumulativeInvested += periodicForYear

                // Growth for the period
                val periodGain = currentBalance * ratePerPeriod
                currentBalance += periodGain
                yearGains += periodGain
            }

            cumulativeGains += yearGains
            yearlyRecords.add(
                YearlyRecord(
                    year = year,
                    openingBalance = yearOpening,
                    yearlyContribution = yearContribution,
                    periodicContribution = periodicForYear,
                    investmentGains = yearGains,
                    closingBalance = currentBalance,
                    cumulativeInvested = cumulativeInvested,
                    cumulativeGains = cumulativeGains
                )
            )
        }

        val totalInvested = cumulativeInvested
        val finalCorpus = currentBalance
        val totalGains = max(0.0, finalCorpus - totalInvested)
        val growthMultiplier = if (totalInvested > 0.0) finalCorpus / totalInvested else 0.0
        val gainsPercentage = if (totalInvested > 0.0) (totalGains / totalInvested) * 100.0 else 0.0

        val target = input.targetCorpus
        val targetAchieved = target != null && finalCorpus >= target
        val targetShortfall = if (target != null) max(0.0, target - finalCorpus) else 0.0

        return SipResult(
            totalInvested = totalInvested,
            totalGains = totalGains,
            finalCorpus = finalCorpus,
            growthMultiplier = growthMultiplier,
            gainsPercentage = gainsPercentage,
            yearlyBreakdown = yearlyRecords,
            targetAchieved = targetAchieved,
            targetShortfall = targetShortfall
        )
    }

    /**
     * Calculates SWP (Systematic Withdrawal Plan).
     * Withdrawals happen at the beginning of each period, with the remaining corpus earning interest.
     */
    fun calculateSWP(input: SwpInput): SwpResult {
        val periodsPerYear = input.frequency.periodsPerYear
        val totalYears = max(1, input.durationYears)
        val ratePerPeriod = (input.expectedReturnAnnual / 100.0) / periodsPerYear

        var currentCorpus = max(0.0, input.startingCorpus)
        val baseWithdrawal = max(0.0, input.periodicWithdrawal)
        val annualIncreaseRate = if (input.enableAnnualIncrease) input.annualWithdrawalIncreasePercent / 100.0 else 0.0

        var totalWithdrawn = 0.0
        var totalGrowthEarned = 0.0
        var depletionPeriod: Int? = null
        var totalPeriodsPassed = 0

        val yearlyRecords = ArrayList<SwpYearlyRecord>(totalYears)

        for (year in 1..totalYears) {
            val yearOpening = currentCorpus
            var yearWithdrawn = 0.0
            var yearGrowth = 0.0
            var isYearDepleted = false

            // Current year's withdrawal per period
            val periodicWithdrawalForYear = baseWithdrawal * (1.0 + annualIncreaseRate).pow((year - 1).toDouble())

            for (p in 1..periodsPerYear) {
                totalPeriodsPassed++
                if (currentCorpus <= 0.0) {
                    if (depletionPeriod == null) {
                        depletionPeriod = totalPeriodsPassed - 1
                    }
                    isYearDepleted = true
                    continue
                }

                // Withdrawal at beginning of period
                val actualWithdrawal = min(currentCorpus, periodicWithdrawalForYear)
                currentCorpus -= actualWithdrawal
                yearWithdrawn += actualWithdrawal
                totalWithdrawn += actualWithdrawal

                if (currentCorpus <= 0.0) {
                    if (depletionPeriod == null) {
                        depletionPeriod = totalPeriodsPassed
                    }
                    isYearDepleted = true
                } else {
                    // Growth on remaining balance
                    val periodGrowth = currentCorpus * ratePerPeriod
                    currentCorpus += periodGrowth
                    yearGrowth += periodGrowth
                    totalGrowthEarned += periodGrowth
                }
            }

            yearlyRecords.add(
                SwpYearlyRecord(
                    year = year,
                    openingCorpus = yearOpening,
                    annualWithdrawal = yearWithdrawn,
                    periodicWithdrawal = periodicWithdrawalForYear,
                    growthEarned = yearGrowth,
                    closingCorpus = currentCorpus,
                    cumulativeWithdrawn = totalWithdrawn,
                    isDepleted = isYearDepleted || currentCorpus <= 0.0
                )
            )
        }

        val survivesFullDuration = depletionPeriod == null && currentCorpus > 0.0
        val depletionYear = depletionPeriod?.let { it.toDouble() / periodsPerYear }
        val safeWithdrawalRatePercent = if (input.startingCorpus > 0.0) {
            (baseWithdrawal * periodsPerYear / input.startingCorpus) * 100.0
        } else 0.0

        return SwpResult(
            totalWithdrawn = totalWithdrawn,
            totalGrowthEarned = totalGrowthEarned,
            remainingCorpus = currentCorpus,
            survivesFullDuration = survivesFullDuration,
            depletionMonth = depletionPeriod?.let { (it * 12) / periodsPerYear },
            depletionYear = depletionYear,
            safeWithdrawalRatePercent = safeWithdrawalRatePercent,
            yearlyBreakdown = yearlyRecords
        )
    }

    /**
     * Combined SIP -> SWP Lifecycle.
     * Phase 1 accumulates wealth, Phase 2 decumulates through structured retirement withdrawals.
     */
    fun calculateSIPToSWP(input: SipToSwpInput): SipToSwpResult {
        // Phase 1: SIP Accumulation
        val sipInput = SipInput(
            initialLumpsum = input.sipLumpsum,
            periodicInvestment = input.sipInvestment,
            frequency = Frequency.MONTHLY,
            durationYears = input.sipDurationYears,
            expectedReturnAnnual = input.sipExpectedReturn,
            enableStepUp = input.sipStepUpPercent > 0.0,
            stepUpType = StepUpType.PERCENTAGE,
            stepUpValue = input.sipStepUpPercent,
            stepUpFrequencyYears = 1
        )
        val sipResult = calculateSIP(sipInput)

        // Phase 2: SWP Distribution
        val swpInput = SwpInput(
            startingCorpus = sipResult.finalCorpus,
            periodicWithdrawal = input.swpMonthlyWithdrawal,
            frequency = Frequency.MONTHLY,
            durationYears = input.swpDurationYears,
            expectedReturnAnnual = input.swpExpectedReturn,
            enableAnnualIncrease = input.swpAnnualIncreasePercent > 0.0,
            annualWithdrawalIncreasePercent = input.swpAnnualIncreasePercent
        )
        val swpResult = calculateSWP(swpInput)

        return SipToSwpResult(
            accumulationInvested = sipResult.totalInvested,
            accumulatedCorpus = sipResult.finalCorpus,
            accumulationGains = sipResult.totalGains,
            totalWithdrawn = swpResult.totalWithdrawn,
            finalRemainingCorpus = swpResult.remainingCorpus,
            totalGrowthEarnedInSwp = swpResult.totalGrowthEarned,
            swpSurvives = swpResult.survivesFullDuration,
            swpDepletionMonth = swpResult.depletionMonth,
            accumulationBreakdown = sipResult.yearlyBreakdown,
            withdrawalBreakdown = swpResult.yearlyBreakdown
        )
    }

    /**
     * Goal-Based Reverse SIP with Inflation Adjustment.
     */
    fun calculateGoalSIP(input: GoalInput): GoalResult {
        val years = max(1, input.yearsUntilGoal)
        val inflationMultiplier = (1.0 + input.inflationRateAnnual / 100.0).pow(years.toDouble())
        val futureTarget = input.targetAmountToday * inflationMultiplier

        // Flat SIP calculation
        val monthlyRate = (input.expectedReturnAnnual / 100.0) / 12.0
        val totalMonths = years * 12

        val requiredFlatSip = if (monthlyRate == 0.0) {
            futureTarget / totalMonths
        } else {
            // FV = S * ((1+r)^n - 1)/r * (1+r)
            val factor = ((1.0 + monthlyRate).pow(totalMonths.toDouble()) - 1.0) / monthlyRate * (1.0 + monthlyRate)
            futureTarget / factor
        }

        // Step-up starting SIP calculation:
        // Run test with base SIP = 1.0, step-up percent
        val testInput = SipInput(
            initialLumpsum = 0.0,
            periodicInvestment = 1.0,
            frequency = Frequency.MONTHLY,
            durationYears = years,
            expectedReturnAnnual = input.expectedReturnAnnual,
            enableStepUp = input.stepUpPercent > 0.0,
            stepUpType = StepUpType.PERCENTAGE,
            stepUpValue = input.stepUpPercent
        )
        val testResult = calculateSIP(testInput)
        val stepUpMultiplier = testResult.finalCorpus
        val requiredStepUpStartingSip = if (stepUpMultiplier > 0.0) futureTarget / stepUpMultiplier else requiredFlatSip

        // Total invested and gains for Flat
        val totalInvestedFlat = requiredFlatSip * totalMonths
        val gainsFlat = max(0.0, futureTarget - totalInvestedFlat)

        // Total invested and gains for Step-up
        val stepUpActualResult = calculateSIP(testInput.copy(periodicInvestment = requiredStepUpStartingSip))
        val totalInvestedStepUp = stepUpActualResult.totalInvested
        val gainsStepUp = max(0.0, futureTarget - totalInvestedStepUp)

        return GoalResult(
            futureTargetAmount = futureTarget,
            inflationMultiplier = inflationMultiplier,
            requiredFlatMonthlySip = requiredFlatSip,
            requiredStepUpStartingSip = requiredStepUpStartingSip,
            totalInvestedFlat = totalInvestedFlat,
            totalInvestedStepUp = totalInvestedStepUp,
            expectedGainsFlat = gainsFlat,
            expectedGainsStepUp = gainsStepUp
        )
    }

    /**
     * Reverse SWP: Calculates the required corpus to sustain a monthly withdrawal for N years.
     */
    fun calculateRequiredCorpusForSwp(
        monthlyWithdrawal: Double,
        durationYears: Int,
        expectedReturnAnnual: Double,
        annualIncreasePercent: Double = 0.0
    ): Double {
        val testCorpus = 10_000_000.0
        val testInput = SwpInput(
            startingCorpus = testCorpus,
            periodicWithdrawal = monthlyWithdrawal,
            frequency = Frequency.MONTHLY,
            durationYears = durationYears,
            expectedReturnAnnual = expectedReturnAnnual,
            enableAnnualIncrease = annualIncreasePercent > 0.0,
            annualWithdrawalIncreasePercent = annualIncreasePercent
        )

        // Exact present-value discounted sum of withdrawals
        val monthlyRate = (expectedReturnAnnual / 100.0) / 12.0
        var pv = 0.0
        val annualIncreaseRate = annualIncreasePercent / 100.0

        for (m in 1..(durationYears * 12)) {
            val year = (m - 1) / 12
            val w = monthlyWithdrawal * (1.0 + annualIncreaseRate).pow(year.toDouble())
            val discount = if (monthlyRate == 0.0) 1.0 else (1.0 + monthlyRate).pow((m - 1).toDouble())
            pv += w / discount
        }
        return pv
    }

    /**
     * Maximum sustainable monthly withdrawal that never depletes the corpus (Perpetual / Endowment).
     */
    fun calculateSustainableMonthlyWithdrawal(corpus: Double, expectedReturnAnnual: Double): Double {
        val monthlyRate = (expectedReturnAnnual / 100.0) / 12.0
        if (monthlyRate <= 0.0) return 0.0
        // W = Corpus * r / (1 + r)
        return (corpus * monthlyRate) / (1.0 + monthlyRate)
    }

    /**
     * Scenario comparison: Conservative vs Base vs Optimistic returns.
     */
    fun calculateScenarios(
        baseInput: SipInput,
        conservativeReturn: Double = 8.0,
        optimisticReturn: Double = 15.0
    ): ScenarioComparison {
        val consResult = calculateSIP(baseInput.copy(expectedReturnAnnual = conservativeReturn))
        val baseResult = calculateSIP(baseInput)
        val optResult = calculateSIP(baseInput.copy(expectedReturnAnnual = optimisticReturn))

        return ScenarioComparison(
            conservativeReturn = conservativeReturn,
            baseReturn = baseInput.expectedReturnAnnual,
            optimisticReturn = optimisticReturn,
            conservativeCorpus = consResult.finalCorpus,
            baseCorpus = baseResult.finalCorpus,
            optimisticCorpus = optResult.finalCorpus,
            totalInvested = baseResult.totalInvested
        )
    }

    /**
     * Calculates the purchasing power equivalent in today's money of a future nominal amount.
     * Formula: PV = FV / (1 + r_inflation)^n
     */
    fun calculatePurchasingPower(
        futureAmount: Double,
        inflationRateAnnual: Double,
        durationYears: Int
    ): Double {
        if (inflationRateAnnual <= 0.0 || durationYears <= 0) return futureAmount
        val discountFactor = (1.0 + inflationRateAnnual / 100.0).pow(durationYears.toDouble())
        return if (discountFactor > 0.0) futureAmount / discountFactor else futureAmount
    }

    /**
     * Sensitivity Matrix: Generates 25 matrix cells for varying returns and durations.
     */
    fun calculateSensitivityMatrix(
        periodicAmount: Double,
        stepUpPercent: Double,
        returnRates: List<Double> = listOf(8.0, 10.0, 12.0, 14.0, 16.0),
        durations: List<Int> = listOf(5, 10, 15, 20, 25)
    ): List<SensitivityPoint> {
        val points = mutableListOf<SensitivityPoint>()
        for (r in returnRates) {
            for (d in durations) {
                val input = SipInput(
                    periodicInvestment = periodicAmount,
                    durationYears = d,
                    expectedReturnAnnual = r,
                    enableStepUp = stepUpPercent > 0.0,
                    stepUpType = StepUpType.PERCENTAGE,
                    stepUpValue = stepUpPercent
                )
                val res = calculateSIP(input)
                points.add(SensitivityPoint(returnRate = r, durationYears = d, finalCorpus = res.finalCorpus))
            }
        }
        return points
    }

    /**
     * What-If Simulator: Calculates differential between base plan and tweaked inputs.
     */
    fun calculateWhatIf(
        baseInput: SipInput,
        deltaMonthlySip: Double = 0.0,
        deltaYears: Int = 0,
        deltaReturnAnnual: Double = 0.0,
        deltaStepUpPercent: Double = 0.0
    ): WhatIfDelta {
        val baseResult = calculateSIP(baseInput)

        val newSip = max(500.0, baseInput.periodicInvestment + deltaMonthlySip)
        val newYears = max(1, baseInput.durationYears + deltaYears)
        val newReturn = max(0.0, baseInput.expectedReturnAnnual + deltaReturnAnnual)
        val newStepUp = max(0.0, (if (baseInput.enableStepUp) baseInput.stepUpValue else 0.0) + deltaStepUpPercent)

        val newInput = baseInput.copy(
            periodicInvestment = newSip,
            durationYears = newYears,
            expectedReturnAnnual = newReturn,
            enableStepUp = newStepUp > 0.0,
            stepUpValue = newStepUp
        )
        val newResult = calculateSIP(newInput)

        return WhatIfDelta(
            baseCorpus = baseResult.finalCorpus,
            newCorpus = newResult.finalCorpus,
            differenceCorpus = newResult.finalCorpus - baseResult.finalCorpus,
            baseInvested = baseResult.totalInvested,
            newInvested = newResult.totalInvested,
            differenceInvested = newResult.totalInvested - baseResult.totalInvested,
            differenceGains = newResult.totalGains - baseResult.totalGains
        )
    }

    /**
     * Cost of Delay: How much wealth is forfeited by delaying investment by delayYears.
     */
    fun calculateCostOfDelay(baseInput: SipInput, delayYears: Int = 3): Double {
        val normalResult = calculateSIP(baseInput)
        val delayedYears = max(1, baseInput.durationYears - delayYears)
        val delayedResult = calculateSIP(baseInput.copy(durationYears = delayedYears))
        return max(0.0, normalResult.finalCorpus - delayedResult.finalCorpus)
    }
}
