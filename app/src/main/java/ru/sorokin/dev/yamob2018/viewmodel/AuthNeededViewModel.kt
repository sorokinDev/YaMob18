package ru.sorokin.dev.yamob2018.viewmodel

import ru.sorokin.dev.yamob2018.util.mutableLiveDataWithValue
import ru.sorokin.dev.yamob2018.viewmodel.base.BaseFragmentViewModel

class AuthNeededViewModel: BaseFragmentViewModel() {
    companion object {
        const val STATE_NO_AUTH = "no_auth"
        const val STATE_TOKEN_ONLY = "token_only"
        const val STATE_CANT_LOAD_DATA = "cant_load"
    }

    val currentState = mutableLiveDataWithValue(AuthNeededViewModel.STATE_NO_AUTH)
}