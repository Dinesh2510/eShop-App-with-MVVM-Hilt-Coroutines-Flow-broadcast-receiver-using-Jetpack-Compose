package com.app.composedemo.api

import com.app.composedemo.Constants.Companion.List_Of_product
import com.app.composedemo.model.ProductList
import retrofit2.http.GET

interface ApiService {
    @GET(List_Of_product)
    suspend fun getProducts(): ProductList
}
