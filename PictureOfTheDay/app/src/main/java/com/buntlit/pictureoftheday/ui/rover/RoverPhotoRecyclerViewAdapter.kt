package com.buntlit.pictureoftheday.ui.rover

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buntlit.pictureoftheday.databinding.ItemRecyclerViewRoverPhotoBinding

class RoverPhotoRecyclerViewAdapter :
    RecyclerView.Adapter<RoverPhotoRecyclerViewAdapter.RecyclerViewRoverPhotoViewHolder>() {
    private var mapOfCameras: MutableMap<RoverServerResponseDataItemCameras, MutableList<String>> =
        mutableMapOf()
    private var listOfKeys: List<RoverServerResponseDataItemCameras> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(mutableMap: MutableMap<RoverServerResponseDataItemCameras, MutableList<String>>) {
        mapOfCameras = mutableMap
        listOfKeys = mapOfCameras.keys.toList()
        notifyDataSetChanged()
    }

    inner class RecyclerViewRoverPhotoViewHolder(private val binding: ItemRecyclerViewRoverPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

//        private var photoAdapter: PhotoRecyclerViewAdapter = PhotoRecyclerViewAdapter()

        fun bind(
            dataItemCameras: RoverServerResponseDataItemCameras,
            listOfPhotos: MutableList<String>
        ) {
            var isOpen = false
            binding.cameraName.text = dataItemCameras.name
            binding.cameraFullName.text = dataItemCameras.fullName

            binding.cameraName.setOnClickListener {
                if (isOpen) {
                    binding.roverCameraView.visibility = View.GONE
                } else {
                    binding.roverCameraView.visibility = View.VISIBLE
                }
                isOpen = !isOpen
            }
//            binding.recyclerPhotos.layoutManager = LinearLayoutManager(binding.root.context,
//                LinearLayoutManager.HORIZONTAL, false)
//            photoAdapter.updatePhotos(listOfPhotos)
            binding.recyclerPhotos.adapter = PhotoRecyclerViewAdapter(listOfPhotos)

        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerViewRoverPhotoViewHolder = RecyclerViewRoverPhotoViewHolder(
        ItemRecyclerViewRoverPhotoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun getItemCount(): Int = listOfKeys.size

    override fun onBindViewHolder(holder: RecyclerViewRoverPhotoViewHolder, position: Int) {
        holder.bind(listOfKeys[position], mapOfCameras[listOfKeys[position]]!!)
    }

}