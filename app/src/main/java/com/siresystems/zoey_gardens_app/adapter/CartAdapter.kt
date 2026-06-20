package com.siresystems.zoey_gardens_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.siresystems.zoey_gardens_app.R
import com.siresystems.zoey_gardens_app.model.CartItem
import com.siresystems.zoey_gardens_app.utils.CartManager

class CartAdapter(
    private val list: MutableList<CartItem>,
    private val onUpdate: () -> Unit
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName: TextView = view.findViewById(R.id.txtName)
        val txtPrice: TextView = view.findViewById(R.id.txtPrice)
        val txtQty: TextView = view.findViewById(R.id.txtQty)
        val btnPlus: Button = view.findViewById(R.id.btnPlus)
        val btnMinus: Button = view.findViewById(R.id.btnMinus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.txtName.text = item.name
        holder.txtPrice.text = "K ${item.price}"
        holder.txtQty.text = item.quantity.toString()

        holder.btnPlus.setOnClickListener {
            item.quantity++
            notifyItemChanged(position)
            onUpdate()
        }

        holder.btnMinus.setOnClickListener {
            if (item.quantity > 1) {
                item.quantity--
            } else {
                CartManager.removeItem(item)
                notifyDataSetChanged()
            }
            onUpdate()
        }
    }
}