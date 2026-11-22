package com.example.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToLong

object CurrencyFormatter {

    /**
     * Formats a double value according to the Indian Numbering System:
     * e.g., 1000 -> "1,000", 100000 -> "1,00,000", 10000000 -> "1,00,00,000"
     */
    fun formatInr(amount: Double, includeDecimals: Boolean = false): String {
        if (amount.isNaN() || amount.isInfinite()) return "₹0"
        val isNegative = amount < 0
        val absAmount = abs(amount)

        val integerPart = absAmount.toLong()
        val formattedInt = formatIndianInteger(integerPart)

        val result = if (includeDecimals) {
            val decimal = ((absAmount - integerPart) * 100).roundToLong()
            val decStr = String.format(Locale.US, "%02d", decimal)
            "₹$formattedInt.$decStr"
        } else {
            "₹$formattedInt"
        }

        return if (isNegative) "-$result" else result
    }

    /**
     * Formats an amount compactly in Lakhs (L) or Crores (Cr) or Thousands (K):
     * e.g., 1,25,00,000 -> "₹1.25 Cr"
     *       45,00,000 -> "₹45.00 L"
     *       50,000 -> "₹50.0 K"
     */
    fun formatInrCompact(amount: Double): String {
        if (amount.isNaN() || amount.isInfinite()) return "₹0"
        val isNegative = amount < 0
        val absAmount = abs(amount)

        val text = when {
            absAmount >= 10_000_000.0 -> { // 1 Crore = 10^7
                val cr = absAmount / 10_000_000.0
                if (cr >= 100) {
                    String.format(Locale.US, "%.1f Cr", cr)
                } else {
                    String.format(Locale.US, "%.2f Cr", cr)
                }
            }
            absAmount >= 100_000.0 -> { // 1 Lakh = 10^5
                val lk = absAmount / 100_000.0
                if (lk >= 100) {
                    String.format(Locale.US, "%.1f L", lk)
                } else {
                    String.format(Locale.US, "%.2f L", lk)
                }
            }
            absAmount >= 1_000.0 -> { // 1 Thousand
                val k = absAmount / 1_000.0
                String.format(Locale.US, "%.1f K", k)
            }
            else -> {
                String.format(Locale.US, "%.0f", absAmount)
            }
        }

        val prefix = if (isNegative) "-₹" else "₹"
        return "$prefix$text"
    }

    /**
     * Core Indian Integer comma placement algorithm:
     * Last 3 digits grouped, then every 2 digits grouped to the left.
     */
    private fun formatIndianInteger(num: Long): String {
        if (num < 1000) return num.toString()
        val s = num.toString()
        val last3 = s.substring(s.length - 3)
        var remaining = s.substring(0, s.length - 3)

        val parts = mutableListOf<String>()
        while (remaining.length > 2) {
            val part = remaining.substring(remaining.length - 2)
            parts.add(0, part)
            remaining = remaining.substring(0, remaining.length - 2)
        }
        if (remaining.isNotEmpty()) {
            parts.add(0, remaining)
        }
        parts.add(last3)
        return parts.joinToString(",")
    }

    fun formatPercent(rate: Double): String {
        if (rate.isNaN() || rate.isInfinite()) return "0%"
        return if (rate % 1.0 == 0.0) {
            "${rate.toInt()}%"
        } else {
            String.format(Locale.US, "%.1f%%", rate)
        }
    }

    fun formatMultiple(mult: Double): String {
        if (mult.isNaN() || mult.isInfinite()) return "0.0x"
        return String.format(Locale.US, "%.1fx", mult)
    }

    fun parseSafeDouble(text: String, defaultValue: Double = 0.0): Double {
        val clean = text.replace("₹", "").replace(",", "").trim()
        return clean.toDoubleOrNull() ?: defaultValue
    }
}
