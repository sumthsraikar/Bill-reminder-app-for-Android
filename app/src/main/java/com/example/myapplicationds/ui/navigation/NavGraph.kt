package com.example.myapplicationds.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.myapplicationds.ui.addedit.AddEditBillScreen
import com.example.myapplicationds.ui.addedit.AddEditBillViewModel
import com.example.myapplicationds.ui.calendar.CalendarScreen
import com.example.myapplicationds.ui.calendar.CalendarViewModel
import com.example.myapplicationds.ui.home.HomeScreen
import com.example.myapplicationds.ui.home.HomeViewModel
import com.example.myapplicationds.ui.settings.SettingsScreen
import com.example.myapplicationds.ui.settings.SettingsViewModel
import com.example.myapplicationds.ui.statistics.StatisticsScreen
import com.example.myapplicationds.ui.statistics.StatisticsViewModel

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Calendar.route,
        Screen.Statistics.route,
        Screen.Settings.route
    )

    Scaffold(
        containerColor = com.example.myapplicationds.ui.theme.DarkBackground
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                val viewModel = hiltViewModel<HomeViewModel>()
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToAddBill = {
                        navController.navigate(Screen.AddEditBill.createRoute())
                    },
                    onNavigateToEditBill = { billId ->
                        navController.navigate(Screen.AddEditBill.createRoute(billId))
                    },
                    onNavigateToCalendar = {
                        navController.navigate(Screen.Calendar.route)
                    },
                    onNavigateToAnalytics = {
                        navController.navigate(Screen.Statistics.route)
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
            }

            composable(
                route = Screen.AddEditBill.route,
                arguments = listOf(
                    navArgument("billId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = "-1"
                    }
                )
            ) {
                val viewModel = hiltViewModel<AddEditBillViewModel>()
                AddEditBillScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Calendar.route) {
                val viewModel = hiltViewModel<CalendarViewModel>()
                CalendarScreen(
                    viewModel = viewModel,
                    onNavigateToEditBill = { billId ->
                        navController.navigate(Screen.AddEditBill.createRoute(billId))
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Statistics.route) {
                val viewModel = hiltViewModel<StatisticsViewModel>()
                StatisticsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                val viewModel = hiltViewModel<SettingsViewModel>()
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
