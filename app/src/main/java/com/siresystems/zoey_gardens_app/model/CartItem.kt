package com.siresystems.zoey_gardens_app.model

data class CartItem(
    val id: Int,
    val name: String,
    val price: Double,
    var quantity: Int = 1
)