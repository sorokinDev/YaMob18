package ru.sorokin.dev.yamob2018.viewmodel

import android.view.View
import io.realm.Realm
import io.realm.kotlin.deleteFromRealm
import ru.sorokin.dev.yamob2018.DriveApp
import ru.sorokin.dev.yamob2018.Pref
import ru.sorokin.dev.yamob2018.Screens
import ru.sorokin.dev.yamob2018.model.entity.AccountInfo
import ru.sorokin.dev.yamob2018.model.entity.DriveInfo
import ru.sorokin.dev.yamob2018.model.repository.AccountRepo
import ru.sorokin.dev.yamob2018.model.repository.DriveRepo
import ru.sorokin.dev.yamob2018.util.RealmLiveData
import ru.sorokin.dev.yamob2018.util.RealmSingleLiveData
import ru.sorokin.dev.yamob2018.util.mutableLiveDataWithValue
import ru.sorokin.dev.yamob2018.viewmodel.base.BaseFragmentViewModel

class SettingsViewModel: BaseFragmentViewModel() {
    override var bottomBarVisibility = mutableLiveDataWithValue(View.VISIBLE)

    var accountRepo = AccountRepo()
    var driveRepo = DriveRepo()

    var account : RealmLiveData<AccountInfo> = RealmLiveData(accountRepo.getAccountInfo(false))
    var driveInfo : RealmLiveData<DriveInfo> = RealmLiveData(driveRepo.getDiskInfo())

    fun signout(){
        accountRepo.signOut()

    }



}