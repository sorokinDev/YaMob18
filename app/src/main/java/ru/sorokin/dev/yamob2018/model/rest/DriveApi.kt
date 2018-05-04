package ru.sorokin.dev.yamob2018.model.rest

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import ru.sorokin.dev.yamob2018.model.entity.DriveGetImagesResult
import ru.sorokin.dev.yamob2018.model.entity.DriveInfo

interface DriveApi {
    companion object {
        val API_VERSION = "v1"
        val BASE_URL = "https://cloud-api.yandex.net/${DriveApi.API_VERSION}/disk/"
    }

    @GET(".")
    fun getDriveInfo(): Call<DriveInfo>

    @GET("resources/files?media_type=image")
    fun getImages(@Query("fields") fields: String, @Query("limit") limit: Int, @Query("offset") offset: Int,
                  @Query("preview_crop") preview_crop: Boolean, @Query("preview_size") preview_size: String,
                  @Query("sort") sort: String): Call<DriveGetImagesResult>
}