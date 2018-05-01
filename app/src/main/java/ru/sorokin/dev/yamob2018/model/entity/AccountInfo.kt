package ru.sorokin.dev.yamob2018.model.entity

import com.google.gson.annotations.SerializedName
import io.realm.RealmModel
import io.realm.annotations.RealmClass

@RealmClass
open class AccountInfo(
        @SerializedName("login") var login: String = "",
        @SerializedName("id") var id: String = "",
        var token: String = "",
        @SerializedName("is_avatar_empty") var isAvatarEmpty: Boolean = true,
        @SerializedName("default_avatar_id") var avatarId: String = "")
    : RealmModel{

}