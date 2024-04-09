package com.buntlit.pictureoftheday.ui.clip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.buntlit.pictureoftheday.databinding.FragmentChipsBinding

class ChipsFragment : Fragment() {
    private var binding: FragmentChipsBinding? = null

    companion object{
        fun newInstance() = ChipsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChipsBinding.inflate(inflater)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}