package ru.sorokin.dev.yamob2018.view

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import kotlinx.android.synthetic.main.fragment_one_image.*
import ru.sorokin.dev.yamob2018.R
import ru.sorokin.dev.yamob2018.model.repository.AccountRepo
import ru.sorokin.dev.yamob2018.util.GlideApp
import ru.sorokin.dev.yamob2018.util.asMainActivity
import ru.sorokin.dev.yamob2018.util.mutableLiveDataWithValue
import ru.sorokin.dev.yamob2018.view.base.BaseFragmentWithVM
import ru.sorokin.dev.yamob2018.viewmodel.ImageGalleryViewModel
import ru.sorokin.dev.yamob2018.viewmodel.base.BaseFragmentViewModel


class OneImageFragment : BaseFragmentWithVM<ImageGalleryViewModel>() {

    lateinit var oneImageViewModel: OneImageViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        oneImageViewModel = ViewModelProviders.of(this)[OneImageViewModel::class.java]

        if(arguments != null){
            oneImageViewModel.pos.value = arguments!!.getInt("POS")
        }

        val url = GlideUrl(viewModel.imagesAsList[oneImageViewModel.pos.value!!].file, LazyHeaders.Builder().addHeader("Authorization", "OAuth ${AccountRepo.token}").build())
        GlideApp.with(this.context!!)
                .load(url) //TODO: refactor
                .placeholder(R.drawable.ic_home_black_24dp) // TODO: find nice placeholder
                .fitCenter()
                .into(img)
    }

    override var bottomBarVisibility = mutableLiveDataWithValue(View.GONE)

    override fun provideViewModel(): ImageGalleryViewModel {
        return ViewModelProviders.of(activity.asMainActivity()!!)[ImageGalleryViewModel::class.java]
    }

    override val fragmentLayoutResource: Int
        get() = R.layout.fragment_one_image


    companion object {
        fun newInstance(position: Int) = OneImageFragment().apply { arguments = Bundle().apply { putInt("POS", position) } }
    }

    class OneImageViewModel: BaseFragmentViewModel() {
        val pos = mutableLiveDataWithValue(0)
    }
}
