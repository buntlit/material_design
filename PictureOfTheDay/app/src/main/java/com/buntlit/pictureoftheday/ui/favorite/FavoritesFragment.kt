package com.buntlit.pictureoftheday.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.view.ViewGroup
import android.webkit.WebView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.SharedElementCallback
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.buntlit.pictureoftheday.R
import com.buntlit.pictureoftheday.databinding.FragmentFavoritesBinding
import com.buntlit.pictureoftheday.ui.view.EquilateralImageView
import java.util.concurrent.TimeUnit


class FavoritesFragment : Fragment() {

    private var binding: FragmentFavoritesBinding? = null
    private lateinit var adapter: FavoritesRecyclerViewAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private var isNotDeletedPosition = true
    private var position = 0
    private val viewModelHistory: FavoritesViewModel by lazy {
        ViewModelProvider(
            requireParentFragment(),
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )[FavoritesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }


    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    private fun initView(){
        position = viewModelHistory.getPosition()

        initRecyclerView()

        viewModelHistory.getData().observe(viewLifecycleOwner) {
            if (isNotDeletedPosition && it.isNotEmpty()) {
                adapter.updateList(it)
                binding?.favoritesStub?.visibility = View.GONE
                binding?.groupFiltersAndRecycler?.visibility = View.VISIBLE
            }
            isNotDeletedPosition = true
            waitToTransition(binding?.recyclerFavorites)
        }

        chipBehavior()
        prepareTransitions()

    }

    private fun initRecyclerView(){
        adapter = FavoritesRecyclerViewAdapter(object : OnStartDragListener {
            override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                itemTouchHelper.startSwipe(viewHolder)
            }

        }, object : FavoritesRecyclerViewAdapter.OnItemDismissListener {
            override fun delete(data: RoomFavoritesPictures) {
                viewModelHistory.deleteFromFavorite(data)
                isNotDeletedPosition = false
            }

        }, object : FavoritesRecyclerViewAdapter.OnItemClickListener {
            override fun onClick(position: Int) {
                viewModelHistory.setPosition(position)
            }

        })

        binding?.recyclerFavorites?.adapter = adapter

        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback()).apply {
            attachToRecyclerView(binding?.recyclerFavorites)
        }
    }

    private fun chipBehavior() {
        binding?.chipGroupSort?.setOnCheckedStateChangeListener { group, _ ->
            when (group.checkedChipId) {
                binding?.chipSortDefault!!.id -> viewModelHistory.getFavoritesSortDefault()
                binding?.chipSortName!!.id -> viewModelHistory.getFavoritesSortName()
                binding?.chipSortDate!!.id -> viewModelHistory.getFavoritesSortDate()
                binding?.chipSortType!!.id -> viewModelHistory.getFavoritesSortType()
            }
        }
    }

    private fun prepareTransitions() {

        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.layout_fav_exit_transition)

        postponeEnterTransition(250, TimeUnit.MILLISECONDS)

        setExitSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>,
                sharedElements: MutableMap<String, View>
            ) {
                binding?.recyclerFavorites?.findViewWithTag<ViewGroup>(position)?.apply {
                    this.findViewById<EquilateralImageView>(R.id.photo_fav_image_view)?.let {
                        sharedElements[names[0]] = it
                    }
                    this.findViewById<WebView>(R.id.video_fav)?.let {
                        sharedElements[names[0]] = it
                    }
                    this.findViewById<AppCompatTextView>(R.id.photo_name)?.let {
                        sharedElements[names[0]] = it
                    }
                    this.findViewById<AppCompatTextView>(R.id.photo_author)?.let {
                        sharedElements[names[0]] = it
                    }
                    this.findViewById<AppCompatTextView>(R.id.photo_date)?.let {
                        sharedElements[names[0]] = it
                    }

                }
//                binding?.recyclerFavorites?.findViewHolderForAdapterPosition(position)?.itemView?.apply {
//                    this.findViewById<EquilateralImageView>(R.id.photo_fav_image_view)?.let {
//                        sharedElements[names[0]] = it
//                    }
//                    this.findViewById<WebView>(R.id.video_fav)?.let {
//                        sharedElements[names[0]] = it
//                    }
//                    this.findViewById<AppCompatTextView>(R.id.photo_name)?.let {
//                        sharedElements[names[0]] = it
//                    }
//                    this.findViewById<AppCompatTextView>(R.id.photo_author)?.let {
//                        sharedElements[names[0]] = it
//                    }
//                    this.findViewById<AppCompatTextView>(R.id.photo_date)?.let {
//                        sharedElements[names[0]] = it
//                    }
//                }
            }
        })
    }

    private fun scrollToPosition() {

        binding?.recyclerFavorites?.addOnLayoutChangeListener(
            object : OnLayoutChangeListener {
                override fun onLayoutChange(
                    view: View,
                    left: Int,
                    top: Int,
                    right: Int,
                    bottom: Int,
                    oldLeft: Int,
                    oldTop: Int,
                    oldRight: Int,
                    oldBottom: Int
                ) {
                    binding?.recyclerFavorites?.removeOnLayoutChangeListener(this)
                    val layoutManager = binding?.recyclerFavorites?.layoutManager
                    val viewAtPosition =
                        layoutManager?.findViewByPosition(position)
                    if (viewAtPosition == null
                        || layoutManager.isViewPartiallyVisible(viewAtPosition, false, true)
                    ) {
                        binding?.recyclerFavorites?.post { layoutManager?.scrollToPosition(position) }
                    }
                }
            })

    }

    private fun Fragment.waitToTransition(view: RecyclerView?){
        postponeEnterTransition()
        view?.doOnPreDraw {
            scrollToPosition()
            startPostponedEnterTransition()
        }
    }
}