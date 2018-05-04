package ru.sorokin.dev.yamob2018

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.support.multidex.MultiDex
import io.realm.Realm

class DriveApp : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {
        const val SHARED_PREF_NAME = "DriveApp"

        const val YA_ID = "85820f69416b4c99a807bd5184ce45f3"

        lateinit var INSTANCE: DriveApp

        val preferences: SharedPreferences
            get() = INSTANCE.getSharedPreferences(DriveApp.SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this

        Realm.init(applicationContext)
    }

}