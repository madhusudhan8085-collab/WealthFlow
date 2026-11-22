package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculations.FinancialMathEngine
import com.example.data.model.SipInput
import com.example.data.model.SipResult
import com.example.data.model.SwpInput
import com.example.data.model.SwpResult
import com.example.utils.CurrencyFormatter

@Composable
fun SipInsightsSection(
    input: SipInput,
    result: SipResult,
    modifier: Modifier = Modifier
) {
    val doublingYears = if (input.expectedReturnAnnual > 0) {
        String.format(java.util.Locale.US, "%.1f", 72.0 / input.expectedReturnAnnual)
    } else "N/A"

    val costOfDelay = FinancialMathEngine.calculateCostOfDelay(input, delayYears = 3)
    val gainsPortionPercent = if (result.finalCorpus > 0) {
        (result.totalGains / result.finalCorpus) * 100.0
    } else 0.0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sip_insights_card"),
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
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "FINANCIAL INSIGHTS & TAKEAWAYS",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            InsightRow(
                icon = Icons.Default.Timeline,
                iconColor = MaterialTheme.colorScheme.primary,
                title = "Rule of 72 Doubling Time",
                description = "At ${input.expectedReturnAnnual}% annual return, your invested corpus doubles approximately every $doublingYears years."
            )

            Spacer(modifier = Modifier.height(10.dp))

            InsightRow(
                icon = Icons.Default.AutoAwesome,
                iconColor = MaterialTheme.colorScheme.secondary,
                title = "Wealth Creation Power",
                description = "Compound growth generates ${String.format(java.util.Locale.US, "%.0f", gainsPortionPercent)}% (${CurrencyFormatter.formatInrCompact(result.totalGains)}) of your final corpus, while your principal was only ${CurrencyFormatter.formatInrCompact(result.totalInvested)}."
            )

            Spacer(modifier = Modifier.height(10.dp))

            InsightRow(
                icon = Icons.Default.HourglassBottom,
                iconColor = MaterialTheme.colorScheme.error,
                title = "Cost of Delaying by 3 Years",
                description = "Postponing your investment journey by just 3 years would reduce your final estimated corpus by approximately ${CurrencyFormatter.formatInr(costOfDelay)}."
            )

            if (input.enableStepUp) {
                Spacer(modifier = Modifier.height(10.dp))
                InsightRow(
                    icon = Icons.Default.Upgrade,
                    iconColor = MaterialTheme.colorScheme.tertiary,
                    title = "Step-Up SIP Acceleration",
                    description = "Increasing your SIP ${if (input.stepUpType.name == "PERCENTAGE") "${input.stepUpValue}%" else CurrencyFormatter.formatInr(input.stepUpValue)} each year keeps your savings pace with salary hikes and dramatically boosts terminal wealth."
                )
            }
        }
    }
}

@Composable
fun SwpInsightsSection(
    input: SwpInput,
    result: SwpResult,
    modifier: Modifier = Modifier
) {
    val swr = result.safeWithdrawalRatePercent
    val isDepleted = !result.survivesFullDuration

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("swp_insights_card"),
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
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = if (isDepleted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "RETIREMENT WITHDRAWAL HEALTH",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            val swrStatus = when {
                swr <= 5.0 -> "Safe (Low Risk)"
                swr <= 7.0 -> "Moderate"
                swr <= 9.0 -> "Aggressive"
                else -> "High Risk of Early Depletion"
            }

            InsightRow(
                icon = Icons.Default.Percent,
                iconColor = if (swr <= 6.0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                title = "Initial Withdrawal Rate: ${String.format(java.util.Locale.US, "%.1f", swr)}% ($swrStatus)",
                description = "Global retirement guidelines recommend a 4% to 6% initial withdrawal rate for lifelong portfolio survival."
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (isDepleted) {
                val depletionYears = result.depletionYear?.let { String.format(java.util.Locale.US, "%.1f", it) } ?: "N/A"
                InsightRow(
                    icon = Icons.Default.Warning,
                    iconColor = MaterialTheme.colorScheme.error,
                    title = "Corpus Depletion Alert",
                    description = "At this withdrawal rate and return, your corpus is projected to run out in year $depletionYears. Consider reducing monthly withdrawals or boosting capital."
                )
            } else {
                InsightRow(
                    icon = Icons.Default.CheckCircle,
                    iconColor = MaterialTheme.colorScheme.primary,
                    title = "Portfolio Longevity Confirmed",
                    description = "Your corpus survives the full ${input.durationYears}-year horizon with ${CurrencyFormatter.formatInr(result.remainingCorpus)} remaining balance."
                )
            }
        }
    }
}

@Composable
private fun InsightRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
        }
    }
}
