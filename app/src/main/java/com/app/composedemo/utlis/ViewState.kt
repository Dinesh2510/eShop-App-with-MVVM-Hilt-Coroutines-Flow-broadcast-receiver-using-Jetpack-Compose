package com.app.composedemo.utlis

sealed class ViewState<out T> {
    data class Success<T>(val data: T) : ViewState<T>()
    data class Error(val message: String) : ViewState<Nothing>()
    object Loading : ViewState<Nothing>()
}