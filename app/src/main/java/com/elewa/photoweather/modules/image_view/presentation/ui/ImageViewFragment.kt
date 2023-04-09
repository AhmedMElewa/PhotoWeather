package com.elewa.photoweather.modules.image_view.presentation.ui

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.elewa.photoweather.R
import com.elewa.photoweather.base.BaseFragment
import com.elewa.photoweather.databinding.FragmentImageViewBinding
import com.elewa.photoweather.extentions.loadImages
import com.elewa.photoweather.extentions.share
import com.elewa.photoweather.extentions.toGone
import com.elewa.photoweather.extentions.toVisible
import com.elewa.photoweather.modules.home.presentation.uimodel.HomeSideEffect
import com.elewa.photoweather.modules.home.presentation.uimodel.ImageUiModel
import com.elewa.photoweather.modules.home.presentation.viewmodel.HomeViewModel
import com.elewa.photoweather.modules.image_view.presentation.uimodel.ImageSideEffect
import com.elewa.photoweather.modules.image_view.presentation.viewmodel.ImageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.io.File

@AndroidEntryPoint
class ImageViewFragment : BaseFragment<FragmentImageViewBinding>() {

    private val viewModel: ImageViewModel by viewModels()

    private var currentImage: ImageUiModel? = null

    override val bindLayout: (LayoutInflater, ViewGroup?, Boolean) -> FragmentImageViewBinding
        get() = FragmentImageViewBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var args = ImageViewFragmentArgs.fromBundle(requireArguments())
        currentImage = ImageUiModel(args.imgId, args.imgPath)
        initView()
        initEffectObservation()
    }

    private fun initView() {
        var file = File(currentImage?.imgPath)
        var uri = Uri.fromFile(file)
        binding.imgOpened.loadImages(uri)

        binding.imgShare.setOnClickListener {
            file.share(requireContext(), getString(R.string.share_on))
        }

        binding.imgDelete.setOnClickListener {
            val castedImage = currentImage?.imgPath?.let { it1 -> File(it1) }
            if (castedImage != null && castedImage.exists()) {
                deleteImage()
            }
        }
    }

    private fun initEffectObservation() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uiEffects.collectLatest { effect ->
                when (effect) {
                    is ImageSideEffect.Error -> {
                        Toast.makeText(
                            requireContext(),
                            getString(effect.message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is ImageSideEffect.DeleteImage -> {
                        currentImage = null
                        Toast.makeText(
                            requireContext(),
                            getString(effect.message),
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun deleteImage() {
        val castedImage = currentImage?.imgPath?.let { it1 -> File(it1) }
        if (castedImage != null && castedImage.exists()) {
            castedImage.delete()
            currentImage?.imgId?.toInt()?.let { viewModel.deleteImage(it) }

        }
    }
}