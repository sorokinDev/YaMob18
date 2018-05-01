package ru.sorokin.dev.yamob2018.viewmodel.base

import android.arch.lifecycle.ViewModel
import ru.sorokin.dev.yamob2018.DriveApp
import ru.terrakok.cicerone.Router

open class BaseViewModel : ViewModel() {
    var router: Router = DriveApp.INSTANCE.router

}