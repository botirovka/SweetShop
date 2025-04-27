package com.botirovka.sweetshopcompose.models

data class Pie (
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var imageUrl: String = "",
    var price: Int = 0,
    var weight: Int = 0,
    var isFavorite: Boolean = false,
    var quantity: Int = 1
)