package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StepUpType
import com.example.ui.components.*
import com.example.ui.viewmodel.WealthFlowUiState
import com.example.ui.viewmodel.WealthFlowViewModel
import com.example.utils.CurrencyFormatter
import com.example.utils.ExportUtils
import com.example.utils.SoundManager

@Composable
fun SwpTabScreen(
    state: WealthFlowUiState,
    viewModel: WealthFlowViewModel,
    modifier: Modifier = Modifier
) {
    val input = state.swpInput
    val result = state.swpResult
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("swp_screen_scroll"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section: Hero Result Card
        item {
            val isHealthy = result.survivesFullDuration
            val badge = if (isHealthy) "Corpus Survives" else "Depletes in ${result.depletionYear?.let { String.format(java.util.Locale.US, "%.1f Yrs", it) } ?: "Early"}"
            val purchasingPowerRem = com.example.calculations.FinancialMathEngine.calculatePurchasingPower(
                futureAmount = result.remainingCorpus,
                inflationRateAnnual = state.defaultInflation,
                durationYears = input.durationYears
            )
            MetricCard(
                title = "Total Amount Withdrawn",
                primaryAmount = result.totalWithdrawn,
                investedAmount = result.remainingCorpus,
                gainsAmount = result.totalGrowthEarned,
                investedLabel = "Remaining Corpus",
                gainsLabel = "Total Growth",
                badgeText = badge,
                isPositive = isHealthy,
                subBadge = "SWR: ${String.format(java.util.Locale.US, "%.1f%%", result.safeWithdrawalRatePercent)}",
                durationText = "${input.durationYears} Yrs Horizon | Initial: ${CurrencyFormatter.formatInrCompact(input.startingCorpus)}",
                returnText = "${input.expectedReturnAnnual}% p.a. Return",
                purchasingPowerToday = purchasingPowerRem,
                inflationRateAnnual = state.defaultInflation,
                testTag = "swp_metric_card"
            )
        }

        // Export PDF Report Action Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("swp_export_card"),
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
                                text = "Export SWP Pension PDF",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "A4 retirement cashflow & longevity schedule",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    FilledTonalButton(
                        onClick = {
                            SoundManager.play(SoundManager.SoundType.SUCCESS)
                            ExportUtils.shareSwpPdf(context, input, result)
                        },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("swp_export_pdf_button")
                    ) {
                        Text("Export", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Reverse SWP & Perpetual Sustainable Withdrawal Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AllInclusive,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = "SUSTAINABILITY & REVERSE SWP",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Max Safe Monthly Withdrawal",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "(Perpetual Zero-Depletion)",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = CurrencyFormatter.formatInr(state.maxSustainableMonthlyWithdrawal),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Required Starting Corpus",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "(For ${CurrencyFormatter.formatInr(input.periodicWithdrawal)}/mo)",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                val reqCorpus = com.example.calculations.FinancialMathEngine.calculateRequiredCorpusForSwp(
                                    monthlyWithdrawal = input.periodicWithdrawal,
                                    durationYears = input.durationYears,
                                    expectedReturnAnnual = input.expectedReturnAnnual,
                                    annualIncreasePercent = if (input.enableAnnualIncrease) input.annualWithdrawalIncreasePercent else 0.0
                                )
                                Text(
                                    text = CurrencyFormatter.formatInrCompact(reqCorpus),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Core Inputs
        item {
            Text(
                text = "WITHDRAWAL INPUTS",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        }

        // Starting Corpus Input
        item {
            FinancialSliderInput(
                title = "Total Starting Corpus",
                value = input.startingCorpus,
                onValueChange = { viewModel.updateSwpCorpus(it) },
                range = 500000f..50000000f,
                step = 100000f,
                isCurrency = true,
                presets = listOf(
                    "₹25L" to 2500000.0,
                    "₹50L" to 5000000.0,
                    "₹1Cr" to 10000000.0,
                    "₹2Cr" to 20000000.0,
                    "₹3Cr" to 30000000.0
                ),
                testTag = "swp_corpus_input"
            )
        }

        // Monthly Withdrawal Input
        item {
            FinancialSliderInput(
                title = "Monthly Withdrawal",
                value = input.periodicWithdrawal,
                onValueChange = { viewModel.updateSwpWithdrawal(it) },
                range = 5000f..300000f,
                step = 1000f,
                isCurrency = true,
                presets = listOf(
                    "₹25K" to 25000.0,
                    "₹40K" to 40000.0,
                    "₹60K" to 60000.0,
                    "₹80K" to 80000.0,
                    "₹1L" to 100000.0
                ),
                testTag = "swp_amount_input"
            )
        }

        // Withdrawal Duration Input
        item {
            FinancialSliderInput(
                title = "Withdrawal Duration",
                value = input.durationYears.toDouble(),
                onValueChange = { viewModel.updateSwpDuration(it.toInt()) },
                range = 1f..35f,
                step = 1f,
                unitSuffix = " Years",
                presets = listOf(
                    "10Y" to 10.0,
                    "15Y" to 15.0,
                    "20Y" to 20.0,
                    "25Y" to 25.0,
                    "30Y" to 30.0
                ),
                testTag = "swp_duration_input"
            )
        }

        // Expected Return Input
        item {
            FinancialSliderInput(
                title = "Expected Post-Retirement Return (p.a.)",
                value = input.expectedReturnAnnual,
                onValueChange = { viewModel.updateSwpReturn(it) },
                range = 0f..20f,
                step = 0.5f,
                unitSuffix = "%",
                presets = listOf(
                    "7% (Conservative)" to 7.0,
                    "8.5% (Balanced)" to 8.5,
                    "10% (Hybrid)" to 10.0,
                    "12% (Equity)" to 12.0
                ),
                testTag = "swp_return_input"
            )
        }

        // Annual Withdrawal Increase (Inflation Protection)
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
                                text = "Inflation-Adjusted Withdrawals",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Increase withdrawals annually to maintain purchasing power",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = input.enableAnnualIncrease,
                            onCheckedChange = { 
                                com.example.utils.SoundManager.play(if (it) com.example.utils.SoundManager.SoundType.TOGGLE_ON else com.example.utils.SoundManager.SoundType.TOGGLE_OFF)
                                viewModel.toggleSwpAnnualIncrease(it) 
                            }
                        )
                    }

                    if (input.enableAnnualIncrease) {
                        Spacer(modifier = Modifier.height(10.dp))
                        FinancialSliderInput(
                            title = "Annual Increase Rate",
                            value = input.annualWithdrawalIncreasePercent,
                            onValueChange = { viewModel.updateSwpAnnualIncreasePercent(it) },
                            range = 1f..15f,
                            step = 0.5f,
                            unitSuffix = "%",
                            presets = listOf("4%" to 4.0, "5%" to 5.0, "6%" to 6.0, "7%" to 7.0)
                        )
                    }
                }
            }
        }

        // Section: Trajectory Chart
        item {
            SwpDepletionChart(breakdown = result.yearlyBreakdown)
        }

        // Section: Insights
        item {
            SwpInsightsSection(input = input, result = result)
        }

        // Section: Schedule Table
        item {
            SwpYearlyBreakdownTable(input = input, result = result)
        }

        item {
            DisclaimerFooter()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
