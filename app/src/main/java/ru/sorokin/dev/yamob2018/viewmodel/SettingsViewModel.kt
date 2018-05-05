package ru.sorokin.dev.yamob2018.viewmodel

import ru.sorokin.dev.yamob2018.model.entity.AccountInfo
import ru.sorokin.dev.yamob2018.model.entity.DriveInfo
import ru.sorokin.dev.yamob2018.model.repository.AccountRepo
import ru.sorokin.dev.yamob2018.model.repository.DriveRepo
import ru.sorokin.dev.yamob2018.util.RealmLiveData
import ru.sorokin.dev.yamob2018.viewmodel.base.BaseFragmentViewModel

class SettingsViewModel: BaseFragmentViewModel() {
    var accountRepo = AccountRepo()
    var driveRepo = DriveRepo()

    var account : RealmLiveData<AccountInfo> = RealmLiveData(accountRepo.getAccount(false))
    var driveInfo : RealmLiveData<DriveInfo> = RealmLiveData(driveRepo.getDiskInfo())




}