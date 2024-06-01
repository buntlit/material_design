package com.buntlit.pictureoftheday.ui.favorite

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.SharedElementCallback
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.buntlit.pictureoftheday.R
import com.buntlit.pictureoftheday.databinding.FragmentFavoritesDetailInfoBinding
import com.buntlit.pictureoftheday.ui.view.EquilateralImageView
import com.buntlit.pictureoftheday.ui.view.OnImageReadyListener
import java.util.concurrent.TimeUnit

class FavoritesDetailInfoFragment : Fragment() {

    private var binding: FragmentFavoritesDetailInfoBinding? = null
    private val viewModel: FavoritesViewModel by lazy {
        ViewModelProvider(
            requireParentFragment(),
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )[FavoritesViewModel::class.java]
    }
    private lateinit var adapter: FavoritesDetailInfoViewPager2Adapter
    private var position = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoritesDetailInfoBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    private fun initView(){
        position = viewModel.getPosition()

        viewModel.getData().observe(viewLifecycleOwner) {
            adapter = FavoritesDetailInfoViewPager2Adapter(it, object : OnImageReadyListener {
                override fun onImageReady(position: Int) {
                    if (position == this@FavoritesDetailInfoFragment.position) {
                        startPostponedEnterTransition()
                    }
                }
            })
        }

        initViewPager()

        prepareSharedElement()
    }

    private fun initViewPager(){

        binding?.viewpagerDetail?.post {
            binding?.viewpagerDetail?.adapter = adapter
            binding?.viewpagerDetail?.offscreenPageLimit = adapter.itemCount
            binding?.viewpagerDetail?.doOnPreDraw {
                binding?.viewpagerDetail?.setCurrentItem(position, false)
            }
        }

        binding?.viewpagerDetail?.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.setPosition(position)
            }
        })
    }


    private fun prepareSharedElement() {

        sharedElementEnterTransition =
            TransitionInflater.from(requireContext())
                .inflateTransition(R.transition.image_shared_element_transition)

        postponeEnterTransition(250, TimeUnit.MILLISECONDS)

        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>,
                sharedElements: MutableMap<String, View>
            ) {
                binding?.viewpagerDetail?.findViewWithTag<ViewGroup>(position)?.apply {
                    this.findViewById<EquilateralImageView>(R.id.detail_info_photo)?.let {
                        sharedElements[names[0]] = it
                    }
                    this.findViewById<WebView>(R.id.detail_info_video)?.let {
                        sharedElements[names[0]] = it
                    }
                    this.findViewById<AppCompatTextView>(R.id.detail_info_title)?.let {
                        sharedElements[names[0]] = it
                    }
                    this.findViewById<AppCompatTextView>(R.id.detail_info_author)?.let {
                        sharedElements[names[0]] = it
                    }
                    this.findViewById<AppCompatTextView>(R.id.detail_info_description)?.let {
                        sharedElements[names[0]] = it
                    }

                }
            }
        })

    }
}