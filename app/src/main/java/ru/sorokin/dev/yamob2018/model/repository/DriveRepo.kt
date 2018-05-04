package ru.sorokin.dev.yamob2018.model.repository

import android.util.Log
import io.realm.Realm
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Response
import ru.sorokin.dev.yamob2018.model.entity.DriveGetImagesResult
import ru.sorokin.dev.yamob2018.model.entity.DriveInfo
import ru.sorokin.dev.yamob2018.model.rest.BaseCallback
import ru.sorokin.dev.yamob2018.model.rest.DriveApi
import ru.sorokin.dev.yamob2018.model.rest.Providers


class DriveRepo {

    var realm :Realm = Realm.getDefaultInstance()
    var driveApi : DriveApi = Providers.provideDriveApi()

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

    fun getImages(limit: Int, offset: Int, preview_crop: Boolean,
                   preview_size: String, sort: String){
        val fields = "items.resource_id,items.name,items.created,items.modified," +
                "items.file,items.preview,limit,offset"
        driveApi.getImages(fields, limit, offset, preview_crop, preview_size, sort).enqueue(object : BaseCallback<DriveGetImagesResult>() {
            override val toastOnFailure: String? = "No internet"
            override val snackOnFailure: String? = null

            override fun onSucceessResponse(call: Call<DriveGetImagesResult>, response: Response<DriveGetImagesResult>) {
                response.body()?.let {
                    Log.i("DriveRepo", "Loaded: ${it.items.size} images")
                    realm.executeTransaction { rm ->
                        rm.insertOrUpdate(it.items)
                    }
                }
            }

            override fun onBadResponse(call: Call<DriveGetImagesResult>, response: Response<DriveGetImagesResult>) {
                Log.i("DriveRepo", "Bad response")
            }
        })

    }

}