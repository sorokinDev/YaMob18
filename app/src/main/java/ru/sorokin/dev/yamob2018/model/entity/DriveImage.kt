package ru.sorokin.dev.yamob2018.model.entity

import com.google.gson.annotations.SerializedName
import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import java.util.*

@RealmClass
open class DriveImage(
        @PrimaryKey @Required @SerializedName("resource_id") var resourceId : String = "",
        @SerializedName("name") var name:String = "",
        @SerializedName("created") var dateCreated: Date = Date(),
        @SerializedName("modified") var dateModified: Date = Date(),
        @SerializedName("file") var file: String = "",
        @SerializedName("preview") var preview: String = ""
) : RealmModel