package ru.sorokin.dev.yamob2018.model.repository

import android.util.Log
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
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
import ru.sorokin.dev.yamob2018.util.ApiQueryCallback


class DriveRepo {

    companion object {
        fun buildGlideUrl(url: String){
            GlideUrl(url, LazyHeaders.Builder().addHeader("Authorization", "OAuth ${AccountRepo.token}").build())
        }
    }
    var realm :Realm = Realm.getDefaultInstance()
    var driveApi : DriveApi = Providers.provideDriveApi()
    var isFirstLoad: Boolean = true

    fun getDiskInfo(resCallback: ApiQueryCallback<DriveInfo>) : RealmResults<DriveInfo> {
        driveApi.getDriveInfo().enqueue(object : BaseCallback<DriveInfo>() {
            override val toastOnFailure: String? = "Не удалось загрузить данные"

            override fun onSucceessResponse(call: Call<DriveInfo>, response: Response<DriveInfo>) {
                response.body()?.let { di ->
                    di.token = AccountRepo.token!!

                    realm.executeTransaction {
                        it.insertOrUpdate(di)
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

        return realm.where(DriveInfo::class.java).equalTo("token", AccountRepo.token).findAllAsync()
    }

    fun getImages(isFirst: Boolean, limit: Int, offset: Int, preview_crop: Boolean, preview_size: String, sort: String,
                       resCallback: ApiQueryCallback<DriveGetImagesResult>){

        val fields = "items.resource_id,items.name,items.created,items.modified," +
                "items.file,items.preview,limit,offset"

        driveApi.getImages(fields, limit, offset, preview_crop, preview_size, sort).enqueue(object : BaseCallback<DriveGetImagesResult>() {
            override val toastOnFailure: String? = "Не удалось загрузить данные" //TODO: Change no internet text

            override fun onSucceessResponse(call: Call<DriveGetImagesResult>, response: Response<DriveGetImagesResult>) {
                response.body()?.let {
                    Log.i("DriveRepo", "Loaded: ${it.items.size} images")
                    realm.executeTransaction { rm ->
                        if(isFirst) rm.delete(DriveImage::class.java)
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