package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlanType
import com.example.data.model.SavedPlan
import com.example.ui.components.DisclaimerFooter
import com.example.ui.viewmodel.MainDestination
import com.example.ui.viewmodel.PlanSubTab
import com.example.ui.viewmodel.WealthFlowUiState
import com.example.ui.viewmodel.WealthFlowViewModel
import com.example.utils.CurrencyFormatter
import com.example.utils.ExportUtils
import com.example.utils.SoundManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MyPlansScreen(
    state: WealthFlowUiState,
    viewModel: WealthFlowViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf<PlanType?>(null) }
    var planToDelete by remember { mutableStateOf<SavedPlan?>(null) }

    val allPlans = state.savedPlans
    val filteredPlans = remember(allPlans, selectedFilter) {
        if (selectedFilter == null) allPlans else allPlans.filter { it.type == selectedFilter }
    }

    val totalTrackedCorpus = remember(allPlans) {
        allPlans.sumOf { it.primaryMetricValue }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("my_plans_screen_scroll"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("my_plans_summary_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "PORTFOLIO TARGET TRACKER",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = CurrencyFormatter.formatInr(totalTrackedCorpus),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = "${allPlans.size} Saved Plans",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                SoundManager.play(SoundManager.SoundType.POP)
                                viewModel.selectPlanSubTab(PlanSubTab.SIP)
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("New Plan", fontWeight = FontWeight.Bold)
                        }

                        if (allPlans.isNotEmpty()) {
                            OutlinedButton(
                                onClick = {
                                    SoundManager.play(SoundManager.SoundType.SUCCESS)
                                    val csv = buildAllPlansCsv(allPlans)
                                    ExportUtils.shareCsvFile(context, csv, "WealthFlow_All_Saved_Plans.csv")
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FileDownload,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Export All", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        // Filter Chips Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = {
                        SoundManager.play(SoundManager.SoundType.TICK)
                        selectedFilter = null
                    },
                    label = { Text("All (${allPlans.size})", fontWeight = FontWeight.Bold) }
                )

                PlanType.values().forEach { type ->
                    val count = allPlans.count { it.type == type }
                    FilterChip(
                        selected = selectedFilter == type,
                        onClick = {
                            SoundManager.play(SoundManager.SoundType.TICK)
                            selectedFilter = if (selectedFilter == type) null else type
                        },
                        label = { Text("${type.displayName} ($count)") }
                    )
                }
            }
        }

        // List of Plans or Empty State
        if (filteredPlans.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (selectedFilter == null) "No Saved Plans Yet" else "No ${selectedFilter?.displayName} Plans",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (selectedFilter == null)
                                "Design your personalized SIP, SWP or Goal target strategies in the Plan tab and tap 'Save Plan' to track them here."
                            else
                                "Switch filter or create a new ${selectedFilter?.displayName} plan.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                SoundManager.play(SoundManager.SoundType.POP)
                                val targetSub = when (selectedFilter) {
                                    PlanType.SIP -> PlanSubTab.SIP
                                    PlanType.SWP -> PlanSubTab.SWP
                                    PlanType.SIP_TO_SWP -> PlanSubTab.SIP_TO_SWP
                                    PlanType.GOAL -> PlanSubTab.GOAL
                                    null -> PlanSubTab.SIP
                                }
                                viewModel.selectPlanSubTab(targetSub)
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Create a Plan Now", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            items(filteredPlans, key = { it.id }) { plan ->
                SavedPlanCard(
                    plan = plan,
                    onOpen = {
                        SoundManager.play(SoundManager.SoundType.POP)
                        viewModel.loadPlanIntoCalculator(plan)
                    },
                    onShare = {
                        SoundManager.play(SoundManager.SoundType.SUCCESS)
                        val summary = "WealthFlow Plan: ${plan.name} (${plan.type.displayName})\n" +
                                "${plan.primaryMetricLabel}: ${CurrencyFormatter.formatInr(plan.primaryMetricValue)}\n" +
                                "${plan.secondaryMetricLabel}: ${CurrencyFormatter.formatInr(plan.secondaryMetricValue)}\n" +
                                "Tenure: ${plan.tenureYears} Years @ ${plan.returnRateAnnual}% p.a."
                        ExportUtils.shareTextSummary(context, summary)
                    },
                    onDelete = {
                        planToDelete = plan
                    }
                )
            }
        }

        item {
            DisclaimerFooter()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Delete Confirmation Dialog
    if (planToDelete != null) {
        val plan = planToDelete!!
        AlertDialog(
            onDismissRequest = { planToDelete = null },
            title = {
                Text("Delete Plan?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Are you sure you want to delete \"${plan.name}\"? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePlan(plan.id)
                        planToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { planToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SavedPlanCard(
    plan: SavedPlan,
    onOpen: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = remember(plan.dateCreatedMillis) {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        sdf.format(Date(plan.dateCreatedMillis))
    }

    val typeColor = when (plan.type) {
        PlanType.SIP -> MaterialTheme.colorScheme.primary
        PlanType.SWP -> Color(0xFF38BDF8) // Sky blue
        PlanType.SIP_TO_SWP -> Color(0xFFA855F7) // Purple
        PlanType.GOAL -> Color(0xFFF59E0B) // Amber
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("saved_plan_card_${plan.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header Row: Type badge + Date + Delete icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = typeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = plan.type.displayName.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = typeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        letterSpacing = 0.8.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Plan",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Plan Title
            Text(
                text = plan.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Metrics Grid
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = plan.primaryMetricLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = CurrencyFormatter.formatInr(plan.primaryMetricValue),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = plan.secondaryMetricLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = CurrencyFormatter.formatInr(plan.secondaryMetricValue),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Parameters row: Amount, Horizon, Return
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Periodic: ${CurrencyFormatter.formatInrCompact(plan.monthlyAmount)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "•",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "${plan.tenureYears} Years",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "•",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "${plan.returnRateAnnual}% p.a.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Actions: Open in Calculator & Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpen,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Launch,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Open in Calculator", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                OutlinedIconButton(
                    onClick = onShare,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

private fun buildAllPlansCsv(plans: List<SavedPlan>): String {
    val sb = StringBuilder()
    sb.append("ID,Name,Type,Primary Metric,Primary Value,Secondary Metric,Secondary Value,Monthly Amount,Tenure (Years),Return Rate (%),Created Date\n")
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    for (p in plans) {
        val date = sdf.format(Date(p.dateCreatedMillis))
        val nameSafe = "\"${p.name.replace("\"", "\"\"")}\""
        sb.append("${p.id},$nameSafe,${p.type.name},\"${p.primaryMetricLabel}\",${p.primaryMetricValue},\"${p.secondaryMetricLabel}\",${p.secondaryMetricValue},${p.monthlyAmount},${p.tenureYears},${p.returnRateAnnual},$date\n")
    }
    return sb.toString()
}
