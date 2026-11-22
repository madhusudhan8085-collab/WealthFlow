package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun ScenarioView(
    baseInput: SipInput,
    modifier: Modifier = Modifier
) {
    var consRate by remember { mutableStateOf(8.0) }
    var optRate by remember { mutableStateOf(15.0) }

    val comparison = remember(baseInput, consRate, optRate) {
        FinancialMathEngine.calculateScenarios(
            baseInput = baseInput,
            conservativeReturn = consRate,
            optimisticReturn = optRate
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("scenario_comparison_card"),
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
                text = "RETURN SCENARIOS FOR YOUR ACTIVE SIP",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Simulated returns for ${CurrencyFormatter.formatInr(baseInput.periodicInvestment)}/mo over ${baseInput.durationYears} years across different market regimes.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3 Scenario Cards in a Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Conservative Card
                ScenarioCard(
                    title = "Conservative",
                    rate = consRate,
                    corpus = comparison.conservativeCorpus,
                    totalInvested = comparison.totalInvested,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    highlightColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )

                // Base Card (Highlighted as User's Active Plan)
                ScenarioCard(
                    title = "Your Plan",
                    rate = comparison.baseReturn,
                    corpus = comparison.baseCorpus,
                    totalInvested = comparison.totalInvested,
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    highlightColor = MaterialTheme.colorScheme.primary,
                    isRecommended = true,
                    modifier = Modifier.weight(1.1f)
                )

                // Optimistic Card
                ScenarioCard(
                    title = "Optimistic",
                    rate = optRate,
                    corpus = comparison.optimisticCorpus,
                    totalInvested = comparison.totalInvested,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    highlightColor = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ScenarioCard(
    title: String,
    rate: Double,
    corpus: Double,
    totalInvested: Double,
    containerColor: androidx.compose.ui.graphics.Color,
    highlightColor: androidx.compose.ui.graphics.Color,
    isRecommended: Boolean = false,
    modifier: Modifier = Modifier
) {
    val gains = kotlin.math.max(0.0, corpus - totalInvested)

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        border = if (isRecommended) CardDefaults.outlinedCardBorder() else null,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = highlightColor
            )
            Text(
                text = "${rate.toInt()}% p.a.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = CurrencyFormatter.formatInrCompact(corpus),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Gains: ${CurrencyFormatter.formatInrCompact(gains)}",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
