package ru.sorokin.dev.yamob2018.model.repository

import com.yandex.authsdk.YandexAuthSdk
import io.realm.Realm
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Response
import ru.sorokin.dev.yamob2018.DriveApp
import ru.sorokin.dev.yamob2018.Pref
import ru.sorokin.dev.yamob2018.R
import ru.sorokin.dev.yamob2018.model.entity.AccountInfo
import ru.sorokin.dev.yamob2018.model.entity.DriveImage
import ru.sorokin.dev.yamob2018.model.entity.DriveInfo
import ru.sorokin.dev.yamob2018.model.rest.BaseCallback
import ru.sorokin.dev.yamob2018.util.Providers
import ru.sorokin.dev.yamob2018.util.isNullOrEmpty


class AccountRepo {
    companion object {
        var token: String = ""
            get() = DriveApp.preferences.getString(Pref.TOKEN, "")
    }

    var authSdk: YandexAuthSdk = Providers.provideYandexAuthSdk()
    var realm : Realm = Providers.provideRealm()
    val accountApi = Providers.provideAccountApi()

    fun saveAuth(token: String){
        DriveApp.preferences.edit().putString(Pref.TOKEN, token).apply()
        realm.executeTransaction {
            realm.insertOrUpdate(AccountInfo(token = token))
        }
    }

    fun getLocalAccount(): RealmResults<AccountInfo> {
        return realm.where(AccountInfo::class.java).findAllAsync()
    }

    fun getAccount(refreshAnyway: Boolean = false): RealmResults<AccountInfo>{
        val accRes = getLocalAccount().apply { load() }
        val currentAcc = accRes.singleOrNull { !isNullOrEmpty(it.token) && it.token == AccountRepo.token }

        if(currentAcc != null && !isNullOrEmpty(currentAcc.id) && !refreshAnyway){
            return realm.where(AccountInfo::class.java).findAllAsync()
        }

        if(isNullOrEmpty(AccountRepo.token)){
            return getLocalAccount()
        }

        accountApi.getAccountInfo().enqueue(object : BaseCallback<AccountInfo>() {
            override val toastOnFailure = DriveApp.INSTANCE.resources.getString(R.string.cant_load_data)


            override fun onSucceessResponse(call: Call<AccountInfo>, response: Response<AccountInfo>) {
                response.body()?.let { acc ->
                    realm.executeTransaction {rm -> rm.insertOrUpdate(acc.apply { token = AccountRepo.token }) }
                }
            }

            override fun onBadResponse(call: Call<AccountInfo>, response: Response<AccountInfo>) {
                signOut()
            }

            override fun onFailure(call: Call<AccountInfo>?, t: Throwable?) {
                super.onFailure(call, t)
            }
        })

        return getLocalAccount()
    }

    fun getUserAvatarUrl(usr: AccountInfo): String{
        return "https://avatars.yandex.net/get-yapic/${usr.avatarId}/islands-200"
    }

    fun signOut(){
        DriveApp.preferences.edit().remove(Pref.TOKEN).apply()
        deleteUserData()
    }

    fun deleteUserData(){
        realm.executeTransaction {
            it.delete(AccountInfo::class.java)
            it.delete(DriveInfo::class.java)
            it.delete(DriveImage::class.java)
        }
    }


}
