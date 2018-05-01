package ru.sorokin.dev.yamob2018.viewmodel.base

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData

abstract class BaseFragmentViewModel: BaseViewModel() {
    abstract var bottomBarVisibility: MutableLiveData<Int>


}