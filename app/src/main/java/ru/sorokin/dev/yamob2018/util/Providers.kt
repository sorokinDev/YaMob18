package ru.sorokin.dev.yamob2018.util

import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthSdk
import io.realm.Realm
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.sorokin.dev.yamob2018.DriveApp
import ru.sorokin.dev.yamob2018.model.repository.AccountRepo
import ru.sorokin.dev.yamob2018.model.rest.AccountApi
import ru.sorokin.dev.yamob2018.model.rest.DriveApi


class Providers {
    companion object {
        fun provideAccountApi() : AccountApi = Retrofit.Builder()
            .baseUrl(AccountApi.BASE_URL)
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(AccountApi::class.java)

        fun provideDriveApi() : DriveApi = Retrofit.Builder()
                .baseUrl(DriveApi.BASE_URL)
                .client(provideOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(DriveApi::class.java)

        private fun provideOkHttpClient(): OkHttpClient {
            return OkHttpClient.Builder().addInterceptor(Interceptor { chain ->
                val original = chain.request()

                val request = original.newBuilder()
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/json")
                        .header("Authorization", "OAuth ${AccountRepo.token}")
                        .method(original.method(), original.body())
                        .build()

                return@Interceptor chain.proceed(request)
            }).build()
        }

        fun provideYandexAuthSdk(): YandexAuthSdk = YandexAuthSdk(DriveApp.INSTANCE.applicationContext,
            YandexAuthOptions(DriveApp.INSTANCE.applicationContext, true))

        fun provideRealm(): Realm = Realm.getDefaultInstance()

    }
}