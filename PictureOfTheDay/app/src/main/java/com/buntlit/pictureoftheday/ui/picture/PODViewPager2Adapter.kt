package com.buntlit.pictureoftheday.ui.picture

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.buntlit.pictureoftheday.R
import com.buntlit.pictureoftheday.databinding.ItemViewPagerImagePodBinding
import com.buntlit.pictureoftheday.databinding.ItemViewPagerVideoPodBinding
import com.google.android.material.chip.Chip

class PODViewPager2Adapter(
    private val listOfTheDayData: List<PODServerResponseData?>,
    private val onChipClickListener: OnChipClickListener
) :
    RecyclerView.Adapter<PODViewPager2Adapter.BaseViewHolder>() {

    companion object {
        private const val TYPE_IMAGE = 0
        private const val TYPE_VIDEO = 1
    }

    private val isLikedList: MutableList<Boolean> = MutableList(listOfTheDayData.size) { false }

    fun updateLists(isLikedHistory: Boolean, position: Int) {
        isLikedList[position] = isLikedHistory
        notifyItemChanged(position)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

        return if (viewType == TYPE_IMAGE)
            ViewPagerImageViewHolder(
                ItemViewPagerImagePodBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            ) else
            ViewPagerVideoViewHolder(
                ItemViewPagerVideoPodBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(listOfTheDayData[position], isLikedList[position])
    }

    override fun getItemCount(): Int = listOfTheDayData.size

    override fun getItemViewType(position: Int): Int {
        return if (listOfTheDayData[position] == null)
            -1
        else when (listOfTheDayData[position]?.mediaType) {
            "image" -> TYPE_IMAGE
            "video" -> TYPE_VIDEO
            else -> TYPE_IMAGE
        }
    }

    abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(data: PODServerResponseData?, isLiked: Boolean)
    }

    inner class ViewPagerImageViewHolder(private var binding: ItemViewPagerImagePodBinding) :
        BaseViewHolder(binding.root) {
        override fun bind(data: PODServerResponseData?, isLiked: Boolean) {

            if (data == null) {
                binding.groupChips.visibility = View.GONE
            } else {
                binding.hdChip.isChecked = false

                chipLikeBehavior(binding.likedChip, isLiked, adapterPosition, data)

                binding.imageView.load(data.url) {
                    placeholder(R.drawable.ic_no_photo_vector)
                    listener(onSuccess = {_, _ ->
                        binding.groupChips.visibility = View.VISIBLE
                    })
                }
                binding.hdChip.setOnCheckedChangeListener { _, isChecked ->
                    binding.imageView.load(if (isChecked) data.hdUrl else data.url){
                        placeholder(R.drawable.ic_no_photo_vector)
                    }
                }
            }
        }
    }

    inner class ViewPagerVideoViewHolder(private var binding: ItemViewPagerVideoPodBinding) :
        BaseViewHolder(binding.root) {
        @SuppressLint("SetJavaScriptEnabled")
        override fun bind(data: PODServerResponseData?, isLiked: Boolean) {
            if (data == null) {
                binding.stubImageView.visibility = View.VISIBLE
                binding.groupView.visibility = View.GONE
                binding.stubImageView.load(R.drawable.ic_no_photo_vector)
            } else {
                chipLikeBehavior(binding.likedChip, isLiked, adapterPosition, data)

                binding.webView.webViewClient = WebViewClient()
                binding.webView.settings.allowFileAccess = true
                binding.webView.settings.javaScriptEnabled = true
                data.url?.let {
                    binding.stubImageView.visibility = View.GONE
                    binding.groupView.visibility = View.VISIBLE
                    binding.webView.loadUrl(it)
                }
            }
        }
    }

    private fun chipLikeBehavior(chip: Chip, isLiked: Boolean, position: Int, data: PODServerResponseData) {
        chip.isChecked = isLiked
        chip.setOnCheckedChangeListener { _, isChecked ->
            isLikedList[position] = isChecked
            onChipClickListener.onLikeClick(data, isChecked)
        }
    }

    fun interface OnChipClickListener {
        fun onLikeClick(data: PODServerResponseData, isChecked: Boolean)
    }

}