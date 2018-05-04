package ru.sorokin.dev.yamob2018.model.rest

import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.sorokin.dev.yamob2018.DriveApp

abstract class BaseCallback<T> : Callback<T> {
    abstract val toastOnFailure: String? //This text will be shown in toast on failure. If null, won't show
    abstract val snackOnFailure: String?//This text will be shown in snackbar on failure. If null, won't show

    override fun onFailure(call: Call<T>?, t: Throwable?) {
        if(snackOnFailure != null){

        }else if(toastOnFailure != null){
            Toast.makeText(DriveApp.INSTANCE.applicationContext, toastOnFailure, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResponse(call: Call<T>?, response: Response<T>?) {
        if(response != null && response.isSuccessful){
            onSucceessResponse(call!!, response)
        }else if(response != null){
            onBadResponse(call!!, response)
        }
    }

    abstract fun onSucceessResponse(call: Call<T>, response: Response<T>)

    abstract fun onBadResponse(call: Call<T>, response: Response<T>)
}