package com.elewa.photoweather.modules.image_view.presentation.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.elewa.photoweather.R
import com.elewa.photoweather.base.BaseFragment
import com.elewa.photoweather.databinding.FragmentImageViewBinding
import com.elewa.photoweather.extentions.loadImages
import com.elewa.photoweather.extentions.share
import com.elewa.photoweather.modules.home.presentation.uimodel.ImageUiModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ImageViewFragment : BaseFragment<FragmentImageViewBinding>() {

    private var currentImage: ImageUiModel? = null

    override val bindLayout: (LayoutInflater, ViewGroup?, Boolean) -> FragmentImageViewBinding
        get() = FragmentImageViewBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var args = ImageViewFragmentArgs.fromBundle(requireArguments())
        currentImage = ImageUiModel(args.imgId, args.imgPath)
        initView()
    }

    private fun initView() {
        var file = File(currentImage?.imgPath)
        var uri = Uri.fromFile(file)
        binding.imgOpened.loadImages(uri)

        binding.imgShare.setOnClickListener {
            file.share(requireContext(), getString(R.string.share_on))
        }
    }
}