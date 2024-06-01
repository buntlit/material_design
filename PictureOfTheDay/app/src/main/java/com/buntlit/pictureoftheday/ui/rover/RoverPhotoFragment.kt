package com.buntlit.pictureoftheday.ui.rover

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.transition.*
import com.buntlit.pictureoftheday.R
import com.buntlit.pictureoftheday.databinding.FragmentRoverPhotoBinding
import com.buntlit.pictureoftheday.ui.view.DateMask
import com.buntlit.pictureoftheday.ui.view.EquilateralImageView
import java.text.SimpleDateFormat
import java.util.*

class RoverPhotoFragment : Fragment() {
    private var binding: FragmentRoverPhotoBinding? = null
    private lateinit var adapter: RoverPhotoRecyclerViewAdapter
    private val viewModel: RoversViewModel by lazy {
        ViewModelProvider(requireActivity())[RoversViewModel::class.java]
    }
    private var isCalendarVisible = false
    private var savedAdapterPosition = 0
    private val stateViewModel: SaveStateViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoverPhotoBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

    }

    override fun onDestroyView() {
        stateViewModel.saveStateRecycler(adapter.getListOfItemIsOpen())
        stateViewModel.saveStateFragment(false)
        stateViewModel.saveStateCalendar(isCalendarVisible)
        binding = null
        super.onDestroyView()
    }

    private fun initView(){

        initRecyclerViewAndViewStates()

        initViewVisibility()

        viewModel.getRoversData().observe(viewLifecycleOwner) {
            renderData(it)
        }
        viewModel.getPhotosData().observe(viewLifecycleOwner) {
            adapter.updateList(it, stateViewModel.getRecyclerIsOpens())
        }
    }

    private fun initRecyclerViewAndViewStates(){

        var isFirstTimeOpen = true
        var savedPhotoAdapterPosition = 0

        if (stateViewModel.getFragmentIsFirstTime() != null) {
            isFirstTimeOpen = stateViewModel.getFragmentIsFirstTime()!!
            isCalendarVisible = stateViewModel.getCalendarIsOpen()!!
            if (stateViewModel.getAdapterPosition() != null) {
                savedAdapterPosition = stateViewModel.getAdapterPosition()!!
                savedPhotoAdapterPosition = stateViewModel.getPhotosAdapterPosition()!!
            }
        }

        adapter =
            RoverPhotoRecyclerViewAdapter(
                isFirstTimeOpen,
                savedAdapterPosition,
                savedPhotoAdapterPosition
            ) { list, photoPosition, adapterPosition, imageView, holder ->
                prepareTransitions(holder)
                findNavController().navigate(
                    RoverPhotoFragmentDirections.actionRoverPhotoFragmentToRoverPhotoScaledFragment()
                        .apply {
                            arguments.putStringArrayList("LIST", ArrayList(list))
                            arguments.putInt("POSITION", photoPosition)
                            stateViewModel.saveStateAdapter(adapterPosition)
                            stateViewModel.saveStatePhotosAdapter(photoPosition)
                        },
                    FragmentNavigatorExtras(imageView to imageView.transitionName)
                )
            }

        binding?.recyclerRovers?.adapter = adapter
    }

    private fun initViewVisibility() {

        binding?.root?.doOnPreDraw {
            binding?.calendarLayout?.visibility = if (isCalendarVisible) View.VISIBLE else View.GONE
            binding?.recyclerRovers?.post {
                binding?.recyclerRovers?.layoutManager?.scrollToPosition(savedAdapterPosition)
            }
        }

    }

    private fun renderData(data: RoversServerResponseDataItem) {
        val formatterDateUS = "yyyy-MM-dd"
        val formatterDateRU = "dd.MM.yyyy"
        val maxDate = convertStringToDate(data.maxDate!!, formatterDateUS)
        val stringMaxDate = convertDateToSting(maxDate, formatterDateRU)
        val minDate = convertStringToDate(data.landingDate!!, formatterDateUS)
        val stringMinDate = convertDateToSting(minDate, formatterDateRU)
        var chooseDateString = ""
        binding?.calendar?.maxDate = maxDate.time
        binding?.calendar?.minDate = minDate.time

        binding?.inputDateLayout?.helperText =
            "Minimal date: $stringMinDate\nMaximum date: $stringMaxDate"

        DateMask(binding?.inputEditDate!!, maxDate.time, minDate.time)

        binding?.inputDateLayout?.setEndIconOnClickListener {
            isCalendarVisible = !isCalendarVisible
            calendarAppearanceDisappearance()
        }

        binding?.calendar?.setOnDateChangeListener { _, year, month, dayOfMonth ->
            chooseDateString = String.format("%02d.%02d.%02d", dayOfMonth, month + 1, year)
        }

        binding?.fabOk?.setOnClickListener {
            if (chooseDateString == "") {
                chooseDateString =
                    convertDateToSting(Date(binding?.calendar?.date!!), formatterDateRU)
            }
            binding?.inputEditDate?.setText(chooseDateString)
            binding?.inputEditDate?.setSelection(chooseDateString.length)
            showPhotos(data.name.toString(), chooseDateString, formatterDateRU, formatterDateUS)
        }
        binding?.showPhotos?.setOnClickListener {
            chooseDateString = binding?.inputEditDate?.text.toString()
            val cleanString = chooseDateString.replace("[^\\d.]|\\.".toRegex(), "")
            if (cleanString.length == 8) {
                binding?.calendar
                showPhotos(data.name.toString(), chooseDateString, formatterDateRU, formatterDateUS)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertStringToDate(stringDateToConvert: String, pattern: String): Date {
        val formatter = SimpleDateFormat(pattern)
        return formatter.parse(stringDateToConvert)!!
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertDateToSting(dateToConvert: Date, pattern: String): String {
        val formatter = SimpleDateFormat(pattern)
        return formatter.format(dateToConvert)
    }

    private fun setResponsePhotos(roverName: String, date: String) {
        viewModel.updatePhotosData(roverName, date)
    }

    private fun showPhotos(
        roverName: String,
        stringToConvert: String,
        formatter1: String,
        formatter2: String
    ) {
        isCalendarVisible = false
        calendarAppearanceDisappearance()
        setResponsePhotos(
            roverName, convertDateToSting(
                convertStringToDate(stringToConvert, formatter1),
                formatter2
            )
        )
    }

    private fun calendarAppearanceDisappearance() {
        binding?.calendarLayout?.visibility =
            if (isCalendarVisible) View.VISIBLE else View.GONE
        TransitionManager.beginDelayedTransition(binding?.root!!, TransitionSet().apply {
            duration = 600
            ordering = TransitionSet.ORDERING_SEQUENTIAL
            addTransition(ChangeBounds())
            addTransition(Fade())
        })
    }

    private fun prepareTransitions(holder: ViewHolder) {
        exitTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.layout_exit_transition)

        setExitSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>,
                sharedElements: MutableMap<String, View>
            ) {
                holder.itemView.findViewById<EquilateralImageView>(R.id.photo_image_view).let {
                    sharedElements[names[0]] = it
                }
            }
        })
    }

}