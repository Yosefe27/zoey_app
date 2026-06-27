package com.siresystems.zoey_gardens_app.model

data class SalesHistory(
    val entry_date: String,
    val item_description: String,
    val item_price: Double,
    val quantity_sold: Int,
    val value: Double
)