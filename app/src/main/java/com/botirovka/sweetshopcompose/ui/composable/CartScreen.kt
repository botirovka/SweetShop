package com.botirovka.sweetshopcompose.ui.composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.botirovka.sweetshopcompose.data.FirebaseRepository
import com.botirovka.sweetshopcompose.data.Response
import com.botirovka.sweetshopcompose.models.Order
import com.botirovka.sweetshopcompose.models.Pie
import com.botirovka.sweetshopcompose.ui.theme.SweetShopComposeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CartScreen(navController: NavHostController) {
    var isPieListExpanded by remember { mutableStateOf(true) }
    val cartItems = remember { mutableStateListOf(*FirebaseRepository.user.cartPies.toTypedArray()) }
    val scope = rememberCoroutineScope()

    val totalPrice by remember {
        derivedStateOf {
            cartItems.sumOf { it.price * it.quantity }
        }
    }

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$totalPrice UAH",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        handleBuy(cartItems, scope, navController)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Buy Now")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cart (${cartItems.size} items)",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = { isPieListExpanded = !isPieListExpanded }) {
                    Icon(
                        imageVector = if (isPieListExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (isPieListExpanded) "Collapse" else "Expand"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isPieListExpanded) {
                PieCardList(
                    pies = cartItems,
                    onRemove = { cartItems.remove(it)
                                scope.launch {
                                    val currentCartPies = FirebaseRepository.user.cartPies.toMutableList()
                                    currentCartPies.remove(it)

                                    val updatedUser = FirebaseRepository.user.copy(cartPies = currentCartPies)
                                    FirebaseRepository.user = updatedUser
                                    scope.launch {
                                        FirebaseRepository.uploadInfo(updatedUser)
                                    }
                                }
                               },
                    onQuantityChange = { pie, newQuantity ->
                        val index = cartItems.indexOf(pie)
                        if (index != -1) {
                            cartItems[index] = pie.copy(quantity = newQuantity)
                            scope.launch {
                                val currentCartPies = FirebaseRepository.user.cartPies.toMutableList()
                                val cartIndex = currentCartPies.indexOfFirst { it.id == pie.id }
                                if (cartIndex != -1) {
                                    currentCartPies[cartIndex] = currentCartPies[cartIndex].copy(quantity = newQuantity)
                                }
                                val updatedUser = FirebaseRepository.user.copy(cartPies = currentCartPies)
                                FirebaseRepository.user = updatedUser
                                FirebaseRepository.uploadInfo(updatedUser)
                            }
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun PieCardList(
    pies: List<Pie>,
    onRemove: (Pie) -> Unit,
    onQuantityChange: (Pie, Int) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(pies) { pie ->
            PieCardWithoutImage(
                pie = pie,
                onRemove = { onRemove(pie) },
                onQuantityChange = { newQty -> onQuantityChange(pie, newQty) }
            )
        }
    }
}

@Composable
fun PieCardWithoutImage(
    pie: Pie,
    onRemove: () -> Unit,
    onQuantityChange: (Int) -> Unit
) {
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

                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Remove",
                        tint = MaterialTheme.colorScheme.error
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
                    IconButton(
                        onClick = {
                            if (pie.quantity > 1) onQuantityChange(pie.quantity - 1)
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Remove,
                            contentDescription = "Decrease",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = pie.quantity.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    IconButton(
                        onClick = {
                            onQuantityChange(pie.quantity + 1)
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Increase",
                            modifier = Modifier.size(16.dp)
                        )
                    }
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

fun handleBuy(cartItems: List<Pie>, scope: CoroutineScope,navController: NavHostController) {
    scope.launch { withContext(Dispatchers.IO){

        val newOrderList = FirebaseRepository.user.listOrder.toMutableList()
        newOrderList.add(Order(cartItems))

        val updatedUser = FirebaseRepository.user.copy(cartPies = emptyList(), listOrder = newOrderList)
        val response = FirebaseRepository.uploadInfo(updatedUser)

        when (response) {
            is Response.Success -> {
                println("Order placed successfully")

            }
            is Response.Error -> {

                println("Failed to place order: ${response.message}")
            }
        }
    }
    val result = FirebaseRepository.getUserInfo()
        if(result.isSuccess){
            navController.navigate("account") {
                popUpTo("cart") { inclusive = true }
            }
        }
    }


}


@Preview(showBackground = true)
@Composable
fun CartScreenPreview() {
    SweetShopComposeTheme {
        CartScreen(rememberNavController())
    }
}

