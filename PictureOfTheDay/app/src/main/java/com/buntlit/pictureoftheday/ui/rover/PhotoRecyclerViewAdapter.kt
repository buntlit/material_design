package com.buntlit.pictureoftheday.ui.rover

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.buntlit.pictureoftheday.R
import com.buntlit.pictureoftheday.databinding.ItemRecyclerViewPhotosBinding

class PhotoRecyclerViewAdapter(
    private val listOfPhotos: List<String>,
    private val onClickListener: OnClickListener
) :
    Adapter<PhotoRecyclerViewAdapter.ViewHolderPhotoRecyclerView>() {

    inner class ViewHolderPhotoRecyclerView(private val binding: ItemRecyclerViewPhotosBinding) :
        ViewHolder(binding.root) {

        fun bind(photoUrl: String, position: Int) {
            binding.photoImageView.load(photoUrl) {
                placeholder(R.drawable.ic_no_photo_vector)
            }
            binding.photoImageView.transitionName = photoUrl
            binding.photoImageView.setOnClickListener {
                onClickListener.onPictureClick(listOfPhotos, position, it)
//                it.findNavController()
//                    .navigate(
//                        RoverPhotoFragmentDirections.actionRoverPhotoFragmentToRoverPhotoScaledFragment()
//                            .apply {
//                                arguments.putStringArrayList("LIST", ArrayList(listOfPhotos))
//                                arguments.putInt("POSITION", position)
//                            },
//                        FragmentNavigatorExtras(it to it.transitionName)
//                    )
            }
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
        holder.bind(listOfPhotos[position], position)
    }

    fun interface OnClickListener {
        fun onPictureClick(list: List<String>, position: Int, imageView: View)
    }
}