package ru.sorokin.dev.yamob2018.view.base

import android.os.Bundle
import ru.sorokin.dev.yamob2018.viewmodel.base.BaseViewModel

abstract class BaseActivityWithVM<ViewModelT: BaseViewModel> : BaseActivity() {
    lateinit var viewModel: ViewModelT
    abstract fun provideViewModel(): ViewModelT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = provideViewModel()
    }
}