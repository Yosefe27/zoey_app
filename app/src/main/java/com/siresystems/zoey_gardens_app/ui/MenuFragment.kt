package com.siresystems.zoey_gardens_app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.siresystems.zoey_gardens_app.R

import com.siresystems.zoey_gardens_app.adapter.MenuAdapter
import com.siresystems.zoey_gardens_app.api.ApiClient
import com.siresystems.zoey_gardens_app.model.CartItem
import com.siresystems.zoey_gardens_app.model.MenuItem
import com.siresystems.zoey_gardens_app.utils.CartManager

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import android.content.Intent
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.siresystems.zoey_gardens_app.CartActivity

class MenuFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MenuAdapter

    private val list = ArrayList<MenuItem>()
    private var category: String = ""

    companion object {
        fun newInstance(category: String): MenuFragment {
            val fragment = MenuFragment()
            val args = Bundle()
            args.putString("category", category)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onResume() {
        super.onResume()

        val txtCartCount = view?.findViewById<TextView>(R.id.txtCartCount)
        txtCartCount?.text = CartManager.cartItems.size.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = arguments?.getString("category") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val fabCart = view.findViewById<FloatingActionButton>(R.id.fabCart)
        val txtCartCount = view.findViewById<TextView>(R.id.txtCartCount)

        // 🛒 Adapter (ADD TO CART FIXED)
        adapter = MenuAdapter(list) { item ->

            CartManager.addItem(
                CartItem(
                    id = item.id,
                    name = item.name,
                    price = item.price
                )
            )

            // 🔢 Update cart count
            txtCartCount.text = CartManager.cartItems.size.toString()

            Toast.makeText(
                requireContext(),
                "${item.name} added to cart",
                Toast.LENGTH_SHORT
            ).show()
        }

        recyclerView.adapter = adapter

        // 🛒 Open cart
        fabCart.setOnClickListener {
            startActivity(Intent(requireContext(), CartActivity::class.java))
        }

        // 🔢 Initial count
        txtCartCount.text = CartManager.cartItems.size.toString()

        loadMenu()

        return view
    }

    private fun loadMenu() {
        ApiClient.instance.getMenu().enqueue(object : Callback<List<MenuItem>> {

            override fun onResponse(
                call: Call<List<MenuItem>>,
                response: Response<List<MenuItem>>
            ) {
                if (response.isSuccessful && response.body() != null) {

                    val filtered = response.body()!!.filter {
                        it.category.equals(category, ignoreCase = true)
                    }

                    list.clear()
                    list.addAll(filtered)

                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to load menu",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<MenuItem>>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "Network error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}