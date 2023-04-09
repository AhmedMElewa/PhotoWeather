package com.elewa.photoweather.extentions

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.io.File

fun View.toVisible() {
    this.visibility = View.VISIBLE
}

fun View.toGone() {
    this.visibility = View.GONE
}
fun View.toInvisible() {
    this.visibility = View.INVISIBLE
}

fun ImageView.loadImages(url: Uri)
{
    Glide.with(this)
        .load(url)
        .into(this)
}

fun File.share(context: Context, message: String){
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "image/*"
    val imageUri = Uri.fromFile(this)
    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
    context.startActivity(Intent.createChooser(shareIntent, message))
}

