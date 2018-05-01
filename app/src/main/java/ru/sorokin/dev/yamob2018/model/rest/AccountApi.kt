package ru.sorokin.dev.yamob2018.model.rest

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import ru.sorokin.dev.yamob2018.model.entity.AccountInfo

interface AccountApi {
    companion object {
        val BASE_URL = "https://login.yandex.ru"
    }

    @GET("/info")
    fun getAccountInfo(@Header("Authorization") oauth_token: String): Call<AccountInfo>

}
