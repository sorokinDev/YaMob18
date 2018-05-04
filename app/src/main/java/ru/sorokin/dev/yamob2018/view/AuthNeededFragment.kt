package ru.sorokin.dev.yamob2018.view


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_auth_needed.*
import ru.sorokin.dev.yamob2018.R
import ru.sorokin.dev.yamob2018.Screens
import ru.sorokin.dev.yamob2018.util.observe
import ru.sorokin.dev.yamob2018.view.base.BaseFragmentWithVM
import ru.sorokin.dev.yamob2018.viewmodel.AuthNeededViewModel
import ru.sorokin.dev.yamob2018.viewmodel.AuthViewModel

class AuthNeededFragment : BaseFragmentWithVM<AuthNeededViewModel>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).setSupportActionBar(toolbar)
        (activity as MainActivity).setTitle(R.string.title_auth_failure)

        btn_login.setOnClickListener {
            (activity as MainActivity).viewModel.navTo(Screens.AUTH)
        }

        viewModel.currentState.observe(this){
            it?.let {
                view_no_auth.visibility = if(it == AuthNeededViewModel.STATE_NO_AUTH) View.VISIBLE else View.GONE
                view_token_only.visibility = if(it == AuthNeededViewModel.STATE_TOKEN_ONLY) View.VISIBLE else View.GONE
                view_cant_load_info.visibility = if(it == AuthNeededViewModel.STATE_CANT_LOAD_DATA) View.VISIBLE else View.GONE
            }
        }

        (activity as MainActivity).authViewModel.authState.observe(this){
            it?.let {
                when(it){
                    AuthViewModel.NO_AUTH -> viewModel.currentState.value = AuthNeededViewModel.STATE_NO_AUTH
                    AuthViewModel.TOKEN_ONLY -> viewModel.currentState.value = AuthNeededViewModel.STATE_TOKEN_ONLY
                    AuthViewModel.COMPLETE_AUTH -> viewModel.currentState.value = AuthNeededViewModel.STATE_NO_AUTH
                }
            }
        }

    }

    override fun provideViewModel() = ViewModelProviders.of(this)[AuthNeededViewModel::class.java]

    override val fragmentLayoutResource: Int = R.layout.fragment_auth_needed

    companion object {
        fun newInstance() = AuthNeededFragment()
    }
}
