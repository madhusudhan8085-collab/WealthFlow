package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.viewmodel.WealthFlowUiState
import com.example.ui.viewmodel.WealthFlowViewModel
import com.example.utils.CurrencyFormatter

@Composable
fun SipToSwpScreen(
    state: WealthFlowUiState,
    viewModel: WealthFlowViewModel,
    modifier: Modifier = Modifier
) {
    val input = state.sipToSwpInput
    val result = state.sipToSwpResult

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("sip_to_swp_screen_scroll"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Hero Overview Card
        item {
            val accumPurchasingPower = com.example.calculations.FinancialMathEngine.calculatePurchasingPower(
                futureAmount = result.accumulatedCorpus,
                inflationRateAnnual = state.defaultInflation,
                durationYears = input.sipDurationYears
            )
            val pensionPurchasingPower = com.example.calculations.FinancialMathEngine.calculatePurchasingPower(
                futureAmount = input.swpMonthlyWithdrawal,
                inflationRateAnnual = state.defaultInflation,
                durationYears = input.sipDurationYears
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sip_swp_lifecycle_card"),
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
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ACCUMULATION & RETIREMENT LIFECYCLE",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = if (result.swpSurvives) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                   else MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (result.swpSurvives) "Plan Fully Funded" else "Depletes Early",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (result.swpSurvives) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Prominent Dual Metric Hero: Accumulated Wealth & Monthly Retirement Pension
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("ACCUMULATED WEALTH", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = CurrencyFormatter.formatInrCompact(result.accumulatedCorpus),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text("At Yr ${input.sipDurationYears} (Today: ~${CurrencyFormatter.formatInrCompact(accumPurchasingPower)})", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.35f),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("MONTHLY PENSION", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = CurrencyFormatter.formatInrCompact(input.swpMonthlyWithdrawal) + "/mo",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text("Over ${input.swpDurationYears}Y (Today: ~${CurrencyFormatter.formatInrCompact(pensionPurchasingPower)}/mo)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Principal Invested", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(CurrencyFormatter.formatInr(result.accumulationInvested), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Remaining Legacy Corpus", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                CurrencyFormatter.formatInr(result.finalRemainingCorpus),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (result.swpSurvives) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        // Section: Phase 1 Inputs
        item {
            Text(
                text = "PHASE 1: ACCUMULATION (CAREER)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            FinancialSliderInput(
                title = "Monthly SIP During Career",
                value = input.sipInvestment,
                onValueChange = { viewModel.updateSipToSwpSip(it) },
                range = 5000f..150000f,
                step = 1000f,
                isCurrency = true,
                presets = listOf("₹10K" to 10000.0, "₹20K" to 20000.0, "₹35K" to 35000.0, "₹50K" to 50000.0)
            )
        }

        item {
            FinancialSliderInput(
                title = "Accumulation Duration",
                value = input.sipDurationYears.toDouble(),
                onValueChange = { viewModel.updateSipToSwpAccumulationYears(it.toInt()) },
                range = 5f..30f,
                step = 1f,
                unitSuffix = " Years",
                presets = listOf("10Y" to 10.0, "15Y" to 15.0, "20Y" to 20.0, "25Y" to 25.0)
            )
        }

        item {
            FinancialSliderInput(
                title = "Annual Step-Up During Career",
                value = input.sipStepUpPercent,
                onValueChange = { viewModel.updateSipToSwpStepUp(it) },
                range = 0f..25f,
                step = 1f,
                unitSuffix = "%",
                presets = listOf("0%" to 0.0, "5%" to 5.0, "10%" to 10.0, "15%" to 15.0)
            )
        }

        // Section: Phase 2 Inputs
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "PHASE 2: DISTRIBUTION (RETIREMENT)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            FinancialSliderInput(
                title = "Desired Monthly Pension / Withdrawal",
                value = input.swpMonthlyWithdrawal,
                onValueChange = { viewModel.updateSipToSwpWithdrawal(it) },
                range = 10000f..250000f,
                step = 5000f,
                isCurrency = true,
                presets = listOf("₹40K" to 40000.0, "₹60K" to 60000.0, "₹80K" to 80000.0, "₹1L" to 100000.0)
            )
        }

        item {
            FinancialSliderInput(
                title = "Retirement Duration",
                value = input.swpDurationYears.toDouble(),
                onValueChange = { viewModel.updateSipToSwpWithdrawalYears(it.toInt()) },
                range = 10f..35f,
                step = 1f,
                unitSuffix = " Years",
                presets = listOf("15Y" to 15.0, "20Y" to 20.0, "25Y" to 25.0, "30Y" to 30.0)
            )
        }

        // Section: Accumulation Chart
        item {
            Text(
                text = "Phase 1: Accumulation Growth",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            GrowthAreaChart(breakdown = result.accumulationBreakdown)
        }

        // Section: Withdrawal Depletion Chart
        item {
            Text(
                text = "Phase 2: Retirement Corpus Trajectory",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            SwpDepletionChart(breakdown = result.withdrawalBreakdown)
        }

        item {
            DisclaimerFooter()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
