package com.example.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PriceCheck
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculations.FinancialMathEngine
import com.example.ui.components.*
import com.example.ui.viewmodel.AnalyzeSubTab
import com.example.ui.viewmodel.WealthFlowUiState
import com.example.ui.viewmodel.WealthFlowViewModel
import com.example.utils.CurrencyFormatter
import com.example.utils.SoundManager
import kotlin.math.pow

@Composable
fun AnalyzeScreen(
    state: WealthFlowUiState,
    viewModel: WealthFlowViewModel,
    modifier: Modifier = Modifier
) {
    val currentSubTab = state.currentAnalyzeSubTab

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("analyze_screen_root")
    ) {
        // Top Sub-Navigation Tabs (Sensitivity Matrix Removed as Requested)
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            ScrollableTabRow(
                selectedTabIndex = currentSubTab.ordinal,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {},
                modifier = Modifier.fillMaxWidth()
            ) {
                AnalyzeSubTab.values().forEach { subTab ->
                    val isSelected = currentSubTab == subTab
                    Tab(
                        selected = isSelected,
                        onClick = {
                            if (!isSelected) {
                                SoundManager.play(SoundManager.SoundType.TAB)
                                viewModel.selectAnalyzeSubTab(subTab)
                            }
                        },
                        text = {
                            Text(
                                text = subTab.title,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }
        }

        // Active Analysis Tool: Connected Directly to Active SIP Plan
        Box(modifier = Modifier.weight(1f)) {
            Crossfade(targetState = currentSubTab, label = "analyze_subtab_crossfade") { tab ->
                when (tab) {
                    AnalyzeSubTab.COMPARE -> {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                PlanComparisonView(
                                    activeSipInput = state.sipInput,
                                    onApplyPlan = { newPlan ->
                                        viewModel.updateSipInvestment(newPlan.periodicInvestment)
                                        viewModel.updateSipDuration(newPlan.durationYears)
                                        viewModel.updateSipReturn(newPlan.expectedReturnAnnual)
                                        if (newPlan.enableStepUp) {
                                            viewModel.toggleStepUp(true)
                                        }
                                    }
                                )
                            }
                            item {
                                DisclaimerFooter()
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                    AnalyzeSubTab.WHAT_IF -> {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                WhatIfSimulatorView(baseInput = state.sipInput)
                            }
                            item {
                                DisclaimerFooter()
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                    AnalyzeSubTab.SCENARIOS -> {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                ScenarioView(baseInput = state.sipInput)
                            }
                            item {
                                DisclaimerFooter()
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                    AnalyzeSubTab.INFLATION -> {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                InflationImpactCard(state = state, viewModel = viewModel)
                            }
                            item {
                                DisclaimerFooter()
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                    AnalyzeSubTab.INSIGHTS -> {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                SipInsightsSection(
                                    input = state.sipInput,
                                    result = state.sipResult
                                )
                            }
                            item {
                                DisclaimerFooter()
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InflationImpactCard(
    state: WealthFlowUiState,
    viewModel: WealthFlowViewModel
) {
    val sip = state.sipInput
    val result = state.sipResult
    var testedInflation by remember(state.defaultInflation) { mutableStateOf(state.defaultInflation) }

    val purchasingPower = FinancialMathEngine.calculatePurchasingPower(
        futureAmount = result.finalCorpus,
        inflationRateAnnual = testedInflation,
        durationYears = sip.durationYears
    )

    val inflationMultiplier = (1.0 + testedInflation / 100.0).pow(sip.durationYears.toDouble())
    val erosionRate = if (inflationMultiplier > 0.0) (1.0 - (1.0 / inflationMultiplier)) * 100.0 else 0.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "INFLATION IMPACT ON YOUR ACTIVE SIP",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${String.format(java.util.Locale.US, "%.1f", erosionRate)}% Purchasing Power Erosion",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.error,
                fontSize = 22.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Over your ${sip.durationYears}-year horizon at ${testedInflation}% annual inflation, your estimated SIP corpus of ${CurrencyFormatter.formatInrCompact(result.finalCorpus)} has purchasing power equivalent to ~${CurrencyFormatter.formatInr(purchasingPower)} in today's money.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Real vs Nominal Corpus Comparison Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Estimated Nominal Corpus",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = CurrencyFormatter.formatInrCompact(result.finalCorpus),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Today's Purchasing Power",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = CurrencyFormatter.formatInrCompact(purchasingPower),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FinancialSliderInput(
                title = "Adjust Tested Inflation Rate",
                value = testedInflation,
                onValueChange = { testedInflation = it },
                range = 2f..14f,
                step = 0.5f,
                unitSuffix = "%",
                presets = listOf("5%" to 5.0, "6% (Avg India)" to 6.0, "7%" to 7.0, "8% (Education)" to 8.0)
            )
        }
    }
}
