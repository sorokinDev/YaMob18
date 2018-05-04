package ru.sorokin.dev.yamob2018.viewmodel

import android.arch.lifecycle.MutableLiveData
import ru.sorokin.dev.yamob2018.Screens
import ru.sorokin.dev.yamob2018.viewmodel.base.BaseViewModel


class MainViewModel: BaseViewModel() {

    var currentScreen = MutableLiveData<String>()
    var needGoHome = true




    fun navTo(screenName : String) {
        currentScreen.value = screenName
        needGoHome = screenName == Screens.AUTH_NEEDED || screenName == Screens.AUTH
    }





}