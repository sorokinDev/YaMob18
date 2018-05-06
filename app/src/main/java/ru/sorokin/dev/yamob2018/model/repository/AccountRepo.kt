package ru.sorokin.dev.yamob2018.model.repository

import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthSdk
import io.realm.Realm
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Response
import ru.sorokin.dev.yamob2018.DriveApp
import ru.sorokin.dev.yamob2018.Pref
import ru.sorokin.dev.yamob2018.model.entity.AccountInfo
import ru.sorokin.dev.yamob2018.model.rest.BaseCallback
import ru.sorokin.dev.yamob2018.model.rest.Providers
import ru.sorokin.dev.yamob2018.util.isNullOrEmpty


class AccountRepo {
    companion object {
        var token: String? = null
            get() = DriveApp.preferences.getString(Pref.TOKEN, "")

        //val SCOPES = setOf("cloud_api:disk.read", "cloud_api:disk.app_folder", "cloud_api:disk.info", "cloud_api:disk.write", "login:avatar")
    }

    var authSdk: YandexAuthSdk =
            YandexAuthSdk(DriveApp.INSTANCE.applicationContext, YandexAuthOptions(DriveApp.INSTANCE.applicationContext, true))
    var realm : Realm = Realm.getDefaultInstance()

    fun saveAuth(token: String){

        DriveApp.preferences.edit().putString(Pref.TOKEN, token).apply()
        realm.executeTransaction {
            realm.insertOrUpdate(AccountInfo(token = token))
        }

        getAccount(true)

    }

    fun getAccount(refreshAnyway: Boolean = false): RealmResults<AccountInfo>{
        val accRes = realm.where(AccountInfo::class.java).findAll()

        val currentAcc = accRes.singleOrNull { it.token == AccountRepo.token }
        if(currentAcc != null && !isNullOrEmpty(currentAcc.id) && !refreshAnyway){
            return realm.where(AccountInfo::class.java).findAllAsync()
        }

        val accountApi = Providers.provideAccountApi()

        accountApi.getAccountInfo().enqueue(object : BaseCallback<AccountInfo>() {
            override val toastOnFailure: String? = null
            override val snackOnFailure: String? = "No internet"

            override fun onSucceessResponse(call: Call<AccountInfo>, response: Response<AccountInfo>) {
                response.body()?.let { acc ->
                    //Log.i("accRepo", "get acc OnSuccess response")

                    acc.token = AccountRepo.token!!
                    realm.executeTransaction {realm ->
                        realm.insertOrUpdate(acc)
                    }
                }
            }

            override fun onBadResponse(call: Call<AccountInfo>, response: Response<AccountInfo>) {

            }
        })

        return accRes
    }

    fun signOut(){
        DriveApp.preferences.edit().remove(Pref.TOKEN).apply()
        deleteUserData()
    }

    fun deleteUserData(){
        //TODO: make good signing out
        realm.executeTransaction {
            it.deleteAll()

        }
    }
}
