package ru.sorokin.dev.yamob2018.view

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.util.Log
import com.ncapdevi.fragnav.FragNavController
import com.yandex.authsdk.YandexAuthException
import kotlinx.android.synthetic.main.activity_main.*
import ru.sorokin.dev.yamob2018.R
import ru.sorokin.dev.yamob2018.Screens
import ru.sorokin.dev.yamob2018.model.repository.AccountRepo
import ru.sorokin.dev.yamob2018.util.BottomNavigationViewBehavior
import ru.sorokin.dev.yamob2018.util.observe
import ru.sorokin.dev.yamob2018.view.base.BaseActivityWithVM
import ru.sorokin.dev.yamob2018.viewmodel.MainViewModel




class MainActivity : BaseActivityWithVM<MainViewModel>() {

    companion object {
        const val REQUEST_CODE_YA_LOGIN = 1001
        val TAB_MAPPING = mapOf(Pair(R.id.navigation_feed, Screens.IMAGES), Pair(R.id.navigation_offline, Screens.OFFLINE),
                Pair(R.id.navigation_settings, Screens.SETTINGS))
    }

    val containerId = R.id.content

    lateinit var fragNavController: FragNavController


    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_YA_LOGIN) {
            if(resultCode == Activity.RESULT_OK && data != null){
                try {
                    viewModel.onAuth(resultCode, data)
                } catch (e: YandexAuthException) {
                    Log.e("AUTH", e.message)
                }
            }else{

            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (fragNavController != null) {
            fragNavController.onSaveInstanceState(outState)
        }
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        Log.i("MainAct", "OnNavListener")
        viewModel.currentScreen.value = MainActivity.TAB_MAPPING[item.itemId]!!

        return@OnNavigationItemSelectedListener true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        fragNavController = FragNavController
                .newBuilder(savedInstanceState, supportFragmentManager, containerId)
                .rootFragments(listOf(
                        ImageGalleryFragment.newInstance(),
                        Fragment(),
                        SettingsFragment.newInstance(),
                        AuthFailureFragment.newInstance()
                )).build()

        viewModel.currentScreen.observe(this){
            it?.let {
                val tabN = when(it) {
                    Screens.IMAGES -> 0
                    Screens.OFFLINE -> 1
                    Screens.SETTINGS -> 2
                    Screens.AUTH_FAILURE -> 3
                    Screens.HOME -> 0
                    else -> -1
                }

                if(tabN >= 0){
                    Log.i("MainActivity", "NAV_TO >= 0")
                    fragNavController.switchTab(tabN)
                    setSelectedNavItem(tabN)
                    return@let
                }

                when(it){
                    Screens.AUTH -> {
                        Log.i("MainActivity", "NAV_TO_AUTH")
                        startActivityForResult(viewModel.auth.authSdk.createLoginIntent(this, AccountRepo.SCOPES), MainActivity.REQUEST_CODE_YA_LOGIN)
                    }
                }

            }
        }

        val layoutParams = navigation.getLayoutParams() as CoordinatorLayout.LayoutParams
        layoutParams.behavior = BottomNavigationViewBehavior()

        if (intent != null && intent.data != null) {
            return
        }

        viewModel.updateUI(savedInstanceState == null)
    }

    fun setSelectedNavItem(itemN: Int){
        Log.i("SelNav", itemN.toString())
        val menu = navigation.menu
        menu.getItem(itemN).isChecked = true
        for (i in 0..menu.size()-1){
            val item = menu.getItem(i)
            Log.i("SelNav", item.title.toString())
            item.isChecked = i == itemN
            break
        }
    }

    override fun provideViewModel(): MainViewModel =
            ViewModelProviders.of(this)[MainViewModel::class.java]

    fun setBottomBarVisibility(vis: Int){
        navigation.visibility = vis
    }

}
