package com.siresystems.zoey_gardens_app.model

data class UserResponse(
    val status: String,
    val message: String?,
    val user_id: Int?,
    val name: String?
)