package com.example.bcsd_android_login.adapter

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.bcsd_android_login.R
import com.example.bcsd_android_login.model.ImageData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImageAdapter() : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    val testList = mutableListOf<ImageData>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.item_image)
        fun bind(imageData: ImageData) {
            Glide.with(itemView.context)
                .load(imageData.contentUri)
                .centerCrop()
                .listener(createLogListener())
                .into(image)
        }
    }

    private fun createLogListener(): RequestListener<Drawable> {
        return object : RequestListener<Drawable> {
            override fun onLoadFailed(  // Image Load 실패 시 CallBack
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady( // Image Load 후 CallBack
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: com.bumptech.glide.load.DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                if (resource is BitmapDrawable) {
                    val bitmap = resource.bitmap
                    Log.d(
                        "Glide", String.format(
                            "bitmap %,d btyes, size: %d x %d",
                            bitmap.byteCount,        // 리사이징된 이미지 바이트
                            bitmap.width,            // 이미지 넓이
                            bitmap.height            // 이미지 높이
                        )
                    )
                }
                return false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_image_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(testList[position])
    }

    override fun getItemCount(): Int {
        return testList.size
    }
}