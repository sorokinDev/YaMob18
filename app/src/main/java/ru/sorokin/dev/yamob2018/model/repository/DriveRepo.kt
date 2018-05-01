package ru.sorokin.dev.yamob2018.model.repository

import com.yandex.disk.rest.Credentials
import com.yandex.disk.rest.RestClient

class DriveRepo {
    var driveSdk = RestClient(Credentials(AccountRepo.username, AccountRepo.token))

    fun aaa(){
        driveSdk.diskInfo
    }

}