package ru.sorokin.dev.yamob2018.model.repository

import android.util.Log
import io.realm.Realm
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Response
import ru.sorokin.dev.yamob2018.model.entity.AccountInfo
import ru.sorokin.dev.yamob2018.model.entity.DriveInfo
import ru.sorokin.dev.yamob2018.model.rest.BaseCallback
import ru.sorokin.dev.yamob2018.model.rest.Providers


class DriveRepo {

    var realm = Realm.getDefaultInstance()
    var driveApi = Providers.provideDriveApi()

    fun getDiskInfo() : RealmResults<DriveInfo> {
        driveApi.getDriveInfo().enqueue(object : BaseCallback<DriveInfo>() {
            override val toastOnFailure: String? = null
            override val snackOnFailure: String? = null

            override fun onSucceessResponse(call: Call<DriveInfo>, response: Response<DriveInfo>) {
                response.body()?.let { di ->
                    di.token = AccountRepo.token!!

                    realm.executeTransaction {
                        it.insertOrUpdate(di)
                    }
                }
            }

            override fun onBadResponse(call: Call<DriveInfo>, response: Response<DriveInfo>) {

            }
        })

        return realm.where(DriveInfo::class.java).equalTo("token", AccountRepo.token).findAllAsync()
    }

}