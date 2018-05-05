package ru.sorokin.dev.yamob2018.model.repository

import android.util.Log
import io.realm.Realm
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Response
import ru.sorokin.dev.yamob2018.model.entity.DriveGetImagesResult
import ru.sorokin.dev.yamob2018.model.entity.DriveImage
import ru.sorokin.dev.yamob2018.model.entity.DriveInfo
import ru.sorokin.dev.yamob2018.model.rest.BaseCallback
import ru.sorokin.dev.yamob2018.model.rest.DriveApi
import ru.sorokin.dev.yamob2018.model.rest.Providers


class DriveRepo {

    var realm :Realm = Realm.getDefaultInstance()
    var driveApi : DriveApi = Providers.provideDriveApi()
    var isFirstLoad: Boolean = true

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
                   preview_size: String, sort: String, onAfterResponse: (extraOffset: Int) -> Unit, onNoConnection: () -> Unit){

        val fields = "items.resource_id,items.name,items.created,items.modified," +
                "items.file,items.preview,limit,offset"

        driveApi.getImages(fields, limit, offset, preview_crop, preview_size, sort).enqueue(object : BaseCallback<DriveGetImagesResult>() {

            override val toastOnFailure: String? = "No internet"
            override val snackOnFailure: String? = null
            var extraOffset = 0

            override fun onSucceessResponse(call: Call<DriveGetImagesResult>, response: Response<DriveGetImagesResult>) {
                response.body()?.let {
                    if(isFirstLoad){
                        isFirstLoad = false
                        realm.executeTransaction { rm -> rm.delete(DriveImage::class.java) }
                    }
                    Log.i("DriveRepo", "Loaded: ${it.items.size} images")

                    if(it.items.isNotEmpty()) {
                        extraOffset = realm.where(DriveImage::class.java).lessThanOrEqualTo("dateModified", it.items.first().dateModified).count().toInt()
                    }
                    realm.executeTransaction { rm ->
                        rm.insertOrUpdate(it.items)
                    }

                }
                onAfterResponse(extraOffset)
            }

            override fun onBadResponse(call: Call<DriveGetImagesResult>, response: Response<DriveGetImagesResult>) {
                onNoConnection()
            }
        })

    }

}