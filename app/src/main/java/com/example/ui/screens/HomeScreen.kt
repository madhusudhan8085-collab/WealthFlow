package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.DisclaimerFooter
import com.example.ui.viewmodel.AnalyzeSubTab
import com.example.ui.viewmodel.MainDestination
import com.example.ui.viewmodel.PlanSubTab
import com.example.ui.viewmodel.WealthFlowUiState
import com.example.ui.viewmodel.WealthFlowViewModel
import com.example.utils.CurrencyFormatter

@Composable
fun HomeScreen(
    state: WealthFlowUiState,
    viewModel: WealthFlowViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_scroll"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // A. Welcome Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_welcome_card"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                ),
                shape = RoundedCornerShape(24.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.wealthflow_logo),
                                contentDescription = "WealthFlow Logo",
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Column {
                                Text(
                                    text = "WEALTHFLOW",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 1.2.sp
                                )
                                Text(
                                    text = "FINANCIAL PLANNING PLATFORM",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 9.5.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                com.example.utils.SoundManager.play(com.example.utils.SoundManager.SoundType.TAB)
                                viewModel.selectDestination(MainDestination.SETTINGS)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Plan your investments.\nUnderstand your future.",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 32.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Model compound interest, annual step-ups, inflation-proof retirement pensions, and stress test your portfolios with mathematical precision.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.selectPlanSubTab(PlanSubTab.SIP)
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Start a Plan", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.selectDestination(MainDestination.ANALYZE)
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Explore Analysis", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Fund Finder Highlight Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_fund_finder_banner"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.35f)
                ),
                shape = RoundedCornerShape(22.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Explore,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "NEW FEATURE",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        Text(
                            text = "SEBI Aligned",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Fund Finder",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Discover Mutual Funds Matching Your Goals & Risk Capacity",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.tertiary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Assess risk tolerance vs. loss absorption capacity, explore suitable categories, compare verified schemes, and build an illustrative monthly SIP plan.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            com.example.utils.SoundManager.play(com.example.utils.SoundManager.SoundType.TAB)
                            viewModel.selectPlanSubTab(PlanSubTab.FUND_FINDER)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_launch_fund_finder_button")
                    ) {
                        Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Launch Fund Finder", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Learning Hub Highlight Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_learning_hub_card"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f)
                ),
                shape = RoundedCornerShape(22.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "FREE EDUCATION",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.secondary,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        Text(
                            text = "Videos & Varsity",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Learning Hub",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Learn how to explore mutual funds and understand the stock market.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            com.example.utils.SoundManager.play(com.example.utils.SoundManager.SoundType.TAB)
                            viewModel.selectDestination(MainDestination.LEARNING_HUB)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_launch_learning_hub_button")
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Explore Learning Hub", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // B. Financial Overview (No fake data: shows actual saved plans or informative empty state)
        item {
            Text(
                text = "YOUR FINANCIAL OVERVIEW",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            if (state.savedPlans.isNotEmpty()) {
                val totalProjected = state.savedPlans.sumOf { it.primaryMetricValue }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Total Tracked Plan Targets",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = CurrencyFormatter.formatInr(totalProjected),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            ) {
                                Text(
                                    text = "${state.savedPlans.size} Active Plans",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(10.dp))

                        // Latest Plan Preview
                        val latest = state.savedPlans.first()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.loadPlanIntoCalculator(latest) },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Latest: ${latest.name}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${latest.type.displayName} • ${latest.tenureYears} Years @ ${latest.returnRateAnnual}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Open Plan",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            } else {
                // Honest, encouraging empty state
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "No active saved plans yet",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Design and save your first SIP, SWP or Goal plan to track your portfolio targets here.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        }
                        TextButton(
                            onClick = { viewModel.selectPlanSubTab(PlanSubTab.SIP) }
                        ) {
                            Text("Create")
                        }
                    }
                }
            }
        }

        // D. Quick Action Shortcuts
        item {
            Text(
                text = "QUICK ACTIONS",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionCard(
                    icon = Icons.Default.TrendingUp,
                    title = "New SIP",
                    onClick = { viewModel.selectPlanSubTab(PlanSubTab.SIP) },
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    icon = Icons.Default.AccountBalanceWallet,
                    title = "Retire SWP",
                    onClick = { viewModel.selectPlanSubTab(PlanSubTab.SWP) },
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    icon = Icons.Default.Flag,
                    title = "Goal Target",
                    onClick = { viewModel.selectPlanSubTab(PlanSubTab.GOAL) },
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    icon = Icons.Default.CompareArrows,
                    title = "Compare",
                    onClick = { viewModel.selectAnalyzeSubTab(AnalyzeSubTab.COMPARE) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // C. Feature Directory (Organized by category with icons and descriptions)
        item {
            Text(
                text = "FEATURE DIRECTORY",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        // Category 1: Investment Planning
        item {
            CategoryHeader(title = "Investment Planning", subtitle = "Wealth accumulation & compounding strategies")
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DirectoryItem(
                    icon = Icons.Default.TrendingUp,
                    title = "SIP Calculator",
                    description = "Predict recurring investment growth with exact beginning-of-period compounding",
                    onClick = { viewModel.selectPlanSubTab(PlanSubTab.SIP) }
                )
                DirectoryItem(
                    icon = Icons.Default.Upgrade,
                    title = "Step-Up SIP Calculator",
                    description = "Simulate annual salary increments with percentage or fixed annual contribution hikes",
                    onClick = { viewModel.selectPlanSubTab(PlanSubTab.SIP) }
                )
                DirectoryItem(
                    icon = Icons.Default.AltRoute,
                    title = "SIP → SWP Financial Journey",
                    description = "Seamlessly bridge career accumulation with post-retirement monthly distribution",
                    onClick = { viewModel.selectPlanSubTab(PlanSubTab.SIP_TO_SWP) }
                )
                DirectoryItem(
                    icon = Icons.Default.Explore,
                    title = "Fund Finder — Mutual Fund Discovery",
                    description = "SEBI risk profiling, asset suitability, fund comparison & illustrative monthly SIP plan",
                    onClick = { viewModel.selectPlanSubTab(PlanSubTab.FUND_FINDER) }
                )
                DirectoryItem(
                    icon = Icons.Default.School,
                    title = "Learning Hub",
                    description = "Learn how to explore mutual funds and understand the stock market.",
                    onClick = { viewModel.selectDestination(MainDestination.LEARNING_HUB) }
                )
            }
        }

        // Category 2: Withdrawal and Retirement
        item {
            CategoryHeader(title = "Withdrawal & Retirement", subtitle = "Systematic pension & capital preservation")
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DirectoryItem(
                    icon = Icons.Default.AccountBalanceWallet,
                    title = "SWP Pension Calculator",
                    description = "Plan predictable monthly cashflows from accumulated capital with annual inflation raises",
                    onClick = { viewModel.selectPlanSubTab(PlanSubTab.SWP) }
                )
                DirectoryItem(
                    icon = Icons.Default.HourglassBottom,
                    title = "Corpus Depletion & Longevity",
                    description = "Analyze exact timeline when capital runs out under varying withdrawal and return rates",
                    onClick = { viewModel.selectPlanSubTab(PlanSubTab.SWP) }
                )
                DirectoryItem(
                    icon = Icons.Default.AllInclusive,
                    title = "Perpetual Safe Withdrawal",
                    description = "Calculate the maximum monthly payout that leaves your core capital intact forever",
                    onClick = { viewModel.selectPlanSubTab(PlanSubTab.SWP) }
                )
            }
        }

        // Category 3: Goals & Inflation
        item {
            CategoryHeader(title = "Goals & Inflation", subtitle = "Target-based reverse planning & purchasing power")
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DirectoryItem(
                    icon = Icons.Default.Flag,
                    title = "Goal-Based Reverse SIP",
                    description = "Calculate required monthly savings to achieve target milestones like house or education",
                    onClick = { viewModel.selectPlanSubTab(PlanSubTab.GOAL) }
                )
                DirectoryItem(
                    icon = Icons.Default.PriceCheck,
                    title = "Inflation Purchasing Power",
                    description = "See how much ₹50 Lakhs in today's money requires in 10-30 years to beat inflation",
                    onClick = { viewModel.selectAnalyzeSubTab(AnalyzeSubTab.INFLATION) }
                )
            }
        }

        // Category 4: Analysis & Stress Testing
        item {
            CategoryHeader(title = "Analysis & Comparisons", subtitle = "Scenario stress testing & sensitivity")
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DirectoryItem(
                    icon = Icons.Default.CompareArrows,
                    title = "Compare Plans (Plan A vs B)",
                    description = "Put two distinct investment strategies side-by-side with difference analysis",
                    onClick = { viewModel.selectAnalyzeSubTab(AnalyzeSubTab.COMPARE) }
                )
                DirectoryItem(
                    icon = Icons.Default.Bolt,
                    title = "What-If Simulator",
                    description = "Interactive sandbox testing the effect of adding +₹5K SIP or +3 years to horizon",
                    onClick = { viewModel.selectAnalyzeSubTab(AnalyzeSubTab.WHAT_IF) }
                )
                DirectoryItem(
                    icon = Icons.Default.Lightbulb,
                    title = "SIP Financial Insights",
                    description = "Rule of 72 doubling time, compounding power, and cost of delay analysis",
                    onClick = { viewModel.selectAnalyzeSubTab(AnalyzeSubTab.INSIGHTS) }
                )
                DirectoryItem(
                    icon = Icons.Default.Shield,
                    title = "Return Scenarios (Stress Test)",
                    description = "Examine Conservative (8%), Base (12%), and Optimistic (15%) portfolio trajectories",
                    onClick = { viewModel.selectAnalyzeSubTab(AnalyzeSubTab.SCENARIOS) }
                )
            }
        }

        // E. Financial Insights Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = "FINANCIAL GOLDEN RULES",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• Rule of 72: At 12% annual return, your money doubles approximately every 6 years.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Compounding Friction: Delaying investments by just 3 years can cost upwards of 35% in terminal wealth.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Safe Withdrawal Rate: Keeping initial SWP withdrawals between 4% to 6% maximizes lifetime portfolio survival.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        item {
            DisclaimerFooter()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = CardDefaults.outlinedCardBorder(),
        modifier = modifier
            .height(72.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun CategoryHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(top = 4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DirectoryItem(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
