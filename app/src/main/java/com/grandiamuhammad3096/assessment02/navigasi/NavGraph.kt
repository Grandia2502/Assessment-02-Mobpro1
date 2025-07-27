package com.grandiamuhammad3096.assessment02.navigasi

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.grandiamuhammad3096.assessment02.ui.screen.MainScreen
import com.grandiamuhammad3096.assessment02.ui.screen.ThemeViewModel
import com.grandiamuhammad3096.assessment02.ui.screen.TransactionScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(navController: NavHostController = rememberNavController(), themeViewModel: ThemeViewModel) {
    NavHost(
        navController = navController,
        startDestination = Screens.Main.route
    ) {
        composable(route = Screens.Main.route) {
            MainScreen(navController, themeViewModel)
        }
        composable(route = Screens.Transaction.route) {
            TransactionScreen(navController)
        }
        composable(
            route = Screens.TransactionEdit.route,
            arguments = listOf(
                navArgument("transactionId") { type = NavType.LongType; }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("transactionId") ?: 0L
            TransactionScreen(navController, id)
        }
    }
}
