package ru.sorokin.dev.yamob2018.view


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import kotlinx.android.synthetic.main.fragment_auth_failure.*

import ru.sorokin.dev.yamob2018.R
import ru.sorokin.dev.yamob2018.view.base.BaseFragmentWithVM
import ru.sorokin.dev.yamob2018.viewmodel.AuthFailureViewModel

class AuthFailureFragment : BaseFragmentWithVM<AuthFailureViewModel>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).setSupportActionBar(toolbar)
        (activity as MainActivity).title = "Login"

        btn_login.setOnClickListener {
            (activity as MainActivity).viewModel.navToAuth()
        }
    }

    override fun provideViewModel() = ViewModelProviders.of(this)[AuthFailureViewModel::class.java]

    override val fragmentLayoutResource: Int = R.layout.fragment_auth_failure

    companion object {
        fun newInstance() = AuthFailureFragment()
    }
}
