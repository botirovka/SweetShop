package com.botirovka.sweetshopcompose.models

data class User (
    var likedPies: List<String> = emptyList(),
    var cartPies: List<Pie> = emptyList(),
    var listOrder: List<Order> = emptyList()
)