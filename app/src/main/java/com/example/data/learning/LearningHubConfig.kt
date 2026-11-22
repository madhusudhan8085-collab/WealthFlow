package com.example.data.learning

/**
 * Central configuration file for WealthFlow Learning Hub.
 * Contains verified video guide URLs, YouTube video IDs, and official Zerodha Varsity educational resources.
 * All URLs can be easily updated or replaced here.
 */
object LearningHubConfig {

    enum class VideoLanguage(
        val displayName: String,
        val nativeName: String,
        val videoId: String,
        val youtubeUrl: String
    ) {
        KANNADA(
            displayName = "Kannada",
            nativeName = "ಕನ್ನಡ",
            videoId = "e_nmT0LTThg",
            youtubeUrl = "https://www.youtube.com/watch?v=e_nmT0LTThg"
        ),
        ENGLISH(
            displayName = "English",
            nativeName = "English",
            videoId = "NW_G4iQheYw",
            youtubeUrl = "https://www.youtube.com/watch?v=NW_G4iQheYw"
        ),
        HINDI(
            displayName = "Hindi",
            nativeName = "हिन्दी",
            videoId = "XxqQ59NcCTY",
            youtubeUrl = "https://www.youtube.com/watch?v=XxqQ59NcCTY"
        )
    }

    // Official Zerodha Varsity URLs
    const val ZERODHA_VARSITY_HOME_URL = "https://zerodha.com/varsity/"
    const val ZERODHA_VARSITY_MODULES_URL = "https://zerodha.com/varsity/modules/"

    // Statutory attribution note
    const val ZERODHA_ATTRIBUTION_NOTE =
        "Zerodha Varsity is an independent educational platform. Its content is provided by Zerodha."

    data class LearningTopic(
        val title: String,
        val description: String,
        val targetUrl: String = ZERODHA_VARSITY_MODULES_URL
    )

    val varsityTopics: List<LearningTopic> = listOf(
        LearningTopic(
            title = "Introduction to Stock Markets",
            description = "Learn how stock exchanges, IPOs, demat accounts, and market participants operate.",
            targetUrl = ZERODHA_VARSITY_MODULES_URL
        ),
        LearningTopic(
            title = "Fundamental Analysis",
            description = "Understand balance sheets, P&L statements, financial ratios, and business valuation.",
            targetUrl = ZERODHA_VARSITY_MODULES_URL
        ),
        LearningTopic(
            title = "Technical Analysis",
            description = "Explore price charts, candlesticks, support/resistance, and volume indicators.",
            targetUrl = ZERODHA_VARSITY_MODULES_URL
        ),
        LearningTopic(
            title = "Personal Finance and Mutual Funds",
            description = "Discover emergency funds, health/term insurance, SIP compounding, and asset allocation.",
            targetUrl = ZERODHA_VARSITY_MODULES_URL
        ),
        LearningTopic(
            title = "Risk Management and Trading Psychology",
            description = "Master position sizing, stop-loss discipline, and cognitive biases in market volatility.",
            targetUrl = ZERODHA_VARSITY_MODULES_URL
        ),
        LearningTopic(
            title = "Markets and Taxation",
            description = "Learn capital gains tax (STCG/LTCG), equity vs debt taxation, and tax-loss harvesting.",
            targetUrl = ZERODHA_VARSITY_MODULES_URL
        )
    )
}
