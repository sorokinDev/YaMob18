package ru.sorokin.dev.yamob2018.view

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.util.Log
import com.ncapdevi.fragnav.FragNavController
import com.ncapdevi.fragnav.FragNavTransactionOptions
import com.yandex.authsdk.YandexAuthException
import kotlinx.android.synthetic.main.activity_main.*
import ru.sorokin.dev.yamob2018.R
import ru.sorokin.dev.yamob2018.Screens
import ru.sorokin.dev.yamob2018.util.BottomNavigationViewBehavior
import ru.sorokin.dev.yamob2018.util.observe
import ru.sorokin.dev.yamob2018.view.base.BaseActivityWithVM
import ru.sorokin.dev.yamob2018.viewmodel.AuthViewModel
import ru.sorokin.dev.yamob2018.viewmodel.ImageGalleryViewModel
import ru.sorokin.dev.yamob2018.viewmodel.MainViewModel

class MainActivity : BaseActivityWithVM<MainViewModel>() {
    override fun onBackPressed() {
        try{
            fragNavController.popFragment()
        }catch(e: Exception){
            this.finish()
        }


    }

    companion object {
        const val REQUEST_CODE_YA_LOGIN = 1001
    }

    //region NAVIGATION
    lateinit var fragNavController: FragNavController

    val contentContainerId = R.id.content

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        Log.i("MainAct", "OnNavListener")
        viewModel.currentScreen.value = when(item.itemId){
            R.id.navigation_images -> Screens.IMAGES
            R.id.navigation_offline -> Screens.OFFLINE
            R.id.navigation_settings -> Screens.SETTINGS
            else -> Screens.HOME
        }

        navigation.clearAnimation()
        navigation.animate().translationY(0f).duration = 100

        return@OnNavigationItemSelectedListener true
    }

    //Highlights item in bottom nav view
    fun setSelectedNavItem(itemN: Int){
        Log.i("SelNav", itemN.toString())
        val menu = navigation.menu
        if(itemN < menu.size()) {
            menu.getItem(itemN).isChecked = true
        }
    }

    //Observer for viewModel.currentScreen
    fun onCurrentScreenChanged(it: String) {
        val tabN = when(it) {
            Screens.IMAGES -> 0
            Screens.OFFLINE -> 1
            Screens.SETTINGS -> 2
            Screens.AUTH_NEEDED -> 3
            Screens.HOME -> 0
            else -> -1
        }

        if(tabN >= 0){
            fragNavController.switchTab(tabN)
            setSelectedNavItem(tabN)
            return
        }

        when(it){
            Screens.AUTH -> {
                startActivityForResult(authViewModel.accountRepo.authSdk.createLoginIntent(this, null), MainActivity.REQUEST_CODE_YA_LOGIN)
            }
        }
    }

    fun createFragNavController(savedInstanceState: Bundle?){
        fragNavController =  FragNavController
                .newBuilder(savedInstanceState, supportFragmentManager, contentContainerId)
                .rootFragments(listOf(
                        ImageGalleryFragment.newInstance(),
                        Fragment(),
                        SettingsFragment.newInstance(),
                        AuthNeededFragment.newInstance()
                ))
                .defaultTransactionOptions(FragNavTransactionOptions.newBuilder().transition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).build())
                .build()
    }

    //Does all navigation initialisation
    fun initNavigation(savedInstanceState: Bundle?){
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        createFragNavController(savedInstanceState)

        viewModel.currentScreen.observe(this){ it?.let { onCurrentScreenChanged(it) } }

        val layoutParams = navigation.getLayoutParams() as CoordinatorLayout.LayoutParams
        layoutParams.behavior = BottomNavigationViewBehavior()
    }

    fun setBottomBarVisibility(vis: Int){
        navigation.visibility = vis
    }

    //endregion

    //region LIIFECYCLE
    override fun provideViewModel(): MainViewModel =
            ViewModelProviders.of(this)[MainViewModel::class.java]

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (::fragNavController.isInitialized) {
            fragNavController.onSaveInstanceState(outState)
        }
    }
    //endregion

    //region AUTH
    lateinit var authViewModel: AuthViewModel

    fun initAuthViewModel(){
        authViewModel = ViewModelProviders.of(this)[AuthViewModel::class.java]
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_YA_LOGIN) {
            if(resultCode == Activity.RESULT_OK && data != null){
                try {
                    authViewModel.onAuth(resultCode, data)
                } catch (e: YandexAuthException) {
                    Log.e("AUTH", e.message)
                }
            }else{
                Log.e("MainAct", "OnRes")
            }
        }

    }
    //endregion

    lateinit var imageGalleryViewModel: ImageGalleryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initNavigation(savedInstanceState)

        initAuthViewModel()

        imageGalleryViewModel = ViewModelProviders.of(this)[ImageGalleryViewModel::class.java]
        authViewModel.authState.observe(this){
            when(it){
                AuthViewModel.NO_AUTH -> viewModel.navTo(Screens.AUTH_NEEDED)
                AuthViewModel.TOKEN_ONLY -> viewModel.navTo(Screens.AUTH_NEEDED)
                AuthViewModel.COMPLETE_AUTH -> {
                    if(viewModel.needGoHome){
                        viewModel.navTo(Screens.HOME)
                    }
                }
            }
        }

        if (intent != null && intent.data != null) {
            return
        }

    }


}
