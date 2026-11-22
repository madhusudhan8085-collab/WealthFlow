package com.example.data.fundfinder

/**
 * Verified Mutual Fund Data Provider based on official AMFI & AMC public statutory disclosures.
 * Transparently exposes data source, scheme codes, expense ratios, benchmarks, and Riskometer badges.
 */
object FundDataProvider {

    const val DATA_SOURCE_NOTICE = "Data Source: Association of Mutual Funds in India (AMFI) & AMC Statutory Disclosures Snapshot (October 2024). Net Asset Values (NAV) & trailing CAGR returns are for illustrative educational discovery."

    val verifiedSchemes: List<MutualFundScheme> = listOf(
        // Index Funds (Passive)
        MutualFundScheme(
            amfiCode = "120716",
            amcName = "UTI Mutual Fund",
            schemeName = "UTI Nifty 50 Index Fund Direct - Growth",
            category = SebiCategory.INDEX_FUND,
            planType = FundPlanType.DIRECT,
            option = FundOption.GROWTH,
            nav = 176.45,
            expenseRatioPercent = 0.18,
            riskometer = RiskometerLevel.VERY_HIGH,
            returns1Y = 28.4,
            returns3Y = 14.8,
            returns5Y = 16.2,
            benchmarkIndex = "Nifty 50 TRI",
            minSipAmount = 500.0,
            exitLoadSummary = "Nil",
            aumCrores = 18450.0
        ),
        MutualFundScheme(
            amfiCode = "119062",
            amcName = "HDFC Mutual Fund",
            schemeName = "HDFC Index Fund - BSE Sensex Plan Direct - Growth",
            category = SebiCategory.INDEX_FUND,
            planType = FundPlanType.DIRECT,
            option = FundOption.GROWTH,
            nav = 724.12,
            expenseRatioPercent = 0.20,
            riskometer = RiskometerLevel.VERY_HIGH,
            returns1Y = 26.9,
            returns3Y = 14.2,
            returns5Y = 15.8,
            benchmarkIndex = "S&P BSE Sensex TRI",
            minSipAmount = 500.0,
            exitLoadSummary = "0.25% if redeemed within 3 days; Nil thereafter",
            aumCrores = 7890.0
        ),

        // Flexi Cap Funds
        MutualFundScheme(
            amfiCode = "122639",
            amcName = "PPFAS Mutual Fund",
            schemeName = "Parag Parikh Flexi Cap Fund Direct - Growth",
            category = SebiCategory.FLEXI_CAP,
            planType = FundPlanType.DIRECT,
            option = FundOption.GROWTH,
            nav = 83.25,
            expenseRatioPercent = 0.63,
            riskometer = RiskometerLevel.VERY_HIGH,
            returns1Y = 36.2,
            returns3Y = 19.5,
            returns5Y = 22.4,
            benchmarkIndex = "Nifty 500 TRI",
            minSipAmount = 1000.0,
            exitLoadSummary = "2% if redeemed within 365 days; 1% between 366-730 days; Nil after 2 years",
            aumCrores = 73200.0
        ),
        MutualFundScheme(
            amfiCode = "120503",
            amcName = "HDFC Mutual Fund",
            schemeName = "HDFC Flexi Cap Fund Direct - Growth",
            category = SebiCategory.FLEXI_CAP,
            planType = FundPlanType.DIRECT,
            option = FundOption.GROWTH,
            nav = 1842.30,
            expenseRatioPercent = 0.81,
            riskometer = RiskometerLevel.VERY_HIGH,
            returns1Y = 38.5,
            returns3Y = 23.8,
            returns5Y = 20.6,
            benchmarkIndex = "Nifty 500 TRI",
            minSipAmount = 500.0,
            exitLoadSummary = "1% if redeemed within 1 year; Nil thereafter",
            aumCrores = 61400.0
        ),

        // Large Cap
        MutualFundScheme(
            amfiCode = "120303",
            amcName = "ICICI Prudential Mutual Fund",
            schemeName = "ICICI Prudential Bluechip Fund Direct - Growth",
            category = SebiCategory.LARGE_CAP,
            planType = FundPlanType.DIRECT,
            option = FundOption.GROWTH,
            nav = 108.62,
            expenseRatioPercent = 0.89,
            riskometer = RiskometerLevel.VERY_HIGH,
            returns1Y = 34.1,
            returns3Y = 18.2,
            returns5Y = 17.5,
            benchmarkIndex = "Nifty 100 TRI",
            minSipAmount = 500.0,
            exitLoadSummary = "1% if redeemed within 1 year; Nil thereafter",
            aumCrores = 56800.0
        ),

        // Large & Mid Cap
        MutualFundScheme(
            amfiCode = "120485",
            amcName = "Mirae Asset Mutual Fund",
            schemeName = "Mirae Asset Large & Midcap Fund Direct - Growth",
            category = SebiCategory.LARGE_AND_MID,
            planType = FundPlanType.DIRECT,
            option = FundOption.GROWTH,
            nav = 145.20,
            expenseRatioPercent = 0.65,
            riskometer = RiskometerLevel.VERY_HIGH,
            returns1Y = 35.8,
            returns3Y = 18.9,
            returns5Y = 19.8,
            benchmarkIndex = "Nifty LargeMidcap 250 TRI",
            minSipAmount = 1000.0,
            exitLoadSummary = "1% if redeemed within 1 year; Nil thereafter",
            aumCrores = 38500.0
        ),

        // Mid Cap (Satellite)
        MutualFundScheme(
            amfiCode = "120166",
            amcName = "Kotak Mahindra Mutual Fund",
            schemeName = "Kotak Emerging Equity Fund Direct - Growth",
            category = SebiCategory.MID_CAP,
            planType = FundPlanType.DIRECT,
            option = FundOption.GROWTH,
            nav = 124.80,
            expenseRatioPercent = 0.42,
            riskometer = RiskometerLevel.VERY_HIGH,
            returns1Y = 48.6,
            returns3Y = 24.2,
            returns5Y = 25.1,
            benchmarkIndex = "Nifty Midcap 150 TRI",
            minSipAmount = 1000.0,
            exitLoadSummary = "1% if redeemed within 1 year; Nil thereafter",
            aumCrores = 49200.0
        ),
        MutualFundScheme(
            amfiCode = "119598",
            amcName = "HDFC Mutual Fund",
            schemeName = "HDFC Mid-Cap Opportunities Fund Direct - Growth",
            category = SebiCategory.MID_CAP,
            planType = FundPlanType.DIRECT,
            option = FundOption.GROWTH,
            nav = 189.50,
            expenseRatioPercent = 0.74,
            riskometer = RiskometerLevel.VERY_HIGH,
            returns1Y = 46.2,
            returns3Y = 26.5,
            returns5Y = 25.8,
            benchmarkIndex = "Nifty Midcap 150 TRI",
            minSipAmount = 500.0,
            exitLoadSummary = "1% if redeemed within 1 year; Nil thereafter",
            aumCrores = 71800.0
        ),

        // Small Cap (Strict Satellite)
        MutualFundScheme(
            amfiCode = "125354",
            amcName = "Nippon India Mutual Fund",
            schemeName = "Nippon India Small Cap Fund Direct - Growth",
            category = SebiCategory.SMALL_CAP,
            planType = FundPlanType.DIRECT,
            option = FundOption.GROWTH,
            nav = 178.40,
            expenseRatioPercent = 0.71,
            riskometer = RiskometerLevel.VERY_HIGH,
            returns1Y = 52.8,
            returns3Y = 30.1,
            returns5Y = 31.4,
            benchmarkIndex = "Nifty Smallcap 250 TRI",
            minSipAmount = 500.0,
            exitLoadSummary = "1% if redeemed within 1 month; Nil thereafter",
            aumCrores = 58200.0
        ),
        MutualFundScheme(
            amfiCode = "119803",
            amcName = "SBI Mutual Fund",
            schemeName = "SBI Small Cap Fund Direct - Growth",
            category = SebiCategory.SMALL_CAP,
            planType = FundPlanType.DIRECT,
            option = FundOption.GROWTH,
            nav = 188.75,
            expenseRatioPercent = 0.68,
            riskometer = RiskometerLevel.VERY_HIGH,
            returns1Y = 39.4,
            returns3Y = 22.8,
            returns5Y = 26.5,
            benchmarkIndex = "BSE 250 SmallCap TRI",
            minSipAmount = 500.0,
            exitLoadSummary = "1% if redeemed within 1 year; Nil thereafter",
            aumCrores = 31400.0
        ),

        // Balanced Advantage (Dynamic Asset Allocation)
        MutualFundScheme(
            amfiCode = "120372",
            amcName = "ICICI Prudential Mutual Fund",
            schemeName = "ICICI Prudential Balanced Advantage Fund Direct - Growth",
            category = SebiCategory.BALANCED_ADVANTAGE,
            planType = FundPlanType.DIRECT,
            option = FundOption.GROWTH,
            nav = 78.45,
            expenseRatioPercent = 0.88,
            riskometer = RiskometerLevel.HIGH,
            returns1Y = 23.5,
            returns3Y = 14.1,
            returns5Y = 14.8,
            benchmarkIndex = "Nifty 50 Hybrid Composite Debt 50:50 Index",
            minSipAmount = 500.0,
            exitLoadSummary = "Nil for up to 10% units each year; 1% for excess within 1 year",
            aumCrores = 60100.0
        ),
        MutualFundScheme(
            amfiCode = "119053",
            amcName = "HDFC Mutual Fund",
            schemeName = "HDFC Balanced Advantage Fund Direct - Growth",
            category = SebiCategory.BALANCED_ADVANTAGE,
            planType = FundPlanType.DIRECT,
            option = FundOption.GROWTH,
            nav = 512.90,
            expenseRatioPercent = 0.76,
            riskometer = RiskometerLevel.HIGH,
            returns1Y = 32.8,
            returns3Y = 20.4,
            returns5Y = 19.1,
            benchmarkIndex = "Nifty 50 Hybrid Composite Debt 50:50 Index",
            minSipAmount = 500.0,
            exitLoadSummary = "1% if redeemed within 1 year; Nil thereafter",
            aumCrores = 92400.0
        ),

        // Aggressive Hybrid
        MutualFundScheme(
            amfiCode = "119808",
            amcName = "SBI Mutual Fund",
            schemeName = "SBI Equity Hybrid Fund Direct - Growth",
            category = SebiCategory.AGGRESSIVE_HYBRID,
            planType = FundPlanType.DIRECT,
            option = FundOption.GROWTH,
            nav = 284.10,
            expenseRatioPercent = 0.77,
            riskometer = RiskometerLevel.VERY_HIGH,
            returns1Y = 25.2,
            returns3Y = 15.6,
            returns5Y = 16.3,
            benchmarkIndex = "CRISIL Hybrid 35+65 Aggressive Index",
            minSipAmount = 500.0,
            exitLoadSummary = "1% if redeemed within 1 year; Nil thereafter",
            aumCrores = 71200.0
        ),

        // Multi Asset Allocation
        MutualFundScheme(
            amfiCode = "120379",
            amcName = "ICICI Prudential Mutual Fund",
            schemeName = "ICICI Prudential Multi-Asset Fund Direct - Growth",
            category = SebiCategory.MULTI_ASSET,
            planType = FundPlanType.DIRECT,
            option = FundOption.GROWTH,
            nav = 684.20,
            expenseRatioPercent = 0.96,
            riskometer = RiskometerLevel.VERY_HIGH,
            returns1Y = 32.1,
            returns3Y = 21.8,
            returns5Y = 21.4,
            benchmarkIndex = "Nifty 200 TRI (65%) + Nifty Composite Debt (25%) + Domestic Gold (10%)",
            minSipAmount = 500.0,
            exitLoadSummary = "1% if redeemed within 1 year; Nil thereafter",
            aumCrores = 47800.0
        ),

        // Conservative Hybrid
        MutualFundScheme(
            amfiCode = "119827",
            amcName = "SBI Mutual Fund",
            schemeName = "SBI Conservative Hybrid Fund Direct - Growth",
            category = SebiCategory.CONSERVATIVE_HYBRID,
            planType = FundPlanType.DIRECT,
            option = FundOption.GROWTH,
            nav = 76.50,
            expenseRatioPercent = 0.58,
            riskometer = RiskometerLevel.MODERATELY_HIGH,
            returns1Y = 14.8,
            returns3Y = 10.2,
            returns5Y = 10.9,
            benchmarkIndex = "CRISIL Hybrid 85+15 - Conservative Index",
            minSipAmount = 500.0,
            exitLoadSummary = "1% if redeemed within 1 year; Nil thereafter",
            aumCrores = 9200.0
        ),

        // Short Duration Debt Fund
        MutualFundScheme(
            amfiCode = "119067",
            amcName = "HDFC Mutual Fund",
            schemeName = "HDFC Short Term Debt Fund Direct - Growth",
            category = SebiCategory.SHORT_DURATION,
            planType = FundPlanType.DIRECT,
            option = FundOption.GROWTH,
            nav = 31.42,
            expenseRatioPercent = 0.36,
            riskometer = RiskometerLevel.MODERATE,
            returns1Y = 7.8,
            returns3Y = 6.4,
            returns5Y = 7.2,
            benchmarkIndex = "CRISIL Short Duration Debt B-II Index",
            minSipAmount = 500.0,
            exitLoadSummary = "Nil",
            aumCrores = 18600.0
        ),

        // Liquid Debt Fund
        MutualFundScheme(
            amfiCode = "120392",
            amcName = "ICICI Prudential Mutual Fund",
            schemeName = "ICICI Prudential Liquid Fund Direct - Growth",
            category = SebiCategory.LIQUID,
            planType = FundPlanType.DIRECT,
            option = FundOption.GROWTH,
            nav = 368.50,
            expenseRatioPercent = 0.20,
            riskometer = RiskometerLevel.LOW,
            returns1Y = 7.3,
            returns3Y = 6.1,
            returns5Y = 5.7,
            benchmarkIndex = "CRISIL Liquid Debt A-I Index",
            minSipAmount = 500.0,
            exitLoadSummary = "Graded exit load up to Day 6 (0.0070% on Day 1 to 0.0045% on Day 6); Nil from Day 7",
            aumCrores = 52300.0
        ),
        MutualFundScheme(
            amfiCode = "119066",
            amcName = "HDFC Mutual Fund",
            schemeName = "HDFC Liquid Fund Direct - Growth",
            category = SebiCategory.LIQUID,
            planType = FundPlanType.DIRECT,
            option = FundOption.GROWTH,
            nav = 4785.20,
            expenseRatioPercent = 0.19,
            riskometer = RiskometerLevel.LOW,
            returns1Y = 7.2,
            returns3Y = 6.0,
            returns5Y = 5.6,
            benchmarkIndex = "CRISIL Liquid Debt A-I Index",
            minSipAmount = 500.0,
            exitLoadSummary = "Graded exit load up to Day 6; Nil from Day 7",
            aumCrores = 64800.0
        ),

        // ELSS (Tax Saving)
        MutualFundScheme(
            amfiCode = "120505",
            amcName = "Mirae Asset Mutual Fund",
            schemeName = "Mirae Asset ELSS Tax Saver Fund Direct - Growth",
            category = SebiCategory.ELSS,
            planType = FundPlanType.DIRECT,
            option = FundOption.GROWTH,
            nav = 48.90,
            expenseRatioPercent = 0.59,
            riskometer = RiskometerLevel.VERY_HIGH,
            returns1Y = 36.5,
            returns3Y = 18.5,
            returns5Y = 20.2,
            benchmarkIndex = "Nifty 500 TRI",
            minSipAmount = 500.0,
            exitLoadSummary = "Nil (Mandatory 3-year statutory lock-in under Section 80C)",
            aumCrores = 24100.0
        )
    )

    fun getSchemesForCategory(category: SebiCategory): List<MutualFundScheme> {
        return verifiedSchemes.filter { it.category == category }
    }

    fun getSchemeByAmfiCode(code: String): MutualFundScheme? {
        return verifiedSchemes.find { it.amfiCode == code }
    }

    fun getTopSchemeForCategory(category: SebiCategory): MutualFundScheme? {
        return verifiedSchemes.firstOrNull { it.category == category }
    }

    fun searchSchemes(query: String): List<MutualFundScheme> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return verifiedSchemes
        return verifiedSchemes.filter {
            it.schemeName.lowercase().contains(q) ||
            it.amcName.lowercase().contains(q) ||
            it.category.displayName.lowercase().contains(q) ||
            it.benchmarkIndex.lowercase().contains(q)
        }
    }
}
