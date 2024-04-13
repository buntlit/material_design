package com.buntlit.pictureoftheday.ui.picture

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.buntlit.pictureoftheday.MainActivity
import com.buntlit.pictureoftheday.R
import com.buntlit.pictureoftheday.databinding.FragmentPodBinding
import com.buntlit.pictureoftheday.ui.clip.ChipsFragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.text.SimpleDateFormat

class PictureOfTheDayFragment : Fragment() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private var binding: FragmentPodBinding? = null
    private val viewModel: PictureOfTheDayViewModel by lazy {
        ViewModelProvider(this)[PictureOfTheDayViewModel::class.java]
    }

    companion object {
        fun newInstance() = PictureOfTheDayFragment()
        private var isMain = true
    }

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
        binding?.bottomSheet?.bottomSheetContainer?.let { setBottomSheetBehavior(it) }
        binding?.inputLayout?.setEndIconOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data =
                    Uri.parse("https://en.wikipedia.org/wiki/${binding?.inputEditText?.text?.toString()}")
            })
        }
        viewModel.getData().observe(viewLifecycleOwner) {
            renderData(it)
        }
        setBottomAppBar()
        chipDaysBehavior()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_bottom_bar, menu)
    }

    @SuppressLint("CommitTransaction")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_fav -> toast("show favorite")
            R.id.app_bar_settings -> activity?.supportFragmentManager?.beginTransaction()
                ?.add(R.id.container, ChipsFragment())?.addToBackStack(null)?.commit()

            android.R.id.home ->
                activity?.let {
                    BottomNavigationDrawerFragment().show(it.supportFragmentManager, "tag")
                }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun renderData(data: PictureOfTheDayData) {
        when (data) {
            is PictureOfTheDayData.Success -> {
                val serverResponseData = data.serverResponseData
                val url = serverResponseData.url
                val hdUrl = serverResponseData.hdurl
                val title = serverResponseData.title
                val description = serverResponseData.explanation

                if (url.isNullOrEmpty() && hdUrl.isNullOrEmpty()) {
                    toast("Link is empty")
                } else {
                    binding?.hdChip?.isChecked = false
                    loadImage(url!!)
                    chipHDBehavior(url, hdUrl!!)
                    binding?.bottomSheet?.bottomSheetDescriptionHeader?.text = title
                    binding?.bottomSheet?.bottomSheetDescription?.text = description
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
            is PictureOfTheDayData.Error -> {
                toast(data.error.message)
            }
            is PictureOfTheDayData.Loading -> {

            }
        }
    }

    private fun loadImage(url: String) {
        binding?.imageView?.load(url) {
            lifecycle(viewLifecycleOwner)
            error(R.drawable.ic_load_error_vector)
            placeholder(R.drawable.ic_no_photo_vector)
            crossfade(true)
        }
        binding?.hdChip?.visibility = View.VISIBLE
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
                        binding?.bottomSheet?.bottomSheetDescriptionHeader?.visibility = View.VISIBLE
                    }
//                    BottomSheetBehavior.STATE_EXPANDED -> toast("state expanded")
//                    BottomSheetBehavior.STATE_HALF_EXPANDED -> toast("state half expended")
//                    BottomSheetBehavior.STATE_HIDDEN -> toast("state hidden")
//                    BottomSheetBehavior.STATE_SETTLING -> toast("state settling")
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                toast("on slide")
            }

        })
    }

    private fun chipHDBehavior(url: String, hdUrl: String) {
        binding?.hdChip?.setOnCheckedChangeListener { buttonView, isChecked ->
            buttonView.visibility = View.GONE
            if (isChecked) {
                loadImage(hdUrl)
            } else {
                loadImage(url)
            }
        }
    }

    private fun chipDaysBehavior(){
        binding?.chipGroupDays?.setOnCheckedStateChangeListener { group, checkedIds ->
            var date = ""
            when(group.checkedChipId){
                R.id.chip_today -> date = ""
                R.id.chip_yesterday -> date = getDate()
            }
            viewModel.updateData(date)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDate(): String{
        val date = Calendar.getInstance()
        date.add(Calendar.DATE, -1)
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(date.time)
    }

    private fun setBottomAppBar() {
        val context = activity as MainActivity
        context.setSupportActionBar(binding?.bottomAppBar)
        setHasOptionsMenu(true)

        binding?.fab?.setOnClickListener {
            if (isMain) {
                fabChange(
                    null,
                    BottomAppBar.FAB_ALIGNMENT_MODE_END,
                    R.drawable.ic_back_fab,
                    R.menu.menu_bottom_bar_other_screen
                )

            } else {
                fabChange(
                    ContextCompat.getDrawable(context, R.drawable.ic_hamburger_menu_bottom_bar),
                    BottomAppBar.FAB_ALIGNMENT_MODE_CENTER,
                    R.drawable.ic_plus_fab,
                    R.menu.menu_bottom_bar
                )
            }

            isMain = !isMain
        }
    }

    private fun fabChange(
        navigationIcon: Drawable?,
        fabAlignmentMode: Int,
        imageFabId: Int,
        menuId: Int
    ) {
        binding?.bottomAppBar?.navigationIcon = navigationIcon
        binding?.bottomAppBar?.fabAlignmentMode = fabAlignmentMode
        binding?.fab?.setImageDrawable(
            context?.let { ContextCompat.getDrawable(it, imageFabId) })
        binding?.bottomAppBar?.replaceMenu(menuId)
    }
}