package ru.sorokin.dev.yamob2018.util

import retrofit2.Response

interface ApiQueryCallback<T>{
    fun handle(isSuccessResponse: Boolean, isFailure: Boolean, response: Response<T>?, error: Throwable?)
}