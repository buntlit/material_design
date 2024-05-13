package com.buntlit.pictureoftheday.ui.rover

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.buntlit.pictureoftheday.databinding.FragmentRoversBinding

class RoversFragment : Fragment(), RoversRecyclerViewAdapter.OnButtonClickListener {

    private var binding: FragmentRoversBinding? = null
    private lateinit var adapter: RoversRecyclerViewAdapter
    private val viewModel: RoversViewModel by lazy {
        ViewModelProvider(requireActivity())[RoversViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoversBinding.inflate(inflater, container, false)
        adapter = RoversRecyclerViewAdapter(this)
        binding?.recyclerRovers?.adapter = adapter
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getData().observe(viewLifecycleOwner) {
            renderData(it)
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun renderData(data: RoversData) {
        when (data) {
            is RoversData.Success -> {
                adapter.updateRoversList(data.serverResponseData.rover!!)
            }
            is RoversData.Error -> {
                Toast.makeText(context, data.error.message, Toast.LENGTH_SHORT).show()
            }
            is RoversData.Loading -> {

            }
        }
    }

    override fun buttonShowListener(data: RoversServerResponseDataItem) {
        viewModel.setRoverData(data)
        findNavController().navigate(
            RoversFragmentDirections.actionRoversFragmentToRoverPhotoFragment(
                data.name!!
            )
        )
    }
}