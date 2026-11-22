package com.example.data.fundfinder

/**
 * Educational glossary & SEBI statutory disclosures for Fund Finder.
 */
object FundEducationData {

    const val SEBI_REGULATORY_DISCLAIMER =
        "STATUTORY SEBI REGULATORY NOTICE: WealthFlow Fund Finder is an educational decision-support tool designed to help investors understand financial risk profiling, asset allocation, and SEBI mutual fund classifications. WealthFlow is NOT a SEBI-registered Investment Adviser (RIA), Portfolio Manager (PMS), or Mutual Fund Distributor (ARN). Nothing herein constitutes personalized investment advice, research recommendation, solicitation, or endorsement of any asset management company (AMC) or financial security. Mutual fund investments are subject to market risks. Read all scheme-related documents carefully before investing. Past performance is no guarantee of future returns."

    data class GlossaryEntry(
        val term: String,
        val tag: String,
        val shortSummary: String,
        val detailedExplanation: String
    )

    val glossaryItems: List<GlossaryEntry> = listOf(
        GlossaryEntry(
            term = "Direct Plan vs. Regular Plan",
            tag = "Cost Efficiency",
            shortSummary = "Direct plans eliminate distributor commissions, saving 0.5% - 1.2% every year.",
            detailedExplanation = "Every mutual fund scheme has two plans: Direct and Regular. In Regular plans, the AMC pays a recurring trail commission to the intermediary/broker out of your investment pool. Direct plans bypass distributors, resulting in a lower Total Expense Ratio (TER) and noticeably higher compounded NAV over 10 to 20 years."
        ),
        GlossaryEntry(
            term = "Growth vs. IDCW Option",
            tag = "Tax & Compounding",
            shortSummary = "Growth reinvests profits automatically for maximal compounding. IDCW pays out taxable cash dividends.",
            detailedExplanation = "Under the Growth option, realized capital gains and dividends remain invested inside the fund, compounding continuously until redemption. Under the IDCW (Income Distribution cum Capital Withdrawal) option, the fund distributes periodic payouts which are added to your annual income and taxed at your marginal tax slab rate."
        ),
        GlossaryEntry(
            term = "Total Expense Ratio (TER)",
            tag = "Fund Management Fees",
            shortSummary = "The annualized percentage of fund assets deducted daily to cover fund management and administration.",
            detailedExplanation = "SEBI caps TER based on fund asset size (AUM). Passive index funds typically charge 0.10% to 0.30%, while active equity funds charge 0.60% to 1.80%. A lower expense ratio directly enhances net investor returns over long horizons."
        ),
        GlossaryEntry(
            term = "SEBI Riskometer",
            tag = "Statutory Risk Rating",
            shortSummary = "A 6-level standardized risk gauge required by SEBI on all mutual fund factsheets.",
            detailedExplanation = "The Riskometer evaluates funds monthly from 'Low' to 'Very High' risk based on underlying portfolio parameters: liquidity risk, credit quality, interest rate duration (for debt), and market capitalization volatility (for equity)."
        ),
        GlossaryEntry(
            term = "Active vs. Passive (Index Funds)",
            tag = "Investment Philosophy",
            shortSummary = "Active managers attempt to beat the market; passive index funds replicate benchmark indices at minimal cost.",
            detailedExplanation = "Active funds employ research analysts to pick stocks, charging higher fees with the goal of generating 'Alpha' (outperforming benchmark). Passive funds replicate indices like Nifty 50 or Sensex with negligible tracking error and ultra-low expense ratios."
        ),
        GlossaryEntry(
            term = "Core vs. Satellite Allocation",
            tag = "Portfolio Architecture",
            shortSummary = "Anchor 70-80% of your wealth in stable, diversified core funds; allocate 20-30% to high-growth satellite tilts.",
            detailedExplanation = "A sound portfolio separates core holdings (Large Cap, Flexi Cap, or Broad Index) that you hold across all market cycles, from satellite positions (Mid Cap, Small Cap, or Sectoral) which seek higher alpha but carry elevated volatility."
        )
    )
}
