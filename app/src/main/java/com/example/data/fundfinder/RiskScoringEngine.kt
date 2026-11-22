package com.example.data.fundfinder

/**
 * Deterministic, transparent, and regulatory-aligned risk scoring engine.
 * Distinguishes Risk Tolerance (emotional willingness) from Risk Capacity (financial ability).
 */
object RiskScoringEngine {

    val questions: List<RiskQuestion> = listOf(
        // Dimension A: Willingness to take risk (Tolerance)
        RiskQuestion(
            id = "tol_fluctuation",
            dimension = RiskDimension.TOLERANCE,
            prompt = "How comfortable are you with short-term market fluctuations in portfolio value?",
            subtitle = "Equities fluctuate daily while debt products offer smoother trajectories.",
            options = listOf(
                RiskOption("Extremely uncomfortable", "I prefer zero volatility even if returns just match inflation", 1),
                RiskOption("Slightly cautious", "I can accept minor dips up to 5% if recoveries are quick", 2),
                RiskOption("Comfortable with normal cycles", "I understand 10-15% corrections are standard market events", 3),
                RiskOption("High tolerance", "I look through 20-30% swings knowing long-term compounding requires volatility", 5)
            )
        ),
        RiskQuestion(
            id = "tol_decline_10",
            dimension = RiskDimension.TOLERANCE,
            prompt = "What would you do if your investment portfolio declined by 10% in two months?",
            subtitle = "Testing your immediate psychological response to a routine market correction.",
            options = listOf(
                RiskOption("Exit immediately", "Sell all units and move to a bank fixed deposit", 1),
                RiskOption("Stop fresh investments", "Hold existing units but pause all monthly SIPs", 2),
                RiskOption("Stay the course", "Continue SIPs uninterrupted as planned", 4),
                RiskOption("Top up / Buy more", "Invest an additional lumpsum to accumulate units at lower NAVs", 5)
            )
        ),
        RiskQuestion(
            id = "tol_decline_20",
            dimension = RiskDimension.TOLERANCE,
            prompt = "What would you do if a severe market crash reduced your portfolio by 25%?",
            subtitle = "Major bear markets occur once or twice every decade.",
            options = listOf(
                RiskOption("Panic and exit", "I cannot bear losing a quarter of my hard-earned savings", 1),
                RiskOption("Switch to safe debt funds", "Redeem equities and switch to liquid/overnight funds", 2),
                RiskOption("Do nothing and wait", "Wait patiently for the market cycle to recover over 2-3 years", 4),
                RiskOption("Aggressively increase allocation", "Accelerate SIPs and deploy tactical reserves", 5)
            )
        ),
        RiskQuestion(
            id = "tol_downturn_continue",
            dimension = RiskDimension.TOLERANCE,
            prompt = "Would you continue investing month after month during a prolonged 1-year bear market?",
            subtitle = "SIP rupee cost averaging only works if you stay invested during down markets.",
            options = listOf(
                RiskOption("No, I would stop", "I only invest when market indices are visibly climbing", 1),
                RiskOption("Hesitant / Reduce amount", "I would scale down monthly investments to minimize anxiety", 2),
                RiskOption("Yes, absolutely", "I understand that bear markets yield the highest long-term returns per rupee", 5)
            )
        ),

        // Dimension B: Financial capacity to absorb losses (Capacity)
        RiskQuestion(
            id = "cap_emergency_fund",
            dimension = RiskDimension.CAPACITY,
            prompt = "Do you have an emergency fund set aside in liquid savings?",
            subtitle = "An emergency fund prevents having to distress-sell investments during emergencies.",
            options = listOf(
                RiskOption("No emergency fund", "Less than 1 month of living expenses saved", 1),
                RiskOption("Small reserve", "1 to 2 months of household expenses in bank savings", 2),
                RiskOption("Adequate cushion", "3 to 6 months in liquid funds / savings account", 4),
                RiskOption("Robust safety net", "6+ months of expenses plus comprehensive health & term insurance", 5)
            )
        ),
        RiskQuestion(
            id = "cap_debt",
            dimension = RiskDimension.CAPACITY,
            prompt = "What is your current high-interest personal debt status?",
            subtitle = "Credit card revolving balances or personal loans erode wealth faster than funds generate.",
            options = listOf(
                RiskOption("High debt burden", "Active personal loans or revolving credit card interest (>14% APR)", 1),
                RiskOption("Moderate EMIs", "Auto loan or consumer loans consuming >25% of take-home pay", 2),
                RiskOption("Low / Manageable debt", "Affordable home loan EMI (<30% of income), no credit card debt", 4),
                RiskOption("Debt-free", "Zero outstanding high-interest debt or personal loans", 5)
            )
        ),
        RiskQuestion(
            id = "cap_income_stability",
            dimension = RiskDimension.CAPACITY,
            prompt = "How predictable and stable is your monthly income stream?",
            subtitle = "Stable cashflows allow investors to endure prolonged market downturns.",
            options = listOf(
                RiskOption("Highly uncertain", "Irregular contracts, unemployed, or fluctuating earnings", 1),
                RiskOption("Moderate fluctuations", "Commission-heavy, freelance, or seasonal business earnings", 2),
                RiskOption("Predictable & secure", "Steady salaried corporate/government role or established profession", 4),
                RiskOption("Multiple diversified streams", "Salaried plus rental or dividend income with high savings rate", 5)
            )
        ),
        RiskQuestion(
            id = "cap_liquidity_horizon",
            dimension = RiskDimension.CAPACITY,
            prompt = "Is there any chance you will need to withdraw this money before your horizon?",
            subtitle = "Equity mutual funds require committed, untouched horizons of at least 5-7 years.",
            options = listOf(
                RiskOption("Likely needed early", "Might need these funds within 1 to 2 years for emergency or purchase", 1),
                RiskOption("Possibility of partial withdrawal", "Might need to access 20-30% within 3 years", 2),
                RiskOption("Unlikely to touch", "Dedicated solely for the stated goal with other liquid buffers available", 4),
                RiskOption("Completely locked", "Strictly ring-fenced long-term capital with zero early withdrawal pressure", 5)
            )
        ),
        RiskQuestion(
            id = "cap_essential_expense",
            dimension = RiskDimension.CAPACITY,
            prompt = "If this portfolio faced a 20% loss, would your daily living expenses be affected?",
            subtitle = "Investment capital must always be surplus capital beyond essential survival needs.",
            options = listOf(
                RiskOption("Severely affected", "I depend on this money for groceries, rent, or school fees", 1),
                RiskOption("Mild lifestyle impact", "Would need to cut back significantly on non-essential spending", 2),
                RiskOption("Zero effect on essentials", "My daily and family living expenses are completely insulated", 5)
            )
        ),

        // Dimension C: Investment knowledge & familiarity
        RiskQuestion(
            id = "know_experience",
            dimension = RiskDimension.KNOWLEDGE,
            prompt = "How would you describe your understanding of mutual fund structures?",
            subtitle = "Helps match appropriate complexity (e.g. simple index funds vs active satellite funds).",
            options = listOf(
                RiskOption("Beginner", "Unfamiliar with difference between equity, debt, NAV, and expense ratios", 1),
                RiskOption("Basic understanding", "Know about SIPs and bank FDs, but not portfolio rebalancing", 2),
                RiskOption("Intermediate", "Understand asset classes, market indices, Riskometer, and Direct vs Regular", 4),
                RiskOption("Advanced / Expert", "Experienced in asset allocation, debt credit risk, duration, and tracking error", 5)
            )
        )
    )

    fun calculateScores(answers: Map<String, Int>): RiskAssessmentResult {
        var toleranceSum = 0
        var toleranceMax = 0
        var capacitySum = 0
        var capacityMax = 0
        var knowledgeSum = 0
        var knowledgeMax = 0

        for (q in questions) {
            val score = answers[q.id] ?: 3 // default middle if not answered
            val maxQ = q.options.maxOfOrNull { it.scoreValue } ?: 5
            when (q.dimension) {
                RiskDimension.TOLERANCE -> {
                    toleranceSum += score
                    toleranceMax += maxQ
                }
                RiskDimension.CAPACITY -> {
                    capacitySum += score
                    capacityMax += maxQ
                }
                RiskDimension.KNOWLEDGE -> {
                    knowledgeSum += score
                    knowledgeMax += maxQ
                }
            }
        }

        val toleranceScore = if (toleranceMax > 0) ((toleranceSum.toDouble() / toleranceMax) * 100).toInt() else 50
        val capacityScore = if (capacityMax > 0) ((capacitySum.toDouble() / capacityMax) * 100).toInt() else 50
        val knowledgeScore = if (knowledgeMax > 0) ((knowledgeSum.toDouble() / knowledgeMax) * 100).toInt() else 50

        // Capacity Constraint Rule:
        // A person with high risk tolerance (e.g. 85%) but low financial capacity (e.g. 40%)
        // MUST NOT receive an aggressive profile, as market drawdowns could wipe out their essentials.
        val hasMismatch = toleranceScore > (capacityScore + 18)
        val mismatchExplanation = if (hasMismatch) {
            "Tolerance vs. Capacity Mismatch Detected: Your willingness to take volatility ($toleranceScore/100) exceeds your current financial capacity ($capacityScore/100) due to liquidity or debt factors. To protect your financial security, your investment profile has been responsibly constrained to match your financial capacity."
        } else null

        // Composite calculation with capacity bounding
        val rawComposite = (toleranceScore * 0.40) + (capacityScore * 0.45) + (knowledgeScore * 0.15)
        val effectiveComposite = if (hasMismatch) {
            // Clamp so profile cannot exceed capacity tier
            Math.min(rawComposite, capacityScore.toDouble() + 5.0).toInt()
        } else {
            rawComposite.toInt()
        }

        val profile = when {
            effectiveComposite < 35 -> RiskProfileCategory.CONSERVATIVE
            effectiveComposite < 52 -> RiskProfileCategory.MODERATELY_CONSERVATIVE
            effectiveComposite < 68 -> RiskProfileCategory.MODERATE
            effectiveComposite < 82 -> RiskProfileCategory.MODERATELY_AGGRESSIVE
            else -> {
                // To be truly Aggressive, must have both high tolerance AND high capacity
                if (toleranceScore >= 70 && capacityScore >= 70) {
                    RiskProfileCategory.AGGRESSIVE
                } else {
                    RiskProfileCategory.MODERATELY_AGGRESSIVE
                }
            }
        }

        val rationale = buildRationale(profile, toleranceScore, capacityScore, knowledgeScore, hasMismatch)

        return RiskAssessmentResult(
            toleranceScore = toleranceScore,
            capacityScore = capacityScore,
            knowledgeScore = knowledgeScore,
            compositeScore = effectiveComposite,
            profile = profile,
            hasToleranceCapacityMismatch = hasMismatch,
            mismatchExplanation = mismatchExplanation,
            rationale = rationale
        )
    }

    private fun buildRationale(
        profile: RiskProfileCategory,
        tol: Int,
        cap: Int,
        know: Int,
        mismatch: Boolean
    ): String {
        return buildString {
            append("You have been classified as a **${profile.title} Investor** ")
            append("based on a Risk Tolerance of $tol/100, Financial Capacity of $cap/100, and Knowledge score of $know/100. ")
            when (profile) {
                RiskProfileCategory.CONSERVATIVE -> {
                    append("Your primary objective should be capital preservation and near-zero volatility. Suitable investments focus on liquid, money market, and high-quality short-duration debt instruments.")
                }
                RiskProfileCategory.MODERATELY_CONSERVATIVE -> {
                    append("You seek steady returns with low to moderate volatility. A portfolio anchored in debt funds with a 20-35% allocation to conservative hybrid or arbitrage funds suits your horizon.")
                }
                RiskProfileCategory.MODERATE -> {
                    append("You are comfortable with moderate market fluctuations to achieve balanced growth over 5-7 years. A blend of balanced advantage, multi-asset, and large cap index funds provides balanced compounding.")
                }
                RiskProfileCategory.MODERATELY_AGGRESSIVE -> {
                    append("You possess healthy capacity and a comfortable appetite for market cycles over 7+ years. Your core allocation focuses on diversified large cap, flexi cap, and aggressive hybrid funds.")
                }
                RiskProfileCategory.AGGRESSIVE -> {
                    append("You have both strong financial capacity and high psychological resilience for 8-10+ year compounding cycles. You can accommodate higher equity allocations across flexi cap, large & mid cap, and controlled satellite allocations.")
                }
            }
            if (mismatch) {
                append(" Notice: We prioritized capital security because your financial capacity buffer is more restrictive than your willingness to take risk.")
            }
        }
    }
}
