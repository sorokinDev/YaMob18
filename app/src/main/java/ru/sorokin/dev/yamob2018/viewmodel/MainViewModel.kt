package ru.sorokin.dev.yamob2018.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.support.annotation.Nullable
import ru.sorokin.dev.yamob2018.DriveApp
import ru.sorokin.dev.yamob2018.Pref
import ru.sorokin.dev.yamob2018.Screens
import ru.sorokin.dev.yamob2018.model.entity.AccountInfo
import ru.sorokin.dev.yamob2018.viewmodel.base.BaseViewModel
import ru.sorokin.dev.yamob2018.model.repository.AccountRepo


class MainViewModel: BaseViewModel() {
    val auth = AccountRepo()
    var currentScreen = MutableLiveData<String>()


    fun updateUI(shouldNavToStart: Boolean) {
        if(AccountRepo.token == null){
            navTo(Screens.AUTH_FAILURE)
            //navTo(Screens.AUTH)
            return
        }

        if(shouldNavToStart){
            navTo(Screens.HOME)
        }
    }

    fun navTo(screenName : String) {
        currentScreen.value = screenName
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

        }
    }


}