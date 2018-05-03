package ru.sorokin.dev.yamob2018.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.support.annotation.Nullable
import com.yandex.authsdk.YandexAuthToken
import ru.sorokin.dev.yamob2018.DriveApp
import ru.sorokin.dev.yamob2018.Pref
import ru.sorokin.dev.yamob2018.Screens
import ru.sorokin.dev.yamob2018.model.entity.AccountInfo
import ru.sorokin.dev.yamob2018.viewmodel.base.BaseViewModel
import ru.sorokin.dev.yamob2018.model.repository.AccountRepo


class MainViewModel: BaseViewModel() {
    val auth = AccountRepo()
    var bottomMenuSelectedItem = MutableLiveData<String>()


    fun updateUI(shouldNavToStart: Boolean) {
        if(AccountRepo.token == null){
            navToAuth()
            return
        }

        if(shouldNavToStart){
            navToStart()
        }
    }

    fun navToAuth(){
        router.navigateTo(Screens.AUTH)
    }

    fun navToStart() {
        navTo(Screens.FEED)
    }

    fun navTo(screenName : String) {
        bottomMenuSelectedItem.value = screenName
    }

    fun onAuth(resultCode: Int, @Nullable data: Intent) {
        val yandexAuthToken = auth.authSdk.extractToken(resultCode, data)
        if (yandexAuthToken != null) {

            DriveApp.preferences.edit().putString(Pref.TOKEN, yandexAuthToken.value).apply()
            auth.realm.executeTransaction {
                it.delete(AccountInfo::class.java)
                it.insertOrUpdate(AccountInfo(token = yandexAuthToken.value))
            }

            auth.getAccountInfo(true)

            updateUI(true)
        }else{
            onAuthError()
        }
    }

    fun onAuthError() {
        router.newRootScreen(Screens.AUTH_FAILURE)
    }
}