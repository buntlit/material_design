package com.buntlit.pictureoftheday.ui.favorite

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.method.ScrollingMovementMethod
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.buntlit.pictureoftheday.R
import com.buntlit.pictureoftheday.databinding.ItemViewPagerDetailInfoImageFavoriteBinding
import com.buntlit.pictureoftheday.databinding.ItemViewPagerDetailInfoVideoFavoriteBinding
import com.buntlit.pictureoftheday.ui.view.OnImageReadyListener

class FavoritesDetailInfoViewPager2Adapter(
    private val list: List<RoomFavoritesPictures>,
    private val onImageReadyListener: OnImageReadyListener
) :
    Adapter<FavoritesDetailInfoViewPager2Adapter.BaseDetailInfoViewHolder>() {

    companion object {
        private const val TYPE_IMAGE = 0
        private const val TYPE_VIDEO = 1
    }

    abstract class BaseDetailInfoViewHolder(viewItem: View) : ViewHolder(viewItem) {
        abstract fun bind(data: RoomFavoritesPictures)
    }

    inner class DetailInfoPhotoViewHolder(private val binding: ItemViewPagerDetailInfoImageFavoriteBinding) :
        BaseDetailInfoViewHolder(binding.root) {
        override fun bind(data: RoomFavoritesPictures) {

            binding.detailInfoPhoto.transitionName = data.url
            binding.detailInfoAuthor.transitionName = data.copyright ?: data.toString()
            binding.detailInfoTitle.transitionName = data.title
            binding.detailInfoDescription.transitionName = data.explanation

            binding.detailInfoPhoto.load(data.url) {
                listener(onSuccess = {_,_ ->
                    onImageReadyListener.onImageReady(adapterPosition)
                })

            }
            binding.detailInfoAuthor.text = binding.root.resources.getString(
                R.string.media_author,
                convertString(data.copyright)
            )
            binding.detailInfoTitle.text =
                binding.root.resources.getString(R.string.media_title, convertString(data.title))
            binding.detailInfoDescription.text = convertString(data.explanation)

            binding.detailInfoDescription.movementMethod = ScrollingMovementMethod.getInstance()
        }

    }

    inner class DetailInfoVideoViewHolder(private val binding: ItemViewPagerDetailInfoVideoFavoriteBinding) :
        BaseDetailInfoViewHolder(binding.root) {
        @SuppressLint("SetJavaScriptEnabled")
        override fun bind(data: RoomFavoritesPictures) {

            binding.detailInfoVideo.transitionName = "video" + data.url
            binding.detailInfoAuthor.transitionName = data.copyright ?: data.toString()
            binding.detailInfoTitle.transitionName = data.title
            binding.detailInfoDescription.transitionName = data.explanation

            binding.detailInfoVideo.webViewClient = WebViewClient()
            binding.detailInfoVideo.settings.allowFileAccess = true
            binding.detailInfoVideo.settings.javaScriptEnabled = true
            data.url?.let {
                binding.detailInfoVideo.loadUrl(it).apply {
                    onImageReadyListener.onImageReady(adapterPosition)
                }
            }
            binding.detailInfoAuthor.text = binding.root.resources.getString(
                R.string.media_author,
                convertString(data.copyright)
            )
            binding.detailInfoTitle.text =
                binding.root.resources.getString(R.string.media_title, convertString(data.title))
            binding.detailInfoDescription.text = convertString(data.explanation)

            binding.detailInfoDescription.movementMethod = ScrollingMovementMethod.getInstance()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseDetailInfoViewHolder =
        if (viewType == TYPE_IMAGE) DetailInfoPhotoViewHolder(
            ItemViewPagerDetailInfoImageFavoriteBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
        else DetailInfoVideoViewHolder(
            ItemViewPagerDetailInfoVideoFavoriteBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )


    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        return when (list[position].mediaType) {
            "image" -> TYPE_IMAGE
            "video" -> TYPE_VIDEO
            else -> TYPE_IMAGE
        }
    }

    override fun onBindViewHolder(holder: BaseDetailInfoViewHolder, position: Int) {
        holder.bind(list[position])
    }

    private fun convertString(string: String?): String = string?.replace("\n", "") ?: "none"
}