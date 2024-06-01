package com.buntlit.pictureoftheday.ui.rover

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.RecyclerView
import com.buntlit.pictureoftheday.databinding.ItemRecyclerViewRoverPhotoBinding
import com.buntlit.pictureoftheday.ui.view.SliderAnimation

class RoverPhotoRecyclerViewAdapter(
    private val isFirsTimeOpen: Boolean,
    private val savedAdapterPosition: Int,
    private val savedPhotoAdapterPosition: Int,
    private val onClickListener: OnClickListener
) :
    RecyclerView.Adapter<RoverPhotoRecyclerViewAdapter.RecyclerViewRoverPhotoViewHolder>() {
    private var mapOfCameras: MutableMap<RoverServerResponseDataItemCameras, MutableList<String>> =
        mutableMapOf()
    private var listOfKeys: List<RoverServerResponseDataItemCameras> = emptyList()
    private var listOfItemIsOpen: MutableList<Boolean> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(
        mutableMap: MutableMap<RoverServerResponseDataItemCameras, MutableList<String>>,
        listOfItemIsOpen: List<Boolean>?
    ) {
        mapOfCameras = mutableMap
        listOfKeys = mapOfCameras.keys.toList()
        this.listOfItemIsOpen = if (listOfItemIsOpen.isNullOrEmpty())
            MutableList(listOfKeys.size) { false }
        else listOfItemIsOpen as MutableList<Boolean>
        notifyDataSetChanged()
    }

    fun getListOfItemIsOpen(): List<Boolean> {
        return listOfItemIsOpen
    }

    inner class RecyclerViewRoverPhotoViewHolder(private val binding: ItemRecyclerViewRoverPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(
            dataItemCameras: RoverServerResponseDataItemCameras,
            listOfPhotos: MutableList<String>,
            pos: Int
        ) {
            var isOpen = listOfItemIsOpen[pos]
            binding.cameraName.text = dataItemCameras.name
            binding.cameraFullName.text = dataItemCameras.fullName

            binding.recyclerPhotos.adapter =
                PhotoRecyclerViewAdapter(listOfPhotos) { list, photoPosition, imageView ->
                    onClickListener.onPictureClicked(
                        list = list,
                        photoPosition = photoPosition,
                        adapterPosition = adapterPosition,
                        imageView = imageView,
                        holder = binding.recyclerPhotos.findViewHolderForAdapterPosition(photoPosition)!!
                    )
                }

            if (!isFirsTimeOpen) {
                binding.root.doOnPreDraw {
                    SliderAnimation(
                        container = binding.root.parent as ViewGroup,
                        view = binding.roverCameraView,
                        durationTime = 600,
                        isOpen = isOpen,
                        isClicked = false
                    )
                }
            }

            binding.cameraName.setOnClickListener {
                isOpen = !isOpen
                SliderAnimation(
                    container = binding.root.parent as ViewGroup,
                    view = binding.roverCameraView,
                    durationTime = 600,
                    isOpen = isOpen,
                    isClicked = true
                )
                listOfItemIsOpen[pos] = isOpen

            }

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
        holder.bind(listOfKeys[position], mapOfCameras[listOfKeys[position]]!!, position)
        if (isFirsTimeOpen) {
            holder.itemView.startAnimation(AnimationSet(false).apply {
                duration = 600
                addAnimation(ScaleAnimation(1f, 1f, 0f, 1f))
                addAnimation(AlphaAnimation(0f, 1f))
            })
        }
    }

    fun interface OnClickListener {
        fun onPictureClicked(
            list: List<String>,
            photoPosition: Int,
            adapterPosition: Int,
            imageView: View,
            holder: RecyclerView.ViewHolder
        )
    }

}