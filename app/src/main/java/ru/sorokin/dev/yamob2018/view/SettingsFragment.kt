package ru.sorokin.dev.yamob2018.view


import android.app.AlertDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.fragment_settings.*
import ru.sorokin.dev.yamob2018.R
import ru.sorokin.dev.yamob2018.util.*
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
            res?.firstOrNull()?.let {
                tv_username.text = it.login
                if (!it.isAvatarEmpty) {
                    GlideApp
                        .with(this)
                        .load(viewModel.getAvatarUrl(it))
                        .placeholder(RoundedBitmapDrawableFactory.create(resources, BitmapFactory.decodeResource(resources, R.drawable.person_placeholder2)).apply { isCircular = true; setAntiAlias(true) })
                        .circleCrop()
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

        viewModel.getDriveInfo(
            apiQueryCallback { isSuccessResponse, isFailure, response, error ->  }
        )
        viewModel.getAccountInfo()

        btn_get_extra_space.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_get_extra_space))))
        }

        btn_go_to_github.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_go_to_github))))
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.toolbar_settings, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if(id == R.id.action_signout){
            val confDialogBuilder = AlertDialog.Builder(activity)
            confDialogBuilder
                    .setTitle(R.string.question_want_signout)
                    .setPositiveButton(R.string.yes, { _, _ -> activity?.asMainActivity()?.authViewModel?.signout() })
                    .setNegativeButton(R.string.no, { _, _ -> })
            confDialogBuilder.create().show()

            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    override val fragmentLayoutResource = R.layout.fragment_settings

    override fun provideViewModel() = ViewModelProviders.of(this)[SettingsViewModel::class.java]

}
