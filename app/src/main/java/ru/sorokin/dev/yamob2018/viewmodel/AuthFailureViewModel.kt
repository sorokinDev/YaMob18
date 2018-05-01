package ru.sorokin.dev.yamob2018.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.view.View
import ru.sorokin.dev.yamob2018.util.mutableLiveDataWithValue
import ru.sorokin.dev.yamob2018.viewmodel.base.BaseFragmentViewModel

class AuthFailureViewModel: BaseFragmentViewModel() {
    override var bottomBarVisibility = mutableLiveDataWithValue(View.GONE)
}