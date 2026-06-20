package com.siresystems.zoey_gardens_app.utils

import com.siresystems.zoey_gardens_app.model.CartItem

object CartManager {

    val cartItems = mutableListOf<CartItem>()

    fun addItem(item: CartItem) {
        val existing = cartItems.find { it.id == item.id }
        if (existing != null) {
            existing.quantity++
        } else {
            cartItems.add(item)
        }
    }

    fun removeItem(item: CartItem) {
        cartItems.remove(item)
    }

    fun getTotal(): Double {
        return cartItems.sumOf { it.price * it.quantity }
    }

    fun clearCart() {
        cartItems.clear()
    }
}