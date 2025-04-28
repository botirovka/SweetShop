package com.botirovka.sweetshopcompose.ui.composable

import android.util.Log
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue // Імпортуємо getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue // Імпортуємо setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.botirovka.sweetshopcompose.data.FirebaseRepository
import com.botirovka.sweetshopcompose.data.Response
import com.botirovka.sweetshopcompose.models.Pie
import com.botirovka.sweetshopcompose.models.User
import com.botirovka.sweetshopcompose.ui.theme.SweetShopComposeTheme
import kotlinx.coroutines.launch

@Composable
fun FindProductsScreen(isFavoriteScreen: Boolean = false, navController: NavHostController) {
    val piesState = remember { mutableStateOf<List<Pie>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val searchText = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        isLoading.value = true
        val userResponse = FirebaseRepository.getUserInfo()
        if (userResponse.isSuccess) {
            Log.d("debug", "FindProductsScreen: $userResponse")
            FirebaseRepository.user = userResponse.getOrNull() ?: User()
        } else {
            errorMessage.value = userResponse.exceptionOrNull()?.message ?: "Failed to load user"
        }

        when (val piesResponse = FirebaseRepository.getPies()) {
            is Response.Success -> {
                Log.d("debug", "FindProductsScreen: ${piesResponse.data}")
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

    val filteredPies = remember(piesState.value, isFavoriteScreen, searchText.value) {
        val baseList = if (isFavoriteScreen) {
            piesState.value.filter { it.isFavorite }
        } else {
            piesState.value
        }
        if (searchText.value.isBlank()) {
            baseList
        } else {
            baseList.filter { pie ->
                pie.title.contains(
                    searchText.value,
                    ignoreCase = true
                )
            }
        }
    }


    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            SearchBar(
                searchText = searchText.value,
                onSearchTextChanged = { newText -> searchText.value = newText }
            )
            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading.value -> {
                    Text(text = "Loading...", style = MaterialTheme.typography.bodyLarge)
                }

                errorMessage.value != null -> {
                    Text(text = errorMessage.value ?: "Unknown error", color = Color.Red)
                }

                else -> {
                    PieList(pies = filteredPies, navController)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChanged: (String) -> Unit
) {
    TextField(
        value = searchText,
        onValueChange = onSearchTextChanged,
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = "Search Icon")
        },
        placeholder = {
            Text(
                text = "Search",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        },
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
        ),
        singleLine = true
    )
}


@Composable
fun PieList(pies: List<Pie>, navController: NavHostController) {
    if (pies.isEmpty()) {
        Text(
            text = "No pies found.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 16.dp)
        )
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(pies) { pie ->
                PieCard(pie = pie, navController)
            }
        }
    }
}

@Composable
fun PieCard(pie: Pie, navController: NavHostController) {
    var isFavoriteIconState by remember(pie.isFavorite) { mutableStateOf(pie.isFavorite) }
    val scope = rememberCoroutineScope()


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                FirebaseRepository.currentPie = pie
                navController.navigate("pie") {
                    popUpTo("explore") {}
                }
            },
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
                text = "${pie.weight}g",
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
                        val newFavoriteState = !isFavoriteIconState
                        isFavoriteIconState = newFavoriteState
                        val currentLikedPies = FirebaseRepository.user.likedPies.toMutableSet()
                        if (newFavoriteState) {
                            currentLikedPies.add(pie.id)
                        } else {
                            currentLikedPies.remove(pie.id)
                        }
                        val updatedUser =
                            FirebaseRepository.user.copy(likedPies = currentLikedPies.toList())

                        Log.d(
                            "debug",
                            "PieCard Toggled: Pie ID ${pie.id}, New State: $newFavoriteState"
                        )
                        Log.d("debug", "PieCard User Before Update: ${FirebaseRepository.user}")
                        Log.d("debug", "PieCard User To Upload: $updatedUser")

                        scope.launch {
                            when (val result = FirebaseRepository.uploadInfo(updatedUser)) {
                                is Response.Success -> {
                                    FirebaseRepository.user = updatedUser
                                    Log.d("debug", "PieCard User Update Success")
                                }

                                is Response.Error -> {
                                    isFavoriteIconState = !newFavoriteState
                                    Log.e("debug", "PieCard User Update Failed: ${result.message}")
                                }
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isFavoriteIconState) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Add to Favorites",
                        tint = if (isFavoriteIconState) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }
        }
    }
}

// TEST DATA FOR PREVIEW
val samplePies = listOf(
    Pie(
        id = "1",
        title = "Naturel Red Apple",
        weight = 800,
        price = 200,
        isFavorite = false,
        imageUrl = ""
    ),
    Pie(
        id = "2",
        title = "Chocolate Cake",
        weight = 1000,
        price = 250,
        isFavorite = true,
        imageUrl = ""
    ),
    Pie(
        id = "3",
        title = "Strawberry Tart",
        weight = 400,
        price = 180,
        isFavorite = false,
        imageUrl = ""
    ),
    Pie(
        id = "4",
        title = "Blueberry Muffin",
        weight = 500,
        price = 120,
        isFavorite = false,
        imageUrl = ""
    ),
    Pie(
        id = "5",
        title = "Lemon Cheesecake",
        weight = 1200,
        price = 280,
        isFavorite = true,
        imageUrl = ""
    )
)


@Preview(showBackground = true)
@Composable
fun FindProductsScreenPreview() {

    SweetShopComposeTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            SearchBar(searchText = "cake", onSearchTextChanged = {})
            Spacer(modifier = Modifier.height(16.dp))
            PieList(
                pies = samplePies.filter { it.title.contains("cake", ignoreCase = true) },
                rememberNavController()
            )
        }
    }
}