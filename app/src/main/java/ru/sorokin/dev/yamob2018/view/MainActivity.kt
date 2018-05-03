package ru.sorokin.dev.yamob2018.view

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.util.Log
import ru.sorokin.dev.yamob2018.DriveApp
import ru.sorokin.dev.yamob2018.R
import ru.sorokin.dev.yamob2018.Screens
import ru.sorokin.dev.yamob2018.view.base.BaseActivityWithVM
import ru.sorokin.dev.yamob2018.viewmodel.MainViewModel
import com.yandex.authsdk.YandexAuthException
import android.content.Intent
import android.support.annotation.Nullable
import android.support.v4.view.ViewPager
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.support.v4.app.FragmentPagerAdapter
import com.ncapdevi.fragnav.FragNavController
import ru.sorokin.dev.yamob2018.model.repository.AccountRepo
import ru.sorokin.dev.yamob2018.util.observe


class MainActivity : BaseActivityWithVM<MainViewModel>() {

    companion object {
        val REQUEST_CODE_YA_LOGIN = 1001
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
                .newBuilder(savedInstanceState, getSupportFragmentManager(), containerId)
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

        if (getIntent() != null && getIntent().getData() != null) {
            return
        }

        viewModel.updateUI(savedInstanceState == null)
    }

    fun setSelectedNavItem(itemN: Int){
        val menu = navigation.menu
        for (i in 1..menu.size()-1){
            val item = menu.getItem(i)
            item.setChecked(i == itemN)
        }
    }

    override fun provideViewModel(): MainViewModel =
            ViewModelProviders.of(this)[MainViewModel::class.java]

    fun setBottomBarVisibility(vis: Int){
        navigation.visibility = vis
    }

}
