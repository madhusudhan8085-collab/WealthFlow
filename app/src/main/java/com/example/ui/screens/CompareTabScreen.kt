package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.components.DisclaimerFooter
import com.example.ui.components.PlanComparisonView
import com.example.ui.components.WhatIfSimulatorView
import com.example.ui.viewmodel.WealthFlowUiState
import com.example.ui.viewmodel.WealthFlowViewModel

@Composable
fun CompareTabScreen(
    state: WealthFlowUiState,
    viewModel: WealthFlowViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("compare_screen_scroll"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Plan A vs Plan B Smart Comparison (Bound to Active SIP Plan)
        item {
            PlanComparisonView(
                activeSipInput = state.sipInput,
                onApplyPlan = { updatedPlan ->
                    viewModel.updateSipInvestment(updatedPlan.periodicInvestment)
                    viewModel.updateSipDuration(updatedPlan.durationYears)
                    viewModel.updateSipReturn(updatedPlan.expectedReturnAnnual)
                    if (updatedPlan.enableStepUp) {
                        viewModel.toggleStepUp(true)
                    }
                }
            )
        }

        // What-If Simulator
        item {
            WhatIfSimulatorView(baseInput = state.sipInput)
        }

        item {
            DisclaimerFooter()
        }
    }
}
