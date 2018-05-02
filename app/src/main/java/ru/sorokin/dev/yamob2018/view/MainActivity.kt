package ru.sorokin.dev.yamob2018.view

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import ru.sorokin.dev.yamob2018.DriveApp
import ru.sorokin.dev.yamob2018.R
import ru.sorokin.dev.yamob2018.Screens
import ru.sorokin.dev.yamob2018.util.observe
import ru.sorokin.dev.yamob2018.view.base.BaseActivityWithVM
import ru.sorokin.dev.yamob2018.viewmodel.MainViewModel
import com.yandex.authsdk.YandexAuthException
import android.content.Intent
import android.support.annotation.Nullable
import ru.sorokin.dev.yamob2018.util.MainNavigator


class MainActivity : BaseActivityWithVM<MainViewModel>() {

    companion object {
        val REQUEST_CODE_YA_LOGIN = 1001
    }

    val containerId = R.id.fl_content
    val navigator = MainNavigator(this, supportFragmentManager, containerId)


    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_YA_LOGIN) {
            if(resultCode == Activity.RESULT_OK && data != null){
                try {
                    viewModel.onAuth(resultCode, data)
                } catch (e: YandexAuthException) {
                    Log.e("AUTH", e.message)
                    viewModel.onAuthError()
                }
            }else{
                viewModel.onAuthError()
            }
        }

    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        val screenName = when (item.itemId) {
            R.id.navigation_feed -> Screens.FEED
            R.id.navigation_offline -> Screens.OFFLINE
            R.id.navigation_settings -> Screens.ACCOUNT
            else -> Screens.FEED
        }

        if(screenName == viewModel.bottomMenuSelectedItem.value){
            return@OnNavigationItemSelectedListener true
        }

        viewModel.navTo(screenName)

        return@OnNavigationItemSelectedListener true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        viewModel.bottomMenuSelectedItem.observe(this){
            if(it != null){
                val newSelected = when(it){
                    Screens.FEED -> R.id.navigation_feed
                    Screens.OFFLINE -> R.id.navigation_offline
                    Screens.ACCOUNT -> R.id.navigation_settings
                    else -> R.id.navigation_feed
                }
                if(navigation.selectedItemId != newSelected){
                    navigation.selectedItemId = newSelected
                }

                viewModel.router.newRootScreen(it)
            }
        }

        if (getIntent() != null && getIntent().getData() != null) {
            return
        }
        
        viewModel.updateUI(savedInstanceState == null)

    }

    override fun onResume() {
        super.onResume()
        DriveApp.INSTANCE.navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        DriveApp.INSTANCE.navigatorHolder.removeNavigator()
    }

    override fun provideViewModel(): MainViewModel =
            ViewModelProviders.of(this)[MainViewModel::class.java]

    fun setBottomBarVisibility(vis: Int){
        navigation.visibility = vis
    }

}
