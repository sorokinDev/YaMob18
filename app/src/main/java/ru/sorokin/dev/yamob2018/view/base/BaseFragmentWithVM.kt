package ru.sorokin.dev.yamob2018.view.base

import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import android.view.View
import ru.sorokin.dev.yamob2018.util.observe
import ru.sorokin.dev.yamob2018.view.MainActivity
import ru.sorokin.dev.yamob2018.viewmodel.base.BaseFragmentViewModel

abstract class BaseFragmentWithVM<ViewModelT: BaseFragmentViewModel>: BaseFragment() {
    abstract fun provideViewModel(): ViewModelT

    abstract var bottomBarVisibility: MutableLiveData<Int>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = provideViewModel()

        if (activity is MainActivity) {
            bottomBarVisibility.observe(this){
                (activity!! as MainActivity).setBottomBarVisibility(it!!)
            }
        }

    }

    lateinit var viewModel: ViewModelT

}
