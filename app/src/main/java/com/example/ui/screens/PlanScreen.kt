package com.example.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlanType
import com.example.ui.components.SavePlanDialog
import com.example.ui.screens.fundfinder.FundFinderScreen
import com.example.ui.viewmodel.PlanSubTab
import com.example.ui.viewmodel.WealthFlowUiState
import com.example.ui.viewmodel.WealthFlowViewModel
import com.example.utils.CurrencyFormatter
import com.example.utils.ExportUtils
import com.example.utils.SoundManager

@Composable
fun PlanScreen(
    state: WealthFlowUiState,
    viewModel: WealthFlowViewModel,
    modifier: Modifier = Modifier
) {
    var showSaveDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val currentSubTab = state.currentPlanSubTab

    val planType = when (currentSubTab) {
        PlanSubTab.SIP -> PlanType.SIP
        PlanSubTab.SWP -> PlanType.SWP
        PlanSubTab.SIP_TO_SWP -> PlanType.SIP_TO_SWP
        PlanSubTab.GOAL -> PlanType.GOAL
        PlanSubTab.FUND_FINDER -> PlanType.SIP
    }

    val defaultPlanName = when (currentSubTab) {
        PlanSubTab.SIP -> "${state.sipInput.durationYears}Y Wealth SIP (${CurrencyFormatter.formatInrCompact(state.sipInput.periodicInvestment)}/mo)"
        PlanSubTab.SWP -> "Retirement Pension (${CurrencyFormatter.formatInrCompact(state.swpInput.periodicWithdrawal)}/mo)"
        PlanSubTab.SIP_TO_SWP -> "${state.sipToSwpInput.sipDurationYears}Y Accumulate → ${state.sipToSwpInput.swpDurationYears}Y Retire"
        PlanSubTab.GOAL -> "Goal Target (${CurrencyFormatter.formatInrCompact(state.goalInput.targetAmountToday)})"
        PlanSubTab.FUND_FINDER -> "Fund Discovery Portfolio (${CurrencyFormatter.formatInrCompact(state.investorProfile.monthlyBudgetInr)}/mo)"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("plan_screen_root")
    ) {
        // Top Sub-Navigation Tabs
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                ScrollableTabRow(
                    selectedTabIndex = currentSubTab.ordinal,
                    edgePadding = 16.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    divider = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PlanSubTab.values().forEach { subTab ->
                        val isSelected = currentSubTab == subTab
                        Tab(
                            selected = isSelected,
                            onClick = {
                                if (!isSelected) {
                                    SoundManager.play(SoundManager.SoundType.TAB)
                                    viewModel.selectPlanSubTab(subTab)
                                }
                            },
                            text = {
                                Text(
                                    text = subTab.title,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        )
                    }
                }

                // Sub-Bar: Active Plan Status & "Save Plan" action (hidden when in Fund Finder)
                if (currentSubTab != PlanSubTab.FUND_FINDER) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (currentSubTab) {
                                PlanSubTab.SIP -> "SIP Wealth Accumulation"
                                PlanSubTab.SWP -> "SWP Capital Distribution"
                                PlanSubTab.SIP_TO_SWP -> "Career to Retirement Transition"
                                PlanSubTab.GOAL -> "Reverse Inflation Goal Planning"
                                PlanSubTab.FUND_FINDER -> "Mutual Fund Discovery Wizard"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (currentSubTab == PlanSubTab.SIP || currentSubTab == PlanSubTab.SWP) {
                                FilledTonalButton(
                                    onClick = {
                                        SoundManager.play(SoundManager.SoundType.SUCCESS)
                                        if (currentSubTab == PlanSubTab.SIP) {
                                            ExportUtils.shareSipPdf(context, state.sipInput, state.sipResult)
                                        } else {
                                            ExportUtils.shareSwpPdf(context, state.swpInput, state.swpResult)
                                        }
                                    },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .height(34.dp)
                                        .testTag("export_pdf_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PictureAsPdf,
                                        contentDescription = "Export PDF Report",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("PDF Report", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            FilledTonalButton(
                                onClick = { showSaveDialog = true },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("save_plan_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkAdd,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Save Plan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            IconButton(
                                onClick = {
                                    SoundManager.play(SoundManager.SoundType.SUCCESS)
                                    val summary = when (currentSubTab) {
                                        PlanSubTab.SIP -> "WealthFlow SIP Plan: Monthly ${CurrencyFormatter.formatInr(state.sipInput.periodicInvestment)} for ${state.sipInput.durationYears}Y @ ${state.sipInput.expectedReturnAnnual}% yields ${CurrencyFormatter.formatInr(state.sipResult.finalCorpus)}"
                                        PlanSubTab.SWP -> "WealthFlow SWP Plan: Corpus ${CurrencyFormatter.formatInr(state.swpInput.startingCorpus)} allows ${CurrencyFormatter.formatInr(state.swpInput.periodicWithdrawal)}/mo for ${state.swpInput.durationYears}Y"
                                        PlanSubTab.SIP_TO_SWP -> "WealthFlow Journey: Accumulate ${CurrencyFormatter.formatInr(state.sipToSwpResult.accumulatedCorpus)}, then withdraw ${CurrencyFormatter.formatInr(state.sipToSwpInput.swpMonthlyWithdrawal)}/mo"
                                        PlanSubTab.GOAL -> "WealthFlow Goal: Target ${CurrencyFormatter.formatInr(state.goalResult.futureTargetAmount)} in ${state.goalInput.yearsUntilGoal}Y requires SIP of ${CurrencyFormatter.formatInr(state.goalResult.requiredFlatMonthlySip)}/mo"
                                        PlanSubTab.FUND_FINDER -> "WealthFlow Fund Discovery: Portfolio of ${state.illustrativePlan.allocations.size} categories with ${CurrencyFormatter.formatInr(state.illustrativePlan.totalMonthlyBudget)}/mo"
                                    }
                                    ExportUtils.shareTextSummary(context, summary)
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Active Calculator Screen
        Box(modifier = Modifier.weight(1f)) {
            Crossfade(targetState = currentSubTab, label = "plan_subtab_crossfade") { tab ->
                when (tab) {
                    PlanSubTab.SIP -> SipTabScreen(state = state, viewModel = viewModel)
                    PlanSubTab.SWP -> SwpTabScreen(state = state, viewModel = viewModel)
                    PlanSubTab.SIP_TO_SWP -> SipToSwpScreen(state = state, viewModel = viewModel)
                    PlanSubTab.GOAL -> GoalTabScreen(state = state, viewModel = viewModel)
                    PlanSubTab.FUND_FINDER -> FundFinderScreen(state = state, viewModel = viewModel)
                }
            }
        }
    }

    if (showSaveDialog) {
        SavePlanDialog(
            planType = planType,
            defaultName = defaultPlanName,
            onDismiss = { showSaveDialog = false },
            onSave = { name ->
                viewModel.saveCurrentPlan(name, planType)
                showSaveDialog = false
            }
        )
    }
}
