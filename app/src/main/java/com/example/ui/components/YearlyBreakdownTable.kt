package com.example.ui.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SipInput
import com.example.data.model.SipResult
import com.example.data.model.SwpInput
import com.example.data.model.SwpResult
import com.example.data.model.YearlyRecord
import com.example.utils.CurrencyFormatter
import com.example.utils.ExportUtils

@Composable
fun SipYearlyBreakdownTable(
    input: SipInput,
    result: SipResult,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isReversedOrder by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val records = remember(result.yearlyBreakdown, isReversedOrder) {
        if (isReversedOrder) result.yearlyBreakdown.reversed() else result.yearlyBreakdown
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sip_yearly_table_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with toggle & export buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "YEAR-BY-YEAR BREAKDOWN",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${result.yearlyBreakdown.size} Years Projection",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilledTonalIconButton(
                        onClick = { 
                            com.example.utils.SoundManager.play(com.example.utils.SoundManager.SoundType.SUCCESS)
                            ExportUtils.shareSipCsv(context, input, result) 
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Export CSV", modifier = Modifier.size(18.dp))
                    }
                    FilledTonalIconButton(
                        onClick = { 
                            com.example.utils.SoundManager.play(com.example.utils.SoundManager.SoundType.SUCCESS)
                            ExportUtils.shareSipPdf(context, input, result) 
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Export PDF", modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Expand / Collapse preview
            val rowsToShow = if (isExpanded) records else records.take(5)
            val hScrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(hScrollState)
            ) {
                // Table Header Row
                Row(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(vertical = 8.dp, horizontal = 12.dp)
                ) {
                    TableHeaderCell("Year", 55.dp)
                    TableHeaderCell("Opening", 95.dp)
                    TableHeaderCell("Monthly SIP", 95.dp)
                    TableHeaderCell("Annual Deposit", 105.dp)
                    TableHeaderCell("Growth", 95.dp)
                    TableHeaderCell("Ending Corpus", 110.dp)
                    TableHeaderCell("Total Invested", 105.dp)
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Table Data Rows
                rowsToShow.forEachIndexed { idx, row ->
                    val bg = if (idx % 2 == 0) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    Row(
                        modifier = Modifier
                            .background(bg, RoundedCornerShape(4.dp))
                            .padding(vertical = 8.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TableCell("Y${row.year}", 55.dp, isBold = true)
                        TableCell(CurrencyFormatter.formatInrCompact(row.openingBalance), 95.dp)
                        TableCell(CurrencyFormatter.formatInrCompact(row.periodicContribution), 95.dp)
                        TableCell(CurrencyFormatter.formatInrCompact(row.yearlyContribution), 105.dp)
                        TableCell("+${CurrencyFormatter.formatInrCompact(row.investmentGains)}", 95.dp, color = MaterialTheme.colorScheme.primary)
                        TableCell(CurrencyFormatter.formatInrCompact(row.closingBalance), 110.dp, isBold = true)
                        TableCell(CurrencyFormatter.formatInrCompact(row.cumulativeInvested), 105.dp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // View More Button
            if (records.size > 5) {
                TextButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = if (isExpanded) "Show Less" else "View All ${records.size} Years",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
fun SwpYearlyBreakdownTable(
    input: SwpInput,
    result: SwpResult,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val records = result.yearlyBreakdown

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("swp_yearly_table_card"),
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
                Column {
                    Text(
                        text = "ANNUAL WITHDRAWAL SCHEDULE",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${records.size} Years Simulation",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilledTonalIconButton(
                        onClick = { 
                            com.example.utils.SoundManager.play(com.example.utils.SoundManager.SoundType.SUCCESS)
                            ExportUtils.shareSwpCsv(context, input, result) 
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("export_swp_csv_button")
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Export CSV", modifier = Modifier.size(18.dp))
                    }
                    FilledTonalIconButton(
                        onClick = { 
                            com.example.utils.SoundManager.play(com.example.utils.SoundManager.SoundType.SUCCESS)
                            ExportUtils.shareSwpPdf(context, input, result) 
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("export_swp_pdf_button")
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Export PDF", modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val rowsToShow = if (isExpanded) records else records.take(5)
            val hScrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(hScrollState)
            ) {
                Row(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(vertical = 8.dp, horizontal = 12.dp)
                ) {
                    TableHeaderCell("Year", 55.dp)
                    TableHeaderCell("Opening Corpus", 110.dp)
                    TableHeaderCell("Annual Withdrawal", 115.dp)
                    TableHeaderCell("Growth Earned", 100.dp)
                    TableHeaderCell("Ending Corpus", 110.dp)
                    TableHeaderCell("Total Withdrawn", 110.dp)
                }

                Spacer(modifier = Modifier.height(6.dp))

                rowsToShow.forEachIndexed { idx, row ->
                    val bg = if (idx % 2 == 0) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    Row(
                        modifier = Modifier
                            .background(bg, RoundedCornerShape(4.dp))
                            .padding(vertical = 8.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TableCell("Y${row.year}", 55.dp, isBold = true)
                        TableCell(CurrencyFormatter.formatInrCompact(row.openingCorpus), 110.dp)
                        TableCell(CurrencyFormatter.formatInrCompact(row.annualWithdrawal), 115.dp, color = MaterialTheme.colorScheme.tertiary)
                        TableCell("+${CurrencyFormatter.formatInrCompact(row.growthEarned)}", 100.dp, color = MaterialTheme.colorScheme.primary)
                        TableCell(
                            text = if (row.isDepleted) "DEPLETED" else CurrencyFormatter.formatInrCompact(row.closingCorpus),
                            width = 110.dp,
                            color = if (row.isDepleted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                            isBold = true
                        )
                        TableCell(CurrencyFormatter.formatInrCompact(row.cumulativeWithdrawn), 110.dp)
                    }
                }
            }

            if (records.size > 5) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(if (isExpanded) "Show Less" else "View All ${records.size} Years")
                    Icon(if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        }
    }
}

@Composable
private fun TableHeaderCell(text: String, width: androidx.compose.ui.unit.Dp) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.width(width)
    )
}

@Composable
private fun TableCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    isBold: Boolean = false,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
        color = color,
        modifier = Modifier.width(width)
    )
}
