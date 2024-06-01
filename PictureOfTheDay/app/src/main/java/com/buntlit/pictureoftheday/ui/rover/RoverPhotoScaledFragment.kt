package com.buntlit.pictureoftheday.ui.rover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import com.buntlit.pictureoftheday.R
import com.buntlit.pictureoftheday.databinding.FragmentScaledImagesBinding
import com.buntlit.pictureoftheday.ui.view.EquilateralImageView
import com.buntlit.pictureoftheday.ui.view.OnImageReadyListener
import java.util.concurrent.TimeUnit

class RoverPhotoScaledFragment : Fragment() {

    private var binding: FragmentScaledImagesBinding? = null
    private lateinit var adapter: PhotoScaledViewPager2Adapter
    private var position = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScaledImagesBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView(){

        initRecyclerView()

        prepareSharedElement()
    }

    private fun initRecyclerView(){
        arguments?.let {
            position = it.getInt("POSITION")
            adapter = PhotoScaledViewPager2Adapter(it.getStringArrayList("LIST")?.toList()!!,
                object : OnImageReadyListener {
                    override fun onImageReady(position: Int) {
                        if (position == this@RoverPhotoScaledFragment.position) {
                            startPostponedEnterTransition()
                        }
                    }
                })
        }

        binding?.viewPagerScaledImages?.adapter = adapter

        binding?.viewPagerScaledImages?.doOnPreDraw {
            binding?.viewPagerScaledImages?.setCurrentItem(position, false)
        }
    }


    private fun prepareSharedElement() {
        sharedElementReturnTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.image_shared_element_transition)
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.image_shared_element_transition)

        postponeEnterTransition(250, TimeUnit.MILLISECONDS)

        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>,
                sharedElements: MutableMap<String, View>
            ) {
                binding?.viewPagerScaledImages?.findViewWithTag<ViewGroup>(position)
                    ?.findViewById<EquilateralImageView>(R.id.photo_scaled_image_view)?.let {
                      sharedElements.put(names[0], it)
                    }
            }
        })
    }
}