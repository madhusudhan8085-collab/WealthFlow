package com.example.ui.screens.fundfinder

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.WealthFlowUiState
import com.example.ui.viewmodel.WealthFlowViewModel
import com.example.utils.SoundManager

@Composable
fun FundFinderScreen(
    state: WealthFlowUiState,
    viewModel: WealthFlowViewModel,
    modifier: Modifier = Modifier
) {
    val step = state.fundFinderStep

    // Android back button: navigate back a step inside Fund Finder wizard
    BackHandler(enabled = step > 0) {
        SoundManager.play(SoundManager.SoundType.TAB)
        viewModel.setFundFinderStep(step - 1)
    }

    if (state.showGlossaryDialog) {
        FundGlossaryDialog(
            onDismiss = { viewModel.toggleGlossaryDialog(false) }
        )
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("fund_finder_screen_root"),
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (step > 0) {
                                IconButton(
                                    onClick = {
                                        SoundManager.play(SoundManager.SoundType.TAB)
                                        viewModel.setFundFinderStep(step - 1)
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .testTag("fund_finder_back_step_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Previous Step",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = if (step == 0) "Fund Finder" else "Fund Finder • Step $step of 4",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = when (step) {
                                        0 -> "Mutual Fund Category Discovery"
                                        1 -> "Investor Profile"
                                        2 -> "Goal & Horizon"
                                        3 -> "Risk Assessment"
                                        4 -> "Suitability Recommendation"
                                        else -> "Discovery Wizard"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { viewModel.toggleGlossaryDialog(true) },
                                modifier = Modifier.testTag("fund_finder_top_glossary_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = "Glossary",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            if (step > 0) {
                                IconButton(
                                    onClick = { viewModel.resetFundFinder() },
                                    modifier = Modifier.testTag("fund_finder_top_reset_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Reset",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    if (step > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { step / 4f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        },
        bottomBar = {
            if (step > 0) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                SoundManager.play(SoundManager.SoundType.TAB)
                                viewModel.setFundFinderStep(step - 1)
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("fund_finder_bottom_prev_button")
                        ) {
                            Text("Back")
                        }

                        Button(
                            onClick = {
                                SoundManager.play(SoundManager.SoundType.TAB)
                                if (step < 4) {
                                    viewModel.setFundFinderStep(step + 1)
                                } else {
                                    viewModel.loadPrimaryCategoryIntoCalculator()
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1.5f)
                                .testTag("fund_finder_bottom_next_button")
                        ) {
                            Text(if (step < 4) "Continue" else "Load into Calculator", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("fund_finder_scrollable_content"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Crossfade(targetState = step, label = "fund_finder_step_crossfade") { currentStep ->
                    when (currentStep) {
                        0 -> WelcomeStepView(
                            onStart = {
                                SoundManager.play(SoundManager.SoundType.TAB)
                                viewModel.setFundFinderStep(1)
                            },
                            onOpenGlossary = { viewModel.toggleGlossaryDialog(true) }
                        )
                        1 -> InvestorProfileStepView(
                            profile = state.investorProfile,
                            onUpdate = { viewModel.updateInvestorProfile(it) }
                        )
                        2 -> GoalsStepView(
                            goal = state.fundFinderGoal,
                            onUpdate = { viewModel.updateFundFinderGoal(it) }
                        )
                        3 -> RiskAssessmentStepView(
                            answers = state.riskAnswers,
                            onAnswer = { qId, value -> viewModel.answerRiskQuestion(qId, value) }
                        )
                        4 -> SuitabilityResultStepView(
                            state = state,
                            viewModel = viewModel
                        )
                        else -> SuitabilityResultStepView(
                            state = state,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}
