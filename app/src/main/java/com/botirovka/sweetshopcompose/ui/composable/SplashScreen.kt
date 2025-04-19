package com.botirovka.sweetshopcompose.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.botirovka.sweetshopcompose.ui.theme.Brown
import com.botirovka.sweetshopcompose.ui.theme.SweetShopComposeTheme

@Composable
fun SweetShopSplashScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brown),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Sweet Shop",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text ="Get Started",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SweetShopComposeTheme {
        SweetShopSplashScreen(navController = rememberNavController())
    }
}