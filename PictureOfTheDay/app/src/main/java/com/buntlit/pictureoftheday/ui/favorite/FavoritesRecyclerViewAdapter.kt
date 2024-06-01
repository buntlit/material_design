package com.buntlit.pictureoftheday.ui.favorite

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.buntlit.pictureoftheday.R
import com.buntlit.pictureoftheday.databinding.ItemRecyclerViewFavoriteBinding
import com.buntlit.pictureoftheday.ui.view.OnImageReadyListener

class FavoritesRecyclerViewAdapter(
    private val dragListener: OnStartDragListener,
    private val dismissListener: OnItemDismissListener,
    private val itemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<FavoritesRecyclerViewAdapter.ViewHolderFavorites>() {

    private var listOfData: MutableList<RoomFavoritesPictures> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<RoomFavoritesPictures>) {
        listOfData = list as MutableList<RoomFavoritesPictures>
        notifyDataSetChanged()
    }

    inner class ViewHolderFavorites(private val binding: ItemRecyclerViewFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root), ItemTouchHelperViewHolder {

        @SuppressLint("ClickableViewAccessibility")
        fun bind(data: RoomFavoritesPictures) {

            binding.photoFavImageView.transitionName = data.url
            binding.videoFav.transitionName = "video" + data.url
            binding.photoDate.transitionName = data.explanation
            binding.photoName.transitionName = data.title
            binding.photoAuthor.transitionName = data.copyright ?: data.toString()

            binding.photoFavImageView.load(data.url) {
                placeholder(R.drawable.ic_no_photo_vector)
                error(R.drawable.ic_video_stub)
            }
            binding.photoAuthor.text = getSpannableString(
                binding.root.resources.getString(
                    R.string.media_author,
                    convertString(data.copyright)
                )
            )
            binding.photoName.text = getSpannableString(
                binding.root.resources.getString(R.string.media_title, convertString(data.title))
            )
            binding.photoDate.text = getSpannableString(
                binding.root.resources.getString(R.string.media_date, convertString(data.date))
            )
            binding.cardViewFav.setOnClickListener {
                itemClickListener.onClick(adapterPosition)

                it.findNavController().navigate(
                    FavoritesFragmentDirections.actionFavoritesFragmentToFavoritesDetailInfoFragment(),
                    FragmentNavigatorExtras(
                        binding.photoFavImageView to binding.photoFavImageView.transitionName,
                        binding.videoFav to binding.videoFav.transitionName,
                        binding.photoDate to binding.photoDate.transitionName,
                        binding.photoName to binding.photoName.transitionName,
                        binding.photoAuthor to binding.photoAuthor.transitionName
                    )
                )
            }
            binding.dragImageView.setOnClickListener {}

            binding.deletePrompt.setOnClickListener {
                dismissListener.delete(data)
                listOfData.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
                itemView.scrollTo(0, 0)
            }


            binding.dragImageView.setOnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    dragListener.onStartDrag(this)
                }
                false
            }
        }

        override fun onItemSelected() {
            binding.cardViewFav.setBackgroundColor(Color.LTGRAY)
            binding.deletePrompt.visibility = View.VISIBLE
        }

        override fun onItemClear() {
            binding.cardViewFav.setBackgroundColor(0)
        }

        override fun onItemOnStartPosition() {
            binding.deletePrompt.visibility = View.INVISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderFavorites =
        ViewHolderFavorites(
            ItemRecyclerViewFavoriteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = listOfData.size

    override fun onBindViewHolder(holder: ViewHolderFavorites, position: Int) {
        holder.bind(listOfData[position])
    }

    private fun convertString(string: String?): String = string?.replace("\n", "") ?: "none"

    private fun getSpannableString(string: String): SpannableString {
        val spannableString = SpannableString(string)
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            string.indexOf(" "),
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        return spannableString
    }

    interface OnItemDismissListener {
        fun delete(data: RoomFavoritesPictures)
    }

    interface OnItemClickListener {
        fun onClick(position: Int)
    }

}