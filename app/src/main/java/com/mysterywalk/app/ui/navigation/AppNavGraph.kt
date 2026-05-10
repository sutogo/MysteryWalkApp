package com.mysterywalk.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.navArgument
import com.mysterywalk.app.ui.history.HistoryScreen
import com.mysterywalk.app.ui.reward.RewardScreen
import com.mysterywalk.app.ui.reward.RewardViewModel

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "nav_screen") {
        composable("nav_screen") {
            NavScreen(
                onArrived = { distance, lat, lon, name, category ->
                    val encodedName = name ?: "Unknown"
                    val encodedCategory = category ?: "None"
                    navController.navigate("reward_screen/$distance/$lat/$lon/$encodedName/$encodedCategory") {
                        popUpTo("nav_screen") { inclusive = true }
                    }
                },
                onHistoryClick = {
                    navController.navigate("history_screen")
                }
            )
        }

        composable(
            route = "reward_screen/{distance}/{lat}/{lon}/{name}/{category}",
            arguments = listOf(
                navArgument("distance") { type = NavType.IntType },
                navArgument("lat") { type = NavType.FloatType },
                navArgument("lon") { type = NavType.FloatType },
                navArgument("name") { type = NavType.StringType },
                navArgument("category") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val distance = backStackEntry.arguments?.getInt("distance") ?: 0
            val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 0.0
            val lon = backStackEntry.arguments?.getFloat("lon")?.toDouble() ?: 0.0
            val rawName = backStackEntry.arguments?.getString("name")
            val name = if (rawName == "Unknown") null else rawName
            val rawCategory = backStackEntry.arguments?.getString("category")
            val category = if (rawCategory == "None") null else rawCategory
            
            val viewModel: RewardViewModel = hiltViewModel()

            RewardScreen(
                distanceMeters = distance,
                lat = lat,
                lon = lon,
                name = name,
                category = category,
                viewModel = viewModel,
                onFinishClick = {
                    viewModel.stopNavigation()
                    navController.navigate("nav_screen") {
                        popUpTo("reward_screen") { inclusive = true }
                    }
                },
                onReturnClick = {
                    viewModel.enableReturnMode()
                    navController.navigate("nav_screen") {
                        popUpTo("reward_screen") { inclusive = true }
                    }
                }
            )
        }

        composable("history_screen") {
            HistoryScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
