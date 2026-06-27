package com.siresystems.zoey_gardens_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.siresystems.zoey_gardens_app.R
import com.siresystems.zoey_gardens_app.model.SalesGroup
import com.siresystems.zoey_gardens_app.model.SalesHistory

class SalesHistoryAdapter(
    private val groups: List<SalesGroup>
) : RecyclerView.Adapter<SalesHistoryAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val txtDate: TextView = v.findViewById(R.id.txtDate)
        val txtTotal: TextView = v.findViewById(R.id.txtTotal)
        val txtItems: TextView = v.findViewById(R.id.txtItems)
    }

    private fun groupSales(list: List<SalesHistory>): List<SalesGroup> {

        return list
            .groupBy { it.entry_date }
            .map { entry ->

                val items = entry.value
                val total = items.sumOf { it.value }

                SalesGroup(
                    date = entry.key,
                    total = total,
                    items = items
                )
            }
            .sortedByDescending { it.date }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sales_group, parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int = groups.size

    override fun onBindViewHolder(holder: VH, position: Int) {

        val group = groups[position]

        holder.txtDate.text = group.date
        holder.txtTotal.text = "Total: K${group.total}"

        holder.txtItems.text =
            group.items.joinToString("\n") {
                "${it.item_description} x${it.quantity_sold} = K${it.value}"
            }
    }
}