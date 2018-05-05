package ru.sorokin.dev.yamob2018.viewmodel

import android.util.Log
import io.realm.Sort
import ru.sorokin.dev.yamob2018.model.entity.DriveImage
import ru.sorokin.dev.yamob2018.model.repository.DriveRepo
import ru.sorokin.dev.yamob2018.util.RealmLiveData
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

    var currentPosition: Int = 0

    fun loadImages(limit: Int, offset: Int, preview_crop: Boolean,
                  preview_size: String, sort: String, onAfterResponse: () -> Unit, onNoConnection: () -> Unit){
        driveRepo.getImages(limit, offset + extraOffset, preview_crop, preview_size, sort, { exOffset -> extraOffset = exOffset; onAfterResponse() }, onNoConnection)

    }

    fun loadFirst(limit: Int, offset: Int, preview_crop: Boolean,
                  preview_size: String, sort: String, onAfterResponse: () -> Unit, onNoConnection: () -> Unit) {
        extraOffset = 0
        driveRepo.isFirstLoad = true
        loadImages(limit, offset, preview_crop, preview_size, sort, { loadedFirstPage = true; onAfterResponse() }, onNoConnection)
    }
}