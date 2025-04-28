package com.botirovka.sweetshopcompose.ui.composable

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.botirovka.sweetshopcompose.data.FirebaseRepository.getUserInfo
import com.botirovka.sweetshopcompose.data.FirebaseRepository.logout
import com.botirovka.sweetshopcompose.models.Order
import com.botirovka.sweetshopcompose.models.Pie
import com.botirovka.sweetshopcompose.models.User
import kotlinx.coroutines.launch

@Composable
fun AccountScreen(mainNavController: NavHostController) {
    val context = LocalContext.current
    val userInfo = remember { mutableStateOf<User?>(null) }
    val orders = remember { mutableStateOf<List<Order>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val result = getUserInfo()
        if (result.isSuccess) {
            userInfo.value = result.getOrNull()
            orders.value = userInfo.value?.listOrder?.reversed() ?: emptyList()
        } else {
            Toast.makeText(context, "Failed to load user data", Toast.LENGTH_SHORT).show()
        }
        isLoading.value = false
    }

    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {

            Text("Orders:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                itemsIndexed(orders.value) { index, order ->
                    OrderItem(order = order, index = orders.value.size - index)
                    Divider()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch { logout()
                    mainNavController.navigate("splash"){
                        popUpTo("home") { inclusive = true }
                    }}
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Logout")
            }
        }
    }
}

@Composable
fun OrderItem(order: Order, index: Int) {
    val expanded = remember { mutableStateOf(false) }
    val totalPrice = order.cartPies.sumOf { it.price * (it.quantity ?: 1) }
    val totalFormatted = String.format("%.2f", totalPrice.toDouble())

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Order #$index", fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("$totalFormatted UAH", fontWeight = FontWeight.SemiBold)
                IconButton(onClick = { expanded.value = !expanded.value }) {
                    Icon(
                        imageVector = if (expanded.value) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Toggle order details"
                    )
                }
            }
        }

        if (expanded.value) {
            Column(modifier = Modifier.padding(start = 16.dp)) {
                order.cartPies.forEach { pie ->
                    PieItem(pie = pie)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun PieItem(pie: Pie) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CardDefaults.elevatedShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pie.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${pie.weight}kg",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${pie.quantity} x",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Text(
                    text = "${pie.price * pie.quantity} UAH",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

