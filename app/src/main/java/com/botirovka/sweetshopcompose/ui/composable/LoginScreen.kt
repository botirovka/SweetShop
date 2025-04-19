package com.botirovka.sweetshopcompose.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.botirovka.sweetshopcompose.R
import com.botirovka.sweetshopcompose.data.FirebaseRepository
import com.botirovka.sweetshopcompose.data.Response
import com.botirovka.sweetshopcompose.ui.theme.Brown
import com.botirovka.sweetshopcompose.ui.theme.SweetShopComposeTheme
import kotlinx.coroutines.launch

@Composable
fun LoginScreenUI(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Image(
            painter = painterResource(id = R.drawable.donut),
            contentDescription = "Donut Icon",
            modifier = Modifier.size(80.dp)
        )

        Text(
            modifier = Modifier.align(Alignment.Start),
            text = "Login",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            modifier = Modifier.align(Alignment.Start),
            text = "Enter your email and password",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            shape = RoundedCornerShape(8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },
            shape = RoundedCornerShape(8.dp)
        )

        TextButton(onClick = { }, modifier = Modifier.align(Alignment.End)) {
            Text("Forgot Password?", style = TextStyle(color = Brown))
        }

        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    val result = FirebaseRepository.login(email, password)
                    isLoading = false
                    when (result) {
                        is Response.Success -> {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                        is Response.Error -> {
                            errorMessage = result.message
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Brown),
            enabled = !isLoading
        ) {
            Text(
                if (isLoading) "Loading..." else "Log In",
                style = TextStyle(color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            )
        }

        errorMessage?.let {
            Text(
                text = it, color = Color.Red,
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Don't have an account? ", style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = {
                navController.navigate("signUp") {
                    popUpTo("login")
                }
            }) {
                Text("Signup", style = TextStyle(color = Brown, fontWeight = FontWeight.Bold))
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}





@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    SweetShopComposeTheme {
        LoginScreenUI(navController = rememberNavController())
    }
}