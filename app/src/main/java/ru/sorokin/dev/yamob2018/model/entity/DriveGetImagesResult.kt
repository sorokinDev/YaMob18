package ru.sorokin.dev.yamob2018.model.entity

import com.google.gson.annotations.SerializedName

class DriveGetImagesResult (
    @SerializedName("items") var items: ArrayList<DriveImage> = ArrayList(),
    @SerializedName("limit") var limit: Int = 0,
    @SerializedName("offset") var offset: Int = 0
)