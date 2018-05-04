package ru.sorokin.dev.yamob2018.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import android.view.View
import io.realm.Sort
import ru.sorokin.dev.yamob2018.model.entity.DriveImage
import ru.sorokin.dev.yamob2018.model.repository.DriveRepo
import ru.sorokin.dev.yamob2018.util.RealmLiveData
import ru.sorokin.dev.yamob2018.util.mutableLiveDataWithValue
import ru.sorokin.dev.yamob2018.viewmodel.base.BaseFragmentViewModel

class ImageGalleryViewModel: BaseFragmentViewModel() {
    override fun onCleared() {
        super.onCleared()
        Log.i("galleryVM", "onCleared")
    }

    override var bottomBarVisibility: MutableLiveData<Int> = mutableLiveDataWithValue(View.VISIBLE)

    val driveRepo = DriveRepo()

    var images: RealmLiveData<DriveImage> = RealmLiveData(driveRepo.realm.where(DriveImage::class.java).sort("dateModified", Sort.DESCENDING).findAllAsync())

    var imagesAsList: List<DriveImage> = listOf()
        get() = if(images.value != null) images.value!!.filterNot { it.preview == "" || it.file == "" } else listOf()

    fun loadImages(limit: Int, offset: Int, preview_crop: Boolean,
                  preview_size: String, sort: String){
        driveRepo.getImages(limit, offset, preview_crop, preview_size, sort)

    }
}