package ru.sorokin.dev.yamob2018.model.rest

import android.util.Log
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.sorokin.dev.yamob2018.DriveApp
import ru.sorokin.dev.yamob2018.model.repository.AccountRepo

abstract class BaseCallback<T> : Callback<T> {
    abstract val toastOnFailure: String? //This text will be shown in toast on failure. If null, won't show

    override fun onFailure(call: Call<T>?, t: Throwable?) {
        if(toastOnFailure != null){
            Toast.makeText(DriveApp.INSTANCE.applicationContext, toastOnFailure, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResponse(call: Call<T>?, response: Response<T>?) {
        if(response != null && response.isSuccessful){
            onSucceessResponse(call!!, response)
        }else if(response != null){
            Log.i("Response", response.code().toString())
            Log.i("Response", response.body().toString())
            Log.i("Response", response.headers().toString())
            if(response.code() == 401){
                AccountRepo().signOut()
            }
            onBadResponse(call!!, response)
        }
    }

    abstract fun onSucceessResponse(call: Call<T>, response: Response<T>)

    abstract fun onBadResponse(call: Call<T>, response: Response<T>)
}