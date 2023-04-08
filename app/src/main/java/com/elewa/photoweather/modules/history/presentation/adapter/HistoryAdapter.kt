package com.elewa.photoweather.modules.history.presentation.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.elewa.photoweather.databinding.ItemImageViewBinding
import com.elewa.photoweather.extentions.loadImages
import com.elewa.photoweather.modules.home.presentation.uimodel.ImageUiModel
import java.io.File

class HistoryAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<ImageUiModel, HistoryAdapter.MyViewHolder>(MyDiffUtil) {

    companion object MyDiffUtil : DiffUtil.ItemCallback<ImageUiModel>() {
        override fun areItemsTheSame(oldItem: ImageUiModel, newItem: ImageUiModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ImageUiModel, newItem: ImageUiModel): Boolean {
            return oldItem.imgId == newItem.imgId
        }
    }

    inner class MyViewHolder(private val binding: ItemImageViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ImageUiModel?) {
            val file: File = File(item?.imgPath)
            var uri :Uri = Uri.fromFile(file)
            binding.imgCaptured.loadImages(uri)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemImageViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(item)
        }
        holder.bind(item)
    }

    class OnClickListener(val clickListener: (item: ImageUiModel) -> Unit) {
        fun onClick(item: ImageUiModel) = clickListener(item)
    }
}