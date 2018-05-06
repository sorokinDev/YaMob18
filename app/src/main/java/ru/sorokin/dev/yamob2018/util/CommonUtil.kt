package ru.sorokin.dev.yamob2018.util

import android.support.v4.app.FragmentActivity
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import retrofit2.Response
import ru.sorokin.dev.yamob2018.view.MainActivity


@GlideModule
class MyAppGlideModule : AppGlideModule()

const val BYTES_IN_ONE_GB = 1073741824

fun bytesToGbs(bytes: Long): Double {
    return bytes.toDouble() / BYTES_IN_ONE_GB
}

fun isNullOrEmpty(str: String?): Boolean {
    return str == null || str == ""
}

fun FragmentActivity?.asMainActivity(): MainActivity?{
    return if (this == null) null else this as MainActivity
}

fun <T> apiQueryCallback(callback: (isSuccessResponse: Boolean, isFailure: Boolean, response: Response<T>?, error: Throwable?) -> Unit): ApiQueryCallback<T> {
    return object : ApiQueryCallback<T> {
        override fun handle(isSuccessResponse: Boolean, isFailure: Boolean, response: Response<T>?, error: Throwable?) {
            callback(isSuccessResponse, isFailure, response, error)
        }

    }
}

fun <T> Response<T>?.isValid(): Boolean{
    return this != null && this.isSuccessful && this.body() != null
}