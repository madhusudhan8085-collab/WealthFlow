package com.example

import com.example.data.learning.LearningHubConfig
import com.example.ui.viewmodel.MainDestination
import com.example.ui.viewmodel.WealthFlowViewModel
import org.junit.Assert.*
import org.junit.Test

class LearningHubTest {

    @Test
    fun testExactVideoUrlsAndIdsConfigured() {
        val kannada = LearningHubConfig.VideoLanguage.KANNADA
        assertEquals("Kannada", kannada.displayName)
        assertEquals("ಕನ್ನಡ", kannada.nativeName)
        assertEquals("e_nmT0LTThg", kannada.videoId)
        assertEquals("https://www.youtube.com/watch?v=e_nmT0LTThg", kannada.youtubeUrl)

        val english = LearningHubConfig.VideoLanguage.ENGLISH
        assertEquals("English", english.displayName)
        assertEquals("NW_G4iQheYw", english.videoId)
        assertEquals("https://www.youtube.com/watch?v=NW_G4iQheYw", english.youtubeUrl)

        val hindi = LearningHubConfig.VideoLanguage.HINDI
        assertEquals("Hindi", hindi.displayName)
        assertEquals("हिन्दी", hindi.nativeName)
        assertEquals("XxqQ59NcCTY", hindi.videoId)
        assertEquals("https://www.youtube.com/watch?v=XxqQ59NcCTY", hindi.youtubeUrl)
    }

    @Test
    fun testZerodhaVarsityConfiguration() {
        assertEquals("https://zerodha.com/varsity/", LearningHubConfig.ZERODHA_VARSITY_HOME_URL)
        assertEquals("https://zerodha.com/varsity/modules/", LearningHubConfig.ZERODHA_VARSITY_MODULES_URL)
        assertTrue(LearningHubConfig.ZERODHA_ATTRIBUTION_NOTE.contains("Zerodha Varsity is an independent educational platform"))

        val topics = LearningHubConfig.varsityTopics
        assertEquals(6, topics.size)

        val titles = topics.map { it.title }
        assertTrue(titles.contains("Introduction to Stock Markets"))
        assertTrue(titles.contains("Fundamental Analysis"))
        assertTrue(titles.contains("Technical Analysis"))
        assertTrue(titles.contains("Personal Finance and Mutual Funds"))
        assertTrue(titles.contains("Risk Management and Trading Psychology"))
        assertTrue(titles.contains("Markets and Taxation"))

        for (topic in topics) {
            assertEquals("https://zerodha.com/varsity/modules/", topic.targetUrl)
            assertTrue(topic.description.isNotEmpty())
        }
    }

    @Test
    fun testViewModelLearningLanguageSelection() {
        val viewModel = WealthFlowViewModel()

        assertNull(viewModel.uiState.value.selectedLearningLanguage)

        // Select Kannada
        viewModel.selectLearningLanguage(LearningHubConfig.VideoLanguage.KANNADA)
        assertEquals(LearningHubConfig.VideoLanguage.KANNADA, viewModel.uiState.value.selectedLearningLanguage)

        // Select English
        viewModel.selectLearningLanguage(LearningHubConfig.VideoLanguage.ENGLISH)
        assertEquals(LearningHubConfig.VideoLanguage.ENGLISH, viewModel.uiState.value.selectedLearningLanguage)

        // Select Hindi
        viewModel.selectLearningLanguage(LearningHubConfig.VideoLanguage.HINDI)
        assertEquals(LearningHubConfig.VideoLanguage.HINDI, viewModel.uiState.value.selectedLearningLanguage)

        // Clear language (back to selection)
        viewModel.clearLearningLanguage()
        assertNull(viewModel.uiState.value.selectedLearningLanguage)
    }

    @Test
    fun testNavigationToLearningHub() {
        val viewModel = WealthFlowViewModel()

        viewModel.selectDestination(MainDestination.LEARNING_HUB)
        assertEquals(MainDestination.LEARNING_HUB, viewModel.uiState.value.currentDestination)

        viewModel.selectDestination(MainDestination.HOME)
        assertEquals(MainDestination.HOME, viewModel.uiState.value.currentDestination)
    }
}
