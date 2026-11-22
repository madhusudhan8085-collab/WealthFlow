package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculations.FinancialMathEngine
import com.example.data.model.SipInput
import com.example.utils.CurrencyFormatter
import com.example.utils.SoundManager

@Composable
fun PlanComparisonView(
    activeSipInput: SipInput,
    onApplyPlan: ((SipInput) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Plan A is dynamically bound to the user's actual active SIP plan
    val sipA = activeSipInput.periodicInvestment
    val durationA = activeSipInput.durationYears
    val returnA = activeSipInput.expectedReturnAnnual
    val stepUpA = if (activeSipInput.enableStepUp) activeSipInput.stepUpValue else 0.0

    // Plan B defaults to an optimized comparison (+10% step-up or +20% SIP)
    var sipB by remember(activeSipInput) { mutableStateOf(sipA) }
    var durationB by remember(activeSipInput) { mutableStateOf(durationA) }
    var returnB by remember(activeSipInput) { mutableStateOf(returnA) }
    var stepUpB by remember(activeSipInput) { 
        mutableStateOf(if (stepUpA > 0.0) (stepUpA + 5.0).coerceAtMost(25.0) else 10.0) 
    }

    val resultA = remember(activeSipInput) {
        FinancialMathEngine.calculateSIP(activeSipInput)
    }

    val planBInput = remember(sipB, durationB, returnB, stepUpB) {
        SipInput(
            periodicInvestment = sipB,
            durationYears = durationB,
            expectedReturnAnnual = returnB,
            enableStepUp = stepUpB > 0.0,
            stepUpValue = stepUpB
        )
    }

    val resultB = remember(planBInput) {
        FinancialMathEngine.calculateSIP(planBInput)
    }

    val diffCorpus = resultB.finalCorpus - resultA.finalCorpus
    val diffInvested = resultB.totalInvested - resultA.totalInvested
    val diffGains = resultB.totalGains - resultA.totalGains

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("plan_comparison_card"),
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
                    imageVector = Icons.Default.CompareArrows,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "SIP PLAN COMPARISON",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = "Compare your active SIP plan against alternative strategies side-by-side.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Strategy Adjustment Chips for Plan B
            Text(
                text = "TEST ALTERNATIVE STRATEGIES ON PLAN B:",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = stepUpB == 10.0 && sipB == sipA,
                    onClick = {
                        SoundManager.play(SoundManager.SoundType.POP)
                        sipB = sipA
                        durationB = durationA
                        returnB = returnA
                        stepUpB = 10.0
                    },
                    label = { Text("+10% Step-Up", fontSize = 10.sp) },
                    shape = RoundedCornerShape(8.dp)
                )

                FilterChip(
                    selected = sipB == (sipA + 5000.0) && durationB == durationA,
                    onClick = {
                        SoundManager.play(SoundManager.SoundType.POP)
                        sipB = sipA + 5000.0
                        durationB = durationA
                        returnB = returnA
                        stepUpB = stepUpA
                    },
                    label = { Text("+₹5K/mo", fontSize = 10.sp) },
                    shape = RoundedCornerShape(8.dp)
                )

                FilterChip(
                    selected = durationB == (durationA + 5),
                    onClick = {
                        SoundManager.play(SoundManager.SoundType.POP)
                        sipB = sipA
                        durationB = durationA + 5
                        returnB = returnA
                        stepUpB = stepUpA
                    },
                    label = { Text("+5 Years", fontSize = 10.sp) },
                    shape = RoundedCornerShape(8.dp)
                )

                IconButton(
                    onClick = {
                        SoundManager.play(SoundManager.SoundType.POP)
                        sipB = sipA
                        durationB = durationA
                        returnB = returnA
                        stepUpB = stepUpA
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset to Active Plan",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Two Plans Side-by-Side: Plan A (Active) vs Plan B (Alternative)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Plan A (User's Active Plan)
                PlanColumn(
                    planName = "PLAN A",
                    subBadge = "ACTIVE SIP",
                    sip = sipA,
                    duration = durationA,
                    returnRate = returnA,
                    stepUp = stepUpA,
                    corpus = resultA.finalCorpus,
                    invested = resultA.totalInvested,
                    gains = resultA.totalGains,
                    isWinner = resultA.finalCorpus >= resultB.finalCorpus,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f)
                )

                // Plan B (Alternative)
                PlanColumn(
                    planName = "PLAN B",
                    subBadge = "ALTERNATIVE",
                    sip = sipB,
                    duration = durationB,
                    returnRate = returnB,
                    stepUp = stepUpB,
                    corpus = resultB.finalCorpus,
                    invested = resultB.totalInvested,
                    gains = resultB.totalGains,
                    isWinner = resultB.finalCorpus > resultA.finalCorpus,
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Difference Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (diffCorpus >= 0) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                       else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Text(
                        text = "PLAN DELTA (ALTERNATIVE VS YOUR ACTIVE SIP)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${if (diffCorpus >= 0) "+" else ""}${CurrencyFormatter.formatInr(diffCorpus)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (diffCorpus >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        fontSize = 22.sp
                    )
                    Text(
                        text = "Extra Invested: ${if (diffInvested >= 0) "+" else ""}${CurrencyFormatter.formatInrCompact(diffInvested)} | Extra Gains: ${if (diffGains >= 0) "+" else ""}${CurrencyFormatter.formatInrCompact(diffGains)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.5.sp
                    )

                    if (onApplyPlan != null && diffCorpus > 0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        FilledTonalButton(
                            onClick = {
                                SoundManager.play(SoundManager.SoundType.SUCCESS)
                                onApplyPlan(planBInput)
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Apply Plan B to SIP Calculator", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sliders to fine-tune Plan B
            Text(
                text = "FINE-TUNE PLAN B PARAMETERS:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            FinancialSliderInput(
                title = "Plan B Monthly SIP",
                value = sipB,
                onValueChange = { sipB = it },
                range = 1000f..150000f,
                step = 1000f,
                isCurrency = true,
                presets = listOf("₹10K" to 10000.0, "₹20K" to 20000.0, "₹35K" to 35000.0, "₹50K" to 50000.0)
            )

            Spacer(modifier = Modifier.height(8.dp))

            FinancialSliderInput(
                title = "Plan B Duration",
                value = durationB.toDouble(),
                onValueChange = { durationB = it.toInt() },
                range = 1f..35f,
                step = 1f,
                unitSuffix = " Years",
                presets = listOf("10Y" to 10.0, "15Y" to 15.0, "20Y" to 20.0, "25Y" to 25.0)
            )

            Spacer(modifier = Modifier.height(8.dp))

            FinancialSliderInput(
                title = "Plan B Annual Step-Up",
                value = stepUpB,
                onValueChange = { stepUpB = it },
                range = 0f..25f,
                step = 1f,
                unitSuffix = "%",
                presets = listOf("0%" to 0.0, "5%" to 5.0, "10%" to 10.0, "15%" to 15.0)
            )
        }
    }
}

@Composable
private fun PlanColumn(
    planName: String,
    subBadge: String,
    sip: Double,
    duration: Int,
    returnRate: Double,
    stepUp: Double,
    corpus: Double,
    invested: Double,
    gains: Double,
    isWinner: Boolean,
    containerColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = if (isWinner) CardDefaults.outlinedCardBorder() else null,
        modifier = modifier
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
                    text = planName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isWinner) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                           else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = subBadge,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isWinner) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = CurrencyFormatter.formatInrCompact(corpus),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 19.sp
            )
            Text(
                text = "Gains: ${CurrencyFormatter.formatInrCompact(gains)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(6.dp))

            Text("SIP: ${CurrencyFormatter.formatInr(sip)}", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp)
            Text("Horizon: $duration Yrs", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp)
            Text("Return: ${returnRate.toInt()}% p.a.", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp)
            Text("Step-Up: ${if (stepUp > 0) "${stepUp.toInt()}%" else "None"}", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp)
            Text("Invested: ${CurrencyFormatter.formatInrCompact(invested)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
        }
    }
}
