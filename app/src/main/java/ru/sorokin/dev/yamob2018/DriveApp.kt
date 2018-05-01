package ru.sorokin.dev.yamob2018

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import io.realm.Realm
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router

class DriveApp : Application() {

    companion object {
        val SHARED_PREF_NAME = "DriveApp"

        val YA_ID = "85820f69416b4c99a807bd5184ce45f3"
        val YA_PASS = "ebadfad59dcd4857808ec0a346f824da"
        val ACCOUNT_TYPE = "com.yandex"
        val AUTH_URL = "https://oauth.yandex.ru/authorize?response_type=token&client_id=$YA_ID"
        private val ACTION_ADD_ACCOUNT = "com.yandex.intent.ADD_ACCOUNT"
        private val KEY_CLIENT_SECRET = "clientSecret"

        var USERNAME = "example.username"
        var TOKEN = "example.token"

        lateinit var INSTANCE: DriveApp

        val preferences: SharedPreferences
            get() = INSTANCE.getSharedPreferences(DriveApp.SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    private lateinit var cicerone: Cicerone<Router>

    val router: Router
        get() = cicerone.router

    val navigatorHolder: NavigatorHolder
        get() = cicerone.navigatorHolder

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this

        initCicerone()

        Realm.init(applicationContext)
    }

    private fun initCicerone() {
        cicerone = Cicerone.create()
    }
}