package com.example

import com.example.data.fundfinder.*
import org.junit.Assert.*
import org.junit.Test

class FundFinderTest {

    @Test
    fun testRiskScoring_highToleranceAndCapacity_yieldsAggressive() {
        val answers = mutableMapOf<String, Int>()
        for (q in RiskScoringEngine.questions) {
            answers[q.id] = 5 // maximum willingness and capacity
        }

        val result = RiskScoringEngine.calculateScores(answers)
        assertEquals(100, result.toleranceScore)
        assertEquals(100, result.capacityScore)
        assertEquals(RiskProfileCategory.AGGRESSIVE, result.profile)
        assertFalse(result.hasToleranceCapacityMismatch)
    }

    @Test
    fun testRiskScoring_lowToleranceAndCapacity_yieldsConservative() {
        val answers = mutableMapOf<String, Int>()
        for (q in RiskScoringEngine.questions) {
            answers[q.id] = 1 // minimum willingness and capacity
        }

        val result = RiskScoringEngine.calculateScores(answers)
        assertTrue(result.compositeScore < 35)
        assertEquals(RiskProfileCategory.CONSERVATIVE, result.profile)
        assertFalse(result.hasToleranceCapacityMismatch)
    }

    @Test
    fun testRiskScoring_toleranceExceedsCapacity_triggersConstraintMismatch() {
        // High psychological tolerance, but poor financial capacity (debt, no emergency fund, unstable income)
        val answers = mutableMapOf(
            "tol_fluctuation" to 5,
            "tol_decline_10" to 5,
            "tol_decline_20" to 5,
            "tol_downturn_continue" to 5,
            "cap_emergency_fund" to 1,
            "cap_debt" to 1,
            "cap_income_stability" to 1,
            "cap_liquidity_horizon" to 1,
            "cap_essential_expense" to 1,
            "know_experience" to 4
        )

        val result = RiskScoringEngine.calculateScores(answers)
        assertTrue("Tolerance ($result.toleranceScore) must exceed capacity ($result.capacityScore)", result.toleranceScore > result.capacityScore)
        assertTrue("Must detect tolerance vs capacity mismatch", result.hasToleranceCapacityMismatch)
        assertNotNull(result.mismatchExplanation)
        // Must NOT be Aggressive or Moderately Aggressive because capacity is low (around 20)
        assertNotEquals(RiskProfileCategory.AGGRESSIVE, result.profile)
        assertNotEquals(RiskProfileCategory.MODERATELY_AGGRESSIVE, result.profile)
        assertEquals(RiskProfileCategory.CONSERVATIVE, result.profile)
    }

    @Test
    fun testCategoryMatching_shortHorizon_neverRecommendsEquities() {
        val goal = FundFinderGoal(
            type = GoalType.SHORT_TERM,
            durationYears = 2,
            targetAmountToday = 500000.0
        )
        val profileResult = RiskAssessmentResult(
            toleranceScore = 90,
            capacityScore = 90,
            knowledgeScore = 80,
            compositeScore = 88,
            profile = RiskProfileCategory.AGGRESSIVE,
            hasToleranceCapacityMismatch = false,
            mismatchExplanation = null,
            rationale = "High risk test"
        )
        val investor = InvestorProfile(age = 25, monthlyBudgetInr = 20000.0)

        val recommendations = CategoryMatchingEngine.matchCategories(goal, profileResult, investor)

        // For < 3 years, only debt/liquid instruments are suitable
        assertTrue("Must have recommendations", recommendations.isNotEmpty())
        for (rec in recommendations) {
            assertEquals("Asset class must be DEBT for < 3 years", AssetClassCategory.DEBT, rec.sebiCategory.assetClass)
            assertNotEquals(SebiCategory.SMALL_CAP, rec.sebiCategory)
            assertNotEquals(SebiCategory.MID_CAP, rec.sebiCategory)
            assertNotEquals(SebiCategory.FLEXI_CAP, rec.sebiCategory)
        }
    }

    @Test
    fun testCategoryMatching_longHorizonAggressive_respectsSmallCapSatelliteLimit() {
        val goal = FundFinderGoal(
            type = GoalType.WEALTH_CREATION,
            durationYears = 10,
            targetAmountToday = 5000000.0
        )
        val profileResult = RiskAssessmentResult(
            toleranceScore = 85,
            capacityScore = 80,
            knowledgeScore = 80,
            compositeScore = 82,
            profile = RiskProfileCategory.AGGRESSIVE,
            hasToleranceCapacityMismatch = false,
            mismatchExplanation = null,
            rationale = "Aggressive test"
        )
        val investor = InvestorProfile(age = 32, monthlyBudgetInr = 25000.0)

        val recommendations = CategoryMatchingEngine.matchCategories(goal, profileResult, investor)

        val smallCapRec = recommendations.find { it.sebiCategory == SebiCategory.SMALL_CAP }
        if (smallCapRec != null) {
            assertEquals(AllocationRole.SATELLITE, smallCapRec.role)
            assertTrue("Small cap allocation must be capped at <= 15%", smallCapRec.suggestedAllocationPercent <= 15)
        }

        // Sum of allocations must equal 100%
        val totalAlloc = recommendations.sumOf { it.suggestedAllocationPercent }
        assertEquals(100, totalAlloc)
    }

    @Test
    fun testFundDataProvider_verifiedSchemesAvailable() {
        val schemes = FundDataProvider.verifiedSchemes
        assertTrue("Must provide schemes across SEBI categories", schemes.size >= 12)

        val paragParikh = FundDataProvider.getSchemeByAmfiCode("122639")
        assertNotNull(paragParikh)
        assertEquals("Parag Parikh Flexi Cap Fund Direct - Growth", paragParikh?.schemeName)
        assertEquals(FundPlanType.DIRECT, paragParikh?.planType)
        assertEquals(FundOption.GROWTH, paragParikh?.option)

        val hdfcLiquid = FundDataProvider.getTopSchemeForCategory(SebiCategory.LIQUID)
        assertNotNull(hdfcLiquid)
        assertEquals(SebiCategory.LIQUID, hdfcLiquid?.category)

        val searchResults = FundDataProvider.searchSchemes("index")
        assertTrue("Must find index funds", searchResults.any { it.category == SebiCategory.INDEX_FUND })
    }

    @Test
    fun testIllustrativeSipEngine_buildsValidPlanAndSipInput() {
        val recs = listOf(
            CategoryRecommendation(
                sebiCategory = SebiCategory.FLEXI_CAP,
                suggestedAllocationPercent = 60,
                role = AllocationRole.CORE,
                rationale = "Core equity",
                cautionNotes = "Standard volatility",
                expectedReturnRange = "12-14%"
            ),
            CategoryRecommendation(
                sebiCategory = SebiCategory.SHORT_DURATION,
                suggestedAllocationPercent = 40,
                role = AllocationRole.STABILITY,
                rationale = "Debt anchor",
                cautionNotes = "Low returns",
                expectedReturnRange = "7%"
            )
        )

        val plan = IllustrativeSipEngine.buildPortfolioPlan(
            monthlyBudget = 20000.0,
            horizonYears = 10,
            recommendations = recs
        )

        assertEquals(20000.0, plan.totalMonthlyBudget, 0.01)
        assertEquals(10, plan.durationYears)
        assertEquals(2, plan.allocations.size)
        assertTrue("Projected corpus must be greater than total invested", plan.projectedCorpus > plan.totalInvested)

        val sipInput = plan.sipInputForCalculator
        assertEquals(20000.0, sipInput.periodicInvestment, 0.01)
        assertEquals(10, sipInput.durationYears)
        assertEquals(plan.estimatedBlendedCagr, sipInput.expectedReturnAnnual, 0.01)
    }

    @Test
    fun testMatchPrimaryCategory_shortHorizonUnder3Years_yieldsDebtAlternative() {
        val goal = FundFinderGoal(durationYears = 2, type = GoalType.SHORT_TERM)
        val profile = RiskAssessmentResult(
            toleranceScore = 95,
            capacityScore = 90,
            knowledgeScore = 80,
            compositeScore = 90,
            profile = RiskProfileCategory.AGGRESSIVE,
            hasToleranceCapacityMismatch = false,
            mismatchExplanation = null,
            rationale = "Test"
        )
        val investor = InvestorProfile(age = 28, monthlyBudgetInr = 15000.0)

        val guidance = CategoryMatchingEngine.matchPrimaryCategory(goal, profile, investor)
        assertEquals(PrimaryFundCategory.DEBT_HYBRID_ALTERNATIVE, guidance.primaryCategory)
        assertTrue(guidance.whySuited.contains("under 3 years", ignoreCase = true))
        assertFalse(guidance.primaryCategory == PrimaryFundCategory.SMALL_CAP)
    }

    @Test
    fun testMatchPrimaryCategory_highLiquidityNeed_yieldsDebtAlternative() {
        val goal = FundFinderGoal(durationYears = 10, liquidityNeed = LiquidityNeed.HIGH)
        val profile = RiskAssessmentResult(
            toleranceScore = 85,
            capacityScore = 80,
            knowledgeScore = 80,
            compositeScore = 82,
            profile = RiskProfileCategory.AGGRESSIVE,
            hasToleranceCapacityMismatch = false,
            mismatchExplanation = null,
            rationale = "Test"
        )
        val investor = InvestorProfile(age = 30, monthlyBudgetInr = 20000.0)

        val guidance = CategoryMatchingEngine.matchPrimaryCategory(goal, profile, investor)
        assertEquals(PrimaryFundCategory.DEBT_HYBRID_ALTERNATIVE, guidance.primaryCategory)
        assertTrue(guidance.suitabilityHeadline.contains("Liquidity", ignoreCase = true))
    }

    @Test
    fun testMatchPrimaryCategory_youngAgeWithUnstableIncome_neverRecommendsSmallCap() {
        val goal = FundFinderGoal(durationYears = 10, type = GoalType.WEALTH_CREATION)
        // Young investor (21) but with unstable income & low capacity
        val profile = RiskAssessmentResult(
            toleranceScore = 85,
            capacityScore = 35,
            knowledgeScore = 50,
            compositeScore = 40,
            profile = RiskProfileCategory.CONSERVATIVE,
            hasToleranceCapacityMismatch = true,
            mismatchExplanation = "Tolerance exceeds capacity",
            rationale = "Test"
        )
        val investor = InvestorProfile(
            age = 21,
            monthlyBudgetInr = 5000.0,
            incomeStability = IncomeStability.VARIABLE,
            experience = InvestmentExperience.BEGINNER
        )

        val guidance = CategoryMatchingEngine.matchPrimaryCategory(goal, profile, investor)
        // Must never recommend Small-Cap purely because age is young!
        assertNotEquals(PrimaryFundCategory.SMALL_CAP, guidance.primaryCategory)
        assertNotEquals(PrimaryFundCategory.MID_CAP, guidance.primaryCategory)
        assertTrue(guidance.hasToleranceCapacityMismatch)
        assertNotNull(guidance.mismatchWarning)
    }

    @Test
    fun testMatchPrimaryCategory_toleranceCapacityMismatch_highlightsConflictAndBoundsCategory() {
        val goal = FundFinderGoal(durationYears = 8)
        val profile = RiskAssessmentResult(
            toleranceScore = 90,
            capacityScore = 40,
            knowledgeScore = 70,
            compositeScore = 45,
            profile = RiskProfileCategory.CONSERVATIVE,
            hasToleranceCapacityMismatch = true,
            mismatchExplanation = "Capacity warning",
            rationale = "Test"
        )
        val investor = InvestorProfile(age = 35, monthlyBudgetInr = 10000.0)

        val guidance = CategoryMatchingEngine.matchPrimaryCategory(goal, profile, investor)
        assertTrue(guidance.hasToleranceCapacityMismatch)
        assertEquals("Capacity warning", guidance.mismatchWarning)
        assertTrue(guidance.primaryCategory == PrimaryFundCategory.DEBT_HYBRID_ALTERNATIVE || guidance.primaryCategory == PrimaryFundCategory.LARGE_CAP)
    }

    @Test
    fun testMatchPrimaryCategory_longHorizonHighCapacityTolerance_yieldsSmallCapWithWarnings() {
        val goal = FundFinderGoal(durationYears = 10, monthlyBudget = 25000.0)
        val profile = RiskAssessmentResult(
            toleranceScore = 85,
            capacityScore = 85,
            knowledgeScore = 80,
            compositeScore = 85,
            profile = RiskProfileCategory.AGGRESSIVE,
            hasToleranceCapacityMismatch = false,
            mismatchExplanation = null,
            rationale = "Test"
        )
        val investor = InvestorProfile(
            age = 32,
            monthlyBudgetInr = 25000.0,
            incomeStability = IncomeStability.STABLE,
            experience = InvestmentExperience.EXPERIENCED
        )

        val guidance = CategoryMatchingEngine.matchPrimaryCategory(goal, profile, investor)
        assertEquals(PrimaryFundCategory.SMALL_CAP, guidance.primaryCategory)
        assertTrue("Must have drawdown warnings for small cap", guidance.mainRisks.any { it.contains("drawdown", ignoreCase = true) || it.contains("35%", ignoreCase = true) })
        assertTrue("Must emphasize satellite cap in limitations", guidance.limitations.any { it.contains("10%", ignoreCase = true) || it.contains("15%", ignoreCase = true) })
    }

    @Test
    fun testMatchPrimaryCategory_moderateProfile5Years_yieldsLargeCap() {
        val goal = FundFinderGoal(durationYears = 5)
        val profile = RiskAssessmentResult(
            toleranceScore = 60,
            capacityScore = 60,
            knowledgeScore = 60,
            compositeScore = 60,
            profile = RiskProfileCategory.MODERATE,
            hasToleranceCapacityMismatch = false,
            mismatchExplanation = null,
            rationale = "Test"
        )
        val investor = InvestorProfile(age = 30, monthlyBudgetInr = 15000.0)

        val guidance = CategoryMatchingEngine.matchPrimaryCategory(goal, profile, investor)
        assertEquals(PrimaryFundCategory.LARGE_CAP, guidance.primaryCategory)
        assertEquals(12.0, guidance.expectedReturnAnnual, 0.01)
    }

    @Test
    fun testMatchPrimaryCategory_containsMandatorySebiDisclaimer() {
        val goal = FundFinderGoal()
        val profile = RiskScoringEngine.calculateScores(emptyMap())
        val investor = InvestorProfile()

        val guidance = CategoryMatchingEngine.matchPrimaryCategory(goal, profile, investor)
        assertTrue("Must include market risks disclaimer", guidance.regulatoryDisclaimer.contains("Mutual Fund investments are subject to market risks", ignoreCase = true))
        assertTrue("Must state past performance does not guarantee future results", guidance.regulatoryDisclaimer.contains("Past performance does not guarantee future results", ignoreCase = true))
    }
}
