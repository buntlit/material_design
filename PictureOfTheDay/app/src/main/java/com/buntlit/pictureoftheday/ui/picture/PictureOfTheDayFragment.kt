package com.buntlit.pictureoftheday.ui.picture

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.buntlit.pictureoftheday.R
import com.buntlit.pictureoftheday.databinding.FragmentPodBinding
import com.buntlit.pictureoftheday.ui.favorite.FavoritesViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayoutMediator
import java.text.SimpleDateFormat

class PictureOfTheDayFragment : Fragment() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private var binding: FragmentPodBinding? = null
    private val viewModel: PictureOfTheDayViewModel by lazy {
        ViewModelProvider(requireActivity())[PictureOfTheDayViewModel::class.java]
    }
    private val viewModelDatabase: FavoritesViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )[FavoritesViewModel::class.java]
    }
    private lateinit var listOfTheDayData: List<PODServerResponseData?>
    private lateinit var adapter: PODViewPager2Adapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPodBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun initView(){

        binding?.bottomSheet?.bottomSheetContainer?.let { setBottomSheetBehavior(it) }
        binding?.inputLayout?.setEndIconOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data =
                    Uri.parse("https://en.wikipedia.org/wiki/${binding?.inputEditWikiResponse?.text?.toString()}")
            })
        }
        viewModel.getData().observe(viewLifecycleOwner) {
            renderData(it)
        }

        listOfTheDayData = viewModel.getListOfData()

        menuBehaviorFragment()
        viewPager2Behavior()

    }

    private fun menuBehaviorFragment() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_action_bar, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.favorites_fragment -> {
                        findNavController().navigate(R.id.action_pod_fragment_to_favorites_fragment)
                        true
                    }
                    R.id.settings_fragment -> {
                        findNavController().navigate(R.id.action_pod_fragment_to_settings_fragment)
                        true
                    }
                    else -> false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

    private fun renderData(data: PictureOfTheDayData) {
        when (data) {
            is PictureOfTheDayData.Success -> {
                viewModelDatabase.isItemLiked(data.serverResponseData)
                renderDataSuccess(data.serverResponseData)

                viewModelDatabase.getIsItemLikedData().observe(viewLifecycleOwner) {
                    binding?.viewPager?.currentItem?.let { position ->
                        adapter.updateLists(it != null, position)
                    }
                }
            }
            is PictureOfTheDayData.Error -> {
                toast(data.error.message)
            }
            is PictureOfTheDayData.Loading -> {

            }
            else -> {}
        }
    }

    private fun renderDataSuccess(serverResponseData: PODServerResponseData) {
        val url = serverResponseData.url
        val hdUrl = serverResponseData.hdUrl
        val title = serverResponseData.title
        val description = serverResponseData.explanation

        if (url.isNullOrEmpty() && hdUrl.isNullOrEmpty()) {
            toast("Link is empty")
            bottomSheetBehavior.isHideable = true
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            binding?.bottomSheet?.bottomSheetDescriptionHeader?.text = title
            binding?.bottomSheet?.bottomSheetDescription?.text = description
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            bottomSheetBehavior.isHideable = false
        }
    }

    private fun Fragment.toast(string: String?) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
    }

    private fun setBottomSheetBehavior(bottomSheet: ConstraintLayout) {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding?.bottomSheet?.bottomSheetSlider?.visibility = View.VISIBLE
                        binding?.bottomSheet?.bottomSheetDescription?.visibility = View.GONE
                        binding?.bottomSheet?.bottomSheetDescriptionHeader?.visibility = View.GONE
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        binding?.bottomSheet?.bottomSheetSlider?.visibility = View.GONE
                        binding?.bottomSheet?.bottomSheetDescription?.visibility = View.VISIBLE
                        binding?.bottomSheet?.bottomSheetDescriptionHeader?.visibility =
                            View.VISIBLE
                    }
                    else -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }


        })
    }

    private fun viewPager2Behavior() {
        adapter = PODViewPager2Adapter(listOfTheDayData) { data, isSelected ->
            if (isSelected) viewModelDatabase.insertToFavorite(data)
            else viewModelDatabase.deleteFromFavorite(data)
        }
        binding?.viewPager?.offscreenPageLimit = listOfTheDayData.size
        binding?.viewPager?.adapter = adapter
        binding?.tabLayout?.let { tabLayout ->
            binding?.viewPager?.let { viewPager2 ->
                TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
                    when (position) {
                        0 -> tab.text = "today"
                        1 -> tab.text = "yesterday"
                        else -> tab.text = "$position days before"
                    }
                }.attach()
            }
        }
        binding?.viewPager?.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (listOfTheDayData[position] == null) {
                    viewModel.updateData(getDate(position), position)
                } else {
                    listOfTheDayData[position]?.let {
                        renderDataSuccess(it)
                    }
                }
                super.onPageSelected(position)
            }
        }
        )
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDate(daysBefore: Int): String {
        val date = Calendar.getInstance()
        date.add(Calendar.DATE, -1 * daysBefore)
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(date.time)
    }
}