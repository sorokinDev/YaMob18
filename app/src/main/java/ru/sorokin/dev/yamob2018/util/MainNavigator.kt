package ru.sorokin.dev.yamob2018.util

import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.util.Log
import ru.sorokin.dev.yamob2018.Screens
import ru.sorokin.dev.yamob2018.model.repository.AccountRepo
import ru.sorokin.dev.yamob2018.view.SettingsFragment
import ru.sorokin.dev.yamob2018.view.AuthFailureFragment
import ru.sorokin.dev.yamob2018.view.MainActivity
import ru.terrakok.cicerone.android.SupportFragmentNavigator
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward
import ru.terrakok.cicerone.commands.Replace

class MainNavigator(val activity: MainActivity, supportFragmentManager: FragmentManager, val containerId: Int) : SupportFragmentNavigator(supportFragmentManager, containerId){
    override fun setupFragmentTransactionAnimation(command: Command?, currentFragment: Fragment?, nextFragment: Fragment?, fragmentTransaction: FragmentTransaction?) {
        super.setupFragmentTransactionAnimation(command, currentFragment, nextFragment, fragmentTransaction)
        fragmentTransaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
    }

    override fun exit() {
        activity.finish()
    }

    override fun createFragment(screenKey: String?, data: Any?): Fragment? {
        val fr: Fragment? = when(screenKey!!){
            Screens.FEED -> Fragment()
            Screens.OFFLINE -> Fragment()
            Screens.ACCOUNT -> SettingsFragment.newInstance()
            Screens.AUTH_FAILURE -> AuthFailureFragment.newInstance()
            else -> null
        }
        return fr
    }

    override fun unknownScreen(command: Command?) {
        if(command == null || !(command is Forward || command is Replace)){
            //Log.e("NAVIGATOR", "unknown screen error")
            return
        }
        if((command is Forward) && command.screenKey == Screens.AUTH){
            Log.i("AUTH", "Starting ya login intent")
            activity.startActivityForResult(activity.viewModel.auth.authSdk.createLoginIntent(activity, AccountRepo.SCOPES), MainActivity.REQUEST_CODE_YA_LOGIN)
        }
    }

    override fun showSystemMessage(message: String?) {
        message?.let {
            Snackbar.make(activity.findViewById(containerId), it, Snackbar.LENGTH_LONG).show()
        }
    }
}