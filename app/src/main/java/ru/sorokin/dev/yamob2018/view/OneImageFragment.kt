package ru.sorokin.dev.yamob2018.view

import android.arch.lifecycle.ViewModelProviders
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.fragment_one_image.*
import ru.sorokin.dev.yamob2018.R
import ru.sorokin.dev.yamob2018.model.repository.AccountRepo
import ru.sorokin.dev.yamob2018.util.GlideApp
import ru.sorokin.dev.yamob2018.util.asMainActivity
import ru.sorokin.dev.yamob2018.util.buildGlideUrl
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
            oneImageViewModel.pos.value = arguments!!.getInt(OneImageFragment.ARG_POS)
        }

        loadImage()

        btn_retry.setOnClickListener {
            layout_on_failure.visibility = View.GONE
            img.visibility = View.VISIBLE
            loadImage()
        }

        img.setOnClickListener {
            fullScreen()
        }
    }

    fun loadImage(){
        GlideApp.with(this.activity!!)
                .load(buildGlideUrl(viewModel.imagesAsList[oneImageViewModel.pos.value!!].file,
                        AccountRepo.token))
                .placeholder(R.drawable.picture_placeholder2)
                .fitCenter()
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        layout_on_failure.visibility = View.VISIBLE
                        img.visibility = View.GONE
                        return true
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        return false
                    }
                })
                .into(img)
    }

    fun fullScreen() {
        /*activity?.let {
            if(it.asMainActivity()!!.supportActionBar!!.isShowing){
                it.asMainActivity()!!.supportActionBar!!.hide()
            }else{
                it.asMainActivity()!!.supportActionBar!!.show()
            }
        }*/

    }

    override var bottomBarVisibility = mutableLiveDataWithValue(View.GONE)

    override fun provideViewModel(): ImageGalleryViewModel {
        return ViewModelProviders.of(activity.asMainActivity()!!)[ImageGalleryViewModel::class.java]
    }

    override val fragmentLayoutResource: Int
        get() = R.layout.fragment_one_image


    companion object {
        const val ARG_POS = "pos"
        fun newInstance(position: Int) = OneImageFragment().apply { arguments = Bundle().apply { putInt(ARG_POS, position) } }
    }

    class OneImageViewModel: BaseFragmentViewModel() {
        val pos = mutableLiveDataWithValue(0)
    }
}
