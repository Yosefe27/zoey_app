package com.siresystems.zoey_gardens_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.siresystems.zoey_gardens_app.R
import com.siresystems.zoey_gardens_app.model.MenuItem

class MenuAdapter(
    private val list: List<MenuItem>,
    private val onAddClick: (MenuItem) -> Unit
) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.txtName)
        val price: TextView = view.findViewById(R.id.txtPrice)
        val image: ImageView = view.findViewById(R.id.imgFood)
        val btnAdd: Button = view.findViewById(R.id.btnAdd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.name.text = item.name
        holder.price.text = "K ${item.price}"

        // Load image (if you have URLs)
        Glide.with(holder.itemView.context)
            .load(item.image)
            .into(holder.image)

        holder.btnAdd.setOnClickListener {
            onAddClick(item)
        }
    }
}