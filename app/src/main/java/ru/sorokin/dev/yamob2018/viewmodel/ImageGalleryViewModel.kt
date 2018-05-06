package ru.sorokin.dev.yamob2018.viewmodel

import android.util.Log
import io.realm.Sort
import ru.sorokin.dev.yamob2018.model.entity.DriveGetImagesResult
import ru.sorokin.dev.yamob2018.model.entity.DriveImage
import ru.sorokin.dev.yamob2018.model.repository.DriveRepo
import ru.sorokin.dev.yamob2018.util.*
import ru.sorokin.dev.yamob2018.viewmodel.base.BaseFragmentViewModel

class ImageGalleryViewModel: BaseFragmentViewModel() {
    override fun onCleared() {
        super.onCleared()
        Log.i("galleryVM", "onCleared")
    }

    val driveRepo = DriveRepo()
    var extraOffset = 0

    var images: RealmLiveData<DriveImage> = RealmLiveData(driveRepo.realm.where(DriveImage::class.java).sort("dateModified", Sort.DESCENDING).findAllAsync())

    var loadedFirstPage: Boolean = false

    var imagesAsList: List<DriveImage> = listOf()
        get() = if(images.value != null && images.value!!.isValid) images.value!! else listOf()

    var currentPosition = mutableLiveDataWithValue(-1)
    var rvPosition = mutableLiveDataWithValue(-1)

    var prevItemCount: Int = 0

    var loading = mutableLiveDataWithValue(false)

    fun loadImages(limit: Int, offset: Int, preview_crop: Boolean,
                  preview_size: String, sort: String, resCallback: ApiQueryCallback<DriveGetImagesResult>){
        prevItemCount = imagesAsList.count()
        loading.value = true

        driveRepo.getImages(false, limit, offset + extraOffset, preview_crop, preview_size, sort,
            apiQueryCallback { isSuccessResponse, isFailure, response, error ->
                loading.value = false
                if(response.isValid()){
                    extraOffset = response!!.body()!!.items.count() - (imagesAsList.size - prevItemCount)
                }

                resCallback.handle(isSuccessResponse, isFailure, response, error)
            }
        )

    }

    fun loadFirst(limit: Int, offset: Int, preview_crop: Boolean,
                  preview_size: String, sort: String, resCallback: ApiQueryCallback<DriveGetImagesResult>) {
        extraOffset = 0
        driveRepo.isFirstLoad = true
        loading.value = true
        driveRepo.getImages(false, limit, offset + extraOffset, preview_crop, preview_size, sort,
                apiQueryCallback { isSuccessResponse, isFailure, response, error ->
                    loadedFirstPage = true
                    loading.value = false
                    resCallback.handle(isSuccessResponse, isFailure, response, error)
                })
    }
}