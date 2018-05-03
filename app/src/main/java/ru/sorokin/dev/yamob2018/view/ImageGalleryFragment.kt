package ru.sorokin.dev.yamob2018.view


import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager

import android.view.View
import kotlinx.android.synthetic.main.fragment_image_gallery.*

import ru.sorokin.dev.yamob2018.R
import ru.sorokin.dev.yamob2018.view.base.BaseFragmentWithVM
import ru.sorokin.dev.yamob2018.viewmodel.ImageGalleryViewModel
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import ru.sorokin.dev.yamob2018.model.entity.DriveImage
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import ru.sorokin.dev.yamob2018.DriveApp
import ru.sorokin.dev.yamob2018.model.repository.AccountRepo
import ru.sorokin.dev.yamob2018.util.GlideApp
import ru.sorokin.dev.yamob2018.util.observe


class ImageGalleryFragment : BaseFragmentWithVM<ImageGalleryViewModel>() {
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("RVPOS", rvLayoutManager.findFirstVisibleItemPosition())

    }


    companion object {
        fun newInstance() = ImageGalleryFragment()
    }

    override fun provideViewModel(): ImageGalleryViewModel = ViewModelProviders.of(this)[ImageGalleryViewModel::class.java]

    override val fragmentLayoutResource: Int = R.layout.fragment_image_gallery

    lateinit var rvLayoutManager: GridLayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setSupportActionBar(toolbar)
        (activity as MainActivity).setTitle(R.string.title_all_photos)


        rv_images.setHasFixedSize(true)
        rvLayoutManager = GridLayoutManager(context, Math.max(3, rv_images.width    / 100))
        rvLayoutManager.orientation = LinearLayout.VERTICAL
        rv_images.layoutManager = rvLayoutManager

        val adapter = ImageGalleryAdapter(DriveApp.INSTANCE.applicationContext, viewModel.imagesAsList)
        rv_images.adapter = adapter

        viewModel.images.observe(this) {
            it?.let {
                if (it.isValid) {
                    Log.i("Gallery", "In observe")
                    adapter.images = viewModel.imagesAsList
                    adapter.notifyDataSetChanged()
                }
            }
        }


        if(savedInstanceState == null){
            viewModel.loadImages(20, 0, true, "S", "-modified")
        }

        if(savedInstanceState != null){
            rvLayoutManager.scrollToPosition(savedInstanceState.getInt("RVPOS"))
            Log.i("RVPOS", savedInstanceState.getInt("RVPOS").toString())

        }



    }


    private inner class ImageGalleryAdapter(
            private val mContext: Context,
            public var images: List<DriveImage>)
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

                    Log.i("GALLERY", "CLICKED: ${img.resourceId}")
                    //val intent = Intent(mContext, SpacePhotoActivity::class.java)
                    //intent.putExtra(SpacePhotoActivity.EXTRA_SPACE_PHOTO, img)
                    //startActivity(intent)
                }
            }
        }
    }





}
