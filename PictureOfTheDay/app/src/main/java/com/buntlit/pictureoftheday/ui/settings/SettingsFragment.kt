package com.buntlit.pictureoftheday.ui.settings

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.buntlit.pictureoftheday.R
import com.buntlit.pictureoftheday.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var binding: FragmentSettingsBinding? = null
    private val viewModel: SettingsViewModel by lazy {
        ViewModelProvider(requireActivity())[SettingsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater)
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
        binding?.chipGroupTheme?.setOnCheckedStateChangeListener { group, _ ->
            var themeId = 0
            when (group.checkedChipId) {
                binding?.chipThemePurple!!.id -> themeId = R.style.Theme_PictureOfTheDay
                binding?.chipThemeIndigo!!.id -> themeId = R.style.Theme_PictureOfTheDayIndigo
                binding?.chipThemePink!!.id -> themeId = R.style.Theme_PictureOfTheDayPink
            }
            viewModel.setData(group.checkedChipId, themeId, true)
        }
    }


}