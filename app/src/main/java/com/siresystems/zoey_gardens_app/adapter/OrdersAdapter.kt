package com.siresystems.zoey_gardens_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.siresystems.zoey_gardens_app.R
import com.siresystems.zoey_gardens_app.model.Order

class OrdersAdapter(private val list: List<Order>) :
    RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val id = view.findViewById<TextView>(R.id.tvOrderId)
        val total = view.findViewById<TextView>(R.id.tvTotal)
        val date = view.findViewById<TextView>(R.id.tvDate)
        val status = view.findViewById<TextView>(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val order = list[position]

        holder.id.text = "Order #${order.id}"
        holder.total.text = "Total: K${order.total}"
        holder.date.text = order.created_at
        holder.status.text = order.status
    }

    override fun getItemCount() = list.size
}