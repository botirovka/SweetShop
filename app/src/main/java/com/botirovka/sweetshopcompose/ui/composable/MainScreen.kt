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
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavigationGraph(navController = navController)
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
fun NavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Explore.route
    ) {
        composable(Screen.Explore.route) { FindProductsScreen() }
        composable(Screen.Cart.route) { CartScreen() }
        composable(Screen.Favourite.route) { FindProductsScreen(true) }
        composable(Screen.Account.route) { AccountScreen() }
    }
}

@Composable
private fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}



@Composable
fun AccountScreen() {
    Text("Account Screen")
}

sealed class Screen(val route: String, val title: String, @DrawableRes val icon: Int) {
    object Explore : Screen("explore", "Explore", R.drawable.ic_explore)
    object Cart : Screen("cart", "Cart", R.drawable.ic_explore)
    object Favourite : Screen("favourite", "Favourite", R.drawable.ic_explore)
    object Account : Screen("account", "Account", R.drawable.ic_explore)
}