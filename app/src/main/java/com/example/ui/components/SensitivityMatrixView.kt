package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculations.FinancialMathEngine
import com.example.utils.CurrencyFormatter

@Composable
fun SensitivityMatrixView(
    monthlySip: Double,
    stepUpPercent: Double,
    currentReturn: Double,
    currentDuration: Int,
    modifier: Modifier = Modifier
) {
    val returnRates = listOf(8.0, 10.0, 12.0, 14.0, 16.0)
    val durations = listOf(5, 10, 15, 20, 25)

    val matrixPoints = remember(monthlySip, stepUpPercent) {
        FinancialMathEngine.calculateSensitivityMatrix(
            periodicAmount = monthlySip,
            stepUpPercent = stepUpPercent,
            returnRates = returnRates,
            durations = durations
        )
    }

    var selectedCell by remember { mutableStateOf<Pair<Double, Int>?>(null) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sensitivity_matrix_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "SENSITIVITY ANALYSIS (RETURN VS DURATION)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Observe how compounding accelerates over time and rates for ${CurrencyFormatter.formatInr(monthlySip)}/mo SIP.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
            ) {
                // Table Column Headers (Durations)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Rate \\ Yr",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 10.sp
                        )
                    }

                    durations.forEach { d ->
                        val isCurrentD = d == currentDuration
                        Box(
                            modifier = Modifier
                                .width(78.dp)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isCurrentD) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${d}Y",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isCurrentD) FontWeight.ExtraBold else FontWeight.Bold,
                                color = if (isCurrentD) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Table Rows
                returnRates.forEach { rate ->
                    val isCurrentR = kotlin.math.abs(rate - currentReturn) < 0.5
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Rate Label
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isCurrentR) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${rate.toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isCurrentR) FontWeight.ExtraBold else FontWeight.Bold,
                                color = if (isCurrentR) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Matrix Cells
                        durations.forEach { d ->
                            val pt = matrixPoints.find { it.returnRate == rate && it.durationYears == d }
                            val corpus = pt?.finalCorpus ?: 0.0
                            val isMatchCurrent = isCurrentR && d == currentDuration
                            val isSelected = selectedCell?.first == rate && selectedCell?.second == d

                            val cellBg = when {
                                isSelected -> MaterialTheme.colorScheme.primary
                                isMatchCurrent -> MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                                corpus >= 10_000_000.0 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            }

                            val textColor = when {
                                isSelected -> MaterialTheme.colorScheme.onPrimary
                                isMatchCurrent -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSurface
                            }

                            Box(
                                modifier = Modifier
                                    .width(78.dp)
                                    .height(38.dp)
                                    .padding(2.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(cellBg)
                                    .clickable { 
                                        com.example.utils.SoundManager.play(com.example.utils.SoundManager.SoundType.POP)
                                        selectedCell = Pair(rate, d) 
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = CurrencyFormatter.formatInrCompact(corpus),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isMatchCurrent || isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp,
                                    color = textColor
                                )
                            }
                        }
                    }
                }
            }

            if (selectedCell != null) {
                Spacer(modifier = Modifier.height(10.dp))
                val selRate = selectedCell!!.first
                val selYears = selectedCell!!.second
                val selCorpus = matrixPoints.find { it.returnRate == selRate && it.durationYears == selYears }?.finalCorpus ?: 0.0
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "Selected: ${selRate.toInt()}% over $selYears Years yields ${CurrencyFormatter.formatInr(selCorpus)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
