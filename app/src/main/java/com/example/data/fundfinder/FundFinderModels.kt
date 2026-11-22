package com.example.data.fundfinder

/**
 * Domain models for WealthFlow Fund Finder & Mutual Fund Discovery.
 * Aligned with SEBI mutual fund categorization guidelines.
 */

enum class InvestmentExperience(val title: String, val description: String) {
    BEGINNER("Beginner", "New to mutual funds, learning the basics"),
    INTERMEDIATE("Intermediate", "Invested in mutual funds or SIPs for 1-3 years"),
    EXPERIENCED("Experienced", "Deep understanding of equity, debt cycles & asset allocation")
}

enum class IncomeStability(val title: String, val description: String) {
    STABLE("Stable Income", "Salaried / steady business with predictable monthly cashflows"),
    VARIABLE("Variable Income", "Freelance, commission, or seasonal business with fluctuating earnings"),
    NOT_EARNING("Currently Not Earning", "Student, homemaker, or between career transitions")
}

data class InvestorProfile(
    val age: Int = 30,
    val monthlyBudgetInr: Double = 15000.0,
    val experience: InvestmentExperience = InvestmentExperience.INTERMEDIATE,
    val incomeStability: IncomeStability = IncomeStability.STABLE
)

enum class GoalType(val displayName: String, val typicalHorizonYears: Int) {
    WEALTH_CREATION("Long-Term Wealth Creation", 10),
    RETIREMENT("Retirement Nest Egg", 20),
    HOUSE_PURCHASE("Buying a Home", 7),
    CHILD_EDUCATION("Children's Higher Education", 12),
    EMERGENCY_FUND("Emergency Liquidity Fund", 1),
    SHORT_TERM("Short-Term Vacation / Purchase", 2),
    OTHER("Custom Goal", 5)
}

enum class LiquidityNeed(val label: String, val description: String) {
    LOW("Low Liquidity Need", "I will not need to touch this money before my goal horizon"),
    HIGH("High Liquidity Need", "I might need emergency or early access to these funds within 1-2 years")
}

data class FundFinderGoal(
    val type: GoalType = GoalType.WEALTH_CREATION,
    val name: String = "Wealth Creation",
    val targetAmountToday: Double = 2500000.0, // ₹25 Lakhs
    val durationYears: Int = 10,
    val existingSavingsAllocated: Double = 50000.0,
    val monthlyBudget: Double = 15000.0,
    val assumedInflationRate: Double = 6.0,
    val liquidityNeed: LiquidityNeed = LiquidityNeed.LOW
) {
    val futureInflatedTarget: Double
        get() = targetAmountToday * Math.pow(1.0 + (assumedInflationRate / 100.0), durationYears.toDouble())
}

enum class RiskDimension {
    TOLERANCE, // Willingness to accept volatility & drawdown
    CAPACITY,  // Financial ability to absorb losses without distress
    KNOWLEDGE  // Familiarity with instruments & compounding
}

data class RiskQuestion(
    val id: String,
    val dimension: RiskDimension,
    val prompt: String,
    val subtitle: String,
    val options: List<RiskOption>
)

data class RiskOption(
    val label: String,
    val description: String,
    val scoreValue: Int // 1 to 5
)

enum class RiskProfileCategory(
    val title: String,
    val headline: String,
    val recommendedHorizon: String,
    val assetMixGuideline: String
) {
    CONSERVATIVE(
        "Conservative",
        "Capital Preservation & Low Volatility",
        "1 to 3 Years",
        "70-90% Debt / Liquid, 10-30% Hybrid / Arbitrage"
    ),
    MODERATELY_CONSERVATIVE(
        "Moderately Conservative",
        "Steady Income with Mild Capital Growth",
        "3 to 5 Years",
        "50-70% Debt, 30-50% Equity / Hybrid"
    ),
    MODERATE(
        "Moderate",
        "Balanced Growth with Moderate Volatility",
        "5 to 7 Years",
        "40-60% Equity, 40-60% Debt & Hybrid"
    ),
    MODERATELY_AGGRESSIVE(
        "Moderately Aggressive",
        "Long-Term Capital Appreciation",
        "7+ Years",
        "65-80% Equity (Core Diversified), 20-35% Debt / Multi-Asset"
    ),
    AGGRESSIVE(
        "Aggressive",
        "Maximum Long-Term Wealth Accumulation",
        "8 to 10+ Years",
        "80-90% Equity (Core + Satellite), 10-20% Dynamic / Liquid"
    )
}

data class RiskAssessmentResult(
    val toleranceScore: Int, // 0 to 100
    val capacityScore: Int,  // 0 to 100
    val knowledgeScore: Int, // 0 to 100
    val compositeScore: Int, // Clamped deterministic score
    val profile: RiskProfileCategory,
    val hasToleranceCapacityMismatch: Boolean,
    val mismatchExplanation: String?,
    val rationale: String
)

enum class AssetClassCategory(val displayName: String) {
    EQUITY("Equity"),
    DEBT("Debt & Fixed Income"),
    HYBRID("Hybrid & Dynamic"),
    PASSIVE("Passive / Index")
}

enum class AllocationRole(val label: String) {
    CORE("Core (Foundational)"),
    SATELLITE("Satellite (Alpha Tilt)"),
    STABILITY("Capital Stability / Liquidity")
}

enum class SebiCategory(
    val assetClass: AssetClassCategory,
    val codeName: String,
    val displayName: String,
    val minHorizonYears: Int,
    val description: String
) {
    // Equity
    LARGE_CAP(AssetClassCategory.EQUITY, "large_cap", "Large Cap Fund", 5, "Invests in top 100 companies by market capitalization. High stability among equities."),
    FLEXI_CAP(AssetClassCategory.EQUITY, "flexi_cap", "Flexi Cap Fund", 7, "Dynamic allocation across large, mid, and small cap stocks without rigid mandate constraints."),
    MULTI_CAP(AssetClassCategory.EQUITY, "multi_cap", "Multi Cap Fund", 7, "Mandatory minimum 25% each in Large, Mid, and Small Cap stocks."),
    LARGE_AND_MID(AssetClassCategory.EQUITY, "large_mid_cap", "Large & Mid Cap Fund", 7, "Minimum 35% in large cap and 35% in mid cap stocks."),
    MID_CAP(AssetClassCategory.EQUITY, "mid_cap", "Mid Cap Fund", 7, "Invests in 101st to 250th companies. Higher growth potential with higher drawdowns."),
    SMALL_CAP(AssetClassCategory.EQUITY, "small_cap", "Small Cap Fund", 8, "Invests in companies ranked 251st onwards. High return potential, volatile, requires patient capital."),
    ELSS(AssetClassCategory.EQUITY, "elss", "ELSS (Tax Saving)", 5, "Equity fund with mandatory 3-year lock-in period and Section 80C tax deduction."),

    // Debt
    OVERNIGHT(AssetClassCategory.DEBT, "overnight", "Overnight Fund", 0, "Invests in 1-day securities. Negligible interest rate and credit risk."),
    LIQUID(AssetClassCategory.DEBT, "liquid", "Liquid Fund", 0, "Invests in debt securities with up to 91 days maturity. Ideal for emergency cash."),
    MONEY_MARKET(AssetClassCategory.DEBT, "money_market", "Money Market Fund", 1, "Invests in commercial papers and treasury bills up to 1-year maturity."),
    SHORT_DURATION(AssetClassCategory.DEBT, "short_duration", "Short Duration Fund", 2, "Invests in debt instruments with Macaulay duration between 1 to 3 years."),

    // Hybrid
    CONSERVATIVE_HYBRID(AssetClassCategory.HYBRID, "conservative_hybrid", "Conservative Hybrid Fund", 3, "75-90% in debt instruments, 10-25% in equity for mild capital appreciation."),
    BALANCED_HYBRID(AssetClassCategory.HYBRID, "balanced_hybrid", "Balanced Hybrid Fund", 4, "40-60% in equity and 40-60% in debt. Balanced risk-reward profile."),
    AGGRESSIVE_HYBRID(AssetClassCategory.HYBRID, "aggressive_hybrid", "Aggressive Hybrid Fund", 5, "65-80% in equity and 20-35% in debt. Automatic portfolio rebalancing."),
    BALANCED_ADVANTAGE(AssetClassCategory.HYBRID, "balanced_advantage", "Dynamic Asset Allocation (BAF)", 4, "Actively manages equity allocation from 30% to 80% based on market valuations."),
    MULTI_ASSET(AssetClassCategory.HYBRID, "multi_asset", "Multi Asset Allocation", 5, "Invests in at least 3 asset classes (equity, debt, gold/commodities) with min 10% in each."),

    // Passive
    INDEX_FUND(AssetClassCategory.PASSIVE, "index_fund", "Broad Market Index Fund", 5, "Low-cost passive tracking of benchmark indices like Nifty 50 or Sensex.")
}

data class CategoryRecommendation(
    val sebiCategory: SebiCategory,
    val suggestedAllocationPercent: Int, // e.g. 40%
    val role: AllocationRole,
    val rationale: String,
    val cautionNotes: String,
    val expectedReturnRange: String
)

enum class RiskometerLevel(val title: String) {
    LOW("Low"),
    MODERATELY_LOW("Moderately Low"),
    MODERATE("Moderate"),
    MODERATELY_HIGH("Moderately High"),
    HIGH("High"),
    VERY_HIGH("Very High")
}

enum class FundPlanType(val title: String) {
    DIRECT("Direct Plan"),
    REGULAR("Regular Plan")
}

enum class FundOption(val title: String) {
    GROWTH("Growth Option"),
    IDCW("IDCW (Payout / Reinvestment)")
}

data class MutualFundScheme(
    val amfiCode: String,
    val amcName: String,
    val schemeName: String,
    val category: SebiCategory,
    val planType: FundPlanType = FundPlanType.DIRECT,
    val option: FundOption = FundOption.GROWTH,
    val nav: Double,
    val expenseRatioPercent: Double,
    val riskometer: RiskometerLevel,
    val returns1Y: Double,
    val returns3Y: Double,
    val returns5Y: Double,
    val benchmarkIndex: String,
    val minSipAmount: Double = 500.0,
    val exitLoadSummary: String,
    val aumCrores: Double,
    val updatedDate: String = "October 2024 (Verified AMFI Snapshot)"
)

data class IllustrativeSipAllocation(
    val category: SebiCategory,
    val percentage: Int,
    val monthlyAmount: Double,
    val recommendedScheme: MutualFundScheme?,
    val role: AllocationRole
)

const val MANDATORY_SEBI_RISK_WARNING =
    "Mutual Fund investments are subject to market risks, read all scheme related documents carefully. Past performance does not guarantee future results. Category recommendations are for educational and planning purposes only and should not be construed as investment advice or a recommendation to buy specific securities."

enum class PrimaryFundCategory(
    val displayName: String,
    val headline: String,
    val idealHorizonYears: Int,
    val riskLevelText: String,
    val volatilityDescription: String,
    val expectedReturnRange: String,
    val typicalAssetMix: String
) {
    LARGE_CAP(
        displayName = "Large-Cap Equity Funds",
        headline = "India's Top 100 Established Corporate Giants",
        idealHorizonYears = 5,
        riskLevelText = "Moderate-High Risk (Equity)",
        volatilityDescription = "Lower volatility than mid/small caps with strong market liquidity and resilience through economic cycles.",
        expectedReturnRange = "11.0% - 13.0% p.a.",
        typicalAssetMix = "Minimum 80% in top 100 companies by market capitalization"
    ),
    MID_CAP(
        displayName = "Mid-Cap Equity Funds",
        headline = "Emerging Market Leaders & Growth Companies (101st-250th)",
        idealHorizonYears = 7,
        riskLevelText = "High Risk",
        volatilityDescription = "Higher multi-year growth potential, but subject to 20-30% cyclical drawdowns during broader market declines.",
        expectedReturnRange = "13.0% - 15.5% p.a.",
        typicalAssetMix = "Minimum 65% in companies ranked 101 to 250"
    ),
    SMALL_CAP(
        displayName = "Small-Cap Equity Funds",
        headline = "High-Growth Niche Enterprises (251st Rank Onwards)",
        idealHorizonYears = 8,
        riskLevelText = "Very High Risk",
        volatilityDescription = "Highest long-term return potential, but extreme volatility (can drop 35-45% in bear phases and stay flat for 2-3 years).",
        expectedReturnRange = "14.5% - 18.0% p.a. (Elevated Risk)",
        typicalAssetMix = "Minimum 65% in companies ranked 251 and beyond"
    ),
    DEBT_HYBRID_ALTERNATIVE(
        displayName = "Debt & Conservative Hybrid Alternatives",
        headline = "Capital Preservation, Steady Accrual & Volatility Cushion",
        idealHorizonYears = 2,
        riskLevelText = "Low to Moderately Low Risk",
        volatilityDescription = "Smooth and predictable trajectories with negligible risk of capital loss over recommended duration.",
        expectedReturnRange = "6.5% - 9.0% p.a.",
        typicalAssetMix = "70-100% in fixed income securities, sovereign bonds & money market instruments"
    )
}

data class PrimaryCategoryGuidance(
    val primaryCategory: PrimaryFundCategory,
    val secondaryCategory: PrimaryFundCategory? = null,
    val suitabilityHeadline: String,
    val whySuited: String,
    val mainRisks: List<String>,
    val limitations: List<String>,
    val hasToleranceCapacityMismatch: Boolean,
    val mismatchWarning: String? = null,
    val suggestedSipAmount: Double,
    val horizonYears: Int,
    val expectedReturnAnnual: Double,
    val regulatoryDisclaimer: String = MANDATORY_SEBI_RISK_WARNING
)
