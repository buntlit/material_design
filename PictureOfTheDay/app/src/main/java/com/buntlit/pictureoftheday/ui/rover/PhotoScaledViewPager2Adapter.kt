package com.buntlit.pictureoftheday.ui.rover

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.buntlit.pictureoftheday.R
import com.buntlit.pictureoftheday.databinding.ItemViewPagerScaledPhotoBinding
import com.buntlit.pictureoftheday.ui.view.OnImageReadyListener

class PhotoScaledViewPager2Adapter(
    private val listOfPhotos: List<String>,
    private val onImageReadyListener: OnImageReadyListener
) :
    RecyclerView.Adapter<PhotoScaledViewPager2Adapter.ViewHolderPhotoScaledViewPager>() {

    inner class ViewHolderPhotoScaledViewPager(private val binding: ItemViewPagerScaledPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photoUrl: String) {
            binding.photoScaledImageView.load(photoUrl) {
                placeholder(R.drawable.ic_no_photo_vector)
                listener(onSuccess = {_,_ ->
                    onImageReadyListener.onImageReady(adapterPosition)
                })

            }
            binding.photoScaledImageView.transitionName = photoUrl
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderPhotoScaledViewPager =
        ViewHolderPhotoScaledViewPager(
            ItemViewPagerScaledPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = listOfPhotos.size

    override fun onBindViewHolder(holder: ViewHolderPhotoScaledViewPager, position: Int) {
        holder.bind(listOfPhotos[position])
    }

}