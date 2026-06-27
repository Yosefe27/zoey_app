package com.siresystems.zoey_gardens_app.model

data class DailySale(
    val itemID: Int,
    val item_description: String,
    val item_price: Double,
    var quantity: Int = 0
) {
    fun total(): Double {
        return quantity * item_price
    }
}