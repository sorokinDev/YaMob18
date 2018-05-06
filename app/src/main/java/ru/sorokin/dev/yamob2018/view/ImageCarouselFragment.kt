package ru.sorokin.dev.yamob2018.view


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import kotlinx.android.synthetic.main.fragment_image_carousel.*
import ru.sorokin.dev.yamob2018.R
import ru.sorokin.dev.yamob2018.util.mutableLiveDataWithValue
import ru.sorokin.dev.yamob2018.view.base.BaseFragmentWithVM
import ru.sorokin.dev.yamob2018.viewmodel.ImageGalleryViewModel



class ImageCarouselFragment : BaseFragmentWithVM<ImageGalleryViewModel>() {
    companion object {
        fun newInstance() = ImageCarouselFragment()
    }

    override var bottomBarVisibility = mutableLiveDataWithValue(View.GONE)

    override fun provideViewModel(): ImageGalleryViewModel {
        return ViewModelProviders.of(activity!!)[ImageGalleryViewModel::class.java]
    }

    override val fragmentLayoutResource: Int
        get() = R.layout.fragment_image_carousel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vp_images.adapter = ImagePagerAdapter(this)
        vp_images.currentItem = viewModel.currentPosition.value!!
        vp_images.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                viewModel.rvPosition.value = position
                viewModel.currentPosition.value = position
            }

        })
    }

    inner class ImagePagerAdapter(var fragment: ImageCarouselFragment)
        : FragmentStatePagerAdapter(fragment.getChildFragmentManager()) {

        override fun getCount(): Int {
            return fragment.viewModel.imagesAsList.count()
        }


        override fun getItem(position: Int): Fragment {
            return OneImageFragment.newInstance(position)
        }

    }

}
