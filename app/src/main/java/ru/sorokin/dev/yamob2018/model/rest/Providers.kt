package ru.sorokin.dev.yamob2018.model.rest

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.sorokin.dev.yamob2018.model.repository.AccountRepo



class Providers {
    companion object {
        fun provideAccountApi() = Retrofit.Builder()
            .baseUrl(AccountApi.BASE_URL)
            .client(provideClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(AccountApi::class.java)

        fun provideDriveApi() = Retrofit.Builder()
                .baseUrl(DriveApi.BASE_URL)
                .client(provideClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(DriveApi::class.java)

        private fun provideClient(): OkHttpClient {
            val client = OkHttpClient.Builder().addInterceptor(Interceptor { chain ->
                val original = chain.request()

                //TODO: add timeouts
                val request = original.newBuilder()
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/json")
                        .header("Authorization", "OAuth ${AccountRepo.token}")
                        .method(original.method(), original.body())
                        .build()

                return@Interceptor chain.proceed(request)
            }).build()
            return client
        }
    }
}