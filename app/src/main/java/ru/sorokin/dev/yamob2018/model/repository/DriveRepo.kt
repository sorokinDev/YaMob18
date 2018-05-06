package ru.sorokin.dev.yamob2018.model.repository

import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import retrofit2.Call
import retrofit2.Response
import ru.sorokin.dev.yamob2018.DriveApp
import ru.sorokin.dev.yamob2018.R
import ru.sorokin.dev.yamob2018.model.entity.DriveGetImagesResult
import ru.sorokin.dev.yamob2018.model.entity.DriveImage
import ru.sorokin.dev.yamob2018.model.entity.DriveInfo
import ru.sorokin.dev.yamob2018.model.rest.BaseCallback
import ru.sorokin.dev.yamob2018.model.rest.DriveApi
import ru.sorokin.dev.yamob2018.util.ApiQueryCallback
import ru.sorokin.dev.yamob2018.util.Providers


class DriveRepo {
    var realm :Realm = Realm.getDefaultInstance()
    var driveApi : DriveApi = Providers.provideDriveApi()

    fun getLocalDiskInfo(): RealmResults<DriveInfo> {
        return realm.where(DriveInfo::class.java).equalTo("token", AccountRepo.token).findAllAsync()
    }

    fun getDiskInfo(resCallback: ApiQueryCallback<DriveInfo>) : RealmResults<DriveInfo> {
        driveApi.getDriveInfo().enqueue(object : BaseCallback<DriveInfo>() {
            override val toastOnFailure: String? = DriveApp.INSTANCE.resources.getString(R.string.cant_load_data)

            override fun onSucceessResponse(call: Call<DriveInfo>, response: Response<DriveInfo>) {
                response.body()?.let { di -> realm.executeTransaction {
                        it.insertOrUpdate(di.apply { token = AccountRepo.token })
                    }
                }
                resCallback.handle(true, false, response, null)
            }

            override fun onBadResponse(call: Call<DriveInfo>, response: Response<DriveInfo>) {
                resCallback.handle(false, false, response, null)
            }

            override fun onFailure(call: Call<DriveInfo>?, t: Throwable?) {
                super.onFailure(call, t)
                resCallback.handle(false, true, null, t)
            }
        })

        return getLocalDiskInfo()
    }

    fun getLocalImages(): RealmResults<DriveImage>? {
        return realm.where(DriveImage::class.java).sort("dateModified", Sort.DESCENDING).findAllAsync()
    }

    fun getImages(isFirstLoad: Boolean, limit: Int, offset: Int, preview_crop: Boolean, preview_size: String, sort: String,
                  resCallback: ApiQueryCallback<DriveGetImagesResult>){

        val fields = "items.resource_id,items.name,items.created,items.modified," +
                "items.file,items.preview,limit,offset"

        driveApi.getImages(fields, limit, offset, preview_crop, preview_size, sort).enqueue(object : BaseCallback<DriveGetImagesResult>() {
            override val toastOnFailure: String? = DriveApp.INSTANCE.getString(R.string.cant_load_data)

            override fun onSucceessResponse(call: Call<DriveGetImagesResult>, response: Response<DriveGetImagesResult>) {
                response.body()?.let {
                    //Log.i("DriveRepo", "Loaded: ${it.items.size} images")
                    realm.executeTransaction { rm ->
                        if(isFirstLoad) rm.delete(DriveImage::class.java)
                        rm.insertOrUpdate(it.items)
                    }
                    resCallback.handle(true, false, response, null)
                }
            }

            override fun onBadResponse(call: Call<DriveGetImagesResult>, response: Response<DriveGetImagesResult>) {
                resCallback.handle(false, false, response, null)
            }

            override fun onFailure(call: Call<DriveGetImagesResult>?, t: Throwable?) {
                super.onFailure(call, t)
                resCallback.handle(false, true, null, t)
            }
        })
    }




}