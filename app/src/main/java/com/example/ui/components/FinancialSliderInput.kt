package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.utils.CurrencyFormatter

@Composable
fun FinancialSliderInput(
    title: String,
    value: Double,
    onValueChange: (Double) -> Unit,
    range: ClosedFloatingPointRange<Float>,
    step: Float = 1f,
    unitPrefix: String = "",
    unitSuffix: String = "",
    isCurrency: Boolean = false,
    presets: List<Pair<String, Double>> = emptyList(),
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    var showEditDialog by remember { mutableStateOf(false) }

    val displayValue = remember(value, isCurrency, unitPrefix, unitSuffix) {
        if (isCurrency) {
            CurrencyFormatter.formatInr(value)
        } else {
            val formattedNum = if (value % 1.0 == 0.0) "${value.toInt()}" else String.format(java.util.Locale.US, "%.1f", value)
            "$unitPrefix$formattedNum$unitSuffix"
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Title & Value Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.clickable { showEditDialog = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = displayValue,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit value",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Slider with +/- nudge buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FilledTonalIconButton(
                    onClick = {
                        val next = (value - step).coerceIn(range.start.toDouble(), range.endInclusive.toDouble())
                        com.example.utils.SoundManager.play(com.example.utils.SoundManager.SoundType.TICK)
                        onValueChange(next)
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease",
                        modifier = Modifier.size(16.dp)
                    )
                }

                Slider(
                    value = value.toFloat().coerceIn(range.start, range.endInclusive),
                    onValueChange = { 
                        com.example.utils.SoundManager.play(com.example.utils.SoundManager.SoundType.TICK)
                        onValueChange(it.toDouble()) 
                    },
                    valueRange = range,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("${testTag}_slider"),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )

                FilledTonalIconButton(
                    onClick = {
                        val next = (value + step).coerceIn(range.start.toDouble(), range.endInclusive.toDouble())
                        com.example.utils.SoundManager.play(com.example.utils.SoundManager.SoundType.TICK)
                        onValueChange(next)
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Quick Preset Chips
            if (presets.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presets.forEach { (label, presetVal) ->
                        val isSelected = kotlin.math.abs(value - presetVal) < 0.01
                        FilterChip(
                            selected = isSelected,
                            onClick = { 
                                com.example.utils.SoundManager.play(com.example.utils.SoundManager.SoundType.POP)
                                onValueChange(presetVal) 
                            },
                            label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(28.dp)
                        )
                    }
                }
            }
        }
    }

    // Direct Input Dialog
    if (showEditDialog) {
        var tempText by remember { mutableStateOf(if (value % 1.0 == 0.0) "${value.toLong()}" else "$value") }
        val focusManager = LocalFocusManager.current

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Enter $title", style = MaterialTheme.typography.titleMedium) },
            text = {
                OutlinedTextField(
                    value = tempText,
                    onValueChange = { tempText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Value ($unitPrefix...$unitSuffix)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val parsed = tempText.toDoubleOrNull()
                            if (parsed != null) {
                                onValueChange(parsed.coerceIn(range.start.toDouble(), range.endInclusive.toDouble()))
                            }
                            showEditDialog = false
                            focusManager.clearFocus()
                        }
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = tempText.toDoubleOrNull()
                        if (parsed != null) {
                            onValueChange(parsed.coerceIn(range.start.toDouble(), range.endInclusive.toDouble()))
                        }
                        showEditDialog = false
                    }
                ) {
                    Text("Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
