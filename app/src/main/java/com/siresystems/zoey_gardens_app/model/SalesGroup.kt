package com.siresystems.zoey_gardens_app.model

import com.siresystems.zoey_gardens_app.model.SalesHistory

data class SalesGroup(
    val date: String,
    val total: Double,
    val items: List<SalesHistory>
)