package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Frequency
import com.example.data.model.StepUpType
import com.example.ui.components.*
import com.example.ui.viewmodel.WealthFlowUiState
import com.example.ui.viewmodel.WealthFlowViewModel
import com.example.utils.CurrencyFormatter
import com.example.utils.ExportUtils
import com.example.utils.SoundManager

@Composable
fun SipTabScreen(
    state: WealthFlowUiState,
    viewModel: WealthFlowViewModel,
    modifier: Modifier = Modifier
) {
    val input = state.sipInput
    val result = state.sipResult
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("sip_screen_scroll"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section: Hero Result Card
        item {
            val purchasingPower = com.example.calculations.FinancialMathEngine.calculatePurchasingPower(
                futureAmount = result.finalCorpus,
                inflationRateAnnual = state.defaultInflation,
                durationYears = input.durationYears
            )
            MetricCard(
                title = "Estimated Future Corpus",
                primaryAmount = result.finalCorpus,
                investedAmount = result.totalInvested,
                gainsAmount = result.totalGains,
                badgeText = "+${String.format(java.util.Locale.US, "%.0f", result.gainsPercentage)}% Returns",
                subBadge = "${String.format(java.util.Locale.US, "%.1fx", result.growthMultiplier)} Multiple",
                durationText = "${input.durationYears} Years Horizon",
                returnText = "${input.expectedReturnAnnual}% p.a. Return",
                purchasingPowerToday = purchasingPower,
                inflationRateAnnual = state.defaultInflation,
                testTag = "sip_metric_card"
            )
        }

        // Section: Visual Breakdown (Invested vs Estimated Gains)
        item {
            DonutBreakdownChart(
                investedAmount = result.totalInvested,
                gainsAmount = result.totalGains
            )
        }

        // Export PDF Report Action Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sip_export_card"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Export Offline PDF Report",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "A4 formatted report with schedule & summary",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    FilledTonalButton(
                        onClick = {
                            SoundManager.play(SoundManager.SoundType.SUCCESS)
                            ExportUtils.shareSipPdf(context, input, result)
                        },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("sip_export_pdf_button")
                    ) {
                        Text("Export", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Section: Core Inputs
        item {
            Text(
                text = "INVESTMENT INPUTS",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        }

        // Monthly Investment Input
        item {
            FinancialSliderInput(
                title = "Monthly Investment",
                value = input.periodicInvestment,
                onValueChange = { viewModel.updateSipInvestment(it) },
                range = 500f..200000f,
                step = 500f,
                isCurrency = true,
                presets = listOf(
                    "₹5K" to 5000.0,
                    "₹10K" to 10000.0,
                    "₹25K" to 25000.0,
                    "₹50K" to 50000.0,
                    "₹1L" to 100000.0
                ),
                testTag = "sip_amount_input"
            )
        }

        // Investment Duration Input
        item {
            FinancialSliderInput(
                title = "Investment Horizon",
                value = input.durationYears.toDouble(),
                onValueChange = { viewModel.updateSipDuration(it.toInt()) },
                range = 1f..35f,
                step = 1f,
                unitSuffix = " Years",
                presets = listOf(
                    "5Y" to 5.0,
                    "10Y" to 10.0,
                    "15Y" to 15.0,
                    "20Y" to 20.0,
                    "25Y" to 25.0,
                    "30Y" to 30.0
                ),
                testTag = "sip_duration_input"
            )
        }

        // Expected Return Input
        item {
            FinancialSliderInput(
                title = "Expected Annual Return (p.a.)",
                value = input.expectedReturnAnnual,
                onValueChange = { viewModel.updateSipReturn(it) },
                range = 0f..25f,
                step = 0.5f,
                unitSuffix = "%",
                presets = listOf(
                    "8% (Debt)" to 8.0,
                    "10% (Index)" to 10.0,
                    "12% (Equity)" to 12.0,
                    "15% (Growth)" to 15.0
                ),
                testTag = "sip_return_input"
            )
        }

        // Step-Up SIP Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Annual Step-Up SIP",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Increase contributions annually as income rises",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = input.enableStepUp,
                            onCheckedChange = { 
                                com.example.utils.SoundManager.play(if (it) com.example.utils.SoundManager.SoundType.TOGGLE_ON else com.example.utils.SoundManager.SoundType.TOGGLE_OFF)
                                viewModel.toggleStepUp(it) 
                            }
                        )
                    }

                    if (input.enableStepUp) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = input.stepUpType == StepUpType.PERCENTAGE,
                                onClick = { 
                                    com.example.utils.SoundManager.play(com.example.utils.SoundManager.SoundType.POP)
                                    viewModel.updateStepUpType(StepUpType.PERCENTAGE) 
                                },
                                label = { Text("Percentage (%)") }
                            )
                            FilterChip(
                                selected = input.stepUpType == StepUpType.FIXED_AMOUNT,
                                onClick = { 
                                    com.example.utils.SoundManager.play(com.example.utils.SoundManager.SoundType.POP)
                                    viewModel.updateStepUpType(StepUpType.FIXED_AMOUNT) 
                                },
                                label = { Text("Fixed Amount (₹)") }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (input.stepUpType == StepUpType.PERCENTAGE) {
                            FinancialSliderInput(
                                title = "Annual Increase",
                                value = input.stepUpValue,
                                onValueChange = { viewModel.updateStepUpValue(it) },
                                range = 1f..30f,
                                step = 1f,
                                unitSuffix = "%",
                                presets = listOf("5%" to 5.0, "10%" to 10.0, "15%" to 15.0)
                            )
                        } else {
                            FinancialSliderInput(
                                title = "Annual Fixed Increase",
                                value = input.stepUpValue,
                                onValueChange = { viewModel.updateStepUpValue(it) },
                                range = 500f..25000f,
                                step = 500f,
                                isCurrency = true,
                                presets = listOf("₹1K" to 1000.0, "₹2.5K" to 2500.0, "₹5K" to 5000.0)
                            )
                        }
                    }
                }
            }
        }

        // Initial Lumpsum (Optional)
        item {
            FinancialSliderInput(
                title = "Initial Lumpsum Investment (Optional)",
                value = input.initialLumpsum,
                onValueChange = { viewModel.updateInitialLumpsum(it) },
                range = 0f..1000000f,
                step = 10000f,
                isCurrency = true,
                presets = listOf(
                    "₹0" to 0.0,
                    "₹50K" to 50000.0,
                    "₹1L" to 100000.0,
                    "₹2L" to 200000.0
                )
            )
        }

        // Section: Wealth Growth Trajectory Chart
        item {
            GrowthAreaChart(breakdown = result.yearlyBreakdown)
        }

        // Section: Return Scenarios (Conservative, Base, Optimistic)
        item {
            ScenarioView(baseInput = input)
        }

        // Section: What-If Simulator
        item {
            WhatIfSimulatorView(baseInput = input)
        }

        // Section: Financial Insights
        item {
            SipInsightsSection(input = input, result = result)
        }

        // Section: Detailed Year-by-Year Table
        item {
            SipYearlyBreakdownTable(input = input, result = result)
        }

        // Disclaimer
        item {
            DisclaimerFooter()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
