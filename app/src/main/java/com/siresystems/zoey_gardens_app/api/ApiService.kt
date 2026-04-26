package com.siresystems.zoey_gardens_app.api

import com.siresystems.zoey_gardens_app.model.MenuItem
import com.siresystems.zoey_gardens_app.model.OrderResponse
import com.siresystems.zoey_gardens_app.model.UserResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    // 🔐 SIGNUP
    @FormUrlEncoded
    @POST("signup.php")
    fun signup(
        @Field("name") name: String,
        @Field("phone") phone: String,
        @Field("password") password: String
    ): Call<UserResponse>

    // 🔑 LOGIN
    @FormUrlEncoded
    @POST("login.php")
    fun login(
        @Field("phone") phone: String,
        @Field("password") password: String
    ): Call<UserResponse>

    @FormUrlEncoded
    @POST("save_order.php")
    fun saveOrder(
        //@Field("user_id") userId: Int,
        @Field("phone") phone: String,
        @Field("location") location: String,
        @Field("items") items: String,
        @Field("total") total: Double
    ): Call<OrderResponse>

    @GET("get_menu.php")
    fun getMenu(): Call<List<MenuItem>>
}