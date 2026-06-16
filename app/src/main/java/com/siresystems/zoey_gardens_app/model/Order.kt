package com.siresystems.zoey_gardens_app.model

data class Order(
    val id: Int,
    val total: Double,
    val created_at: String,
    val status: String
)