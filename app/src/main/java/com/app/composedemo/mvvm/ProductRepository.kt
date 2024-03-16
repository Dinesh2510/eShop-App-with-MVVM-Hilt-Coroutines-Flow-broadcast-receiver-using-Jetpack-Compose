package com.app.composedemo.mvvm

import com.app.composedemo.api.ApiService
import com.app.composedemo.model.ProductList
import javax.inject.Inject

class ProductRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getProducts(): ProductList {
        return apiService.getProducts()
    }


}