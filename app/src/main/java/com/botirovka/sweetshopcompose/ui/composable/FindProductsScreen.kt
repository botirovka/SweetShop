package com.botirovka.sweetshopcompose.ui.composable

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.botirovka.sweetshopcompose.data.FirebaseRepository
import com.botirovka.sweetshopcompose.data.Response
import com.botirovka.sweetshopcompose.models.Pie
import com.botirovka.sweetshopcompose.models.User
import com.botirovka.sweetshopcompose.ui.theme.SweetShopComposeTheme
import kotlinx.coroutines.launch

@Composable
fun FindProductsScreen(isFavoriteScreen: Boolean = false) {
    val piesState = remember { mutableStateOf<List<Pie>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoading.value = true
        val userResponse = FirebaseRepository.getUserInfo()
        if (userResponse.isSuccess) {
            FirebaseRepository.user = userResponse.getOrNull() ?: User()
        } else {
            errorMessage.value = userResponse.exceptionOrNull()?.message ?: "Failed to load user"
        }

        when (val piesResponse = FirebaseRepository.getPies()) {
            is Response.Success -> {
                val loadedPies = piesResponse.data
                val updatedPies = loadedPies.map { pie ->
                    pie.copy(isFavorite = FirebaseRepository.user.likedPies.contains(pie.id))
                }
                piesState.value = updatedPies
                errorMessage.value = null
            }
            is Response.Error -> {
                errorMessage.value = piesResponse.message
            }
        }
        isLoading.value = false
    }

    val filteredPies = if (isFavoriteScreen) {
        piesState.value.filter { it.isFavorite }
    } else {
        piesState.value
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            SearchBar()
            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading.value -> {
                    Text(text = "Loading...", style = MaterialTheme.typography.bodyLarge)
                }
                errorMessage.value != null -> {
                    Text(text = errorMessage.value ?: "Unknown error", color = Color.Red)
                }
                else -> {
                    PieList(pies = filteredPies)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar() {
    val searchText = remember { mutableStateOf("") }
    TextField(
        value = searchText.value,
        onValueChange = { searchText.value = it },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = "Search Icon")
        },
        placeholder = { Text(
            text ="Search",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )},
        textStyle = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(15.dp),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            containerColor = Color(0xFFF2F2F2)
        )
    )
}


@Composable
fun PieList(pies: List<Pie>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(pies) { pie ->
            PieCard(pie = pie)
        }
    }
}

@Composable
fun PieCard(pie: Pie) {
    var isFavorite by remember { mutableStateOf(pie.isFavorite) }
    val scope = rememberCoroutineScope()


    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CardDefaults.elevatedShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = pie.imageUrl,
                contentDescription = "Cake Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = pie.title,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${pie.weight}kg",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(thickness = 1.dp)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${pie.price} UAH",
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(
                    onClick = {
                        isFavorite = !isFavorite
                        pie.isFavorite = isFavorite

                        val updatedLikedPies = if (isFavorite) {
                            FirebaseRepository.user.likedPies + pie.id
                        } else {
                            FirebaseRepository.user.likedPies - pie.id
                        }
                        scope.launch {
                            FirebaseRepository.uploadInfo(FirebaseRepository.user)
                        }

                    }
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Add to Favorites"
                    )
                }
            }
        }
    }
}

// Test data
val samplePies = listOf(
    Pie(title = "Naturel Red Apple", weight = 1, price = 200),
    Pie(title = "Chocolate Cake", weight = 1, price = 250),
    Pie(title = "Strawberry Tart", weight = 0, price = 180),
    Pie(title = "Blueberry Muffin", weight = 0, price = 120),
    Pie(title = "Lemon Cheesecake", weight = 1, price = 280)
)


@Preview(showBackground = true)
@Composable
fun FindProductsScreenPreview() {
    SweetShopComposeTheme {
        FindProductsScreen()
    }
}