package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.screens.learning.LearningHubScreen
import com.example.ui.viewmodel.MainDestination
import com.example.ui.viewmodel.WealthFlowUiState
import com.example.ui.viewmodel.WealthFlowViewModel
import com.example.utils.SoundManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WealthFlowMainScreen(
    viewModel: WealthFlowViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Initialize sound engine
    LaunchedEffect(Unit) {
        SoundManager.init(context)
    }

    // Show snackbar notifications (e.g. plan saved, plan deleted)
    LaunchedEffect(state.snackbarMessage) {
        state.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    // Android back handler: pop back to Home if in another screen or step back in Learning Hub
    BackHandler(enabled = state.currentDestination != MainDestination.HOME) {
        SoundManager.play(SoundManager.SoundType.TAB)
        if (state.currentDestination == MainDestination.LEARNING_HUB && state.selectedLearningLanguage != null) {
            viewModel.clearLearningLanguage()
        } else {
            viewModel.selectDestination(MainDestination.HOME)
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isDesktopOrTablet = maxWidth >= 840.dp

        if (isDesktopOrTablet) {
            // Tablet / Desktop layout with Collapsible Left Navigation Sidebar
            Row(modifier = Modifier.fillMaxSize()) {
                DesktopSidebar(
                    state = state,
                    viewModel = viewModel,
                    modifier = Modifier.width(260.dp)
                )

                VerticalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                Scaffold(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    contentWindowInsets = ScaffoldDefaults.contentWindowInsets
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        ActiveScreenContent(state = state, viewModel = viewModel)
                    }
                }
            }
        } else {
            // Mobile-First Layout: Top Bar + Active Content + 4 Permanent Bottom Nav Items
            val topBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
                    .testTag("wealthflow_main_scaffold"),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
                topBar = {
                    if (state.currentDestination != MainDestination.LEARNING_HUB) {
                        TopAppBar(
                            scrollBehavior = topBarScrollBehavior,
                        title = {
                            if (state.currentDestination == MainDestination.SETTINGS) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    IconButton(
                                        onClick = {
                                            SoundManager.play(SoundManager.SoundType.TAB)
                                            viewModel.selectDestination(MainDestination.HOME)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back"
                                        )
                                    }
                                    Text(
                                        text = "Settings",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.wealthflow_logo),
                                            contentDescription = "WealthFlow Logo",
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                        )
                                        Text(
                                            text = "WealthFlow",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Text(
                                        text = "Plan today. Understand tomorrow.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 10.5.sp
                                    )
                                }
                            }
                        },
                        actions = {
                            // Sound Effects Toggle
                            IconButton(
                                onClick = {
                                    viewModel.toggleSound()
                                },
                                modifier = Modifier.testTag("sound_toggle_button")
                            ) {
                                Icon(
                                    imageVector = if (state.isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                    contentDescription = "Sound Settings",
                                    tint = if (state.isSoundEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Theme Toggle
                            IconButton(
                                onClick = {
                                    SoundManager.play(if (state.isDarkTheme) SoundManager.SoundType.TOGGLE_OFF else SoundManager.SoundType.TOGGLE_ON)
                                    viewModel.toggleTheme()
                                },
                                modifier = Modifier.testTag("theme_toggle_button")
                            ) {
                                Icon(
                                    imageVector = if (state.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = "Toggle theme",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Profile / Settings Icon (Never in bottom bar)
                            IconButton(
                                onClick = {
                                    SoundManager.play(SoundManager.SoundType.TAB)
                                    viewModel.selectDestination(MainDestination.SETTINGS)
                                },
                                modifier = Modifier.testTag("nav_settings_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = if (state.currentDestination == MainDestination.SETTINGS)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
                },
                bottomBar = {
                    // Mobile 4-Item Permanent Bottom Navigation: Home, Plan, Analyze, My Plans
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp,
                        windowInsets = WindowInsets.navigationBars,
                        modifier = Modifier.testTag("wealthflow_bottom_navigation")
                    ) {
                        val bottomNavItems = listOf(
                            Triple(MainDestination.HOME, Icons.Default.Home, "Home"),
                            Triple(MainDestination.PLAN, Icons.Default.TrendingUp, "Plan"),
                            Triple(MainDestination.ANALYZE, Icons.Default.Assessment, "Analyze"),
                            Triple(MainDestination.MY_PLANS, Icons.Default.BookmarkBorder, "My Plans")
                        )

                        bottomNavItems.forEach { (destination, icon, label) ->
                            val isSelected = state.currentDestination == destination

                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    if (!isSelected) {
                                        SoundManager.play(SoundManager.SoundType.TAB)
                                        viewModel.selectDestination(destination)
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.testTag("nav_item_${label.lowercase().replace(" ", "_")}")
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    ActiveScreenContent(state = state, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
private fun ActiveScreenContent(
    state: WealthFlowUiState,
    viewModel: WealthFlowViewModel
) {
    Crossfade(targetState = state.currentDestination, label = "main_screen_crossfade") { dest ->
        when (dest) {
            MainDestination.HOME -> HomeScreen(state = state, viewModel = viewModel)
            MainDestination.PLAN -> PlanScreen(state = state, viewModel = viewModel)
            MainDestination.ANALYZE -> AnalyzeScreen(state = state, viewModel = viewModel)
            MainDestination.MY_PLANS -> MyPlansScreen(state = state, viewModel = viewModel)
            MainDestination.SETTINGS -> SettingsScreen(state = state, viewModel = viewModel)
            MainDestination.LEARNING_HUB -> LearningHubScreen(state = state, viewModel = viewModel)
        }
    }
}

@Composable
private fun DesktopSidebar(
    state: WealthFlowUiState,
    viewModel: WealthFlowViewModel,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // WealthFlow Branding Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.wealthflow_logo),
                        contentDescription = "WealthFlow Logo",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Column {
                        Text(
                            text = "WealthFlow",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Plan today. Understand tomorrow.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(6.dp))

                // Navigation Items
                val sidebarItems = listOf(
                    Triple(MainDestination.HOME, Icons.Default.Home, "Home"),
                    Triple(MainDestination.PLAN, Icons.Default.TrendingUp, "Plan"),
                    Triple(MainDestination.ANALYZE, Icons.Default.Assessment, "Analyze"),
                    Triple(MainDestination.MY_PLANS, Icons.Default.BookmarkBorder, "My Plans"),
                    Triple(MainDestination.LEARNING_HUB, Icons.Default.School, "Learning Hub"),
                    Triple(MainDestination.SETTINGS, Icons.Default.Settings, "Settings")
                )

                sidebarItems.forEach { (dest, icon, label) ->
                    val isSelected = state.currentDestination == dest
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (!isSelected) {
                                    SoundManager.play(SoundManager.SoundType.TAB)
                                    viewModel.selectDestination(dest)
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Bottom Quick Controls: Theme & Sound
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.toggleSound() }
                    ) {
                        Icon(
                            imageVector = if (state.isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Toggle Sound",
                            tint = if (state.isSoundEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = { viewModel.toggleTheme() }
                    ) {
                        Icon(
                            imageVector = if (state.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
