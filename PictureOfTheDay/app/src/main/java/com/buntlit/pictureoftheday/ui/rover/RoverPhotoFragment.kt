package com.buntlit.pictureoftheday.ui.rover

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.*
import com.buntlit.pictureoftheday.databinding.FragmentRoverPhotoBinding
import com.buntlit.pictureoftheday.ui.view.DateMask
import java.text.SimpleDateFormat
import java.util.*

class RoverPhotoFragment : Fragment() {
    private var binding: FragmentRoverPhotoBinding? = null
    private lateinit var adapter: RoverPhotoRecyclerViewAdapter
    private val viewModel: RoversViewModel by lazy {
        ViewModelProvider(requireActivity())[RoversViewModel::class.java]
    }
    private var isCalendarVisible = false

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
        adapter = RoverPhotoRecyclerViewAdapter()
        binding?.recyclerRovers?.adapter = adapter
        viewModel.getRoversData().observe(viewLifecycleOwner) {
            renderData(it)
        }
        viewModel.getPhotosData().observe(viewLifecycleOwner) {
            adapter.updateList(it)
        }
        TransitionManager.beginDelayedTransition(binding?.calendarLayout!!, TransitionSet().apply {
            duration = 600
            ordering = TransitionSet.ORDERING_TOGETHER
            addTransition(ChangeBounds())
            addTransition(Slide(Gravity.TOP))
        })

    }

    private fun renderData(data: RoversServerResponseDataItem) {
        requireActivity().title = data.name
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
        binding?.groupCalendarAndRecycler?.visibility = View.VISIBLE
        isCalendarVisible = false
        calendarAppearanceDisappearance()
        binding?.recyclerRovers?.visibility = View.VISIBLE
        setResponsePhotos(
            roverName, convertDateToSting(
                convertStringToDate(stringToConvert, formatter1),
                formatter2
            )
        )
    }

    private fun calendarAppearanceDisappearance(){
        binding?.calendarLayout?.visibility =
            if (isCalendarVisible) View.VISIBLE else View.GONE
        TransitionManager.beginDelayedTransition(binding?.root!!, TransitionSet().apply {
            duration = 600
            ordering = TransitionSet.ORDERING_SEQUENTIAL
            addTransition(ChangeBounds())
            addTransition(Fade())
        })
    }
}