package com.buntlit.pictureoftheday.ui.rover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.buntlit.pictureoftheday.databinding.FragmentRoversBinding

class RoversFragment : Fragment() {

    private var binding: FragmentRoversBinding? = null
    private lateinit var adapter: RoversRecyclerViewAdapter
    private val viewModel: RoversViewModel by lazy {
        ViewModelProvider(requireActivity())[RoversViewModel::class.java]
    }
    private val stateViewModel: SaveStateViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoversBinding.inflate(inflater, container, false)

        initRecyclerView()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getData().observe(viewLifecycleOwner) {
            renderData(it)
        }
    }

    override fun onDestroyView() {
        stateViewModel.saveStateRecycler(adapter.getListOfItemIsOpen())
        stateViewModel.saveStateFragment(false)
        binding = null
        super.onDestroyView()
    }

    private fun initRecyclerView(){

        val isFirstTimeOpen =
            if (stateViewModel.getFragmentIsFirstTime() == null) true
            else stateViewModel.getFragmentIsFirstTime()!!

        adapter = RoversRecyclerViewAdapter(
            isFirsTimeOpen = isFirstTimeOpen
        ) { data ->
            viewModel.setRoverData(data)
            findNavController().navigate(
                RoversFragmentDirections.actionRoversFragmentToRoverPhotoFragment(
                    data.name!!
                )
            )
        }

        binding?.recyclerRovers?.adapter = adapter
    }

    private fun renderData(data: RoversData) {
        when (data) {
            is RoversData.Success -> {
                adapter.updateRoversList(
                    data.serverResponseData.rover!!,
                    stateViewModel.getRecyclerIsOpens()
                )
            }
            is RoversData.Error -> {
                Toast.makeText(context, data.error.message, Toast.LENGTH_SHORT).show()
            }
            is RoversData.Loading -> {

            }
        }
    }
}