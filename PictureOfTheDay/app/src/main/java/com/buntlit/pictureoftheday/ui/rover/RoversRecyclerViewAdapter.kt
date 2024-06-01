package com.buntlit.pictureoftheday.ui.rover

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.RecyclerView
import com.buntlit.pictureoftheday.R
import com.buntlit.pictureoftheday.databinding.ItemRecyclerViewRoversBinding
import com.buntlit.pictureoftheday.ui.view.SliderAnimation

class RoversRecyclerViewAdapter(
    private val isFirsTimeOpen: Boolean,
    private val onButtonClickListener: OnButtonClickListener
) : RecyclerView.Adapter<RoversRecyclerViewAdapter.RecyclerViewViewHolder>() {

    private var listOfRovers: List<RoversServerResponseDataItem> = listOf()
    private var listOfItemIsOpen: MutableList<Boolean> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun updateRoversList(
        listOfRovers: List<RoversServerResponseDataItem>, listOfItemIsOpen: List<Boolean>?
    ) {
        this.listOfRovers = listOfRovers
        this.listOfItemIsOpen =
            if (listOfItemIsOpen == null) MutableList(this.listOfRovers.size) { false }
            else listOfItemIsOpen as MutableList<Boolean>
        notifyDataSetChanged()
    }

    fun getListOfItemIsOpen(): List<Boolean> {
        return listOfItemIsOpen
    }

    inner class RecyclerViewViewHolder(private val binding: ItemRecyclerViewRoversBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: RoversServerResponseDataItem, position: Int) {
            var isOpen = listOfItemIsOpen[position]

            val info = binding.root.resources.getString(
                R.string.rovers_info,
                data.status,
                data.maxDate,
                data.launchDate,
                data.landingDate,
                data.totalPhotos
            )
            binding.root.doOnPreDraw {
                SliderAnimation(
                    container = binding.root.parent as ViewGroup,
                    view = binding.roverInfoView,
                    durationTime = 600,
                    isOpen = isOpen,
                    isClicked = false
                )
            }

            binding.roverName.text = data.name
            binding.roverInfo.text = getSpannableText(info)
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
                isOpen = !isOpen
                SliderAnimation(
                    container = binding.root.parent as ViewGroup,
                    view = binding.roverInfoView,
                    durationTime = 600,
                    isOpen = isOpen,
                    isClicked = true
                )
                listOfItemIsOpen[position] = isOpen
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewViewHolder =
        RecyclerViewViewHolder(
            ItemRecyclerViewRoversBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = listOfRovers.size

    override fun onBindViewHolder(holder: RecyclerViewViewHolder, position: Int) {
        holder.bind(listOfRovers[position], position)

        if (isFirsTimeOpen) {
            holder.itemView.startAnimation(AnimationSet(false).apply {
                duration = 600
                addAnimation(ScaleAnimation(1f, 1f, 0f, 1f))
                addAnimation(AlphaAnimation(0f, 1f))
            })
        }
    }

    private fun getSpannableText(string: String): SpannableString {
        val spannableString = SpannableString(string)
        val subStringList = string.split("\n").toList()
        for (i in subStringList.indices) {
            val positionStart = string.indexOf(subStringList[i])
            val positionEnd = positionStart + subStringList[i].indexOf(':') + 1
            spannableString.setSpan(
                StyleSpan(Typeface.BOLD),
                positionStart,
                positionEnd,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
        }
        return spannableString
    }

    fun interface OnButtonClickListener {
        fun buttonShowListener(data: RoversServerResponseDataItem)
    }


}