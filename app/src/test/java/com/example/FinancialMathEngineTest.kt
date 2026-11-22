package com.example

import com.example.calculations.FinancialMathEngine
import com.example.data.model.*
import org.junit.Assert.*
import org.junit.Test

class FinancialMathEngineTest {

    @Test
    fun testBasicSIPCalculation() {
        // Standard SIP: ₹10,000/mo, 10 years, 12% return, beginning of period
        val input = SipInput(
            initialLumpsum = 0.0,
            periodicInvestment = 10000.0,
            frequency = Frequency.MONTHLY,
            durationYears = 10,
            expectedReturnAnnual = 12.0,
            enableStepUp = false
        )
        val result = FinancialMathEngine.calculateSIP(input)

        // Total invested = 10,000 * 120 = ₹12,00,000
        assertEquals(1200000.0, result.totalInvested, 0.01)

        // FV formula with beginning of period annuity:
        // r = 0.01, n = 120
        // FV = 10000 * ((1.01^120 - 1) / 0.01) * 1.01 = approx 23,23,390.79
        assertTrue(result.finalCorpus > 2300000.0)
        assertTrue(result.finalCorpus < 2350000.0)
        assertTrue(result.totalGains > 1100000.0)
        assertEquals(10, result.yearlyBreakdown.size)
    }

    @Test
    fun testZeroReturnSIP() {
        // Zero return should NOT divide by zero or yield NaN; final corpus must equal total invested
        val input = SipInput(
            initialLumpsum = 50000.0,
            periodicInvestment = 5000.0,
            frequency = Frequency.MONTHLY,
            durationYears = 5,
            expectedReturnAnnual = 0.0,
            enableStepUp = false
        )
        val result = FinancialMathEngine.calculateSIP(input)

        // Total invested = 50,000 + 5,000 * 60 = 3,50,000
        assertEquals(350000.0, result.totalInvested, 0.01)
        assertEquals(350000.0, result.finalCorpus, 0.01)
        assertEquals(0.0, result.totalGains, 0.01)
        assertFalse(result.finalCorpus.isNaN())
        assertFalse(result.finalCorpus.isInfinite())
    }

    @Test
    fun testStepUpSIP() {
        // ₹10,000/mo, 10% annual step-up
        val input = SipInput(
            periodicInvestment = 10000.0,
            frequency = Frequency.MONTHLY,
            durationYears = 3,
            expectedReturnAnnual = 12.0,
            enableStepUp = true,
            stepUpType = StepUpType.PERCENTAGE,
            stepUpValue = 10.0
        )
        val result = FinancialMathEngine.calculateSIP(input)

        // Year 1: ₹10,000/mo -> ₹1,20,000
        // Year 2: ₹11,000/mo -> ₹1,32,000
        // Year 3: ₹12,100/mo -> ₹1,45,200
        val expectedTotalInvested = 120000.0 + 132000.0 + 145200.0
        assertEquals(expectedTotalInvested, result.totalInvested, 0.01)
        assertEquals(10000.0, result.yearlyBreakdown[0].periodicContribution, 0.01)
        assertEquals(11000.0, result.yearlyBreakdown[1].periodicContribution, 0.01)
        assertEquals(12100.0, result.yearlyBreakdown[2].periodicContribution, 0.01)
    }

    @Test
    fun testSWPCalculationAndDepletion() {
        // Starting corpus ₹10 Lakh, withdrawing ₹1,00,000/mo at 6% return for 5 years
        // Withdrawing ₹12 Lakh/yr from ₹10 Lakh will deplete within ~10 months
        val input = SwpInput(
            startingCorpus = 1000000.0,
            periodicWithdrawal = 100000.0,
            frequency = Frequency.MONTHLY,
            durationYears = 5,
            expectedReturnAnnual = 6.0,
            enableAnnualIncrease = false
        )
        val result = FinancialMathEngine.calculateSWP(input)

        assertFalse(result.survivesFullDuration)
        assertNotNull(result.depletionMonth)
        assertTrue(result.depletionMonth!! <= 12)
        assertEquals(0.0, result.remainingCorpus, 0.01)
    }

    @Test
    fun testSWPSustainableCorpus() {
        // Starting corpus ₹1 Crore, withdrawing ₹40,000/mo (₹4.8 L/yr = 4.8%) at 9% return for 20 years
        // Growth outpaces withdrawal so remaining corpus should grow!
        val input = SwpInput(
            startingCorpus = 10000000.0,
            periodicWithdrawal = 40000.0,
            frequency = Frequency.MONTHLY,
            durationYears = 20,
            expectedReturnAnnual = 9.0,
            enableAnnualIncrease = false
        )
        val result = FinancialMathEngine.calculateSWP(input)

        assertTrue(result.survivesFullDuration)
        assertNull(result.depletionMonth)
        assertTrue(result.remainingCorpus > 10000000.0)
    }

    @Test
    fun testGoalReverseSIP() {
        // Target ₹50 Lakh in 15 years at 12% return and 6% inflation
        val input = GoalInput(
            targetAmountToday = 5000000.0,
            inflationRateAnnual = 6.0,
            yearsUntilGoal = 15,
            expectedReturnAnnual = 12.0,
            stepUpPercent = 10.0
        )
        val result = FinancialMathEngine.calculateGoalSIP(input)

        // Future target = 50 Lakh * (1.06)^15 = approx 1.198 Crore
        assertTrue(result.futureTargetAmount > 11900000.0)
        assertTrue(result.futureTargetAmount < 12100000.0)

        // Verifying forward calculation with required flat SIP hits future target
        val testSip = SipInput(
            periodicInvestment = result.requiredFlatMonthlySip,
            frequency = Frequency.MONTHLY,
            durationYears = 15,
            expectedReturnAnnual = 12.0,
            enableStepUp = false
        )
        val checkResult = FinancialMathEngine.calculateSIP(testSip)
        assertEquals(result.futureTargetAmount, checkResult.finalCorpus, 1.0)
    }

    @Test
    fun testSipToSwpIntegration() {
        val input = SipToSwpInput(
            sipInvestment = 20000.0,
            sipStepUpPercent = 10.0,
            sipDurationYears = 15,
            sipExpectedReturn = 12.0,
            sipLumpsum = 0.0,
            swpExpectedReturn = 8.0,
            swpMonthlyWithdrawal = 60000.0,
            swpDurationYears = 20,
            swpAnnualIncreasePercent = 5.0
        )
        val result = FinancialMathEngine.calculateSIPToSWP(input)

        assertTrue(result.accumulatedCorpus > 10000000.0) // > 1 Crore accumulated
        assertTrue(result.totalWithdrawn > 0.0)
        assertEquals(15, result.accumulationBreakdown.size)
        assertEquals(20, result.withdrawalBreakdown.size)
    }

    @Test
    fun testReverseSwpRequiredCorpus() {
        val corpus = FinancialMathEngine.calculateRequiredCorpusForSwp(
            monthlyWithdrawal = 50000.0,
            durationYears = 20,
            expectedReturnAnnual = 8.0,
            annualIncreasePercent = 0.0
        )
        // Check that starting with this corpus sustains 20 years
        val swpCheck = FinancialMathEngine.calculateSWP(
            SwpInput(
                startingCorpus = corpus,
                periodicWithdrawal = 50000.0,
                frequency = Frequency.MONTHLY,
                durationYears = 20,
                expectedReturnAnnual = 8.0,
                enableAnnualIncrease = false
            )
        )
        // At the end of 20 years, corpus will be near 0 (or slight positive due to discrete monthly compounding)
        assertTrue(swpCheck.survivesFullDuration)
        assertTrue(swpCheck.remainingCorpus < 50000.0)
    }

    @Test
    fun testSip_zeroInvestment_returnsZero() {
        val input = SipInput(
            initialLumpsum = 0.0,
            periodicInvestment = 0.0,
            durationYears = 10,
            expectedReturnAnnual = 12.0,
            enableStepUp = false
        )
        val result = FinancialMathEngine.calculateSIP(input)
        assertEquals(0.0, result.totalInvested, 0.001)
        assertEquals(0.0, result.finalCorpus, 0.001)
        assertEquals(0.0, result.totalGains, 0.001)
        assertEquals(0.0, result.growthMultiplier, 0.001)
        assertFalse(result.finalCorpus.isNaN())
    }

    @Test
    fun testSip_negativeReturn_handlesGracefully() {
        val input = SipInput(
            initialLumpsum = 100000.0,
            periodicInvestment = 10000.0,
            durationYears = 5,
            expectedReturnAnnual = -5.0,
            enableStepUp = false
        )
        val result = FinancialMathEngine.calculateSIP(input)
        assertEquals(700000.0, result.totalInvested, 0.01)
        assertTrue("Corpus should be less than invested amount for negative return", result.finalCorpus < result.totalInvested)
        assertTrue("Corpus should be positive", result.finalCorpus > 0.0)
        assertEquals(0.0, result.totalGains, 0.001) // Gains clamped to 0 when in loss
        assertFalse(result.finalCorpus.isNaN())
    }

    @Test
    fun testSip_veryLowReturn_positiveGains() {
        val input = SipInput(
            initialLumpsum = 0.0,
            periodicInvestment = 10000.0,
            durationYears = 5,
            expectedReturnAnnual = 0.1, // 0.1% return
            enableStepUp = false
        )
        val result = FinancialMathEngine.calculateSIP(input)
        assertEquals(600000.0, result.totalInvested, 0.01)
        assertTrue(result.finalCorpus > result.totalInvested)
        assertTrue(result.totalGains > 0.0)
        assertFalse(result.finalCorpus.isNaN())
    }

    @Test
    fun testSip_veryHighReturn_largeCompounding() {
        val input = SipInput(
            initialLumpsum = 0.0,
            periodicInvestment = 10000.0,
            durationYears = 10,
            expectedReturnAnnual = 50.0, // 50% p.a.
            enableStepUp = false
        )
        val result = FinancialMathEngine.calculateSIP(input)
        assertEquals(1200000.0, result.totalInvested, 0.01)
        assertTrue("High return should compound exponentially", result.finalCorpus > 15000000.0)
        assertFalse(result.finalCorpus.isNaN())
        assertFalse(result.finalCorpus.isInfinite())
    }

    @Test
    fun testSip_durationsEdgeCases_1YearAnd50Years() {
        // Minimum duration 1 year
        val minInput = SipInput(periodicInvestment = 5000.0, durationYears = 1, expectedReturnAnnual = 12.0, enableStepUp = false)
        val minResult = FinancialMathEngine.calculateSIP(minInput)
        assertEquals(1, minResult.yearlyBreakdown.size)
        assertEquals(60000.0, minResult.totalInvested, 0.01)

        // Maximum duration 50 years without step-up
        val maxInput = SipInput(periodicInvestment = 5000.0, durationYears = 50, expectedReturnAnnual = 12.0, enableStepUp = false)
        val maxResult = FinancialMathEngine.calculateSIP(maxInput)
        assertEquals(50, maxResult.yearlyBreakdown.size)
        assertEquals(3000000.0, maxResult.totalInvested, 0.01)
        assertTrue(maxResult.finalCorpus > 100000000.0)
    }

    @Test
    fun testSip_decimalInputs_accurateCalculation() {
        val input = SipInput(
            initialLumpsum = 12345.67,
            periodicInvestment = 7890.12,
            durationYears = 3,
            expectedReturnAnnual = 11.75,
            enableStepUp = true,
            stepUpType = StepUpType.PERCENTAGE,
            stepUpValue = 8.25
        )
        val result = FinancialMathEngine.calculateSIP(input)
        assertFalse(result.finalCorpus.isNaN())
        assertFalse(result.totalInvested.isNaN())
        assertTrue(result.totalInvested > 300000.0)
        assertTrue(result.finalCorpus > result.totalInvested)
    }

    @Test
    fun testSip_largeAmounts_noOverflow() {
        val input = SipInput(
            initialLumpsum = 50000000.0, // ₹5 Crore
            periodicInvestment = 1000000.0, // ₹10 Lakh/month
            durationYears = 20,
            expectedReturnAnnual = 14.0,
            enableStepUp = false
        )
        val result = FinancialMathEngine.calculateSIP(input)
        assertEquals(290000000.0, result.totalInvested, 0.01) // ₹29 Crore invested
        assertTrue(result.finalCorpus > 1000000000.0) // > ₹100 Crore
        assertFalse(result.finalCorpus.isInfinite())
    }

    @Test
    fun testSwp_zeroCorpus_depletedImmediately() {
        val input = SwpInput(
            startingCorpus = 0.0,
            periodicWithdrawal = 50000.0,
            durationYears = 10,
            expectedReturnAnnual = 8.0
        )
        val result = FinancialMathEngine.calculateSWP(input)
        assertEquals(0.0, result.totalWithdrawn, 0.001)
        assertEquals(0.0, result.remainingCorpus, 0.001)
        assertFalse(result.survivesFullDuration)
        assertEquals(0, result.depletionMonth)
    }

    @Test
    fun testSwp_zeroWithdrawal_corpusCompoundsOnly() {
        val input = SwpInput(
            startingCorpus = 1000000.0,
            periodicWithdrawal = 0.0,
            durationYears = 5,
            expectedReturnAnnual = 12.0
        )
        val result = FinancialMathEngine.calculateSWP(input)
        assertEquals(0.0, result.totalWithdrawn, 0.001)
        assertTrue("Corpus should compound with zero withdrawal", result.remainingCorpus > 1750000.0)
        assertTrue(result.survivesFullDuration)
    }

    @Test
    fun testSwp_withdrawalExceedsCorpusMonth1() {
        val input = SwpInput(
            startingCorpus = 20000.0,
            periodicWithdrawal = 50000.0,
            durationYears = 5,
            expectedReturnAnnual = 8.0
        )
        val result = FinancialMathEngine.calculateSWP(input)
        assertEquals(20000.0, result.totalWithdrawn, 0.01) // Can only withdraw available 20,000
        assertEquals(0.0, result.remainingCorpus, 0.01)
        assertFalse(result.survivesFullDuration)
        assertEquals(1, result.depletionMonth)
    }

    @Test
    fun testGoal_zeroTarget_zeroRequiredSip() {
        val input = GoalInput(
            targetAmountToday = 0.0,
            inflationRateAnnual = 6.0,
            yearsUntilGoal = 10,
            expectedReturnAnnual = 12.0
        )
        val result = FinancialMathEngine.calculateGoalSIP(input)
        assertEquals(0.0, result.futureTargetAmount, 0.001)
        assertEquals(0.0, result.requiredFlatMonthlySip, 0.001)
        assertEquals(0.0, result.totalInvestedFlat, 0.001)
    }

    @Test
    fun testGoal_zeroInflation_targetUnchanged() {
        val input = GoalInput(
            targetAmountToday = 5000000.0,
            inflationRateAnnual = 0.0,
            yearsUntilGoal = 10,
            expectedReturnAnnual = 12.0
        )
        val result = FinancialMathEngine.calculateGoalSIP(input)
        assertEquals(5000000.0, result.futureTargetAmount, 0.01)
        assertEquals(1.0, result.inflationMultiplier, 0.001)
    }

    @Test
    fun testCurrencyFormatter_indianNumberingAndEdgeCases() {
        assertEquals("₹0", com.example.utils.CurrencyFormatter.formatInr(0.0))
        assertEquals("₹0", com.example.utils.CurrencyFormatter.formatInr(Double.NaN))
        assertEquals("₹500", com.example.utils.CurrencyFormatter.formatInr(500.0))
        assertEquals("₹1,000", com.example.utils.CurrencyFormatter.formatInr(1000.0))
        assertEquals("₹10,000", com.example.utils.CurrencyFormatter.formatInr(10000.0))
        assertEquals("₹1,00,000", com.example.utils.CurrencyFormatter.formatInr(100000.0)) // 1 Lakh
        assertEquals("₹10,00,000", com.example.utils.CurrencyFormatter.formatInr(1000000.0)) // 10 Lakhs
        assertEquals("₹1,00,00,000", com.example.utils.CurrencyFormatter.formatInr(10000000.0)) // 1 Crore
        assertEquals("-₹5,000", com.example.utils.CurrencyFormatter.formatInr(-5000.0))

        assertEquals("₹50.0 K", com.example.utils.CurrencyFormatter.formatInrCompact(50000.0))
        assertEquals("₹10.00 L", com.example.utils.CurrencyFormatter.formatInrCompact(1000000.0))
        assertEquals("₹1.50 Cr", com.example.utils.CurrencyFormatter.formatInrCompact(15000000.0))
        assertEquals("₹100.0 Cr", com.example.utils.CurrencyFormatter.formatInrCompact(1000000000.0))
    }

    @Test
    fun testPurchasingPower_inflationDiscounting() {
        // ₹1 Crore in 10 years at 6% inflation: PV = 10,000,000 / (1.06)^10 = 5,583,947.77
        val pv = FinancialMathEngine.calculatePurchasingPower(10000000.0, 6.0, 10)
        assertEquals(5583947.77, pv, 1.0)

        // Zero inflation retains 100% purchasing power
        val pvZero = FinancialMathEngine.calculatePurchasingPower(5000000.0, 0.0, 15)
        assertEquals(5000000.0, pvZero, 0.01)

        // Zero duration retains 100% purchasing power
        val pvZeroYears = FinancialMathEngine.calculatePurchasingPower(5000000.0, 7.0, 0)
        assertEquals(5000000.0, pvZeroYears, 0.01)

        // ₹2.40 Crore in 15 years at 6% inflation corresponds to ~₹1 Crore today
        val pv15 = FinancialMathEngine.calculatePurchasingPower(24000000.0, 6.0, 15)
        assertTrue("PV should be approximately 1 Crore", pv15 > 9900000.0 && pv15 < 10100000.0)
    }

    @Test
    fun testSoundManager_profilesAndTypesAvailable() {
        assertEquals(3, com.example.utils.SoundManager.SoundProfile.values().size)
        assertEquals(7, com.example.utils.SoundManager.SoundType.values().size)
        assertTrue(com.example.utils.SoundManager.SoundType.values().contains(com.example.utils.SoundManager.SoundType.ERROR))
        assertTrue(com.example.utils.SoundManager.SoundType.values().contains(com.example.utils.SoundManager.SoundType.SUCCESS))
    }
}
