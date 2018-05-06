package ru.sorokin.dev.yamob2018.model.entity

import com.google.gson.annotations.SerializedName
import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required

@RealmClass
open class OfflineImage(
        @PrimaryKey @Required @SerializedName("resource_id") var resourceId : String = ""

        ): RealmModel