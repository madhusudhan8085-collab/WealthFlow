package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DisclaimerFooter
import com.example.ui.components.FinancialSliderInput
import com.example.ui.viewmodel.MainDestination
import com.example.ui.viewmodel.ThemeMode
import com.example.ui.viewmodel.WealthFlowUiState
import com.example.ui.viewmodel.WealthFlowViewModel
import com.example.utils.ExportUtils
import com.example.utils.SoundManager

@Composable
fun SettingsScreen(
    state: WealthFlowUiState,
    viewModel: WealthFlowViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showClearDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen_scroll"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Preferences Intro Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(20.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Column {
                        Text(
                            text = "WealthFlow Preferences",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Fine-tune audio feedback, theme, default returns, and local data",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Section 1: Sound & Audio Feedback
        item {
            SettingsCard(
                title = "AUDIO & HAPTIC FEEDBACK",
                icon = Icons.Default.VolumeUp
            ) {
                // Enable/Disable Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Interactive Sound Effects",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (state.isSoundEnabled) "Sound feedback is active for sliders & actions" else "App sounds are currently muted",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = state.isSoundEnabled,
                        onCheckedChange = { viewModel.toggleSound() },
                        modifier = Modifier.testTag("settings_sound_switch")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "SELECT SOUND PROFILE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                SoundManager.SoundProfile.values().forEach { profile ->
                    val isSelected = state.soundProfile == profile && state.isSoundEnabled
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        border = if (isSelected) CardDefaults.outlinedCardBorder() else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                viewModel.setSoundProfile(profile)
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(profile.displayName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    if (isSelected) {
                                        Surface(
                                            shape = RoundedCornerShape(100.dp),
                                            color = MaterialTheme.colorScheme.primary
                                        ) {
                                            Text(
                                                text = "ACTIVE",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(profile.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                            }

                            // Preview Button
                            FilledTonalButton(
                                onClick = {
                                    SoundManager.playPreview(profile, SoundManager.SoundType.POP)
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Test Sound",
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Test", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Appearance & Theme
        item {
            SettingsCard(
                title = "APPEARANCE & THEME",
                icon = Icons.Default.Palette
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeMode.values().forEach { mode ->
                        val isSelected = state.themeMode == mode
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            border = if (isSelected) CardDefaults.outlinedCardBorder() else null,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    SoundManager.play(SoundManager.SoundType.TAB)
                                    viewModel.setThemeMode(mode)
                                }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 14.dp, horizontal = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = when (mode) {
                                        ThemeMode.DARK -> Icons.Default.DarkMode
                                        ThemeMode.LIGHT -> Icons.Default.LightMode
                                        ThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
                                    },
                                    contentDescription = mode.displayName,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = mode.displayName.replace(" Theme", "").replace(" Default", ""),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Default Financial Assumptions
        item {
            SettingsCard(
                title = "DEFAULT FINANCIAL ASSUMPTIONS",
                icon = Icons.Default.Tune
            ) {
                Text(
                    text = "Configure baseline figures used across new calculators and stress tests.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                FinancialSliderInput(
                    title = "Default Expected Annual Return",
                    value = state.defaultExpectedReturn,
                    onValueChange = { viewModel.updateDefaultReturn(it) },
                    range = 6f..18f,
                    step = 0.5f,
                    unitSuffix = "% p.a.",
                    presets = listOf("8%" to 8.0, "10%" to 10.0, "12%" to 12.0, "15%" to 15.0)
                )

                Spacer(modifier = Modifier.height(12.dp))

                FinancialSliderInput(
                    title = "Default Long-Term Inflation Rate",
                    value = state.defaultInflation,
                    onValueChange = { viewModel.updateDefaultInflation(it) },
                    range = 3f..12f,
                    step = 0.5f,
                    unitSuffix = "% p.a.",
                    presets = listOf("4%" to 4.0, "5%" to 5.0, "6%" to 6.0, "7%" to 7.0)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {
                        SoundManager.play(SoundManager.SoundType.POP)
                        viewModel.updateDefaultReturn(12.0)
                        viewModel.updateDefaultInflation(6.0)
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Restore Benchmark Defaults (12% Return / 6% Inflation)")
                }
            }
        }

        // Section 4: Data & Storage
        item {
            SettingsCard(
                title = "SAVED PLANS DATA MANAGEMENT",
                icon = Icons.Default.FolderShared
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Active Saved Plans", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text("${state.savedPlans.size} plan scenarios stored on device", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    if (state.savedPlans.isNotEmpty()) {
                        OutlinedButton(
                            onClick = {
                                SoundManager.play(SoundManager.SoundType.SUCCESS)
                                val summary = buildAllPlansSummary(state.savedPlans)
                                ExportUtils.shareTextSummary(context, summary)
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Share All")
                        }
                    }
                }

                if (state.savedPlans.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showClearDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Clear All Saved Plans", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section 5: Methodology & Mathematical Engine
        item {
            SettingsCard(
                title = "METHODOLOGY & TRANSPARENCY",
                icon = Icons.Default.Functions
            ) {
                Text(
                    text = "WealthFlow adheres strictly to rigorous actuarial and compound interest mathematics:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                MethodologyRow("SIP Compounding", "Annuity-Due formulation where investments occur at the beginning of each period, compounding for the full duration.")
                MethodologyRow("Step-Up Growth", "Geometric progression applying compounding increments to periodic investments at specified annual intervals.")
                MethodologyRow("SWP Depletion", "Month-by-month capital balance tracking: remaining corpus compounds while systematic withdrawals are deducted.")
                MethodologyRow("Inflation Multiplier", "Purchasing power calculated via (1 + i)^t to reveal real future cost and necessary capital preservation.")

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("WealthFlow Financial Engine v1.2", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("Designed for financial precision and institutional-grade portfolio simulations.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                    }
                }
            }
        }

        item {
            DisclaimerFooter()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Confirmation Dialog for Clearing Plans
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear All Saved Plans?", fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently delete all ${state.savedPlans.size} saved financial plans from your device. Are you sure?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllSavedPlans()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Clear All", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            content()
        }
    }
}

@Composable
private fun MethodologyRow(title: String, desc: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = "• $title", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        Text(text = desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.5.sp, lineHeight = 16.sp)
    }
}

private fun buildAllPlansSummary(plans: List<com.example.data.model.SavedPlan>): String {
    val sb = StringBuilder()
    sb.append("WealthFlow Saved Plans Summary (${plans.size} plans):\n\n")
    for (p in plans) {
        sb.append("• ${p.name} [${p.type.displayName}]: ${p.primaryMetricLabel} = ${com.example.utils.CurrencyFormatter.formatInr(p.primaryMetricValue)}, Tenure = ${p.tenureYears}Y @ ${p.returnRateAnnual}%\n")
    }
    return sb.toString()
}
