package com.example.data.fundfinder

/**
 * Rule-based Category Matching Engine aligned with SEBI mutual fund regulations.
 * Enforces strict horizon constraints, core vs. satellite separation, and capacity checks.
 */
object CategoryMatchingEngine {

    /**
     * Deterministic 4-category suitability engine aligned with SEBI guidelines.
     * Evaluates horizon, capacity, tolerance, and liquidity constraints.
     */
    fun matchPrimaryCategory(
        goal: FundFinderGoal,
        profileResult: RiskAssessmentResult,
        investorProfile: InvestorProfile
    ): PrimaryCategoryGuidance {
        val horizon = goal.durationYears
        val liquidityNeed = goal.liquidityNeed
        val riskProfile = profileResult.profile
        val capacity = profileResult.capacityScore
        val tolerance = profileResult.toleranceScore
        val hasMismatch = profileResult.hasToleranceCapacityMismatch
        val suggestedSip = goal.monthlyBudget

        // Rule 1: Very short horizon (< 3 years), high liquidity need, or emergency fund
        // Capital preservation is paramount. Equity is strictly avoided.
        if (horizon < 3 || liquidityNeed == LiquidityNeed.HIGH || goal.type == GoalType.EMERGENCY_FUND) {
            val headline = if (goal.type == GoalType.EMERGENCY_FUND) {
                "Emergency Liquidity & Principal Safety"
            } else if (liquidityNeed == LiquidityNeed.HIGH) {
                "High Liquidity Buffer (Immediate Access)"
            } else {
                "Short-Term Capital Preservation (Under 3 Years)"
            }

            return PrimaryCategoryGuidance(
                primaryCategory = PrimaryFundCategory.DEBT_HYBRID_ALTERNATIVE,
                secondaryCategory = null,
                suitabilityHeadline = headline,
                whySuited = "For time horizons under 3 years or high liquidity requirements, equity mutual funds carry significant risk of capital loss during market corrections. High-grade debt, liquid, or conservative hybrid funds protect your principal while providing higher yields than standard bank savings accounts.",
                mainRisks = listOf(
                    "Returns generally mirror prevailing RBI repo rates and short-term debt yields (6.5% - 7.5% p.a.).",
                    "Lower tax efficiency for pure debt funds under Indian marginal income tax slab rules.",
                    "Cannot deliver equity-like long-term purchasing power expansion."
                ),
                limitations = listOf(
                    "Not suitable for long-term wealth accumulation (>5 years).",
                    "Do not expect high capital appreciation."
                ),
                hasToleranceCapacityMismatch = hasMismatch,
                mismatchWarning = if (hasMismatch) profileResult.mismatchExplanation else null,
                suggestedSipAmount = suggestedSip,
                horizonYears = horizon,
                expectedReturnAnnual = 7.0
            )
        }

        // Rule 2: Short-to-Medium Horizon (3 to 4 years)
        // Even if the investor is young or has high tolerance, 3-4 years is too brief for pure equities.
        if (horizon in 3..4) {
            return PrimaryCategoryGuidance(
                primaryCategory = PrimaryFundCategory.DEBT_HYBRID_ALTERNATIVE,
                secondaryCategory = PrimaryFundCategory.LARGE_CAP,
                suitabilityHeadline = "Capital Stability with Mild Growth Cushion (3-4 Years)",
                whySuited = "A 3 to 4-year horizon is insufficient to guarantee positive returns from volatile equities. A conservative hybrid or dynamic asset allocation strategy provides capital safety with a mild equity kicker to outpace inflation.",
                mainRisks = listOf(
                    "Moderate returns (7.5% - 9.5% p.a.) compared to pure equities in runaway bull cycles.",
                    "Minor temporary NAV dips (3-5%) during equity corrections if hybrid funds are used."
                ),
                limitations = listOf(
                    "Pure mid-cap and small-cap funds are strictly unsuitable for horizons under 5-7 years.",
                    "Stay invested for at least 3 full years to capture coupon accrual and tax advantages."
                ),
                hasToleranceCapacityMismatch = hasMismatch,
                mismatchWarning = if (hasMismatch) profileResult.mismatchExplanation else null,
                suggestedSipAmount = suggestedSip,
                horizonYears = horizon,
                expectedReturnAnnual = 8.5
            )
        }

        // Rule 3: Tolerance vs. Capacity Conflict (Weakest-Link Principle)
        // If psychological tolerance is high but financial capacity is low (e.g. debt, unstable income, no emergency fund)
        // We MUST bound the recommendation by financial capacity.
        if (hasMismatch) {
            val cappedCategory = if (capacity < 45) {
                PrimaryFundCategory.DEBT_HYBRID_ALTERNATIVE
            } else {
                PrimaryFundCategory.LARGE_CAP
            }

            return PrimaryCategoryGuidance(
                primaryCategory = cappedCategory,
                secondaryCategory = if (cappedCategory == PrimaryFundCategory.LARGE_CAP) PrimaryFundCategory.DEBT_HYBRID_ALTERNATIVE else null,
                suitabilityHeadline = "Capital Protection Focus (Capacity-Constrained)",
                whySuited = "Although your psychological tolerance indicates comfort with market risk, your financial capacity indicates limited emergency cushion, high-interest debt, or income variability. To prevent distress-selling during market crashes, your recommendation is responsibly anchored in stable, highly liquid large-cap or hybrid funds.",
                mainRisks = listOf(
                    "Drawdowns in equity markets can create emotional pressure if living expenses are not protected.",
                    "Build an emergency fund of 3-6 months living expenses before increasing equity exposure."
                ),
                limitations = listOf(
                    "Aggressive mid-cap and small-cap investments are deferred until financial capacity improves.",
                    "Clear high-interest debt before deploying surplus into volatile assets."
                ),
                hasToleranceCapacityMismatch = true,
                mismatchWarning = profileResult.mismatchExplanation,
                suggestedSipAmount = suggestedSip,
                horizonYears = horizon,
                expectedReturnAnnual = if (cappedCategory == PrimaryFundCategory.LARGE_CAP) 11.5 else 8.0
            )
        }

        // Rule 4: Long Horizon (8+ years) + Exceptional Financial Capacity & High Tolerance -> Small-Cap
        // Strict SEBI criteria: Horizon >= 8y, Capacity >= 75, Tolerance >= 75, Stable income
        // AGE ALONE NEVER FORCES THIS.
        val allowSmallCap = horizon >= 8 &&
                capacity >= 75 &&
                tolerance >= 75 &&
                investorProfile.incomeStability == IncomeStability.STABLE &&
                investorProfile.experience != InvestmentExperience.BEGINNER

        if (allowSmallCap && riskProfile == RiskProfileCategory.AGGRESSIVE) {
            return PrimaryCategoryGuidance(
                primaryCategory = PrimaryFundCategory.SMALL_CAP,
                secondaryCategory = PrimaryFundCategory.LARGE_CAP,
                suitabilityHeadline = "High-Growth Niche Champions (Strictly Satellite Allocation)",
                whySuited = "With a long commitment horizon of ${horizon} years, steady surplus income, and high loss-absorption capacity, you have the financial foundation to endure cyclical downturns in high-growth small enterprises.",
                mainRisks = listOf(
                    "Severe Drawdowns: Small-Cap funds can decline 35% to 45% during market downturns and bear markets.",
                    "Prolonged Underperformance: Small caps can stay flat or underperform large caps for 2 to 3 consecutive years.",
                    "Liquidity Squeeze: Fund managers may face redemption bottlenecks during market panics."
                ),
                limitations = listOf(
                    "Small-cap funds must NEVER exceed 10% to 15% of your total equity portfolio; anchor the rest in large-caps.",
                    "Do not stop SIPs during bear phases—stopping destroys the rupee-cost-averaging benefit.",
                    "Not suitable for goals where the money is required on a strict calendar deadline."
                ),
                hasToleranceCapacityMismatch = false,
                suggestedSipAmount = suggestedSip,
                horizonYears = horizon,
                expectedReturnAnnual = 15.0
            )
        }

        // Rule 5: Medium-to-Long Horizon (7+ years) + Strong Capacity & High Tolerance -> Mid-Cap
        val allowMidCap = horizon >= 7 && capacity >= 65 && tolerance >= 60
        if (allowMidCap && (riskProfile == RiskProfileCategory.MODERATELY_AGGRESSIVE || riskProfile == RiskProfileCategory.AGGRESSIVE)) {
            return PrimaryCategoryGuidance(
                primaryCategory = PrimaryFundCategory.MID_CAP,
                secondaryCategory = PrimaryFundCategory.LARGE_CAP,
                suitabilityHeadline = "Emerging Market Leaders with High Operating Leverage",
                whySuited = "Mid-cap companies (ranked 101st to 250th) offer high growth potential as they expand into national corporate champions. Your ${horizon}-year horizon gives adequate time to compound through multiple market cycles.",
                mainRisks = listOf(
                    "Elevated Volatility: Mid-cap funds experience 20% to 30% corrections during broader market declines.",
                    "Sensitive to interest rates, credit conditions, and domestic economic demand."
                ),
                limitations = listOf(
                    "Should be combined with Large-Cap or Index funds (at least 50% core) for downside balance.",
                    "Requires uninterrupted SIP discipline for at least 5 to 7 years."
                ),
                hasToleranceCapacityMismatch = false,
                suggestedSipAmount = suggestedSip,
                horizonYears = horizon,
                expectedReturnAnnual = 13.5
            )
        }

        // Rule 6: Foundational Default (5+ years horizon, Moderate or Moderately Aggressive) -> Large-Cap
        // Also applies if investor is a beginner or conservative equity investor.
        return PrimaryCategoryGuidance(
            primaryCategory = PrimaryFundCategory.LARGE_CAP,
            secondaryCategory = PrimaryFundCategory.DEBT_HYBRID_ALTERNATIVE,
            suitabilityHeadline = "Foundational Blue-Chip Compounding (India's Top 100)",
            whySuited = "Large-cap funds invest in India's top 100 corporate giants (Nifty 50 and BSE Sensex leaders). They provide market-leading liquidity, experienced management, and proven resilience through economic downturns.",
            mainRisks = listOf(
                "Market Corrections: 15% to 20% cyclical declines during global or domestic economic slowdowns.",
                "Short-term negative returns are normal during bear phases."
            ),
            limitations = listOf(
                "Moderate multi-bagger potential compared to smaller businesses; prioritizes stability over speculative gains.",
                "Ideal as the core anchor of any long-term financial plan."
            ),
            hasToleranceCapacityMismatch = false,
            suggestedSipAmount = suggestedSip,
            horizonYears = horizon,
            expectedReturnAnnual = 12.0
        )
    }

    fun matchCategories(
        goal: FundFinderGoal,
        profileResult: RiskAssessmentResult,
        investorProfile: InvestorProfile
    ): List<CategoryRecommendation> {
        val horizon = goal.durationYears
        val riskProfile = profileResult.profile
        val capacity = profileResult.capacityScore

        // Rule 1: Very short horizon (< 3 years) -> Pure Capital Preservation & Liquidity
        // No equity funds permitted regardless of stated risk tolerance.
        if (horizon < 3 || goal.type == GoalType.EMERGENCY_FUND) {
            return listOf(
                CategoryRecommendation(
                    sebiCategory = SebiCategory.LIQUID,
                    suggestedAllocationPercent = 60,
                    role = AllocationRole.STABILITY,
                    rationale = "Ideal for goals under 3 years or emergency funds. Invests in high-grade debt instruments with up to 91 days maturity, minimizing interest rate risk.",
                    cautionNotes = "Returns closely track short-term RBI repo rates; do not expect equity-like capital appreciation.",
                    expectedReturnRange = "6.2% - 6.8% p.a."
                ),
                CategoryRecommendation(
                    sebiCategory = SebiCategory.SHORT_DURATION,
                    suggestedAllocationPercent = 40,
                    role = AllocationRole.STABILITY,
                    rationale = "High-quality short duration papers (1-3 years maturity) offer modest yield pickup over liquid funds without credit risk.",
                    cautionNotes = "Subject to minor NAV fluctuations when market interest rates shift.",
                    expectedReturnRange = "6.8% - 7.5% p.a."
                )
            )
        }

        // Rule 2: Short-to-Medium Horizon (3 to 5 years)
        if (horizon in 3..4) {
            return when (riskProfile) {
                RiskProfileCategory.CONSERVATIVE -> listOf(
                    CategoryRecommendation(
                        sebiCategory = SebiCategory.SHORT_DURATION,
                        suggestedAllocationPercent = 70,
                        role = AllocationRole.STABILITY,
                        rationale = "Anchors capital safely with predictable coupon income over 3-4 years.",
                        cautionNotes = "Fixed income returns are taxed at marginal income tax slab rates.",
                        expectedReturnRange = "7.0% - 7.5% p.a."
                    ),
                    CategoryRecommendation(
                        sebiCategory = SebiCategory.CONSERVATIVE_HYBRID,
                        suggestedAllocationPercent = 30,
                        role = AllocationRole.CORE,
                        rationale = "75-90% debt provides stability while 10-25% equity kicker beats inflation.",
                        cautionNotes = "Minor equity drawdown possible during sharp corrections.",
                        expectedReturnRange = "8.0% - 9.5% p.a."
                    )
                )
                RiskProfileCategory.MODERATELY_CONSERVATIVE, RiskProfileCategory.MODERATE -> listOf(
                    CategoryRecommendation(
                        sebiCategory = SebiCategory.BALANCED_ADVANTAGE,
                        suggestedAllocationPercent = 60,
                        role = AllocationRole.CORE,
                        rationale = "Dynamic asset allocation automatically increases equity during market corrections and books profits when valuations stretch, providing downside protection.",
                        cautionNotes = "Internal derivative hedging models vary across fund houses.",
                        expectedReturnRange = "9.5% - 11.0% p.a."
                    ),
                    CategoryRecommendation(
                        sebiCategory = SebiCategory.SHORT_DURATION,
                        suggestedAllocationPercent = 40,
                        role = AllocationRole.STABILITY,
                        rationale = "Provides predictable fixed income cashflow and capital ballast.",
                        cautionNotes = "Maintain at least 3 years holding to minimize rate volatility.",
                        expectedReturnRange = "7.0% - 7.5% p.a."
                    )
                )
                RiskProfileCategory.MODERATELY_AGGRESSIVE, RiskProfileCategory.AGGRESSIVE -> listOf(
                    CategoryRecommendation(
                        sebiCategory = SebiCategory.BALANCED_ADVANTAGE,
                        suggestedAllocationPercent = 60,
                        role = AllocationRole.CORE,
                        rationale = "Provides dynamic equity participation while strictly cushioning against short 3-4 year market cycle downturns.",
                        cautionNotes = "Pure equity is avoided because 3-4 years is too short to guarantee positive equity returns.",
                        expectedReturnRange = "10.0% - 11.5% p.a."
                    ),
                    CategoryRecommendation(
                        sebiCategory = SebiCategory.MULTI_ASSET,
                        suggestedAllocationPercent = 40,
                        role = AllocationRole.CORE,
                        rationale = "Combines equity, debt, and gold/commodities for uncorrelated steady compounding.",
                        cautionNotes = "Gold allocation may introduce commodity price cyclicality.",
                        expectedReturnRange = "9.5% - 11.0% p.a."
                    )
                )
            }
        }

        // Rule 3: Medium-to-Long Horizon (5 to 7 years)
        if (horizon in 5..6) {
            return when (riskProfile) {
                RiskProfileCategory.CONSERVATIVE -> listOf(
                    CategoryRecommendation(
                        sebiCategory = SebiCategory.CONSERVATIVE_HYBRID,
                        suggestedAllocationPercent = 50,
                        role = AllocationRole.CORE,
                        rationale = "High debt cushion with mild equity exposure to preserve purchasing power.",
                        cautionNotes = "Modest upside relative to broader stock market rallies.",
                        expectedReturnRange = "8.0% - 9.5% p.a."
                    ),
                    CategoryRecommendation(
                        sebiCategory = SebiCategory.BALANCED_ADVANTAGE,
                        suggestedAllocationPercent = 50,
                        role = AllocationRole.CORE,
                        rationale = "Protects capital using counter-cyclical equity management.",
                        cautionNotes = "Requires minimum 3-5 years discipline.",
                        expectedReturnRange = "9.5% - 11.0% p.a."
                    )
                )
                RiskProfileCategory.MODERATELY_CONSERVATIVE -> listOf(
                    CategoryRecommendation(
                        sebiCategory = SebiCategory.BALANCED_ADVANTAGE,
                        suggestedAllocationPercent = 50,
                        role = AllocationRole.CORE,
                        rationale = "Dynamic equity hedge limits drawdowns during bear phases.",
                        cautionNotes = "May lag pure equities in runaway bull markets.",
                        expectedReturnRange = "9.5% - 11.5% p.a."
                    ),
                    CategoryRecommendation(
                        sebiCategory = SebiCategory.INDEX_FUND,
                        suggestedAllocationPercent = 30,
                        role = AllocationRole.CORE,
                        rationale = "Low-cost foundational equity holding in India's top 50 blue chips.",
                        cautionNotes = "Zero downside protection during bear cycles.",
                        expectedReturnRange = "11.0% - 12.5% p.a."
                    ),
                    CategoryRecommendation(
                        sebiCategory = SebiCategory.SHORT_DURATION,
                        suggestedAllocationPercent = 20,
                        role = AllocationRole.STABILITY,
                        rationale = "Maintains liquidity for emergency or tactical rebalancing.",
                        cautionNotes = "Yields mirror prevailing debt rates.",
                        expectedReturnRange = "6.8% - 7.5% p.a."
                    )
                )
                RiskProfileCategory.MODERATE -> listOf(
                    CategoryRecommendation(
                        sebiCategory = SebiCategory.FLEXI_CAP,
                        suggestedAllocationPercent = 40,
                        role = AllocationRole.CORE,
                        rationale = "Fund manager dynamically allocates across large, mid, and small cap market opportunities without mandate restrictions.",
                        cautionNotes = "Volatile in the first 2-3 years; requires steady SIP discipline.",
                        expectedReturnRange = "11.5% - 13.5% p.a."
                    ),
                    CategoryRecommendation(
                        sebiCategory = SebiCategory.AGGRESSIVE_HYBRID,
                        suggestedAllocationPercent = 40,
                        role = AllocationRole.CORE,
                        rationale = "65-80% equity with 20-35% debt dampener. Auto-rebalancing cushions corrections.",
                        cautionNotes = "Taxed as equity fund under Indian IT laws.",
                        expectedReturnRange = "10.5% - 12.0% p.a."
                    ),
                    CategoryRecommendation(
                        sebiCategory = SebiCategory.SHORT_DURATION,
                        suggestedAllocationPercent = 20,
                        role = AllocationRole.STABILITY,
                        rationale = "Provides portfolio stability and rebalancing ammunition.",
                        cautionNotes = "Fixed income yields.",
                        expectedReturnRange = "6.8% - 7.5% p.a."
                    )
                )
                RiskProfileCategory.MODERATELY_AGGRESSIVE, RiskProfileCategory.AGGRESSIVE -> listOf(
                    CategoryRecommendation(
                        sebiCategory = SebiCategory.FLEXI_CAP,
                        suggestedAllocationPercent = 50,
                        role = AllocationRole.CORE,
                        rationale = "Core equity engine for multi-cap Indian economic growth.",
                        cautionNotes = "Requires holding through market downturns without panic.",
                        expectedReturnRange = "12.0% - 14.0% p.a."
                    ),
                    CategoryRecommendation(
                        sebiCategory = SebiCategory.LARGE_AND_MID,
                        suggestedAllocationPercent = 30,
                        role = AllocationRole.CORE,
                        rationale = "Structured 35% large cap stability combined with 35% mid cap growth impulse.",
                        cautionNotes = "Subject to 15-20% corrections during broader market declines.",
                        expectedReturnRange = "12.5% - 14.5% p.a."
                    ),
                    CategoryRecommendation(
                        sebiCategory = SebiCategory.BALANCED_ADVANTAGE,
                        suggestedAllocationPercent = 20,
                        role = AllocationRole.STABILITY,
                        rationale = "Provides dynamic defensive cushion against late-cycle corrections.",
                        cautionNotes = "Lower returns than pure equity during bull spikes.",
                        expectedReturnRange = "10.0% - 11.5% p.a."
                    )
                )
            }
        }

        // Rule 4: Long-Term Horizon (7+ to 20+ years) -> Full Wealth Creation Cycles
        return when (riskProfile) {
            RiskProfileCategory.CONSERVATIVE -> listOf(
                CategoryRecommendation(
                    sebiCategory = SebiCategory.BALANCED_ADVANTAGE,
                    suggestedAllocationPercent = 50,
                    role = AllocationRole.CORE,
                    rationale = "Dynamic equity allocation protects capital while capturing market upside over 7+ years.",
                    cautionNotes = "Returns may trail pure equities during multi-year bull runs.",
                    expectedReturnRange = "10.0% - 11.5% p.a."
                ),
                CategoryRecommendation(
                    sebiCategory = SebiCategory.INDEX_FUND,
                    suggestedAllocationPercent = 30,
                    role = AllocationRole.CORE,
                    rationale = "Low-cost exposure to top 50 corporate champions.",
                    cautionNotes = "Expect periodic market volatility.",
                    expectedReturnRange = "11.0% - 12.5% p.a."
                ),
                CategoryRecommendation(
                    sebiCategory = SebiCategory.SHORT_DURATION,
                    suggestedAllocationPercent = 20,
                    role = AllocationRole.STABILITY,
                    rationale = "Stable fixed income anchor to hedge against equity drawdowns.",
                    cautionNotes = "Fixed income yields.",
                    expectedReturnRange = "6.8% - 7.5% p.a."
                )
            )
            RiskProfileCategory.MODERATELY_CONSERVATIVE -> listOf(
                CategoryRecommendation(
                    sebiCategory = SebiCategory.FLEXI_CAP,
                    suggestedAllocationPercent = 40,
                    role = AllocationRole.CORE,
                    rationale = "Diversified foundational equity engine across Indian market leaders.",
                    cautionNotes = "Requires patience through multi-year economic cycles.",
                    expectedReturnRange = "12.0% - 13.5% p.a."
                ),
                CategoryRecommendation(
                    sebiCategory = SebiCategory.BALANCED_ADVANTAGE,
                    suggestedAllocationPercent = 40,
                    role = AllocationRole.CORE,
                    rationale = "Dynamic asset allocation limits drawdowns and provides emotional peace of mind.",
                    cautionNotes = "Hedged equity returns.",
                    expectedReturnRange = "10.0% - 11.5% p.a."
                ),
                CategoryRecommendation(
                    sebiCategory = SebiCategory.SHORT_DURATION,
                    suggestedAllocationPercent = 20,
                    role = AllocationRole.STABILITY,
                    rationale = "Preserves liquidity and stable portfolio ballast.",
                    cautionNotes = "Fixed income yields.",
                    expectedReturnRange = "6.8% - 7.5% p.a."
                )
            )
            RiskProfileCategory.MODERATE -> listOf(
                CategoryRecommendation(
                    sebiCategory = SebiCategory.INDEX_FUND,
                    suggestedAllocationPercent = 40,
                    role = AllocationRole.CORE,
                    rationale = "Passive low-cost index tracking top blue chips (Nifty 50 / Sensex). Foundation of long-term wealth.",
                    cautionNotes = "Tracks index exactly; no active outperformance or downside avoidance.",
                    expectedReturnRange = "11.5% - 13.0% p.a."
                ),
                CategoryRecommendation(
                    sebiCategory = SebiCategory.FLEXI_CAP,
                    suggestedAllocationPercent = 40,
                    role = AllocationRole.CORE,
                    rationale = "Active unconstrained asset management for alpha generation across capitalizations.",
                    cautionNotes = "Performance depends on fund manager's stock-picking ability.",
                    expectedReturnRange = "12.0% - 14.0% p.a."
                ),
                CategoryRecommendation(
                    sebiCategory = SebiCategory.MULTI_ASSET,
                    suggestedAllocationPercent = 20,
                    role = AllocationRole.STABILITY,
                    rationale = "Combines debt, equity, and gold for multi-dimensional volatility dampening.",
                    cautionNotes = "Asset mix is decided by AMC framework.",
                    expectedReturnRange = "9.5% - 11.5% p.a."
                )
            )
            RiskProfileCategory.MODERATELY_AGGRESSIVE -> {
                val allowMidCapSatellite = capacity >= 65 && horizon >= 7
                if (allowMidCapSatellite) {
                    listOf(
                        CategoryRecommendation(
                            sebiCategory = SebiCategory.FLEXI_CAP,
                            suggestedAllocationPercent = 50,
                            role = AllocationRole.CORE,
                            rationale = "Core anchor (50%) providing broad multi-cap growth with large-cap stability.",
                            cautionNotes = "Subject to periodic market volatility; continue SIPs regardless of news.",
                            expectedReturnRange = "12.0% - 14.0% p.a."
                        ),
                        CategoryRecommendation(
                            sebiCategory = SebiCategory.LARGE_AND_MID,
                            suggestedAllocationPercent = 30,
                            role = AllocationRole.CORE,
                            rationale = "Combines top 100 blue chips with emerging mid-market leaders for higher long-term compounding.",
                            cautionNotes = "Higher volatility than pure large cap funds.",
                            expectedReturnRange = "12.5% - 14.5% p.a."
                        ),
                        CategoryRecommendation(
                            sebiCategory = SebiCategory.MID_CAP,
                            suggestedAllocationPercent = 20,
                            role = AllocationRole.SATELLITE,
                            rationale = "Satellite growth tilt (20%) in high-growth companies ranked 101 to 250 with high operating leverage.",
                            cautionNotes = "Can experience severe 20-30% drawdowns in bear markets. Strictly satellite, never core.",
                            expectedReturnRange = "13.5% - 16.0% p.a."
                        )
                    )
                } else {
                    listOf(
                        CategoryRecommendation(
                            sebiCategory = SebiCategory.FLEXI_CAP,
                            suggestedAllocationPercent = 55,
                            role = AllocationRole.CORE,
                            rationale = "Core anchor across Indian companies with active risk management.",
                            cautionNotes = "Patient holding required.",
                            expectedReturnRange = "12.0% - 14.0% p.a."
                        ),
                        CategoryRecommendation(
                            sebiCategory = SebiCategory.INDEX_FUND,
                            suggestedAllocationPercent = 30,
                            role = AllocationRole.CORE,
                            rationale = "Low expense index anchor in India's top 50 titans.",
                            cautionNotes = "Zero active hedging.",
                            expectedReturnRange = "11.5% - 13.0% p.a."
                        ),
                        CategoryRecommendation(
                            sebiCategory = SebiCategory.BALANCED_ADVANTAGE,
                            suggestedAllocationPercent = 15,
                            role = AllocationRole.STABILITY,
                            rationale = "Defensive cushion to support portfolio during cyclical downshifts.",
                            cautionNotes = "Moderate returns.",
                            expectedReturnRange = "10.0% - 11.5% p.a."
                        )
                    )
                }
            }
            RiskProfileCategory.AGGRESSIVE -> {
                // Strict SEBI guideline adherence:
                // Small Cap CANNOT be recommended without long horizon (>=8y) and high financial capacity (>=75).
                val allowSmallCapSatellite = capacity >= 75 && horizon >= 8
                if (allowSmallCapSatellite) {
                    listOf(
                        CategoryRecommendation(
                            sebiCategory = SebiCategory.FLEXI_CAP,
                            suggestedAllocationPercent = 45,
                            role = AllocationRole.CORE,
                            rationale = "Unconstrained foundational equity engine allocating across high-conviction companies.",
                            cautionNotes = "Continue SIPs through bear cycles to accumulate at lower valuations.",
                            expectedReturnRange = "12.5% - 14.5% p.a."
                        ),
                        CategoryRecommendation(
                            sebiCategory = SebiCategory.LARGE_AND_MID,
                            suggestedAllocationPercent = 30,
                            role = AllocationRole.CORE,
                            rationale = "Strong balance between corporate blue chips and nimble mid-sized innovators.",
                            cautionNotes = "Standard market risk.",
                            expectedReturnRange = "13.0% - 15.0% p.a."
                        ),
                        CategoryRecommendation(
                            sebiCategory = SebiCategory.MID_CAP,
                            suggestedAllocationPercent = 15,
                            role = AllocationRole.SATELLITE,
                            rationale = "Satellite allocation targeting high-velocity mid-cap enterprises.",
                            cautionNotes = "Elevated volatility; must remain capped as satellite.",
                            expectedReturnRange = "14.0% - 16.5% p.a."
                        ),
                        CategoryRecommendation(
                            sebiCategory = SebiCategory.SMALL_CAP,
                            suggestedAllocationPercent = 10,
                            role = AllocationRole.SATELLITE,
                            rationale = "Strictly satellite exposure (max 10%) to emerging small enterprises with high multi-year runway.",
                            cautionNotes = "Small Cap funds can drop 35-45% in bear phases and stay stagnant for 2-3 years. Never invest more than 10-15% of your total equity.",
                            expectedReturnRange = "15.0% - 18.0% p.a. (high risk)"
                        )
                    )
                } else {
                    listOf(
                        CategoryRecommendation(
                            sebiCategory = SebiCategory.FLEXI_CAP,
                            suggestedAllocationPercent = 50,
                            role = AllocationRole.CORE,
                            rationale = "Foundational multi-cap engine with robust liquidity and institutional research backing.",
                            cautionNotes = "Volatile in early years.",
                            expectedReturnRange = "12.5% - 14.5% p.a."
                        ),
                        CategoryRecommendation(
                            sebiCategory = SebiCategory.LARGE_AND_MID,
                            suggestedAllocationPercent = 35,
                            role = AllocationRole.CORE,
                            rationale = "Blends top 100 leaders with mid-market winners for high compounding.",
                            cautionNotes = "Periodic market corrections.",
                            expectedReturnRange = "13.0% - 15.0% p.a."
                        ),
                        CategoryRecommendation(
                            sebiCategory = SebiCategory.MID_CAP,
                            suggestedAllocationPercent = 15,
                            role = AllocationRole.SATELLITE,
                            rationale = "Disciplined satellite tilt in mid-sized businesses.",
                            cautionNotes = "Small cap avoided because your financial capacity buffer requires greater stability.",
                            expectedReturnRange = "14.0% - 16.5% p.a."
                        )
                    )
                }
            }
        }
    }
}
