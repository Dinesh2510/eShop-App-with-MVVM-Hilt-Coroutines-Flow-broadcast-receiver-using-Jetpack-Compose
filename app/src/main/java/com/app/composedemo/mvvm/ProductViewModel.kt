package com.app.composedemo.mvvm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.composedemo.model.ProductList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProductViewModel @Inject constructor(private val productRepository: ProductRepository) :
    ViewModel() {

    private val _product = MutableStateFlow(ProductList())
    val prodducts: MutableStateFlow<ProductList> get() = _product
    val isLoading = MutableStateFlow(false)


    init {
        fetchProducts()
    }

    private fun fetchProducts() {

        viewModelScope.launch {
            isLoading.value = true
            try {
                val result = productRepository.getProducts()
                _product.value = result as ProductList
                Log.e("TAG_SUCCESS", "fetchProducts: ")
            } catch (e: Exception) {
                Log.e("TAG_ERROR", "fetchProducts: ")
            } finally {
                isLoading.value = false
            }

        }


    }


}