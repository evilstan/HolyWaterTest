package com.books.app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.books.app.R
import com.books.app.data.model.Book
import com.books.app.databinding.FragmentDetailsBinding
import com.books.app.ui.fragment.adapters.BookAdapter
import com.books.app.ui.fragment.adapters.BoundsOffsetDecoration
import com.books.app.ui.fragment.adapters.CarouselAdapter
import com.books.app.ui.fragment.adapters.LinearHorizontalSpacingDecoration
import com.books.app.ui.fragment.adapters.CarouselWithSnapLayoutManager
import com.books.app.ui.viewmodel.MainViewModel


class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<MainViewModel>()
    private var currentBookIndex = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.carouselLD.observe(viewLifecycleOwner) {
            if (it.isEmpty()) return@observe
            val currentBook = it.firstOrNull { book -> book.id == arguments?.getInt(BOOK_ID_KEY) }
            currentBookIndex = if (currentBook == null) 0 else it.indexOf(currentBook)
            setViews(currentBook ?: it.first())
            setCarousel(it)
        }

        viewModel.favoritesLD.observe(viewLifecycleOwner) {
            if (it.isEmpty()) return@observe
            binding.rvRecommendations.adapter = BookAdapter(it, true)
        }
    }

    private fun setViews(book: Book) {
        binding.apply {
            counterReaders.tvCounterTitle.text = getString(R.string.readers)
            counterReaders.tvCounterValue.text = book.views

            counterLikes.tvCounterTitle.text = getString(R.string.likes)
            counterLikes.tvCounterValue.text = book.likes

            counterQuotes.tvCounterTitle.text = getString(R.string.quotes)
            counterQuotes.tvCounterValue.text = book.quotes

            counterGenre.tvCounterTitle.text = getString(R.string.genre)
            counterGenre.tvCounterValue.text = book.genre
            counterGenre.ivPepper.isVisible = book.genre.lowercase() == GENRE_WITH_PEPPER

            tvSummaryValue.text = book.summary
            btnBack.setOnClickListener {
                Navigation.findNavController(binding.root).popBackStack()
            }

            tvDetailsAuthor.text = book.author
            tvDetailsTitle.text = book.name

            btnReadNow.setOnClickListener {
                Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setCarousel(items: List<Book>) {
        binding.rvCarousel.apply {
            adapter = CarouselAdapter(items)
            layoutManager = CarouselWithSnapLayoutManager(requireContext())
            onFlingListener = null
            PagerSnapHelper().attachToRecyclerView(binding.rvCarousel)
            addItemDecoration(LinearHorizontalSpacingDecoration(16))
            addItemDecoration(BoundsOffsetDecoration())
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (scrollState != RecyclerView.SCROLL_STATE_IDLE) return
                    if (currentBookIndex < 3 || currentBookIndex > items.size - 5) invalidateItemDecorations()
                    val currentItem =
                        (layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                    if (currentItem >= 0) setViews(items[currentItem])
                }
            })

            val smoothScroller = object : LinearSmoothScroller(context) {
                override fun calculateDxToMakeVisible(view: View?, snapPreference: Int): Int {
                    val dxToStart = super.calculateDxToMakeVisible(view, SNAP_TO_START)
                    val dxToEnd = super.calculateDxToMakeVisible(view, SNAP_TO_END)

                    return (dxToStart + dxToEnd) / 2
                }
            }

            smoothScroller.targetPosition = currentBookIndex
            layoutManager!!.startSmoothScroll(smoothScroller)
        }
    }

    companion object {
        const val BOOK_ID_KEY = "book_id"
        const val GENRE_WITH_PEPPER = "hot"
    }
}