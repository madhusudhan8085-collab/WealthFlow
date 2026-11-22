package com.example.data.fundfinder

import com.example.calculations.FinancialMathEngine
import com.example.data.model.Frequency
import com.example.data.model.SipInput
import com.example.data.model.SipResult
import com.example.data.model.StepUpType

/**
 * Generates an illustrative monthly SIP allocation across matched categories,
 * calculates expected portfolio blended return range, and converts directly into SipInput.
 */
object IllustrativeSipEngine {

    data class SipPortfolioPlan(
        val totalMonthlyBudget: Double,
        val durationYears: Int,
        val allocations: List<IllustrativeSipAllocation>,
        val estimatedBlendedCagr: Double,
        val projectedCorpus: Double,
        val totalInvested: Double,
        val totalEstimatedGain: Double,
        val sipInputForCalculator: SipInput
    )

    fun buildPortfolioPlan(
        monthlyBudget: Double,
        horizonYears: Int,
        recommendations: List<CategoryRecommendation>
    ): SipPortfolioPlan {
        val totalBudget = Math.max(1000.0, monthlyBudget)
        val validHorizon = Math.max(1, horizonYears)

        val allocations = mutableListOf<IllustrativeSipAllocation>()
        var weightedCagrSum = 0.0

        for (rec in recommendations) {
            val rawAmount = (totalBudget * rec.suggestedAllocationPercent) / 100.0
            // Round to nearest 500
            val roundedAmount = Math.max(500.0, Math.round(rawAmount / 500.0) * 500.0)
            val scheme = FundDataProvider.getTopSchemeForCategory(rec.sebiCategory)

            val categoryCagrEstimate = when (rec.sebiCategory.assetClass) {
                AssetClassCategory.EQUITY -> 13.0
                AssetClassCategory.PASSIVE -> 12.0
                AssetClassCategory.HYBRID -> 10.5
                AssetClassCategory.DEBT -> 7.0
            }

            weightedCagrSum += (categoryCagrEstimate * rec.suggestedAllocationPercent) / 100.0

            allocations.add(
                IllustrativeSipAllocation(
                    category = rec.sebiCategory,
                    percentage = rec.suggestedAllocationPercent,
                    monthlyAmount = roundedAmount,
                    recommendedScheme = scheme,
                    role = rec.role
                )
            )
        }

        val blendedCagr = Math.round(weightedCagrSum * 10.0) / 10.0

        val sipInput = SipInput(
            initialLumpsum = 0.0,
            periodicInvestment = totalBudget,
            frequency = Frequency.MONTHLY,
            durationYears = validHorizon,
            expectedReturnAnnual = blendedCagr,
            enableStepUp = true,
            stepUpType = StepUpType.PERCENTAGE,
            stepUpValue = 10.0,
            stepUpFrequencyYears = 1
        )

        val sipResult: SipResult = FinancialMathEngine.calculateSIP(sipInput)

        return SipPortfolioPlan(
            totalMonthlyBudget = totalBudget,
            durationYears = validHorizon,
            allocations = allocations,
            estimatedBlendedCagr = blendedCagr,
            projectedCorpus = sipResult.finalCorpus,
            totalInvested = sipResult.totalInvested,
            totalEstimatedGain = sipResult.totalGains,
            sipInputForCalculator = sipInput
        )
    }
}
