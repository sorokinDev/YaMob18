package ru.sorokin.dev.yamob2018.view


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_account.*

import ru.sorokin.dev.yamob2018.R
import ru.sorokin.dev.yamob2018.util.GlideApp
import ru.sorokin.dev.yamob2018.util.observe
import ru.sorokin.dev.yamob2018.view.base.BaseFragmentWithVM
import ru.sorokin.dev.yamob2018.viewmodel.AccountViewModel

class AccountFragment : BaseFragmentWithVM<AccountViewModel>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setSupportActionBar(toolbar)
        (activity as MainActivity).title = "Account"

        viewModel.accounts.observe(this) {
            val acc = it!!.firstOrNull()
            if(acc != null){
                tv_username.text = acc.login
                if(!acc.isAvatarEmpty){
                    GlideApp
                            .with(this)
                            .load("https://avatars.yandex.net/get-yapic/${acc.avatarId}/islands-200")
                            .circleCrop()
                            .placeholder(R.drawable.ic_account_circle) //TODO: Add HQ default account image
                            .into(iv_avatar)
                }
            }
        }

        viewModel.getCurrentUser()
    }

    override val fragmentLayoutResource = R.layout.fragment_account

    override fun provideViewModel() = ViewModelProviders.of(this)[AccountViewModel::class.java]

    companion object {
        fun newInstance() = AccountFragment()
    }


}
