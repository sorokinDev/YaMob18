package ru.sorokin.dev.yamob2018.viewmodel

import ru.sorokin.dev.yamob2018.model.entity.AccountInfo
import ru.sorokin.dev.yamob2018.model.entity.DriveInfo
import ru.sorokin.dev.yamob2018.model.repository.AccountRepo
import ru.sorokin.dev.yamob2018.model.repository.DriveRepo
import ru.sorokin.dev.yamob2018.util.ApiQueryCallback
import ru.sorokin.dev.yamob2018.util.RealmLiveData
import ru.sorokin.dev.yamob2018.viewmodel.base.BaseFragmentViewModel

class SettingsViewModel: BaseFragmentViewModel() {
    var accountRepo = AccountRepo()
    var driveRepo = DriveRepo()

    var account : RealmLiveData<AccountInfo> = RealmLiveData(driveRepo.realm.where(AccountInfo::class.java).equalTo("token", AccountRepo.token).findAllAsync())
    var driveInfo : RealmLiveData<DriveInfo> = RealmLiveData(driveRepo.realm.where(DriveInfo::class.java).equalTo("token", AccountRepo.token).findAllAsync())

    fun getDriveInfo(resCallback: ApiQueryCallback<DriveInfo>){
        driveRepo.getDiskInfo(resCallback)
    }

    fun getAccountInfo(){
        accountRepo.getAccount(true)
    }

    fun getAvatarUrl(usr: AccountInfo): String {
        return accountRepo.getUserAvatarUrl(usr)
    }

}