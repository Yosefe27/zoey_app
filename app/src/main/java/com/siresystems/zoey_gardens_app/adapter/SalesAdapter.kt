package com.siresystems.zoey_gardens_app

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.siresystems.zoey_gardens_app.model.DailySale

class SalesAdapter(
    private val sales: MutableList<DailySale>,
    private val listener: TotalListener
) : RecyclerView.Adapter<SalesAdapter.SalesViewHolder>() {

    class SalesViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        val txtItem: TextView = v.findViewById(R.id.txtItem)
        val txtPrice: TextView = v.findViewById(R.id.txtPrice)
        val edtQty: EditText = v.findViewById(R.id.edtQty)
        val txtTotal: TextView = v.findViewById(R.id.txtTotal)

        // 🔥 Important: store watcher so we can remove it
        var watcher: TextWatcher? = null
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SalesViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daily_sale, parent, false)

        return SalesViewHolder(view)
    }

    override fun getItemCount(): Int = sales.size

    override fun onBindViewHolder(
        holder: SalesViewHolder,
        position: Int
    ) {

        val sale = sales[position]

        holder.txtItem.text = sale.item_description
        holder.txtPrice.text = "Price: K${sale.item_price}"

        // 🔥 CRITICAL FIX: remove previous watcher before setting text
        holder.watcher?.let {
            holder.edtQty.removeTextChangedListener(it)
        }

        // Set quantity safely (prevents recycled ghost values)
        holder.edtQty.setText(
            if (sale.quantity == 0) "" else sale.quantity.toString()
        )

        holder.txtTotal.text = "Value: K${sale.total()}"

        // Create new watcher
        val textWatcher = object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {}

            override fun afterTextChanged(s: Editable?) {

                val qty = s.toString().toIntOrNull() ?: 0

                sale.quantity = qty

                holder.txtTotal.text = "Value: K${sale.total()}"

                listener.onTotalChanged()
            }
        }

        // Attach and store watcher
        holder.edtQty.addTextChangedListener(textWatcher)
        holder.watcher = textWatcher
    }

    fun getSales(): MutableList<DailySale> {
        return sales
    }
}