package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculations.FinancialMathEngine
import com.example.data.model.SipInput
import com.example.utils.CurrencyFormatter

@Composable
fun WhatIfSimulatorView(
    baseInput: SipInput,
    modifier: Modifier = Modifier
) {
    var deltaSip by remember { mutableStateOf(5000.0) }
    var deltaYears by remember { mutableStateOf(3) }
    var deltaReturn by remember { mutableStateOf(1.0) }

    val deltaResult = remember(baseInput, deltaSip, deltaYears, deltaReturn) {
        FinancialMathEngine.calculateWhatIf(
            baseInput = baseInput,
            deltaMonthlySip = deltaSip,
            deltaYears = deltaYears,
            deltaReturnAnnual = deltaReturn
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("what_if_simulator_card"),
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "WHAT-IF SIMULATOR",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = "Small adjustments create massive compounding differences over time.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Active SIP Base Plan Summary
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
                            text = "YOUR ACTIVE SIP BASE PLAN",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "${CurrencyFormatter.formatInr(baseInput.periodicInvestment)}/mo · ${baseInput.durationYears}Y · ${baseInput.expectedReturnAnnual}% p.a.",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = "Corpus: ${CurrencyFormatter.formatInrCompact(deltaResult.baseCorpus)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // What-If Toggles & Sliders
            WhatIfSliderRow(
                label = "Boost Monthly SIP",
                currentText = "+${CurrencyFormatter.formatInr(deltaSip)}",
                value = deltaSip.toFloat(),
                range = 0f..25000f,
                step = 1000f,
                onValueChange = { deltaSip = it.toDouble() }
            )

            Spacer(modifier = Modifier.height(10.dp))

            WhatIfSliderRow(
                label = "Extend Time Horizon",
                currentText = "+$deltaYears Years",
                value = deltaYears.toFloat(),
                range = 0f..10f,
                step = 1f,
                onValueChange = { deltaYears = it.toInt() }
            )

            Spacer(modifier = Modifier.height(10.dp))

            WhatIfSliderRow(
                label = "Alpha Return Boost",
                currentText = "+${String.format(java.util.Locale.US, "%.1f", deltaReturn)}%",
                value = deltaReturn.toFloat(),
                range = -3f..5f,
                step = 0.5f,
                onValueChange = { deltaReturn = it.toDouble() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Result Delta Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Text(
                        text = "NET IMPACT ON YOUR WEALTH",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "+${CurrencyFormatter.formatInr(deltaResult.differenceCorpus)}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "New Corpus: ${CurrencyFormatter.formatInrCompact(deltaResult.newCorpus)} (was ${CurrencyFormatter.formatInrCompact(deltaResult.baseCorpus)})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Extra Invested: +${CurrencyFormatter.formatInrCompact(deltaResult.differenceInvested)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Extra Gains: +${CurrencyFormatter.formatInrCompact(deltaResult.differenceGains)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WhatIfSliderRow(
    label: String,
    currentText: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    step: Float,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = currentText,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            steps = ((range.endInclusive - range.start) / step).toInt() - 1,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.height(28.dp)
        )
    }
}
