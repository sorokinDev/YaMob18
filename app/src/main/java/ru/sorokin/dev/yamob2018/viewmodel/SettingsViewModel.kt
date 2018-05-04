package ru.sorokin.dev.yamob2018.viewmodel

import android.view.View
import ru.sorokin.dev.yamob2018.model.entity.AccountInfo
import ru.sorokin.dev.yamob2018.model.entity.DriveInfo
import ru.sorokin.dev.yamob2018.model.repository.AccountRepo
import ru.sorokin.dev.yamob2018.model.repository.DriveRepo
import ru.sorokin.dev.yamob2018.util.RealmLiveData
import ru.sorokin.dev.yamob2018.util.mutableLiveDataWithValue
import ru.sorokin.dev.yamob2018.viewmodel.base.BaseFragmentViewModel

class SettingsViewModel: BaseFragmentViewModel() {
    override var bottomBarVisibility = mutableLiveDataWithValue(View.VISIBLE)

    var accountRepo = AccountRepo()
    var driveRepo = DriveRepo()

    var account : RealmLiveData<AccountInfo> = RealmLiveData(accountRepo.getAllAccounts(false))
    var driveInfo : RealmLiveData<DriveInfo> = RealmLiveData(driveRepo.getDiskInfo())




}