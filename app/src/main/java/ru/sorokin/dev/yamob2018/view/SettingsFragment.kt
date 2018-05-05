package ru.sorokin.dev.yamob2018.view


import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.fragment_settings.*
import ru.sorokin.dev.yamob2018.R
import ru.sorokin.dev.yamob2018.model.repository.AccountRepo
import ru.sorokin.dev.yamob2018.util.GlideApp
import ru.sorokin.dev.yamob2018.util.bytesToGbs
import ru.sorokin.dev.yamob2018.util.mutableLiveDataWithValue
import ru.sorokin.dev.yamob2018.util.observe
import ru.sorokin.dev.yamob2018.view.base.BaseFragmentWithVM
import ru.sorokin.dev.yamob2018.viewmodel.SettingsViewModel


class SettingsFragment : BaseFragmentWithVM<SettingsViewModel>() {
    companion object {
        fun newInstance() = SettingsFragment()
    }
    override var bottomBarVisibility = mutableLiveDataWithValue(View.VISIBLE)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setSupportActionBar(toolbar)
        (activity as MainActivity).setTitle(R.string.title_settings)
        setHasOptionsMenu(true)

        viewModel.account.observe(this) {res ->
            res?.singleOrNull{ it.token == AccountRepo.token && it.id != "" }?.let {
                tv_username.text = it.login
                if (!it.isAvatarEmpty) {
                    GlideApp
                            .with(this)
                            .load("https://avatars.yandex.net/get-yapic/${it.avatarId}/islands-200")
                            .circleCrop()
                            .placeholder(R.drawable.ic_account_circle) //TODO: Add HQ default accounts image
                            .into(iv_avatar)
                }
            }

        }

        viewModel.driveInfo.observe(this) { res ->
            res?.firstOrNull()?.let {
                tv_available_space.text = getString(R.string.available_space, bytesToGbs(it.totalSpace - it.usedSpace),
                        bytesToGbs(it.totalSpace))

                pb_available_space.progress = Math.max((it.usedSpace.toDouble() / it.totalSpace * 100).toInt(), 2)

            }
        }

        btn_get_extra_space.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_get_extra_space))))
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.toolbar_settings, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if(id == R.id.action_signout){
            //TODO: add confirmation dialog
            (activity as MainActivity).authViewModel.signout()

            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    override val fragmentLayoutResource = R.layout.fragment_settings

    override fun provideViewModel() = ViewModelProviders.of(this)[SettingsViewModel::class.java]

}
