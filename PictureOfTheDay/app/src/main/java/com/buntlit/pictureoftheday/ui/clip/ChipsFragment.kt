package com.buntlit.pictureoftheday.ui.clip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.buntlit.pictureoftheday.R
import com.buntlit.pictureoftheday.databinding.FragmentChipsBinding

class ChipsFragment : Fragment() {
    private var binding: FragmentChipsBinding? = null
    private val viewModel: ChipViewModel by lazy {
        ViewModelProvider(requireActivity())[ChipViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChipsBinding.inflate(inflater)
        setChipsThemeStatus()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.getData().observe(viewLifecycleOwner) {}
        super.onViewCreated(view, savedInstanceState)
        chipThemeBehavior()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setChipsThemeStatus() {
        if (binding?.chipGroupTheme?.checkedChipId != viewModel.getChipId()) {
            viewModel.getChipId()?.let { binding?.chipGroupTheme?.check(it) }
        }
    }

    private fun chipThemeBehavior() {
        binding?.chipGroupTheme?.setOnCheckedStateChangeListener { group, checkedIds ->
            var themeId = 0
            when (group.checkedChipId) {
                R.id.chipThemePurple -> themeId = R.style.Theme_PictureOfTheDay
                R.id.chipThemeIndigo -> themeId = R.style.Theme_PictureOfTheDayIndigo
                R.id.chipThemePink -> themeId = R.style.Theme_PictureOfTheDayPink
            }
            viewModel.setData(group.checkedChipId, themeId, true)
        }
    }
}