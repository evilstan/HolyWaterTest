package com.books.app.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.books.app.R
import com.books.app.databinding.FragmentMainBinding
import com.books.app.ui.fragment.adapters.BannerAdapter
import com.books.app.ui.fragment.adapters.BannerIndicators
import com.books.app.ui.fragment.adapters.CategoryAdapter
import com.books.app.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<MainViewModel>()
    private val navigateToDetails: (Int) -> Unit = { id ->
        viewModel.fetchDetailsData()
        Navigation.findNavController(requireView())
            .navigate(MainFragmentDirections.actionMainFragmentToDetailsFragment(id))
    }
    private var job = launchNewJob()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setObservers() {
        viewModel.booksLD.observe(viewLifecycleOwner) { list ->
            if (list.isEmpty()) return@observe
            binding.rvMainCategories.adapter = CategoryAdapter(list) { navigateToDetails(it) }
        }

        viewModel.bannersLD.observe(viewLifecycleOwner) {
            if (it.isEmpty()) return@observe
            with(binding.rvMainBanner) {
                if (adapter == null) {
                    val bannerAdapter = BannerAdapter { id -> navigateToDetails(id) }
                    adapter = bannerAdapter
                }
                (adapter as BannerAdapter).updateDataset(it)
                onFlingListener = null
                PagerSnapHelper().attachToRecyclerView(this)
                addItemDecoration(
                    BannerIndicators(
                        it.size,
                        requireContext().getColor(R.color.grey),
                        requireContext().getColor(R.color.pink_dark)
                    )
                )
                //prevent endless banner spinning on swipe
                setOnTouchListener { view, event ->
                    if (event?.action != MotionEvent.ACTION_UP) {
                        job.cancel()
                    } else {
                        view.performClick()
                        job = launchNewJob(); job.start()
                    }
                    false
                }

            }
        }

        viewModel.errorLD.observe(viewLifecycleOwner){
            binding.apply {
                tvErr.isVisible = it != null
                tvErr.text = it ?: ""
                progressErr.isVisible = it == getString(R.string.err_no_internet)
            }
        }

        job.start()
        binding.rvMainBanner.layoutManager!!.scrollToPosition(Int.MAX_VALUE / 2)
    }

    private fun launchNewJob() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.RESUMED) {
            binding.rvMainBanner.apply {
                while (true) {
                    delay(3000)
                    val currentItem =
                        (layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                    smoothScrollToPosition(currentItem + 1)
                }
            }
        }
    }
}
