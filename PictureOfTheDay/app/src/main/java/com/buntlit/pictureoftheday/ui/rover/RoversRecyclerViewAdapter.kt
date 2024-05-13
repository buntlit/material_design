package com.buntlit.pictureoftheday.ui.rover

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import androidx.recyclerview.widget.RecyclerView
import com.buntlit.pictureoftheday.databinding.ItemRecyclerViewRoversBinding
import com.buntlit.pictureoftheday.ui.view.SliderAnimation

class RoversRecyclerViewAdapter(
    private val onButtonClickListener: OnButtonClickListener
) :
    RecyclerView.Adapter<RoversRecyclerViewAdapter.RecyclerViewViewHolder>() {

    private var listOfRovers: List<RoversServerResponseDataItem> = listOf()

    @SuppressLint("NotifyDataSetChanged")
    fun updateRoversList(list: List<RoversServerResponseDataItem>) {
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
            binding.roverInfo.text = info
            binding.showRoverPhotos.setOnClickListener {
                onButtonClickListener.buttonShowListener(data)
            }
            binding.roverName.setOnClickListener {

//                TransitionManager.beginDelayedTransition(
//                    binding.root.parent as ViewGroup,
//                    TransitionSet().apply {
//                        ordering = TransitionSet.ORDERING_TOGETHER
//                        duration = 600
//                        addTransition(ChangeBounds())
//                        addTransition(Slide(Gravity.TOP))
//                    })

                SliderAnimation(
                    binding.root.parent as ViewGroup,
                    binding.roverInfoView,
                    600,
                    isOpen
                )
                isOpen = !isOpen
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

        holder.itemView.startAnimation(AnimationSet(false).apply {
            duration = 600
            addAnimation(ScaleAnimation(1f, 1f, 0f, 1f))
            addAnimation(AlphaAnimation(0f, 1f))
        })
    }

    fun interface OnButtonClickListener {
        fun buttonShowListener(data: RoversServerResponseDataItem)
    }


}