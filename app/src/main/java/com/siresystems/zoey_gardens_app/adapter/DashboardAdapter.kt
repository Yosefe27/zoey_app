package com.siresystems.zoey_gardens_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DashboardAdapter(
    private val items: List<DashboardItem>,
    private val listener: (DashboardItem) -> Unit
) : RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {

    class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view){

        val icon =
            view.findViewById<ImageView>(
                R.id.imgIcon
            )

        val title =
            view.findViewById<TextView>(
                R.id.txtTitle
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.dashboard_card,
                    parent,
                    false
                )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = items[position]

        holder.title.text = item.title
        holder.icon.setImageResource(item.icon)

        holder.itemView.setOnClickListener {
            listener(item)
        }
    }

    override fun getItemCount() = items.size
}