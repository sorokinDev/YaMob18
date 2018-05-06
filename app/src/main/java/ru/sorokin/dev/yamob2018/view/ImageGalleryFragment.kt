package ru.sorokin.dev.yamob2018.view


import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import kotlinx.android.synthetic.main.fragment_image_gallery.*
import ru.sorokin.dev.yamob2018.DriveApp
import ru.sorokin.dev.yamob2018.R
import ru.sorokin.dev.yamob2018.model.entity.DriveImage
import ru.sorokin.dev.yamob2018.model.repository.AccountRepo
import ru.sorokin.dev.yamob2018.util.*
import ru.sorokin.dev.yamob2018.view.base.BaseFragmentWithVM
import ru.sorokin.dev.yamob2018.viewmodel.ImageGalleryViewModel




class ImageGalleryFragment : BaseFragmentWithVM<ImageGalleryViewModel>() {
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(::rvLayoutManager.isInitialized){
            rvLayoutManager.onSaveInstanceState()
        }
    }

    override var bottomBarVisibility = mutableLiveDataWithValue(View.VISIBLE)

    companion object {
        fun newInstance() = ImageGalleryFragment()
    }

    override fun provideViewModel(): ImageGalleryViewModel = ViewModelProviders.of(this.activity!!)[ImageGalleryViewModel::class.java]
    override val fragmentLayoutResource: Int = R.layout.fragment_image_gallery
    lateinit var rvLayoutManager: GridLayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity.asMainActivity()?.setSupportActionBar(toolbar)
        activity?.setTitle(R.string.title_all_photos)

        rv_images.setHasFixedSize(true)
        rvLayoutManager = GridLayoutManager(context, if(activity!!.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 3 else 5)
        rv_images.layoutManager = rvLayoutManager

        val adapter = ImageGalleryAdapter(DriveApp.INSTANCE.applicationContext, viewModel.imagesAsList)
        rv_images.adapter = adapter
        val rvScrollListener = object : EndlessRecyclerViewScrollListener(rvLayoutManager) {
            override fun isLoading(): Boolean = viewModel.loading.value!!

            override fun loadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                viewModel.loadImages(50, totalItemsCount, true, "S", "-modified",
                        apiQueryCallback { isSuccessResponse, isFailure, response, error ->
                            afterLoadMore()
                            if (response.isValid()){
                                loadedLastTime = response!!.body()!!.items.count()
                            }
                        })

            }
        }

        rv_images.addOnScrollListener(rvScrollListener)

        viewModel.images.observe(this) {
            it?.let {
                if (it.isValid) {
                    Log.i("Gallery", "In observe")

                    adapter.images = viewModel.imagesAsList
                    adapter.notifyDataSetChanged()
                }
            }
        }

        swipe_refresh_layout.setColorSchemeResources(R.color.colorYandexYellow)
        swipe_refresh_layout.setOnRefreshListener {

            viewModel.loadFirst(50, 0, true, "S", "-modified",
                    apiQueryCallback { isSuccessResponse, isFailure, response, error ->
                        swipe_refresh_layout.isRefreshing = false

                    })

        }

        if(!viewModel.loadedFirstPage){
            viewModel.loadFirst(50, 0, true, "S", "-modified",
                    apiQueryCallback { isSuccessResponse, isFailure, response, error ->

                    })
        }

        viewModel.rvPosition.observe(this){
            Log.i("rvPos", it!!.toString())
            if(it != -1){
                val viewAtPosition = rvLayoutManager.findViewByPosition(it)
                if (viewAtPosition == null || !rvLayoutManager.isViewPartiallyVisible(viewAtPosition, false, true)) {
                    rv_images.post({ rvLayoutManager.scrollToPosition(it) })
                    viewModel.rvPosition.value = -1
                    Log.i("Gallery", "scrolled")
                }
            }
        }

        activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        if(savedInstanceState != null){
            rvLayoutManager.onRestoreInstanceState(savedInstanceState)
        }

    }

    private inner class ImageGalleryAdapter(
            private val mContext: Context,
            var images: List<DriveImage>)
        : RecyclerView.Adapter<ImageGalleryAdapter.MyViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageGalleryAdapter.MyViewHolder {

            val context = parent.context
            val inflater = LayoutInflater.from(context)
            val imageView = inflater.inflate(R.layout.item_image, parent, false)
            return MyViewHolder(imageView)
        }

        override fun onBindViewHolder(holder: ImageGalleryAdapter.MyViewHolder, position: Int) {
            val image = images[position]
            val imageView = holder.imageView

            GlideApp.with(mContext)
                    .load(GlideUrl(image.preview, LazyHeaders.Builder().addHeader("Authorization", "OAuth ${AccountRepo.token}").build()))
                    .placeholder(R.drawable.ic_home_black_24dp) // TODO: find nice placeholder
                    .centerCrop()
                    .into(imageView)
        }

        override fun getItemCount(): Int {
            return images.size
        }

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
            var imageView: ImageView = itemView.findViewById(R.id.iv_image)

            init {
                itemView.setOnClickListener(this)
            }

            override fun onClick(view: View) {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    viewModel.currentPosition.value = position
                    val img = images[position]

                    Log.i("GALLERY", "CLICKED: ${img.resourceId}")


                    activity.asMainActivity()!!.fragNavController.pushFragment(
                            ImageCarouselFragment.newInstance())

                }
            }
        }
    }
}
