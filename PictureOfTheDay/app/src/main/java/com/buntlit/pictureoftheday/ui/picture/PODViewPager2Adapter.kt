package com.buntlit.pictureoftheday.ui.picture

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.buntlit.pictureoftheday.R
import com.buntlit.pictureoftheday.databinding.ItemViewPagerPodBinding

class PODViewPager2Adapter(private val listOfTheDayData: List<PODServerResponseData?>) :
    RecyclerView.Adapter<PODViewPager2Adapter.ViewPagerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder = ViewPagerViewHolder(
        ItemViewPagerPodBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
    )


    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        holder.bind(listOfTheDayData[position])
    }

    override fun getItemCount(): Int = listOfTheDayData.size


    inner class ViewPagerViewHolder(private var binding: ItemViewPagerPodBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetJavaScriptEnabled")
        fun bind(data: PODServerResponseData?) {
            if (data == null) {
                binding.imageView.visibility = View.VISIBLE
                binding.webView.visibility = View.GONE
                binding.hdChip.visibility = View.GONE
                binding.imageView.load(R.drawable.ic_no_photo_vector)
            } else if (data.mediaType.equals("video")) {
                binding.imageView.visibility = View.GONE
                binding.webView.visibility = View.VISIBLE
                binding.webView.webViewClient = WebViewClient()
                binding.webView.settings.allowFileAccess = true
                binding.webView.settings.javaScriptEnabled = true
                data.url?.let { binding.webView.loadUrl(it) }
            } else {
                binding.hdChip.isChecked = false
                binding.imageView.visibility = View.VISIBLE
                binding.webView.visibility = View.GONE
                binding.hdChip.visibility = View.VISIBLE
                binding.imageView.load(data.url)
                binding.hdChip.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked)
                        binding.imageView.load(data.hdurl)
                    else
                        binding.imageView.load(data.url)
                }
            }
        }
    }

}