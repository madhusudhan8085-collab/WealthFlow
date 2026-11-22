package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.PriceCheck
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DisclaimerFooter
import com.example.ui.components.FinancialSliderInput
import com.example.ui.viewmodel.WealthFlowUiState
import com.example.ui.viewmodel.WealthFlowViewModel
import com.example.utils.CurrencyFormatter

@Composable
fun GoalTabScreen(
    state: WealthFlowUiState,
    viewModel: WealthFlowViewModel,
    modifier: Modifier = Modifier
) {
    val input = state.goalInput
    val result = state.goalResult

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("goal_screen_scroll"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Hero Inflation & Future Target Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("goal_hero_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Header: Goal Title and Inflation Badge (Guaranteed Single-Line, No Wrap)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "INFLATION-ADJUSTED GOAL TARGET",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.1.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${input.inflationRateAnnual}% Inflation",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 1. TOP: Today's Original Goal / Present Purchasing Power
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "TODAY'S ORIGINAL GOAL (PURCHASING POWER)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = CurrencyFormatter.formatInr(input.targetAmountToday),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Present Cost",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. MAIN RESULT: Future Nominal Amount Required
                    Text(
                        text = "FUTURE REQUIRED CORPUS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = CurrencyFormatter.formatInr(result.futureTargetAmount),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 32.sp
                    )
                    Text(
                        text = "Required in ${input.yearsUntilGoal} years to match today's ${CurrencyFormatter.formatInrCompact(input.targetAmountToday)} purchasing power",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(8.dp))

                    // 3. BOTTOM: Equivalent Purchasing Power Explanation
                    val purchasingPowerOf100 = 100.0 / result.inflationMultiplier
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${CurrencyFormatter.formatInrCompact(result.futureTargetAmount)} in ${input.yearsUntilGoal}Y has equal purchasing power to ${CurrencyFormatter.formatInrCompact(input.targetAmountToday)} today. (₹100 today shrinks to ₹${String.format(java.util.Locale.US, "%.1f", purchasingPowerOf100)} at ${input.inflationRateAnnual}% inflation).",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // Section: Required SIP Cards: Flat vs Step-Up
        item {
            Text(
                text = "HOW MUCH TO INVEST EVERY MONTH",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Flat SIP Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Flat Monthly SIP",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = CurrencyFormatter.formatInr(result.requiredFlatMonthlySip),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 19.sp
                        )
                        Text(
                            text = "Fixed every month",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.5.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Invested: ${CurrencyFormatter.formatInrCompact(result.totalInvestedFlat)}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Gains: ${CurrencyFormatter.formatInrCompact(result.expectedGainsFlat)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Step-Up SIP Card (Recommended)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Step-Up SIP",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = "STEP-UP",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    maxLines = 1,
                                    softWrap = false,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = CurrencyFormatter.formatInr(result.requiredStepUpStartingSip),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 19.sp
                        )
                        Text(
                            text = "+${input.stepUpPercent.toInt()}% annual step-up",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.5.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Invested: ${CurrencyFormatter.formatInrCompact(result.totalInvestedStepUp)}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Gains: ${CurrencyFormatter.formatInrCompact(result.expectedGainsStepUp)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Section: Inputs
        item {
            Text(
                text = "GOAL & INFLATION PARAMETERS",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        }

        item {
            FinancialSliderInput(
                title = "Target Amount Today",
                value = input.targetAmountToday,
                onValueChange = { viewModel.updateGoalTargetToday(it) },
                range = 500000f..50000000f,
                step = 250000f,
                isCurrency = true,
                presets = listOf(
                    "₹25L" to 2500000.0,
                    "₹50L" to 5000000.0,
                    "₹1Cr" to 10000000.0,
                    "₹2Cr" to 20000000.0
                )
            )
        }

        item {
            FinancialSliderInput(
                title = "Time Horizon to Reach Goal",
                value = input.yearsUntilGoal.toDouble(),
                onValueChange = { viewModel.updateGoalYears(it.toInt()) },
                range = 1f..30f,
                step = 1f,
                unitSuffix = " Years",
                presets = listOf("5Y" to 5.0, "10Y" to 10.0, "15Y" to 15.0, "20Y" to 20.0, "25Y" to 25.0)
            )
        }

        item {
            FinancialSliderInput(
                title = "Expected Annual Inflation Rate",
                value = input.inflationRateAnnual,
                onValueChange = { viewModel.updateGoalInflation(it) },
                range = 2f..12f,
                step = 0.5f,
                unitSuffix = "%",
                presets = listOf("5%" to 5.0, "6% (Avg India)" to 6.0, "7%" to 7.0, "8% (Education)" to 8.0)
            )
        }

        item {
            FinancialSliderInput(
                title = "Expected Investment Return (p.a.)",
                value = input.expectedReturnAnnual,
                onValueChange = { viewModel.updateGoalReturn(it) },
                range = 4f..20f,
                step = 0.5f,
                unitSuffix = "%",
                presets = listOf("10%" to 10.0, "12% (Nifty 50)" to 12.0, "14%" to 14.0)
            )
        }

        item {
            FinancialSliderInput(
                title = "Annual Step-Up Rate",
                value = input.stepUpPercent,
                onValueChange = { viewModel.updateGoalStepUp(it) },
                range = 0f..25f,
                step = 1f,
                unitSuffix = "%",
                presets = listOf("5%" to 5.0, "10%" to 10.0, "15%" to 15.0)
            )
        }

        item {
            DisclaimerFooter()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
