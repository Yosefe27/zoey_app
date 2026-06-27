package com.siresystems.zoey_gardens_app.model

data class SaveSalesRequest(
    val entry_date: String,
    val employee: String,
    val sales: List<SaveSale>
)