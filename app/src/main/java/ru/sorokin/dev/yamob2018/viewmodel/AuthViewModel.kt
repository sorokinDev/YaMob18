package ru.sorokin.dev.yamob2018.viewmodel

import android.content.Intent
import android.support.annotation.Nullable
import ru.sorokin.dev.yamob2018.model.repository.AccountRepo
import ru.sorokin.dev.yamob2018.util.RealmLiveData
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
    var accounts = RealmLiveData(accountRepo.getAllAccounts(false))

    init {
        if(AccountRepo.token == null || AccountRepo.token == ""){
            authState.value = AuthViewModel.NO_AUTH
        }

        accounts.observeForever {
            it?.singleOrNull { it.token == AccountRepo.token }?.let {
                if(it.id == ""){
                    authState.value = AuthViewModel.TOKEN_ONLY
                }else{
                    authState.value = AuthViewModel.COMPLETE_AUTH
                }
            }
        }
    }

    fun signout(){
        accountRepo.signOut()
    }

    fun onAuth(resultCode: Int, @Nullable data: Intent) {
        val yandexAuthToken = accountRepo.authSdk.extractToken(resultCode, data)

        if (yandexAuthToken != null) {
            accountRepo.saveAuth(yandexAuthToken.value)

        }else{

        }
    }

}