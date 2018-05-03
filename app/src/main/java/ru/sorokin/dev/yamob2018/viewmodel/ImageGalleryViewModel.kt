package ru.sorokin.dev.yamob2018.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.view.View
import io.realm.Sort
import ru.sorokin.dev.yamob2018.model.entity.DriveImage
import ru.sorokin.dev.yamob2018.model.repository.DriveRepo
import ru.sorokin.dev.yamob2018.util.RealmLiveData
import ru.sorokin.dev.yamob2018.util.mutableLiveDataWithValue
import ru.sorokin.dev.yamob2018.viewmodel.base.BaseFragmentViewModel

class ImageGalleryViewModel: BaseFragmentViewModel() {
    override var bottomBarVisibility: MutableLiveData<Int> = mutableLiveDataWithValue(View.VISIBLE)

    val driveRepo = DriveRepo()

    var images: RealmLiveData<DriveImage> = RealmLiveData(driveRepo.realm.where(DriveImage::class.java).sort("dateModified", Sort.DESCENDING).findAllAsync())

    var imagesAsList: List<DriveImage> = listOf()
        get() = if(images.value != null) images.value!!.toList() else listOf()
    val rvPosition = mutableLiveDataWithValue(0)

    fun loadImages(limit: Int, offset: Int, preview_crop: Boolean,
                  preview_size: String, sort: String){
        val fields = "items.resource_id,items.name,items.created,items.modified," +
                "items.file,items.preview,limit,offset"

        driveRepo.getImages(limit, offset, preview_crop, preview_size, sort)

    }
}