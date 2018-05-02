package ru.sorokin.dev.yamob2018.model.entity

import com.google.gson.annotations.SerializedName
import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required

@RealmClass
open class DriveInfo(
        @PrimaryKey @Required var token: String = "",
        @SerializedName("trash_size") var trashSize: Long = 0,
        @SerializedName("total_space") var totalSpace: Long = 0,
        @SerializedName("used_space") var usedSpace: Long = 0
) : RealmModel