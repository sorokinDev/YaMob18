package ru.sorokin.dev.yamob2018.model.entity

import com.google.gson.annotations.SerializedName
import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required

@RealmClass
open class AccountInfo(
        @SerializedName("login") var login: String = "",
        @SerializedName("id") var id: String = "",
        @PrimaryKey @Required var token: String = "",
        @SerializedName("is_avatar_empty") var isAvatarEmpty: Boolean = true,
        @SerializedName("default_avatar_id") var avatarId: String = ""
) : RealmModel