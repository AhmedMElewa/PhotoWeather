package com.elewa.photoweather.modules.history.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.elewa.photoweather.base.BaseFragment
import com.elewa.photoweather.databinding.FragmentHistoryBinding
import com.elewa.photoweather.extentions.toGone
import com.elewa.photoweather.extentions.toVisible
import com.elewa.photoweather.modules.history.presentation.adapter.HistoryAdapter
import com.elewa.photoweather.modules.history.presentation.uimodel.HistoryUiState
import com.elewa.photoweather.modules.history.presentation.viewmodel.HistoryViewModel
import com.elewa.photoweather.modules.home.presentation.ui.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryFragment : BaseFragment<FragmentHistoryBinding>() {

    private val viewModel: HistoryViewModel by viewModels()

    private lateinit var adapter: HistoryAdapter

    override val bindLayout: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHistoryBinding
        get() = FragmentHistoryBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {

        binding.recyclerImages.layoutManager = GridLayoutManager(context, 5)
        binding.recyclerImages.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )


        adapter = HistoryAdapter(HistoryAdapter.OnClickListener { item ->
            val action =
                HistoryFragmentDirections.actionHistoryFragmentToImageViewFragment(
                    item.imgPath,
                    item.imgId
                )
            findNavController().navigate(action)
        })

        viewModel.getImages()
        initObservers()
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is HistoryUiState.Empty -> {
                            binding.progressBlue.toGone()
                        }
                        is HistoryUiState.Loading -> {
                            if (state.loading) {
                                binding.progressBlue.toVisible()
                            } else {
                                binding.progressBlue.toGone()
                            }
                        }
                        is HistoryUiState.Loaded -> {
                            binding.progressBlue.toGone()
                            adapter.submitList(state.imgList)
                            binding.recyclerImages.adapter = adapter
                        }
                    }
                }
            }
        }
    }

}