package ru.sorokin.dev.yamob2018.viewmodel

import android.content.Intent
import android.support.annotation.Nullable
import android.util.Log
import ru.sorokin.dev.yamob2018.model.repository.AccountRepo
import ru.sorokin.dev.yamob2018.util.RealmLiveData
import ru.sorokin.dev.yamob2018.util.isNullOrEmpty
import ru.sorokin.dev.yamob2018.util.mutableLiveDataWithValue
import ru.sorokin.dev.yamob2018.viewmodel.base.BaseViewModel


class AuthViewModel: BaseViewModel() {
    companion object {
        const val NO_AUTH = "no_auth"
        const val TOKEN_ONLY = "token_only"
        const val COMPLETE_AUTH = "complete_auth"
    }
    val accountRepo = AccountRepo()
    val authState = mutableLiveDataWithValue("")
    var accounts = RealmLiveData(accountRepo.getAccount(false))

    init {
        if(isNullOrEmpty(AccountRepo.token)){
            authState.value = AuthViewModel.NO_AUTH
        }

        accounts.observeForever {
            val fst = it?.singleOrNull { it.token == AccountRepo.token }
            Log.i("accounts", "observer")
            if(fst != null){
                if(isNullOrEmpty(fst.id)){
                    authState.value = AuthViewModel.TOKEN_ONLY
                }else{
                    authState.value = AuthViewModel.COMPLETE_AUTH
                }
            }else{
                authState.value = AuthViewModel.NO_AUTH
            }
        }
    }

    fun signout(){
        accountRepo.signOut()
        authState.value = NO_AUTH
    }

    fun onAuth(resultCode: Int, @Nullable data: Intent) {
        val yandexAuthToken = accountRepo.authSdk.extractToken(resultCode, data)

        if (yandexAuthToken != null) {
            authState.value = AuthViewModel.TOKEN_ONLY
            accountRepo.saveAuth(yandexAuthToken.value)
            //accountRepo.getAccount(true)
        }else{
            authState.value = AuthViewModel.NO_AUTH
        }
    }

    fun getAccountInfo(){
        accountRepo.getAccount(true)
    }

    fun loadUserInfo() {
        accountRepo.getAccount(true)
    }

}