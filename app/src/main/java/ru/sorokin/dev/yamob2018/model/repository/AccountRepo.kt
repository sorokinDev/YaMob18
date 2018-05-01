package ru.sorokin.dev.yamob2018.model.repository

import android.util.Log
import com.google.gson.GsonBuilder
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthSdk
import io.realm.Realm
import retrofit.converter.GsonConverter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.sorokin.dev.yamob2018.DriveApp
import ru.sorokin.dev.yamob2018.Pref
import ru.sorokin.dev.yamob2018.model.entity.AccountInfo
import ru.sorokin.dev.yamob2018.model.rest.AccountApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AccountRepo {
    companion object {
        var username: String? = null
            get() = DriveApp.preferences.getString(Pref.USERNAME, null)

        var token: String? = null
            get() = DriveApp.preferences.getString(Pref.TOKEN, null)

        val SCOPES = setOf("cloud_api:disk.read", "cloud_api:disk.app_folder", "cloud_api:disk.info", "cloud_api:disk.write", "login:avatar")
    }


    var authSdk: YandexAuthSdk = YandexAuthSdk(DriveApp.INSTANCE.applicationContext, YandexAuthOptions(DriveApp.INSTANCE.applicationContext, true))
    var realm = Realm.getDefaultInstance()

    fun getAccountInfo(refreshAnyway: Boolean = false){
        var currentAcc = realm.where(AccountInfo::class.java).equalTo("token", AccountRepo.token).findFirst()

        if(currentAcc != null && !refreshAnyway){
            return
        }



        realm.executeTransaction {
            it.where(AccountInfo::class.java).findAll().deleteAllFromRealm()
        }


        var accountApi = Retrofit.Builder()
                .baseUrl(AccountApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(AccountApi::class.java)

        accountApi.getAccountInfo("OAuth " + AccountRepo.token!!).enqueue(object : Callback<AccountInfo> {
            override fun onFailure(call: Call<AccountInfo>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<AccountInfo>?, response: Response<AccountInfo>?) {
                if(response!!.isSuccessful && response.body() != null){
                    currentAcc = response.body()
                    currentAcc?.token = AccountRepo.token!!
                    Log.i("ACCOUNT", currentAcc?.login)
                    realm.executeTransaction {
                        it.insert(currentAcc)
                    }

                }
            }
        })

    }
}
