package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SwpYearlyRecord
import com.example.data.model.YearlyRecord
import com.example.ui.theme.ChartCorpusColor
import com.example.ui.theme.ChartDepletedColor
import com.example.ui.theme.ChartGainsColor
import com.example.ui.theme.ChartInvestedColor
import com.example.utils.CurrencyFormatter
import kotlin.math.max
import kotlin.math.min

/**
 * Donut chart showing Invested Amount vs Estimated Wealth Gains
 */
@Composable
fun DonutBreakdownChart(
    investedAmount: Double,
    gainsAmount: Double,
    modifier: Modifier = Modifier
) {
    val total = max(1.0, investedAmount + gainsAmount)
    val investedPercent = ((investedAmount / total) * 100).toFloat()
    val gainsPercent = ((gainsAmount / total) * 100).toFloat()

    val investedSweep by animateFloatAsState(
        targetValue = (investedPercent / 100f) * 360f,
        animationSpec = tween(600),
        label = "investedSweep"
    )
    val gainsSweep by animateFloatAsState(
        targetValue = (gainsPercent / 100f) * 360f,
        animationSpec = tween(600),
        label = "gainsSweep"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ASSET ALLOCATION BREAKDOWN",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(190.dp)
            ) {
                Canvas(modifier = Modifier.size(170.dp)) {
                    val strokeWidth = 28.dp.toPx()
                    val arcSize = size.width - strokeWidth
                    val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

                    // Draw Invested Arc
                    drawArc(
                        color = ChartInvestedColor,
                        startAngle = -90f,
                        sweepAngle = investedSweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = Size(arcSize, arcSize),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Draw Gains Arc
                    drawArc(
                        color = ChartGainsColor,
                        startAngle = -90f + investedSweep,
                        sweepAngle = gainsSweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = Size(arcSize, arcSize),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                // Center Content
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Total Value",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = CurrencyFormatter.formatInrCompact(total),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Legends
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(
                    color = ChartInvestedColor,
                    label = "Invested",
                    amount = investedAmount,
                    percent = investedPercent
                )
                LegendItem(
                    color = ChartGainsColor,
                    label = "Gains",
                    amount = gainsAmount,
                    percent = gainsPercent
                )
            }
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    amount: Double,
    percent: Float
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Column {
            Text(
                text = "$label (${String.format(java.util.Locale.US, "%.1f", percent)}%)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = CurrencyFormatter.formatInrCompact(amount),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Interactive Timeline Area Chart with touch-scrubbing tooltip
 */
@Composable
fun GrowthAreaChart(
    breakdown: List<YearlyRecord>,
    modifier: Modifier = Modifier
) {
    if (breakdown.isEmpty()) return

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val maxVal = remember(breakdown) {
        breakdown.maxOfOrNull { max(it.closingBalance, it.cumulativeInvested) }?.coerceAtLeast(1000.0) ?: 1000.0
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("growth_area_chart"),
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
                    text = "PORTFOLIO GROWTH TRAJECTORY",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Touch to inspect",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tooltip banner if scrubbed
            val activeRecord = selectedIndex?.let { breakdown.getOrNull(it) } ?: breakdown.lastOrNull()
            if (activeRecord != null) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Year ${activeRecord.year}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Invested: ${CurrencyFormatter.formatInrCompact(activeRecord.cumulativeInvested)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = ChartInvestedColor,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Corpus: ${CurrencyFormatter.formatInrCompact(activeRecord.closingBalance)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = ChartGainsColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chart Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .pointerInput(breakdown) {
                        detectTapGestures(
                            onPress = { offset ->
                                val count = breakdown.size
                                val stepX = size.width / (count - 1).coerceAtLeast(1)
                                val idx = (offset.x / stepX).toInt().coerceIn(0, count - 1)
                                if (selectedIndex != idx) {
                                    com.example.utils.SoundManager.play(com.example.utils.SoundManager.SoundType.TICK)
                                }
                                selectedIndex = idx
                            }
                        )
                    }
                    .pointerInput(breakdown) {
                        detectHorizontalDragGestures(
                            onDragStart = { offset ->
                                val count = breakdown.size
                                val stepX = size.width / (count - 1).coerceAtLeast(1)
                                val idx = (offset.x / stepX).toInt().coerceIn(0, count - 1)
                                selectedIndex = idx
                            },
                            onHorizontalDrag = { change, _ ->
                                val count = breakdown.size
                                val stepX = size.width / (count - 1).coerceAtLeast(1)
                                val idx = (change.position.x / stepX).toInt().coerceIn(0, count - 1)
                                if (selectedIndex != idx) {
                                    com.example.utils.SoundManager.play(com.example.utils.SoundManager.SoundType.TICK)
                                }
                                selectedIndex = idx
                            },
                            onDragEnd = { selectedIndex = null },
                            onDragCancel = { selectedIndex = null }
                        )
                    }
            ) {
                val width = size.width
                val height = size.height
                val count = breakdown.size
                val stepX = width / (count - 1).coerceAtLeast(1)

                // Background horizontal guide lines
                val gridLines = 4
                for (g in 0..gridLines) {
                    val y = height * (g.toFloat() / gridLines)
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.15f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Path for Invested Line
                val investedPath = Path()
                // Path for Corpus Growth Line
                val corpusPath = Path()
                val corpusAreaPath = Path()

                breakdown.forEachIndexed { i, rec ->
                    val x = i * stepX
                    val yInvested = height - (rec.cumulativeInvested / maxVal).toFloat() * height
                    val yCorpus = height - (rec.closingBalance / maxVal).toFloat() * height

                    if (i == 0) {
                        investedPath.moveTo(x, yInvested)
                        corpusPath.moveTo(x, yCorpus)
                        corpusAreaPath.moveTo(x, height)
                        corpusAreaPath.lineTo(x, yCorpus)
                    } else {
                        investedPath.lineTo(x, yInvested)
                        corpusPath.lineTo(x, yCorpus)
                        corpusAreaPath.lineTo(x, yCorpus)
                    }

                    if (i == breakdown.lastIndex) {
                        corpusAreaPath.lineTo(x, height)
                        corpusAreaPath.close()
                    }
                }

                // Draw Area Gradient for Corpus
                drawPath(
                    path = corpusAreaPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(ChartGainsColor.copy(alpha = 0.35f), ChartGainsColor.copy(alpha = 0.02f)),
                        startY = 0f,
                        endY = height
                    )
                )

                // Draw Invested Line (dashed/solid)
                drawPath(
                    path = investedPath,
                    color = ChartInvestedColor,
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw Corpus Line
                drawPath(
                    path = corpusPath,
                    color = ChartGainsColor,
                    style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw touch-scrub indicator
                val activeIdx = selectedIndex ?: (breakdown.size - 1)
                val scrubX = activeIdx * stepX
                val activeRec = breakdown[activeIdx]
                val scrubYCorpus = height - (activeRec.closingBalance / maxVal).toFloat() * height
                val scrubYInvested = height - (activeRec.cumulativeInvested / maxVal).toFloat() * height

                drawLine(
                    color = Color.White.copy(alpha = 0.6f),
                    start = Offset(scrubX, 0f),
                    end = Offset(scrubX, height),
                    strokeWidth = 1.5.dp.toPx()
                )

                drawCircle(color = ChartInvestedColor, radius = 5.dp.toPx(), center = Offset(scrubX, scrubYInvested))
                drawCircle(color = ChartGainsColor, radius = 6.dp.toPx(), center = Offset(scrubX, scrubYCorpus))
                drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = Offset(scrubX, scrubYCorpus))
            }
        }
    }
}

/**
 * Chart showing SWP Corpus Depletion or Longevity trajectory
 */
@Composable
fun SwpDepletionChart(
    breakdown: List<SwpYearlyRecord>,
    modifier: Modifier = Modifier
) {
    if (breakdown.isEmpty()) return

    val maxCorpus = remember(breakdown) {
        breakdown.maxOfOrNull { it.closingCorpus.coerceAtLeast(it.openingCorpus) }?.coerceAtLeast(1000.0) ?: 1000.0
    }

    Card(
        modifier = modifier.fillMaxWidth(),
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "REMAINING CORPUS TRAJECTORY",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                val finalRecord = breakdown.lastOrNull()
                val isHealthy = finalRecord != null && !finalRecord.isDepleted && finalRecord.closingCorpus > 0
                Text(
                    text = if (isHealthy) "Sustainable" else "Depleting",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isHealthy) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                val width = size.width
                val height = size.height
                val count = breakdown.size
                val stepX = width / (count - 1).coerceAtLeast(1)

                val linePath = Path()
                val areaPath = Path()

                breakdown.forEachIndexed { i, rec ->
                    val x = i * stepX
                    val y = height - (rec.closingCorpus / maxCorpus).toFloat().coerceIn(0f, 1f) * height

                    if (i == 0) {
                        linePath.moveTo(x, y)
                        areaPath.moveTo(x, height)
                        areaPath.lineTo(x, y)
                    } else {
                        linePath.lineTo(x, y)
                        areaPath.lineTo(x, y)
                    }

                    if (i == breakdown.lastIndex) {
                        areaPath.lineTo(x, height)
                        areaPath.close()
                    }
                }

                val hasDepleted = breakdown.any { it.isDepleted }
                val mainColor = if (hasDepleted) ChartDepletedColor else ChartCorpusColor

                drawPath(
                    path = areaPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(mainColor.copy(alpha = 0.3f), mainColor.copy(alpha = 0.02f)),
                        startY = 0f,
                        endY = height
                    )
                )

                drawPath(
                    path = linePath,
                    color = mainColor,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw points
                breakdown.forEachIndexed { i, rec ->
                    val x = i * stepX
                    val y = height - (rec.closingCorpus / maxCorpus).toFloat().coerceIn(0f, 1f) * height
                    val pointColor = if (rec.isDepleted) ChartDepletedColor else mainColor
                    drawCircle(color = pointColor, radius = 3.5.dp.toPx(), center = Offset(x, y))
                }
            }
        }
    }
}
