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
import ru.sorokin.dev.yamob2018.model.entity.DriveInfo
import ru.sorokin.dev.yamob2018.model.rest.BaseCallback
import ru.sorokin.dev.yamob2018.model.rest.Providers


class AccountRepo {
    companion object {
        var token: String? = null
            get() = DriveApp.preferences.getString(Pref.TOKEN, null)

        val SCOPES = setOf("cloud_api:disk.read", "cloud_api:disk.app_folder", "cloud_api:disk.info", "cloud_api:disk.write", "login:avatar")
    }


    var authSdk: YandexAuthSdk = YandexAuthSdk(DriveApp.INSTANCE.applicationContext, YandexAuthOptions(DriveApp.INSTANCE.applicationContext, true))
    var realm : Realm = Realm.getDefaultInstance()

    fun getAccountInfo(refreshAnyway: Boolean = false): RealmResults<AccountInfo>{
        val accRes = realm.where(AccountInfo::class.java).equalTo("token", AccountRepo.token).findAllAsync()

        val currentAcc = accRes.firstOrNull()
        if(currentAcc != null && currentAcc.id != "" && !refreshAnyway){
            return accRes
        }

        val accountApi = Providers.provideAccountApi()

        accountApi.getAccountInfo().enqueue(object : BaseCallback<AccountInfo>() {
            override val toastOnFailure: String? = null
            override val snackOnFailure: String? = "No internet"

            override fun onSucceessResponse(call: Call<AccountInfo>, response: Response<AccountInfo>) {
                response.body()?.let { acc ->
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
        realm.executeTransaction {
            it.where(AccountInfo::class.java).equalTo("token", AccountRepo.token).findAll().deleteAllFromRealm()
            it.where(DriveInfo::class.java).equalTo("token", AccountRepo.token).findAll().deleteAllFromRealm()
        }
        DriveApp.preferences.edit().remove(Pref.TOKEN).apply()
    }
}
