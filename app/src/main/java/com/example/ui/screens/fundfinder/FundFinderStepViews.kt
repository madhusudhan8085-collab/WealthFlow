package com.example.ui.screens.fundfinder

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.fundfinder.*
import com.example.ui.viewmodel.WealthFlowUiState
import com.example.ui.viewmodel.WealthFlowViewModel
import com.example.utils.CurrencyFormatter
import com.example.utils.ExportUtils
import com.example.utils.SoundManager

// ==========================================
// STEP 0: WELCOME & JOURNEY OVERVIEW
// ==========================================
@Composable
fun WelcomeStepView(
    onStart: () -> Unit,
    onOpenGlossary: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("fund_finder_welcome_card"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        ),
        shape = RoundedCornerShape(24.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(10.dp)
                ) {}
                Text(
                    text = "SEBI CATEGORY DISCOVERY",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Fund Finder",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Discover Mutual Funds That Match Your Financial Goals",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Most platforms push high-commission or volatile funds based solely on your age. WealthFlow Fund Finder is different: we use a transparent, deterministic suitability engine that separates emotional risk tolerance from actual financial risk capacity.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4 Key Pillars
            val pillars = listOf(
                Pair("Deterministic Profiling", "Tolerance vs. Capacity matrix with strict capital protection rules."),
                Pair("SEBI Horizon Matching", "No equities for <3 year goals. Strict satellite caps on mid & small caps."),
                Pair("Verified AMFI Disclosures", "Direct plans, low expense ratios (TER), and standardized Riskometer badges."),
                Pair("Actionable SIP Bridge", "Convert matched allocations directly into WealthFlow's SIP Calculator.")
            )

            pillars.forEach { (title, desc) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onStart,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("start_fund_finder_button")
                ) {
                    Text("Start Discovery", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }

                OutlinedButton(
                    onClick = onOpenGlossary,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("open_glossary_welcome_button")
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Glossary", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ==========================================
// STEP 1: INVESTOR PROFILE
// ==========================================
@Composable
fun InvestorProfileStepView(
    profile: InvestorProfile,
    onUpdate: (InvestorProfile) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("investor_profile_step_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = "Step 1: Investor Background",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Age provides context on working years, but will never be used alone to force risky funds.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Age Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Your Age", fontWeight = FontWeight.SemiBold)
                Text(
                    text = "${profile.age} Years",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Slider(
                value = profile.age.toFloat(),
                onValueChange = { onUpdate(profile.copy(age = it.toInt())) },
                valueRange = 18f..75f,
                steps = 56,
                modifier = Modifier.testTag("investor_age_slider")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Monthly Investment Budget
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Monthly Investment Budget", fontWeight = FontWeight.SemiBold)
                Text(
                    text = CurrencyFormatter.formatInr(profile.monthlyBudgetInr),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Slider(
                value = profile.monthlyBudgetInr.toFloat(),
                onValueChange = { onUpdate(profile.copy(monthlyBudgetInr = (Math.round(it / 1000f) * 1000).toDouble())) },
                valueRange = 1000f..150000f,
                steps = 148,
                modifier = Modifier.testTag("investor_budget_slider")
            )

            // Budget Quick Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(5000.0, 10000.0, 15000.0, 25000.0, 50000.0, 100000.0).forEach { amount ->
                    val isSel = profile.monthlyBudgetInr == amount
                    FilterChip(
                        selected = isSel,
                        onClick = {
                            SoundManager.play(SoundManager.SoundType.POP)
                            onUpdate(profile.copy(monthlyBudgetInr = amount))
                        },
                        label = { Text(CurrencyFormatter.formatInrCompact(amount)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Investment Experience
            Text("Investment Experience", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            InvestmentExperience.values().forEach { exp ->
                val isSelected = profile.experience == exp
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    border = if (isSelected) CardDefaults.outlinedCardBorder() else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            SoundManager.play(SoundManager.SoundType.POP)
                            onUpdate(profile.copy(experience = exp))
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                SoundManager.play(SoundManager.SoundType.POP)
                                onUpdate(profile.copy(experience = exp))
                            }
                        )
                        Column {
                            Text(exp.title, fontWeight = FontWeight.Bold)
                            Text(exp.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Income Stability
            Text("Income Stability", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            IncomeStability.values().forEach { stab ->
                val isSelected = profile.incomeStability == stab
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    border = if (isSelected) CardDefaults.outlinedCardBorder() else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            SoundManager.play(SoundManager.SoundType.POP)
                            onUpdate(profile.copy(incomeStability = stab))
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                SoundManager.play(SoundManager.SoundType.POP)
                                onUpdate(profile.copy(incomeStability = stab))
                            }
                        )
                        Column {
                            Text(stab.title, fontWeight = FontWeight.Bold)
                            Text(stab.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// STEP 2: FINANCIAL GOALS
// ==========================================
@Composable
fun GoalsStepView(
    goal: FundFinderGoal,
    onUpdate: (FundFinderGoal) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("financial_goal_step_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = "Step 2: Financial Goal & Horizon",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Different goals require fundamentally different asset allocations. Equities are strictly avoided for short-term goals.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Goal Type Selector
            Text("Select Your Primary Goal", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GoalType.values().forEach { type ->
                    val isSelected = goal.type == type
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            SoundManager.play(SoundManager.SoundType.POP)
                            onUpdate(goal.copy(type = type, name = type.displayName, durationYears = type.typicalHorizonYears))
                        },
                        label = { Text(type.displayName) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Target Amount Today
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Target in Today's Money", fontWeight = FontWeight.SemiBold)
                Text(
                    text = CurrencyFormatter.formatInr(goal.targetAmountToday),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Slider(
                value = goal.targetAmountToday.toFloat(),
                onValueChange = { onUpdate(goal.copy(targetAmountToday = (Math.round(it / 50000f) * 50000).toDouble())) },
                valueRange = 100000f..20000000f,
                steps = 199,
                modifier = Modifier.testTag("goal_target_slider")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Investment Duration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Investment Horizon", fontWeight = FontWeight.SemiBold)
                Text(
                    text = "${goal.durationYears} Years",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Slider(
                value = goal.durationYears.toFloat(),
                onValueChange = { onUpdate(goal.copy(durationYears = it.toInt())) },
                valueRange = 1f..25f,
                steps = 23,
                modifier = Modifier.testTag("goal_duration_slider")
            )

            // Horizon Rule Callout if < 3 years
            if (goal.durationYears < 3) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Text(
                            text = "Horizon < 3 Years: Equities carry severe capital loss risk over 1-2 years. WealthFlow will only match capital-preservation Liquid & Short Debt funds.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Liquidity Access Need
            Text("Liquidity Access Need", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Will you need emergency or early access to this money before your horizon?",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LiquidityNeed.values().forEach { need ->
                    val isSel = goal.liquidityNeed == need
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSel) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        border = if (isSel) CardDefaults.outlinedCardBorder() else null,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                SoundManager.play(SoundManager.SoundType.POP)
                                onUpdate(goal.copy(liquidityNeed = need))
                            }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = isSel, onClick = {
                                    SoundManager.play(SoundManager.SoundType.POP)
                                    onUpdate(goal.copy(liquidityNeed = need))
                                })
                                Text(need.label, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Text(need.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Assumed Annual Inflation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Assumed Annual Inflation", fontWeight = FontWeight.SemiBold)
                Text(
                    text = "${goal.assumedInflationRate}% p.a.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Slider(
                value = goal.assumedInflationRate.toFloat(),
                onValueChange = { onUpdate(goal.copy(assumedInflationRate = Math.round(it * 10f) / 10.0)) },
                valueRange = 4.0f..10.0f,
                steps = 11,
                modifier = Modifier.testTag("goal_inflation_slider")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Inflation Impact Comparison Box
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Inflation-Adjusted Target",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Due to ${goal.assumedInflationRate}% annual inflation, a ₹${CurrencyFormatter.formatInrCompact(goal.targetAmountToday)} expense today will require ${CurrencyFormatter.formatInr(goal.futureInflatedTarget)} in ${goal.durationYears} years.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

// ==========================================
// STEP 3: RISK ASSESSMENT QUESTIONNAIRE
// ==========================================
@Composable
fun RiskAssessmentStepView(
    answers: Map<String, Int>,
    onAnswer: (String, Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("risk_assessment_questions_container"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Step 3: Risk Assessment",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Distinguishing psychological risk tolerance (emotional comfort) from financial risk capacity (financial ability to absorb drawdowns).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Section A: Psychological Risk Tolerance
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "PART A: PSYCHOLOGICAL RISK TOLERANCE",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Assessing your emotional willingness to endure portfolio swings and prolonged market downturns.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        val tolQuestions = RiskScoringEngine.questions.filter { it.dimension == RiskDimension.TOLERANCE }
        tolQuestions.forEachIndexed { idx, q ->
            RenderQuestionCard(q, idx + 1, answers[q.id] ?: 3, onAnswer)
        }

        // Section B: Financial Risk Capacity
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "PART B: FINANCIAL RISK CAPACITY",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Evaluating your financial safety buffer (emergency fund, debt status, and impact on living expenses).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        val capQuestions = RiskScoringEngine.questions.filter { it.dimension == RiskDimension.CAPACITY }
        capQuestions.forEachIndexed { idx, q ->
            RenderQuestionCard(q, tolQuestions.size + idx + 1, answers[q.id] ?: 3, onAnswer)
        }

        // Section C: Investment Knowledge & Experience
        val knowQuestions = RiskScoringEngine.questions.filter { it.dimension == RiskDimension.KNOWLEDGE }
        if (knowQuestions.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "PART C: INVESTMENT KNOWLEDGE",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Familiarity with mutual fund structures, direct plans, and compounding cycles.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            knowQuestions.forEachIndexed { idx, q ->
                RenderQuestionCard(q, tolQuestions.size + capQuestions.size + idx + 1, answers[q.id] ?: 3, onAnswer)
            }
        }
    }
}

@Composable
private fun RenderQuestionCard(
    question: RiskQuestion,
    displayNumber: Int,
    selectedOptionValue: Int,
    onAnswer: (String, Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("risk_question_${question.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (question.dimension) {
                        RiskDimension.TOLERANCE -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        RiskDimension.CAPACITY -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                        RiskDimension.KNOWLEDGE -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = "Q$displayNumber • ${question.dimension.name}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (question.dimension) {
                            RiskDimension.TOLERANCE -> MaterialTheme.colorScheme.primary
                            RiskDimension.CAPACITY -> MaterialTheme.colorScheme.tertiary
                            RiskDimension.KNOWLEDGE -> MaterialTheme.colorScheme.secondary
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = question.prompt,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = question.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            question.options.forEach { opt ->
                val isSel = selectedOptionValue == opt.scoreValue
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSel) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                    border = if (isSel) CardDefaults.outlinedCardBorder() else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            SoundManager.play(SoundManager.SoundType.POP)
                            onAnswer(question.id, opt.scoreValue)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RadioButton(
                            selected = isSel,
                            onClick = {
                                SoundManager.play(SoundManager.SoundType.POP)
                                onAnswer(question.id, opt.scoreValue)
                            }
                        )
                        Column {
                            Text(opt.label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text(opt.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// STEP 4: RISK RESULTS & RATIONALE
// ==========================================
@Composable
fun RiskResultStepView(
    result: RiskAssessmentResult
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("risk_results_step_container"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            ),
            shape = RoundedCornerShape(22.dp),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Step 4: Your Investor Profile",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = result.profile.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = result.profile.headline,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Score Gauges
                ScoreBar("Risk Tolerance (Willingness)", result.toleranceScore, MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                ScoreBar("Financial Capacity (Loss Absorption)", result.capacityScore, MaterialTheme.colorScheme.tertiary)
                Spacer(modifier = Modifier.height(8.dp))
                ScoreBar("Investment Knowledge", result.knowledgeScore, MaterialTheme.colorScheme.secondary)

                Spacer(modifier = Modifier.height(14.dp))

                // Mismatch Notice
                if (result.hasToleranceCapacityMismatch && result.mismatchExplanation != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "Capital Protection Guard Applied",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = result.mismatchExplanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Detailed Rationale
                Text(
                    text = result.rationale,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Composable
private fun ScoreBar(label: String, score: Int, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
            Text("$score / 100", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

// ==========================================
// STEP 4: SUITABILITY RESULT & CATEGORY GUIDANCE
// ==========================================
@Composable
fun SuitabilityResultStepView(
    state: WealthFlowUiState,
    viewModel: WealthFlowViewModel
) {
    val guidance = state.primaryCategoryGuidance
    val riskResult = state.riskResult
    val goal = state.fundFinderGoal
    val context = LocalContext.current
    var showSchemeDetails by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("suitability_result_step_container"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Investor Profile & Capacity Summary Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Step 4: Suitability Recommendation",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Risk Assessment: ${riskResult.profile.title} Investor",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                ScoreBar("Risk Tolerance (Willingness)", riskResult.toleranceScore, MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(6.dp))
                ScoreBar("Financial Capacity (Loss Cushion)", riskResult.capacityScore, MaterialTheme.colorScheme.tertiary)
            }
        }

        // Hero Recommendation Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("primary_category_hero_card"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            ),
            shape = RoundedCornerShape(24.dp),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = "PRIMARY SUITABLE CATEGORY",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = guidance.primaryCategory.riskLevelText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = guidance.primaryCategory.displayName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = guidance.primaryCategory.headline,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Tolerance vs Capacity Conflict Warning (if applicable)
                if (guidance.hasToleranceCapacityMismatch && guidance.mismatchWarning != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                            Column {
                                Text(
                                    text = "Tolerance vs. Capacity Conflict Resolved",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = guidance.mismatchWarning,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Why this category suits your profile
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Text(
                                text = "Why This Category Suits You",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = guidance.whySuited,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 21.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Key Category Parameters Grid
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Recommended Horizon:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${guidance.primaryCategory.idealHorizonYears}+ Years (${goal.durationYears}Y Goal)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Historical Return Range:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(guidance.primaryCategory.expectedReturnRange, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Typical Asset Allocation:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(guidance.primaryCategory.typicalAssetMix, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Main Risks
                Text("Main Risks & Volatility Characteristics", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                guidance.mainRisks.forEach { risk ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                        Text(risk, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Limitations
                Text("Important Limitations to Keep in Mind", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                guidance.limitations.forEach { limit ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(14.dp))
                        Text(limit, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Load into SIP Calculator CTA
                Button(
                    onClick = {
                        SoundManager.play(SoundManager.SoundType.SUCCESS)
                        viewModel.loadPrimaryCategoryIntoCalculator()
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("load_category_sip_button")
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Load into SIP Calculator (₹${guidance.suggestedSipAmount.toInt()}/mo)",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Toggle Verified Scheme Examples
                OutlinedButton(
                    onClick = {
                        showSchemeDetails = !showSchemeDetails
                        SoundManager.play(SoundManager.SoundType.POP)
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (showSchemeDetails) "Hide Category Examples" else "Explore Verified Direct Scheme Examples",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Expandable Verified Scheme Examples
        if (showSchemeDetails) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Verified Category Reference Schemes (Direct - Growth)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("AMFI snapshots for educational illustration only. Zero commission Direct plans.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(10.dp))

                    val relevantSchemes = FundDataProvider.verifiedSchemes.filter { sch ->
                        when (guidance.primaryCategory) {
                            PrimaryFundCategory.LARGE_CAP -> sch.category == SebiCategory.LARGE_CAP || sch.category == SebiCategory.INDEX_FUND || sch.category == SebiCategory.FLEXI_CAP
                            PrimaryFundCategory.MID_CAP -> sch.category == SebiCategory.MID_CAP || sch.category == SebiCategory.LARGE_AND_MID
                            PrimaryFundCategory.SMALL_CAP -> sch.category == SebiCategory.SMALL_CAP || sch.category == SebiCategory.MID_CAP
                            PrimaryFundCategory.DEBT_HYBRID_ALTERNATIVE -> sch.category == SebiCategory.LIQUID || sch.category == SebiCategory.SHORT_DURATION || sch.category == SebiCategory.CONSERVATIVE_HYBRID || sch.category == SebiCategory.BALANCED_ADVANTAGE
                        }
                    }

                    relevantSchemes.take(4).forEach { sch ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(sch.schemeName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("TER: ${sch.expenseRatioPercent}% | Risk: ${sch.riskometer.title}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("3Y: ${sch.returns3Y}% p.a.", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Statutory SEBI Disclaimer Notice Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Gavel, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    Text(
                        text = "Mandatory SEBI Regulatory Warning",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = guidance.regulatoryDisclaimer,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// ==========================================
// STEP 5: CATEGORY DISCOVERY & ALLOCATION
// ==========================================
@Composable
fun CategoryDiscoveryStepView(
    recommendations: List<CategoryRecommendation>,
    goal: FundFinderGoal,
    onExploreSchemes: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("category_discovery_step_container"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Step 5: Suitable Mutual Fund Categories",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Asset allocation tailored for your ${goal.durationYears}-year ${goal.name} goal.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Category Recommendations List
                recommendations.forEach { rec ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = rec.sebiCategory.displayName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = rec.role.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "${rec.suggestedAllocationPercent}% Allocation",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = rec.rationale,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Caution Alert
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = rec.cautionNotes,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// STEP 6: SCHEME EXPLORER & VERIFIED DATA
// ==========================================
@Composable
fun FundExplorerStepView(
    state: WealthFlowUiState,
    viewModel: WealthFlowViewModel
) {
    val allSchemes = FundDataProvider.verifiedSchemes
    val filteredSchemes = remember(state.selectedCategoryForFilter, state.fundSearchQuery) {
        val base = if (state.selectedCategoryForFilter != null) {
            allSchemes.filter { it.category == state.selectedCategoryForFilter }
        } else {
            allSchemes
        }
        if (state.fundSearchQuery.isBlank()) {
            base
        } else {
            val q = state.fundSearchQuery.trim().lowercase()
            base.filter {
                it.schemeName.lowercase().contains(q) ||
                it.amcName.lowercase().contains(q) ||
                it.category.displayName.lowercase().contains(q)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("fund_explorer_step_container"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Data notice
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Step 6: Verified Scheme Disclosures",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = FundDataProvider.DATA_SOURCE_NOTICE,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Search Box
                OutlinedTextField(
                    value = state.fundSearchQuery,
                    onValueChange = { viewModel.updateFundSearchQuery(it) },
                    label = { Text("Search Scheme, AMC, or Benchmark...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (state.fundSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateFundSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fund_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = state.selectedCategoryForFilter == null,
                        onClick = { viewModel.filterFundExplorerByCategory(null) },
                        label = { Text("All Categories (${allSchemes.size})") }
                    )
                    state.categoryRecommendations.map { it.sebiCategory }.distinct().forEach { cat ->
                        FilterChip(
                            selected = state.selectedCategoryForFilter == cat,
                            onClick = {
                                viewModel.filterFundExplorerByCategory(
                                    if (state.selectedCategoryForFilter == cat) null else cat
                                )
                            },
                            label = { Text("★ ${cat.displayName}") }
                        )
                    }
                }
            }
        }

        // Scheme Cards
        Text(
            text = "Showing ${filteredSchemes.size} Schemes (${state.comparedSchemeAmfiCodes.size}/4 Selected for Comparison)",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        filteredSchemes.forEach { scheme ->
            val isCompared = state.comparedSchemeAmfiCodes.contains(scheme.amfiCode)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("scheme_card_${scheme.amfiCode}"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp),
                border = if (isCompared) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = scheme.amcName,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = scheme.schemeName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${scheme.category.displayName} • ${scheme.planType.title} • ${scheme.option.title}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = { viewModel.toggleFundComparison(scheme.amfiCode) },
                            modifier = Modifier.testTag("toggle_compare_${scheme.amfiCode}")
                        ) {
                            Icon(
                                imageVector = if (isCompared) Icons.Default.CheckCircle else Icons.Default.AddCircleOutline,
                                contentDescription = "Compare",
                                tint = if (isCompared) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Metrics Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricItem("NAV", "₹${scheme.nav}")
                        MetricItem("TER (Expense)", "${scheme.expenseRatioPercent}%")
                        MetricItem("3Y CAGR", "${scheme.returns3Y}%")
                        MetricItem("Riskometer", scheme.riskometer.title)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Benchmark: ${scheme.benchmarkIndex}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Min SIP: ₹${scheme.minSipAmount.toInt()}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

// ==========================================
// STEP 7: COMPARE & ILLUSTRATIVE SIP PLAN
// ==========================================
@Composable
fun FundCompareAndSipStepView(
    state: WealthFlowUiState,
    viewModel: WealthFlowViewModel
) {
    val context = LocalContext.current
    val comparedSchemes = state.comparedSchemeAmfiCodes.mapNotNull { FundDataProvider.getSchemeByAmfiCode(it) }
    val plan = state.illustrativePlan

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("fund_compare_sip_step_container"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Comparison Matrix (if any selected)
        if (comparedSchemes.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Fund Comparison (${comparedSchemes.size} of 4)",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { viewModel.clearFundComparison() }) {
                            Text("Clear All")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Horizontal scrollable comparison table
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        comparedSchemes.forEach { scheme ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                border = CardDefaults.outlinedCardBorder(),
                                modifier = Modifier.width(220.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = scheme.schemeName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2
                                    )
                                    Text(
                                        text = scheme.category.displayName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))
                                    ComparisonRow("TER (Expense)", "${scheme.expenseRatioPercent}%")
                                    ComparisonRow("Riskometer", scheme.riskometer.title)
                                    ComparisonRow("1Y CAGR", "${scheme.returns1Y}%")
                                    ComparisonRow("3Y CAGR", "${scheme.returns3Y}%")
                                    ComparisonRow("5Y CAGR", "${scheme.returns5Y}%")
                                    ComparisonRow("Min SIP", "₹${scheme.minSipAmount.toInt()}")
                                    ComparisonRow("Exit Load", scheme.exitLoadSummary)

                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedButton(
                                        onClick = { viewModel.toggleFundComparison(scheme.amfiCode) },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Remove", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Illustrative Monthly SIP Plan
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            ),
            shape = RoundedCornerShape(22.dp),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Illustrative Monthly SIP Allocation",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Distributes your ₹${CurrencyFormatter.formatInrCompact(plan.totalMonthlyBudget)}/mo budget across recommended asset categories for ${plan.durationYears} years.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Breakdown list
                plan.allocations.forEach { alloc ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = alloc.category.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${alloc.role.label} • ${alloc.percentage}% of portfolio",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                alloc.recommendedScheme?.let { sch ->
                                    Text(
                                        text = "e.g. ${sch.schemeName}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Text(
                                text = CurrencyFormatter.formatInr(alloc.monthlyAmount) + "/mo",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Summary projection
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Blended Expected Return:", style = MaterialTheme.typography.bodyMedium)
                            Text("${plan.estimatedBlendedCagr}% p.a.", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Invested (with 10% Step-Up):", style = MaterialTheme.typography.bodyMedium)
                            Text(CurrencyFormatter.formatInr(plan.totalInvested), fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Projected Future Corpus:", style = MaterialTheme.typography.bodyMedium)
                            Text(CurrencyFormatter.formatInr(plan.projectedCorpus), fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Primary CTAs: Load into SIP Calculator & Export Report
                Button(
                    onClick = { viewModel.loadIllustrativeSipIntoCalculator() },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("load_into_sip_calculator_button")
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Load into WealthFlow SIP Calculator", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {
                        val reportText = buildString {
                            appendLine("==================================================")
                            appendLine("WEALTHFLOW FUND FINDER DISCOVERY REPORT")
                            appendLine("Generated: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US).format(java.util.Date())}")
                            appendLine("==================================================")
                            appendLine("\n1. INVESTOR PROFILE")
                            appendLine("Age: ${state.investorProfile.age} Years")
                            appendLine("Monthly Budget: ₹${state.investorProfile.monthlyBudgetInr.toInt()}")
                            appendLine("Experience: ${state.investorProfile.experience.title}")
                            appendLine("Income Stability: ${state.investorProfile.incomeStability.title}")
                            appendLine("\n2. FINANCIAL GOAL")
                            appendLine("Goal: ${state.fundFinderGoal.name}")
                            appendLine("Horizon: ${state.fundFinderGoal.durationYears} Years")
                            appendLine("Target Today: ₹${state.fundFinderGoal.targetAmountToday.toInt()}")
                            appendLine("Inflation-Adjusted Target: ₹${state.fundFinderGoal.futureInflatedTarget.toInt()} (@ ${state.fundFinderGoal.assumedInflationRate}% p.a.)")
                            appendLine("\n3. RISK PROFILE RESULT")
                            appendLine("Risk Profile: ${state.riskResult.profile.title}")
                            appendLine("Risk Tolerance Score: ${state.riskResult.toleranceScore}/100")
                            appendLine("Financial Capacity Score: ${state.riskResult.capacityScore}/100")
                            appendLine("Investment Knowledge Score: ${state.riskResult.knowledgeScore}/100")
                            if (state.riskResult.hasToleranceCapacityMismatch) {
                                appendLine("Capacity Constraint Note: ${state.riskResult.mismatchExplanation}")
                            }
                            appendLine("Rationale: ${state.riskResult.rationale}")
                            appendLine("\n4. RECOMMENDED SEBI CATEGORIES")
                            state.categoryRecommendations.forEach { rec ->
                                appendLine("• ${rec.sebiCategory.displayName} (${rec.suggestedAllocationPercent}%): Role ${rec.role.label}")
                                appendLine("  Rationale: ${rec.rationale}")
                            }
                            appendLine("\n5. ILLUSTRATIVE MONTHLY SIP PLAN")
                            plan.allocations.forEach { alloc ->
                                appendLine("• ${alloc.category.displayName}: ₹${alloc.monthlyAmount.toInt()}/mo (${alloc.percentage}%)")
                            }
                            appendLine("Total Monthly SIP: ₹${plan.totalMonthlyBudget.toInt()}/mo")
                            appendLine("Estimated Blended CAGR: ${plan.estimatedBlendedCagr}%")
                            appendLine("Projected Corpus (${plan.durationYears}Y): ₹${plan.projectedCorpus.toInt()}")
                            appendLine("\n==================================================")
                            appendLine(FundEducationData.SEBI_REGULATORY_DISCLAIMER)
                            appendLine("==================================================")
                        }
                        ExportUtils.shareFundDiscoveryReport(context, reportText)
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("export_fund_discovery_report_button")
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export Discovery Report", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Regulatory Notice Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Statutory Regulatory Notice",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = FundEducationData.SEBI_REGULATORY_DISCLAIMER,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun ComparisonRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
    }
}
