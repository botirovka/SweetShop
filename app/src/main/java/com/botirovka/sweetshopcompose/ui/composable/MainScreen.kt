package com.botirovka.sweetshopcompose.ui.composable

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.botirovka.sweetshopcompose.R

@Composable
fun MainScreen(mainNavController: NavHostController) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavigationGraph(navController = navController, mainNavController)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Screen.Explore,
        Screen.Cart,
        Screen.Favourite,
        Screen.Account
    )

    val currentRoute = currentRoute(navController)

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = screen.icon),
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, mainNavController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Explore.route
    ) {
        composable(Screen.Explore.route) { FindProductsScreen(navController = navController) }
        composable(Screen.Cart.route) { CartScreen(navController) }
        composable(Screen.Favourite.route) { FindProductsScreen(true,navController) }
        composable(Screen.Account.route) { AccountScreen(mainNavController) }
        composable("pie") { PieDetailScreen(navController) }
    }
}

@Composable
private fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

sealed class Screen(val route: String, val title: String, @DrawableRes val icon: Int) {
    object Explore : Screen("explore", "Explore", R.drawable.ic_explore)
    object Cart : Screen("cart", "Cart", R.drawable.ic_cart)
    object Favourite : Screen("favourite", "Favourite", R.drawable.ic_favourite)
    object Account : Screen("account", "Account", R.drawable.ic_account)
}