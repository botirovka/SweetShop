package com.botirovka.sweetshopcompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.botirovka.sweetshopcompose.data.FirebaseRepository
import com.botirovka.sweetshopcompose.data.Response
import com.botirovka.sweetshopcompose.ui.composable.FindProductsScreen
import com.botirovka.sweetshopcompose.ui.composable.LoginScreenUI
import com.botirovka.sweetshopcompose.ui.composable.MainScreen
import com.botirovka.sweetshopcompose.ui.composable.SignUpScreenUI
import com.botirovka.sweetshopcompose.ui.composable.SweetShopSplashScreen
import com.botirovka.sweetshopcompose.ui.theme.SweetShopComposeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SweetShopComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SweetShopApp()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        CoroutineScope(Dispatchers.IO).launch {
            val result = FirebaseRepository.getPies()
            when (result) {
                is Response.Success -> {
                    Log.d("Debug", "onStart: ${result.data}")
                }
                is Response.Error -> {
                    Log.d("Debug", "onStart: ${result.message}")
                }
            }
        }
    }
}


@Composable
fun SweetShopApp() {
    val navController = rememberNavController()
    val startDestination: String by lazy {
        if(FirebaseRepository.isUserLoggedIn()){
            "home"
        }
        else{
            "splash"
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(route = "splash") {
            SweetShopSplashScreen(navController)
        }
        composable(route = "login") {
            LoginScreenUI(navController)
        }
        composable(route = "signUp") {
            SignUpScreenUI(navController)
        }
        composable(route = "home") {
            MainScreen()
        }
    }
}






