package com.example.utils

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.SipInput
import com.example.data.model.SipResult
import com.example.data.model.SwpInput
import com.example.data.model.SwpResult
import java.io.File
import java.io.FileOutputStream

object ExportUtils {

    /**
     * Generates a CSV formatted string for SIP calculations and shares it via Android share intent.
     */
    fun shareSipCsv(context: Context, input: SipInput, result: SipResult) {
        val sb = StringBuilder()
        sb.append("WealthFlow SIP Investment Report\n")
        sb.append("Generated on,${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US).format(java.util.Date())}\n")
        sb.append("\nASSUMPTIONS\n")
        sb.append("Monthly Investment,${input.periodicInvestment}\n")
        sb.append("Investment Duration (Years),${input.durationYears}\n")
        sb.append("Expected Annual Return (%),${input.expectedReturnAnnual}%\n")
        sb.append("Step-up Enabled,${input.enableStepUp}\n")
        if (input.enableStepUp) {
            sb.append("Step-up Value,${input.stepUpValue} ${if (input.stepUpType.name == "PERCENTAGE") "%" else "INR"}\n")
        }
        sb.append("Initial Lumpsum,${input.initialLumpsum}\n")

        sb.append("\nSUMMARY RESULTS\n")
        sb.append("Total Invested,${result.totalInvested}\n")
        sb.append("Estimated Gains,${result.totalGains}\n")
        sb.append("Final Corpus,${result.finalCorpus}\n")
        sb.append("Growth Multiple,${String.format(java.util.Locale.US, "%.2fx", result.growthMultiplier)}\n")

        sb.append("\nYEAR-BY-YEAR BREAKDOWN\n")
        sb.append("Year,Opening Balance,Annual Contribution,Periodic SIP,Investment Gains,Closing Balance,Cumulative Invested,Cumulative Gains\n")
        for (r in result.yearlyBreakdown) {
            sb.append("${r.year},${r.openingBalance},${r.yearlyContribution},${r.periodicContribution},${r.investmentGains},${r.closingBalance},${r.cumulativeInvested},${r.cumulativeGains}\n")
        }

        sb.append("\nDISCLAIMER\n")
        sb.append("\"These calculations are illustrative estimates based on the assumptions entered. Actual investment returns may vary and are not guaranteed.\"\n")

        shareTextFile(context, "wealthflow_sip_report.csv", sb.toString(), "text/csv")
    }

    /**
     * Generates a CSV formatted string for SWP calculations.
     */
    fun shareSwpCsv(context: Context, input: SwpInput, result: SwpResult) {
        val sb = StringBuilder()
        sb.append("WealthFlow SWP Withdrawal Report\n")
        sb.append("Starting Corpus,${input.startingCorpus}\n")
        sb.append("Monthly Withdrawal,${input.periodicWithdrawal}\n")
        sb.append("Expected Return (%),${input.expectedReturnAnnual}%\n")
        sb.append("Duration (Years),${input.durationYears}\n")
        sb.append("Annual Withdrawal Increase (%),${if (input.enableAnnualIncrease) input.annualWithdrawalIncreasePercent else 0}%\n")

        sb.append("\nSUMMARY RESULTS\n")
        sb.append("Total Withdrawn,${result.totalWithdrawn}\n")
        sb.append("Total Growth Earned,${result.totalGrowthEarned}\n")
        sb.append("Remaining Corpus,${result.remainingCorpus}\n")
        sb.append("Corpus Survives,${result.survivesFullDuration}\n")

        sb.append("\nYEAR-BY-YEAR WITHDRAWAL SCHEDULE\n")
        sb.append("Year,Opening Corpus,Annual Withdrawal,Growth Earned,Closing Corpus,Cumulative Withdrawn\n")
        for (r in result.yearlyBreakdown) {
            sb.append("${r.year},${r.openingCorpus},${r.annualWithdrawal},${r.growthEarned},${r.closingCorpus},${r.cumulativeWithdrawn}\n")
        }

        shareTextFile(context, "wealthflow_swp_report.csv", sb.toString(), "text/csv")
    }

    /**
     * Generates a structured multi-page A4 PDF report file for SIP calculations.
     */
    fun generateSipPdfFile(context: Context, input: SipInput, result: SipResult): File {
        val document = PdfDocument()
        val pageWidth = 595 // A4 pt width
        val pageHeight = 842 // A4 pt height
        val dateFormat = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.US)
        val formattedDate = dateFormat.format(java.util.Date())

        val paint = Paint().apply { isAntiAlias = true }
        val strokePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 1f
            color = Color.rgb(226, 232, 240)
        }

        val totalRecords = result.yearlyBreakdown.size
        val totalPages = if (totalRecords > 22) 2 else 1

        // ==========================================
        // PAGE 1: EXECUTIVE SUMMARY & SCHEDULE
        // ==========================================
        val pageInfo1 = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page1 = document.startPage(pageInfo1)
        val canvas1 = page1.canvas

        // Header Background Banner (Dark Navy)
        paint.color = Color.rgb(15, 23, 42)
        canvas1.drawRect(0f, 0f, pageWidth.toFloat(), 105f, paint)

        // Brand Name & Logo Symbol
        paint.color = Color.rgb(16, 185, 129) // Emerald
        canvas1.drawRoundRect(32f, 24f, 62f, 54f, 8f, 8f, paint)
        paint.color = Color.WHITE
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas1.drawText("₹", 41f, 46f, paint)

        paint.textSize = 22f
        canvas1.drawText("WealthFlow", 72f, 47f, paint)

        paint.color = Color.rgb(16, 185, 129)
        paint.textSize = 12f
        paint.typeface = Typeface.DEFAULT
        canvas1.drawText("Systematic Investment Plan (SIP) Report", 72f, 68f, paint)

        paint.color = Color.rgb(148, 163, 184)
        paint.textSize = 9.5f
        canvas1.drawText("Plan today. Understand tomorrow. • Generated: $formattedDate", 72f, 86f, paint)

        // Executive Summary Card
        var yPos = 125f
        paint.color = Color.rgb(248, 250, 252)
        canvas1.drawRoundRect(32f, yPos, (pageWidth - 32).toFloat(), yPos + 85f, 12f, 12f, paint)
        canvas1.drawRoundRect(32f, yPos, (pageWidth - 32).toFloat(), yPos + 85f, 12f, 12f, strokePaint)

        // Col 1: Final Projected Corpus
        paint.color = Color.rgb(100, 116, 139)
        paint.textSize = 9f
        paint.typeface = Typeface.DEFAULT
        canvas1.drawText("ESTIMATED FUTURE CORPUS", 48f, yPos + 24f, paint)

        paint.color = Color.rgb(15, 23, 42)
        paint.textSize = 22f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas1.drawText(CurrencyFormatter.formatInr(result.finalCorpus), 48f, yPos + 54f, paint)

        paint.color = Color.rgb(16, 185, 129)
        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas1.drawText("+${String.format(java.util.Locale.US, "%.0f", result.gainsPercentage)}% Estimated Total Return", 48f, yPos + 72f, paint)

        // Col 2: Total Invested
        paint.color = Color.rgb(100, 116, 139)
        paint.textSize = 9f
        paint.typeface = Typeface.DEFAULT
        canvas1.drawText("TOTAL INVESTED", 240f, yPos + 24f, paint)
        paint.color = Color.rgb(30, 41, 59)
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas1.drawText(CurrencyFormatter.formatInr(result.totalInvested), 240f, yPos + 50f, paint)

        // Col 3: Estimated Wealth Gains
        paint.color = Color.rgb(100, 116, 139)
        paint.textSize = 9f
        paint.typeface = Typeface.DEFAULT
        canvas1.drawText("ESTIMATED WEALTH GAIN", 390f, yPos + 24f, paint)
        paint.color = Color.rgb(16, 185, 129)
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas1.drawText(CurrencyFormatter.formatInr(result.totalGains), 390f, yPos + 50f, paint)

        paint.color = Color.rgb(100, 116, 139)
        paint.textSize = 9.5f
        paint.typeface = Typeface.DEFAULT
        canvas1.drawText("${String.format(java.util.Locale.US, "%.1fx", result.growthMultiplier)} Wealth Multiple", 390f, yPos + 68f, paint)

        // Key Assumptions Section
        yPos = 228f
        paint.color = Color.rgb(15, 23, 42)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas1.drawText("Key Investment Assumptions", 32f, yPos, paint)

        yPos += 10f
        paint.color = Color.rgb(248, 250, 252)
        canvas1.drawRoundRect(32f, yPos, (pageWidth - 32).toFloat(), yPos + 68f, 10f, 10f, paint)
        canvas1.drawRoundRect(32f, yPos, (pageWidth - 32).toFloat(), yPos + 68f, 10f, 10f, strokePaint)

        paint.color = Color.rgb(51, 65, 85)
        paint.textSize = 9.5f
        paint.typeface = Typeface.DEFAULT

        canvas1.drawText("• Monthly Contribution: ${CurrencyFormatter.formatInr(input.periodicInvestment)}", 48f, yPos + 22f, paint)
        canvas1.drawText("• Investment Horizon: ${input.durationYears} Years", 48f, yPos + 40f, paint)
        canvas1.drawText("• Expected Annual Return: ${input.expectedReturnAnnual}% p.a.", 48f, yPos + 58f, paint)

        canvas1.drawText("• Annual Step-Up: ${if (input.enableStepUp) "${input.stepUpValue}% annually" else "None (Flat SIP)"}", 300f, yPos + 22f, paint)
        canvas1.drawText("• Initial Lumpsum: ${CurrencyFormatter.formatInr(input.initialLumpsum)}", 300f, yPos + 40f, paint)
        canvas1.drawText("• Calculation Model: Beginning-of-Month Compounding", 300f, yPos + 58f, paint)

        // Annual Schedule Table
        yPos += 88f
        paint.color = Color.rgb(15, 23, 42)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas1.drawText("Annual Growth Schedule", 32f, yPos, paint)

        yPos += 14f
        // Table Header Bar
        paint.color = Color.rgb(226, 232, 240)
        canvas1.drawRoundRect(32f, yPos, (pageWidth - 32).toFloat(), yPos + 22f, 6f, 6f, paint)

        paint.color = Color.rgb(30, 41, 59)
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas1.drawText("Year", 44f, yPos + 15f, paint)
        canvas1.drawText("Annual Deposit", 95f, yPos + 15f, paint)
        canvas1.drawText("Total Invested", 215f, yPos + 15f, paint)
        canvas1.drawText("Annual Growth", 335f, yPos + 15f, paint)
        canvas1.drawText("Ending Corpus", 445f, yPos + 15f, paint)

        yPos += 26f
        val page1Records = result.yearlyBreakdown.take(22)

        page1Records.forEachIndexed { idx, record ->
            if (idx % 2 == 1) {
                paint.color = Color.rgb(248, 250, 252)
                canvas1.drawRect(32f, yPos - 2f, (pageWidth - 32).toFloat(), yPos + 14f, paint)
            }
            paint.color = Color.rgb(51, 65, 85)
            paint.typeface = Typeface.DEFAULT
            paint.textSize = 9f
            canvas1.drawText("Y${record.year}", 44f, yPos + 10f, paint)
            canvas1.drawText(CurrencyFormatter.formatInr(record.yearlyContribution), 95f, yPos + 10f, paint)
            canvas1.drawText(CurrencyFormatter.formatInr(record.cumulativeInvested), 215f, yPos + 10f, paint)
            paint.color = Color.rgb(16, 185, 129)
            canvas1.drawText("+${CurrencyFormatter.formatInr(record.investmentGains)}", 335f, yPos + 10f, paint)
            paint.color = Color.rgb(15, 23, 42)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas1.drawText(CurrencyFormatter.formatInr(record.closingBalance), 445f, yPos + 10f, paint)

            yPos += 17f
        }

        // Disclaimer & Page Footer
        val footerY1 = 805f
        paint.color = Color.rgb(148, 163, 184)
        paint.textSize = 8f
        paint.typeface = Typeface.DEFAULT
        canvas1.drawText("Disclaimer: Calculations are mathematical estimates based on stated assumptions. Mutual fund returns fluctuate. Not financial advice.", 32f, footerY1, paint)
        canvas1.drawText("WealthFlow • Page 1 of $totalPages", (pageWidth - 140).toFloat(), footerY1, paint)

        document.finishPage(page1)

        // ==========================================
        // OPTIONAL PAGE 2: CONTINUATION SCHEDULE
        // ==========================================
        if (totalPages > 1) {
            val pageInfo2 = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 2).create()
            val page2 = document.startPage(pageInfo2)
            val canvas2 = page2.canvas

            // Continuation Banner
            paint.color = Color.rgb(15, 23, 42)
            canvas2.drawRect(0f, 0f, pageWidth.toFloat(), 65f, paint)

            paint.color = Color.WHITE
            paint.textSize = 16f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas2.drawText("WealthFlow • SIP Annual Growth Projection (Continued)", 32f, 36f, paint)

            paint.color = Color.rgb(148, 163, 184)
            paint.textSize = 9.5f
            paint.typeface = Typeface.DEFAULT
            canvas2.drawText("Years 23 to $totalRecords • Generated: $formattedDate", 32f, 52f, paint)

            var yPos2 = 85f
            paint.color = Color.rgb(226, 232, 240)
            canvas2.drawRoundRect(32f, yPos2, (pageWidth - 32).toFloat(), yPos2 + 22f, 6f, 6f, paint)

            paint.color = Color.rgb(30, 41, 59)
            paint.textSize = 9f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas2.drawText("Year", 44f, yPos2 + 15f, paint)
            canvas2.drawText("Annual Deposit", 95f, yPos2 + 15f, paint)
            canvas2.drawText("Total Invested", 215f, yPos2 + 15f, paint)
            canvas2.drawText("Annual Growth", 335f, yPos2 + 15f, paint)
            canvas2.drawText("Ending Corpus", 445f, yPos2 + 15f, paint)

            yPos2 += 26f
            val page2Records = result.yearlyBreakdown.drop(22)

            page2Records.forEachIndexed { idx, record ->
                if (idx % 2 == 1) {
                    paint.color = Color.rgb(248, 250, 252)
                    canvas2.drawRect(32f, yPos2 - 2f, (pageWidth - 32).toFloat(), yPos2 + 14f, paint)
                }
                paint.color = Color.rgb(51, 65, 85)
                paint.typeface = Typeface.DEFAULT
                paint.textSize = 9f
                canvas2.drawText("Y${record.year}", 44f, yPos2 + 10f, paint)
                canvas2.drawText(CurrencyFormatter.formatInr(record.yearlyContribution), 95f, yPos2 + 10f, paint)
                canvas2.drawText(CurrencyFormatter.formatInr(record.cumulativeInvested), 215f, yPos2 + 10f, paint)
                paint.color = Color.rgb(16, 185, 129)
                canvas2.drawText("+${CurrencyFormatter.formatInr(record.investmentGains)}", 335f, yPos2 + 10f, paint)
                paint.color = Color.rgb(15, 23, 42)
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas2.drawText(CurrencyFormatter.formatInr(record.closingBalance), 445f, yPos2 + 10f, paint)

                yPos2 += 17f
            }

            // Disciplined Investing Note Card
            yPos2 += 20f
            paint.color = Color.rgb(240, 253, 244)
            canvas2.drawRoundRect(32f, yPos2, (pageWidth - 32).toFloat(), yPos2 + 65f, 10f, 10f, paint)
            paint.color = Color.rgb(187, 247, 208)
            canvas2.drawRoundRect(32f, yPos2, (pageWidth - 32).toFloat(), yPos2 + 65f, 10f, 10f, strokePaint)

            paint.color = Color.rgb(22, 101, 52)
            paint.textSize = 10f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas2.drawText("The Power of Disciplined Long-Term Compounding", 48f, yPos2 + 22f, paint)

            paint.color = Color.rgb(21, 128, 61)
            paint.textSize = 8.5f
            paint.typeface = Typeface.DEFAULT
            canvas2.drawText("In long-term compounding, the majority of wealth is generated in the final decade. Continuing systematic investments", 48f, yPos2 + 38f, paint)
            canvas2.drawText("through market downturns allows rupee-cost averaging to maximize accumulated units before redemption.", 48f, yPos2 + 52f, paint)

            // Footer Page 2
            val footerY2 = 805f
            paint.color = Color.rgb(148, 163, 184)
            paint.textSize = 8f
            canvas2.drawText("Disclaimer: Calculations are mathematical estimates based on stated assumptions. Past returns do not predict future performance.", 32f, footerY2, paint)
            canvas2.drawText("WealthFlow • Page 2 of 2", (pageWidth - 140).toFloat(), footerY2, paint)

            document.finishPage(page2)
        }

        // Write PDF file to cache directory
        val file = File(context.cacheDir, "wealthflow_sip_report.pdf")
        val outputStream = FileOutputStream(file)
        document.writeTo(outputStream)
        outputStream.flush()
        outputStream.close()
        document.close()

        return file
    }

    /**
     * Generates a structured multi-page A4 PDF report file for SWP retirement calculations.
     */
    fun generateSwpPdfFile(context: Context, input: SwpInput, result: SwpResult): File {
        val document = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val dateFormat = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.US)
        val formattedDate = dateFormat.format(java.util.Date())

        val paint = Paint().apply { isAntiAlias = true }
        val strokePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 1f
            color = Color.rgb(226, 232, 240)
        }

        val totalRecords = result.yearlyBreakdown.size
        val totalPages = if (totalRecords > 22) 2 else 1

        // ==========================================
        // PAGE 1: SWP EXECUTIVE SUMMARY & SCHEDULE
        // ==========================================
        val pageInfo1 = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page1 = document.startPage(pageInfo1)
        val canvas1 = page1.canvas

        // Header Background Banner (Dark Navy)
        paint.color = Color.rgb(15, 23, 42)
        canvas1.drawRect(0f, 0f, pageWidth.toFloat(), 105f, paint)

        // Brand Logo
        paint.color = Color.rgb(16, 185, 129)
        canvas1.drawRoundRect(32f, 24f, 62f, 54f, 8f, 8f, paint)
        paint.color = Color.WHITE
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas1.drawText("₹", 41f, 46f, paint)

        paint.textSize = 22f
        canvas1.drawText("WealthFlow", 72f, 47f, paint)

        paint.color = Color.rgb(16, 185, 129)
        paint.textSize = 12f
        paint.typeface = Typeface.DEFAULT
        canvas1.drawText("Systematic Withdrawal Plan (SWP) Pension Report", 72f, 68f, paint)

        paint.color = Color.rgb(148, 163, 184)
        paint.textSize = 9.5f
        canvas1.drawText("Retirement Cashflow & Longevity Analysis • Generated: $formattedDate", 72f, 86f, paint)

        // Executive Summary Card
        var yPos = 125f
        paint.color = Color.rgb(248, 250, 252)
        canvas1.drawRoundRect(32f, yPos, (pageWidth - 32).toFloat(), yPos + 85f, 12f, 12f, paint)
        canvas1.drawRoundRect(32f, yPos, (pageWidth - 32).toFloat(), yPos + 85f, 12f, 12f, strokePaint)

        // Col 1: Total Withdrawn
        paint.color = Color.rgb(100, 116, 139)
        paint.textSize = 9f
        paint.typeface = Typeface.DEFAULT
        canvas1.drawText("TOTAL CAPITAL WITHDRAWN", 48f, yPos + 24f, paint)

        paint.color = Color.rgb(15, 23, 42)
        paint.textSize = 22f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas1.drawText(CurrencyFormatter.formatInr(result.totalWithdrawn), 48f, yPos + 54f, paint)

        val isHealthy = result.survivesFullDuration
        paint.color = if (isHealthy) Color.rgb(16, 185, 129) else Color.rgb(239, 68, 68)
        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        val statusText = if (isHealthy) "✓ Corpus Survives Full ${input.durationYears} Years"
                         else "⚠ Depletes in Year ${String.format(java.util.Locale.US, "%.1f", result.depletionYear ?: 0.0)}"
        canvas1.drawText(statusText, 48f, yPos + 72f, paint)

        // Col 2: Starting Capital
        paint.color = Color.rgb(100, 116, 139)
        paint.textSize = 9f
        paint.typeface = Typeface.DEFAULT
        canvas1.drawText("STARTING NEST EGG", 240f, yPos + 24f, paint)
        paint.color = Color.rgb(30, 41, 59)
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas1.drawText(CurrencyFormatter.formatInr(input.startingCorpus), 240f, yPos + 50f, paint)

        // Col 3: Remaining Capital & Growth
        paint.color = Color.rgb(100, 116, 139)
        paint.textSize = 9f
        paint.typeface = Typeface.DEFAULT
        canvas1.drawText("FINAL REMAINING CAPITAL", 390f, yPos + 24f, paint)
        paint.color = Color.rgb(15, 23, 42)
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas1.drawText(CurrencyFormatter.formatInr(result.remainingCorpus), 390f, yPos + 50f, paint)

        paint.color = Color.rgb(16, 185, 129)
        paint.textSize = 9.5f
        canvas1.drawText("+${CurrencyFormatter.formatInr(result.totalGrowthEarned)} Growth Earned", 390f, yPos + 68f, paint)

        // Key Assumptions Section
        yPos = 228f
        paint.color = Color.rgb(15, 23, 42)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas1.drawText("Key Retirement Assumptions", 32f, yPos, paint)

        yPos += 10f
        paint.color = Color.rgb(248, 250, 252)
        canvas1.drawRoundRect(32f, yPos, (pageWidth - 32).toFloat(), yPos + 68f, 10f, 10f, paint)
        canvas1.drawRoundRect(32f, yPos, (pageWidth - 32).toFloat(), yPos + 68f, 10f, 10f, strokePaint)

        paint.color = Color.rgb(51, 65, 85)
        paint.textSize = 9.5f
        paint.typeface = Typeface.DEFAULT

        canvas1.drawText("• Monthly Pension Payout: ${CurrencyFormatter.formatInr(input.periodicWithdrawal)}/mo", 48f, yPos + 22f, paint)
        canvas1.drawText("• Retirement Horizon: ${input.durationYears} Years", 48f, yPos + 40f, paint)
        canvas1.drawText("• Portfolio Expected Return: ${input.expectedReturnAnnual}% p.a.", 48f, yPos + 58f, paint)

        canvas1.drawText("• Annual Inflation Increase: ${if (input.enableAnnualIncrease) "${input.annualWithdrawalIncreasePercent}% p.a." else "Fixed (0%)"}", 300f, yPos + 22f, paint)
        canvas1.drawText("• Initial Withdrawal Rate: ${String.format(java.util.Locale.US, "%.2f%%", result.safeWithdrawalRatePercent)}", 300f, yPos + 40f, paint)
        canvas1.drawText("• Sustainable Monthly Rate: ~${CurrencyFormatter.formatInr(input.startingCorpus * (input.expectedReturnAnnual / 100.0) / 12.0)}/mo", 300f, yPos + 58f, paint)

        // Annual Schedule Table
        yPos += 88f
        paint.color = Color.rgb(15, 23, 42)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas1.drawText("Annual Withdrawal & Capital Depletion Schedule", 32f, yPos, paint)

        yPos += 14f
        paint.color = Color.rgb(226, 232, 240)
        canvas1.drawRoundRect(32f, yPos, (pageWidth - 32).toFloat(), yPos + 22f, 6f, 6f, paint)

        paint.color = Color.rgb(30, 41, 59)
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas1.drawText("Year", 44f, yPos + 15f, paint)
        canvas1.drawText("Opening Corpus", 90f, yPos + 15f, paint)
        canvas1.drawText("Annual Payout", 205f, yPos + 15f, paint)
        canvas1.drawText("Growth Earned", 315f, yPos + 15f, paint)
        canvas1.drawText("Ending Corpus", 425f, yPos + 15f, paint)

        yPos += 26f
        val page1Records = result.yearlyBreakdown.take(22)

        page1Records.forEachIndexed { idx, record ->
            if (idx % 2 == 1) {
                paint.color = Color.rgb(248, 250, 252)
                canvas1.drawRect(32f, yPos - 2f, (pageWidth - 32).toFloat(), yPos + 14f, paint)
            }
            paint.color = Color.rgb(51, 65, 85)
            paint.typeface = Typeface.DEFAULT
            paint.textSize = 9f
            canvas1.drawText("Y${record.year}", 44f, yPos + 10f, paint)
            canvas1.drawText(CurrencyFormatter.formatInr(record.openingCorpus), 90f, yPos + 10f, paint)
            paint.color = Color.rgb(239, 68, 68)
            canvas1.drawText("-${CurrencyFormatter.formatInr(record.annualWithdrawal)}", 205f, yPos + 10f, paint)
            paint.color = Color.rgb(16, 185, 129)
            canvas1.drawText("+${CurrencyFormatter.formatInr(record.growthEarned)}", 315f, yPos + 10f, paint)
            paint.color = if (record.closingCorpus > 0) Color.rgb(15, 23, 42) else Color.rgb(239, 68, 68)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas1.drawText(CurrencyFormatter.formatInr(record.closingCorpus), 425f, yPos + 10f, paint)

            yPos += 17f
        }

        // Disclaimer Footer Page 1
        val footerY1 = 805f
        paint.color = Color.rgb(148, 163, 184)
        paint.textSize = 8f
        paint.typeface = Typeface.DEFAULT
        canvas1.drawText("Disclaimer: SWP calculations model fixed compounding. Market drawdowns in early years (sequence-of-returns risk) can accelerate depletion.", 32f, footerY1, paint)
        canvas1.drawText("WealthFlow • Page 1 of $totalPages", (pageWidth - 140).toFloat(), footerY1, paint)

        document.finishPage(page1)

        // ==========================================
        // OPTIONAL PAGE 2: SWP CONTINUATION
        // ==========================================
        if (totalPages > 1) {
            val pageInfo2 = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 2).create()
            val page2 = document.startPage(pageInfo2)
            val canvas2 = page2.canvas

            paint.color = Color.rgb(15, 23, 42)
            canvas2.drawRect(0f, 0f, pageWidth.toFloat(), 65f, paint)

            paint.color = Color.WHITE
            paint.textSize = 16f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas2.drawText("WealthFlow • SWP Annual Distribution Schedule (Continued)", 32f, 36f, paint)

            paint.color = Color.rgb(148, 163, 184)
            paint.textSize = 9.5f
            paint.typeface = Typeface.DEFAULT
            canvas2.drawText("Years 23 to $totalRecords • Generated: $formattedDate", 32f, 52f, paint)

            var yPos2 = 85f
            paint.color = Color.rgb(226, 232, 240)
            canvas2.drawRoundRect(32f, yPos2, (pageWidth - 32).toFloat(), yPos2 + 22f, 6f, 6f, paint)

            paint.color = Color.rgb(30, 41, 59)
            paint.textSize = 9f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas2.drawText("Year", 44f, yPos2 + 15f, paint)
            canvas2.drawText("Opening Corpus", 90f, yPos2 + 15f, paint)
            canvas2.drawText("Annual Payout", 205f, yPos2 + 15f, paint)
            canvas2.drawText("Growth Earned", 315f, yPos2 + 15f, paint)
            canvas2.drawText("Ending Corpus", 425f, yPos2 + 15f, paint)

            yPos2 += 26f
            val page2Records = result.yearlyBreakdown.drop(22)

            page2Records.forEachIndexed { idx, record ->
                if (idx % 2 == 1) {
                    paint.color = Color.rgb(248, 250, 252)
                    canvas2.drawRect(32f, yPos2 - 2f, (pageWidth - 32).toFloat(), yPos2 + 14f, paint)
                }
                paint.color = Color.rgb(51, 65, 85)
                paint.typeface = Typeface.DEFAULT
                paint.textSize = 9f
                canvas2.drawText("Y${record.year}", 44f, yPos2 + 10f, paint)
                canvas2.drawText(CurrencyFormatter.formatInr(record.openingCorpus), 90f, yPos2 + 10f, paint)
                paint.color = Color.rgb(239, 68, 68)
                canvas2.drawText("-${CurrencyFormatter.formatInr(record.annualWithdrawal)}", 205f, yPos2 + 10f, paint)
                paint.color = Color.rgb(16, 185, 129)
                canvas2.drawText("+${CurrencyFormatter.formatInr(record.growthEarned)}", 315f, yPos2 + 10f, paint)
                paint.color = if (record.closingCorpus > 0) Color.rgb(15, 23, 42) else Color.rgb(239, 68, 68)
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas2.drawText(CurrencyFormatter.formatInr(record.closingCorpus), 425f, yPos2 + 10f, paint)

                yPos2 += 17f
            }

            // Longevity Guidance Card
            yPos2 += 20f
            paint.color = Color.rgb(240, 249, 255)
            canvas2.drawRoundRect(32f, yPos2, (pageWidth - 32).toFloat(), yPos2 + 65f, 10f, 10f, paint)
            paint.color = Color.rgb(186, 230, 253)
            canvas2.drawRoundRect(32f, yPos2, (pageWidth - 32).toFloat(), yPos2 + 65f, 10f, 10f, strokePaint)

            paint.color = Color.rgb(3, 105, 161)
            paint.textSize = 10f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas2.drawText("Retirement Sustainability & Sequence of Returns", 48f, yPos2 + 22f, paint)

            paint.color = Color.rgb(14, 116, 144)
            paint.textSize = 8.5f
            paint.typeface = Typeface.DEFAULT
            canvas2.drawText("To guard against capital exhaustion during prolonged market declines, maintain 2-3 years of living expenses", 48f, yPos2 + 38f, paint)
            canvas2.drawText("in liquid/arbitrage funds. This prevents having to liquidate equity holdings at depressed NAV valuations.", 48f, yPos2 + 52f, paint)

            val footerY2 = 805f
            paint.color = Color.rgb(148, 163, 184)
            paint.textSize = 8f
            canvas2.drawText("Disclaimer: Past returns do not guarantee future longevity. Consult an RIA before finalizing retirement distributions.", 32f, footerY2, paint)
            canvas2.drawText("WealthFlow • Page 2 of 2", (pageWidth - 140).toFloat(), footerY2, paint)

            document.finishPage(page2)
        }

        val file = File(context.cacheDir, "wealthflow_swp_report.pdf")
        val outputStream = FileOutputStream(file)
        document.writeTo(outputStream)
        outputStream.flush()
        outputStream.close()
        document.close()

        return file
    }

    /**
     * Generates and shares the structured SIP PDF report.
     */
    fun shareSipPdf(context: Context, input: SipInput, result: SipResult) {
        try {
            val file = generateSipPdfFile(context, input, result)
            sharePdfFile(context, file, "WealthFlow SIP Investment Plan")
        } catch (e: Exception) {
            shareTextSummary(context, "WealthFlow Plan: Final Corpus ${CurrencyFormatter.formatInr(result.finalCorpus)} (Invested ${CurrencyFormatter.formatInr(result.totalInvested)})")
        }
    }

    /**
     * Generates and shares the structured SWP PDF report.
     */
    fun shareSwpPdf(context: Context, input: SwpInput, result: SwpResult) {
        try {
            val file = generateSwpPdfFile(context, input, result)
            sharePdfFile(context, file, "WealthFlow SWP Retirement Plan")
        } catch (e: Exception) {
            shareTextSummary(context, "WealthFlow SWP Plan: Total Withdrawn ${CurrencyFormatter.formatInr(result.totalWithdrawn)} from ${CurrencyFormatter.formatInr(input.startingCorpus)}")
        }
    }

    /**
     * Helper to share a generated PDF file via Android Share Chooser.
     */
    fun sharePdfFile(context: Context, file: File, subject: String) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, "Share PDF Report")
        if (context !is android.app.Activity) {
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    fun shareTextSummary(context: Context, summaryText: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, summaryText)
            type = "text/plain"
        }
        val chooser = Intent.createChooser(sendIntent, "Share WealthFlow Plan")
        if (context !is android.app.Activity) {
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    fun shareCsvFile(context: Context, content: String, filename: String = "WealthFlow_Report.csv") {
        shareTextFile(context, filename, content, "text/csv")
    }

    fun shareTextFile(context: Context, filename: String, content: String, mimeType: String) {
        try {
            val file = File(context.cacheDir, filename)
            file.writeText(content)
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "WealthFlow Report")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(intent, "Export Report")
            if (context !is android.app.Activity) {
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            shareTextSummary(context, content)
        }
    }

    /**
     * Shares a comprehensive, formatted plain-text and markdown investment discovery report.
     */
    fun shareFundDiscoveryReport(
        context: Context,
        reportContent: String
    ) {
        shareTextFile(context, "WealthFlow_Fund_Discovery_Report.txt", reportContent, "text/plain")
    }
}
