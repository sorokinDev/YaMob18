package ru.sorokin.dev.yamob2018.view


import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override var bottomBarVisibility = mutableLiveDataWithValue(View.VISIBLE)
    var rvPos: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(savedInstanceState != null){
            rvPos = savedInstanceState.getInt("RVPOS")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(::rvLayoutManager.isInitialized){
            outState.putInt("RVPOS", rvLayoutManager.findFirstVisibleItemPosition())
        }else{
            outState.putInt("RVPOS", rvPos)
        }

    }


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
        rvLayoutManager = GridLayoutManager(context, 3)
        rv_images.layoutManager = rvLayoutManager

        val adapter = ImageGalleryAdapter(DriveApp.INSTANCE.applicationContext, viewModel.imagesAsList)
        rv_images.adapter = adapter
        val rvScrollListener = object : EndlessRecyclerViewScrollListener(rvLayoutManager) {
            override fun loadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                //Log.i("onLoad", "${page} ${totalItemsCount}")
                //if(!viewModel.loadedFirstPage){ return }
                viewModel.loadImages(100, totalItemsCount, true, "S", "-modified", { afterLoadMore() }, {  }) //TODO: implement noConnection callback

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
            rvScrollListener.loading.value = true
            viewModel.loadFirst(100, 0, true, "S", "-modified", { swipe_refresh_layout.isRefreshing = false; rvScrollListener.loading.value = false }, { swipe_refresh_layout.isRefreshing = false; rvScrollListener.loading.value = true }) //TODO: implement noConnection callback

        }

        if(!viewModel.loadedFirstPage){
            rvScrollListener.loading.value = true
            viewModel.loadFirst(100, 0, true, "S", "-modified", { rvScrollListener.loading.value = false }, { rvScrollListener.loading.value = false }) //TODO: implement noConnection callback
        }

        if(savedInstanceState != null){
            //rvLayoutManager.scrollToPosition(savedInstanceState.getInt("RVPOS"))
            Log.i("RVPOS", savedInstanceState.getInt("RVPOS").toString())
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

            //Log.i("Preview", image.preview)
            val url = GlideUrl(image.preview, LazyHeaders.Builder().addHeader("Authorization", "OAuth ${AccountRepo.token}").build())
            GlideApp.with(mContext)
                    .load(url) //TODO: refactor
                    .placeholder(R.drawable.ic_home_black_24dp) // TODO: find nice placeholder
                    .centerCrop()
                    .into(imageView)
        }

        override fun getItemCount(): Int {
            //Log.i("Gallery", images.size.toString())
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
                    val img = images[position]

                    viewModel.currentPosition = position

                    Log.i("GALLERY", "CLICKED: ${img.resourceId}")

                    val iv = view.findViewById<ImageView>(R.id.iv_image)

                    activity.asMainActivity()!!.fragNavController.pushFragment(
                            ImageCarouselFragment.newInstance())

                    //val intent = Intent(mContext, SpacePhotoActivity::class.java)
                    //intent.putExtra(SpacePhotoActivity.EXTRA_SPACE_PHOTO, img)
                    //startActivity(intent)
                }
            }
        }
    }





}
