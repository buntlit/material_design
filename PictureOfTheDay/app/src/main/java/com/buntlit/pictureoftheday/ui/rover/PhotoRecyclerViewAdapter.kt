package com.buntlit.pictureoftheday.ui.rover

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.buntlit.pictureoftheday.databinding.ItemRecyclerViewPhotosBinding

class PhotoRecyclerViewAdapter(private val listOfPhotos: List<String>) : Adapter<PhotoRecyclerViewAdapter.ViewHolderPhotoRecyclerView>() {

    inner class ViewHolderPhotoRecyclerView(private val binding: ItemRecyclerViewPhotosBinding) :
        ViewHolder(binding.root) {

        fun bind(photoUrl: String) {
            binding.photoImageView.load(photoUrl)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPhotoRecyclerView =
        ViewHolderPhotoRecyclerView(
            ItemRecyclerViewPhotosBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = listOfPhotos.size

    override fun onBindViewHolder(holder: ViewHolderPhotoRecyclerView, position: Int) {
        holder.bind(listOfPhotos[position])
    }
}