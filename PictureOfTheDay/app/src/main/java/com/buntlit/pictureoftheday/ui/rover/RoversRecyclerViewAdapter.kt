package com.buntlit.pictureoftheday.ui.rover

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buntlit.pictureoftheday.databinding.ItemRecyclerViewRoversBinding

class RoversRecyclerViewAdapter(
    private val onButtonClickListener: OnButtonClickListener
) :
    RecyclerView.Adapter<RoversRecyclerViewAdapter.RecyclerViewViewHolder>() {

    private var listOfRovers: List<RoversServerResponseDataItem> = listOf()

    @SuppressLint("NotifyDataSetChanged")
    fun updateRoversList(list: List<RoversServerResponseDataItem>){
        listOfRovers = list
        notifyDataSetChanged()
    }

    inner class RecyclerViewViewHolder(private val binding: ItemRecyclerViewRoversBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: RoversServerResponseDataItem) {
            var isOpen = false
            val info: String = """Rover's status: ${data.status}
            |Rover's max date: ${data.maxDate}
            |Rover's launch date: ${data.launchDate}
            |Rover's landing date: ${data.landingDate}
            |Rover's total photos: ${data.totalPhotos}""".trimMargin()
            binding.roverName.text = data.name
            binding.roverName.setOnClickListener {
                if (isOpen) {
                    binding.roverInfoView.visibility = View.GONE
                } else {
                    binding.roverInfoView.visibility = View.VISIBLE
                }
                isOpen = !isOpen
            }
            binding.roverInfo.text = info
            binding.showRoverPhotos.setOnClickListener {
                onButtonClickListener.buttonShowListener(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewViewHolder =
        RecyclerViewViewHolder(
            ItemRecyclerViewRoversBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun getItemCount(): Int = listOfRovers.size

    override fun onBindViewHolder(holder: RecyclerViewViewHolder, position: Int) {
        holder.bind(listOfRovers[position])
    }

    fun interface OnButtonClickListener {
        fun buttonShowListener(data: RoversServerResponseDataItem)
    }


}