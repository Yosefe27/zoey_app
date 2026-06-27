package com.siresystems.zoey_gardens_app.model

import com.siresystems.zoey_gardens_app.model.SalesHistory

data class SalesHistoryResponse(
    val status: String,
    val data: List<SalesHistory>
)