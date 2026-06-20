package com.siresystems.zoey_gardens_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import androidx.viewpager2.widget.ViewPager2

class MenuDialogFragment : DialogFragment() {

    private val url =
        "https://zoeygardens-001-site1.site4future.com/zoey_apis/get_full_menu.php"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.dialog_menu, container, false)

        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)
        val closeBtn = view.findViewById<ImageButton>(R.id.btnClose)

        closeBtn.setOnClickListener {
            dismiss()
        }

        fetchImages(viewPager)

        return view
    }

    private fun fetchImages(viewPager: ViewPager2) {

        val queue = Volley.newRequestQueue(requireContext())

        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->

                try {

                    val status = response.getString("status")

                    if (status == "success") {

                        // Example: if API returns single image for now
                        val imageUrl = response.getString("image_url")

                        val images = listOf(imageUrl)

                        viewPager.adapter = FullMenuPagerAdapter(images)

                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            },
            { error ->
                error.printStackTrace()
            }
        )

        queue.add(request)
    }
}