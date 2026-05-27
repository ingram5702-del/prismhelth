package com.prismwin.apps.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.prismwin.apps.core.appViewModel
import com.prismwin.apps.ui.screens.HomeScreen
import com.prismwin.apps.ui.screens.OnboardingScreen
import com.prismwin.apps.ui.screens.QrScannerScreen
import com.prismwin.apps.ui.screens.ReactionScreen
import com.prismwin.apps.ui.screens.SequenceScreen
import com.prismwin.apps.ui.screens.SettingsScreen
import com.prismwin.apps.ui.screens.StatsScreen
import com.prismwin.apps.ui.screens.TargetScreen
import com.prismwin.apps.ui.viewmodel.WebGateViewModel
import com.prismwin.apps.ui.viewmodel.OnboardingViewModel
import com.prismwin.apps.ui.web.AdvancedWebViewScreen

@Composable
fun PrismNavHost(
    navController: NavHostController = rememberNavController()
) {
    val webGateViewModel: WebGateViewModel = appViewModel()
    val webGateState by webGateViewModel.appState.collectAsStateWithLifecycle()
    val onboardingViewModel: OnboardingViewModel = appViewModel()
    val onboardingState by onboardingViewModel.uiState.collectAsStateWithLifecycle()

    when (val state = webGateState) {
        WebGateViewModel.AppState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }
        is WebGateViewModel.AppState.WebView -> {
            AdvancedWebViewScreen(initialUrl = state.url)
            return
        }
        WebGateViewModel.AppState.NormalApp -> Unit
    }

    if (onboardingState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val startRoute = if (onboardingState.isCompleted) Routes.HOME else Routes.ONBOARDING

    val items = listOf(Routes.HOME, Routes.STATS, Routes.QR_SCANNER, Routes.SETTINGS)
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    LaunchedEffect(onboardingState.isCompleted) {
        if (onboardingState.isCompleted && currentRoute == Routes.ONBOARDING) {
            navController.navigate(Routes.HOME) {
                popUpTo(Routes.ONBOARDING) { inclusive = true }
                launchSingleTop = true
            }
        }
        if (!onboardingState.isCompleted && currentRoute != Routes.ONBOARDING) {
            navController.navigate(Routes.ONBOARDING) {
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (currentRoute in items) {
                NavigationBar {
                    items.forEach { route ->
                        val (label, icon) = when (route) {
                            Routes.HOME -> "Games" to Icons.Default.Home
                            Routes.STATS -> "Stats" to Icons.Default.BarChart
                            Routes.QR_SCANNER -> "QR" to Icons.Default.QrCodeScanner
                            Routes.SETTINGS -> "Settings" to Icons.Default.Settings
                            else -> "" to Icons.Default.Home
                        }
                        NavigationBarItem(
                            selected = currentRoute == route,
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            label = { Text(label) },
                            icon = { Icon(icon, contentDescription = label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startRoute,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.ONBOARDING) {
                val page = onboardingState.pages[onboardingState.currentPageIndex]
                OnboardingScreen(
                    page = page,
                    pageIndex = onboardingState.currentPageIndex,
                    totalPages = onboardingState.pages.size,
                    onNext = onboardingViewModel::nextPage,
                    onBack = onboardingViewModel::previousPage,
                    onFinish = onboardingViewModel::finishOnboarding
                )
            }
            composable(Routes.HOME) {
                HomeScreen(
                    onOpenReaction = { navController.navigate(Routes.REACTION) },
                    onOpenTarget = { navController.navigate(Routes.TARGET) },
                    onOpenSequence = { navController.navigate(Routes.SEQUENCE) },
                    onOpenStats = { navController.navigate(Routes.STATS) }
                )
            }
            composable(Routes.REACTION) {
                ReactionScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.TARGET) {
                TargetScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.SEQUENCE) {
                SequenceScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.STATS) {
                StatsScreen()
            }
            composable(Routes.QR_SCANNER) {
                QrScannerScreen()
            }
            composable(Routes.SETTINGS) {
                SettingsScreen()
            }
        }
    }
}
