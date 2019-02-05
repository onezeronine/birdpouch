package com.fortcode.birdpouch.adapters

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.fortcode.birdpouch.R
import com.fortcode.birdpouch.models.GalleryImage
import com.squareup.picasso.Picasso

class GalleryImagesAdapter(
        private val activity: Activity,
        private val galleryImages: ArrayList<GalleryImage>) : RecyclerView.Adapter<GalleryImagesAdapter.ViewHolder>() {

    private val gridSize: Int = 2
    private var onSelectImageListener: OnSelectImageListener? = null

    interface OnSelectImageListener {
        fun onSelectImage(galleryImage: GalleryImage)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ViewHolder {
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.template_gallery_images, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        val size = gridSize * 1.0
        return Math.ceil(galleryImages.size / size).toInt()
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val leftIndex = position * gridSize
        val rightIndex = position * gridSize + 1

        Picasso.get().load(galleryImages[leftIndex].uriFilePath).fit().centerCrop()
            .into(viewHolder.leftImageView)

        viewHolder.leftImageView.setOnClickListener {
            if (onSelectImageListener != null) {
                onSelectImageListener?.onSelectImage(galleryImages[leftIndex])
            }
        }
        if(rightIndex < galleryImages.size) {
            Picasso.get().load(galleryImages[rightIndex].uriFilePath).fit().centerCrop()
                .into(viewHolder.rightImageView)

            viewHolder.rightImageView.setOnClickListener {
                if (onSelectImageListener != null) {
                    onSelectImageListener?.onSelectImage(galleryImages[rightIndex])
                }
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val leftImageView: ImageView = itemView.findViewById(R.id.gallery_images_item_left_imageview)
        val rightImageView: ImageView = itemView.findViewById(R.id.gallery_images_item_right_imageview)
    }

    internal fun replaceGalleryImages(list: List<GalleryImage>) {
        galleryImages.clear()
        galleryImages.addAll(list)
        notifyDataSetChanged()
    }

    internal fun setOnSelectImageListener(listener: OnSelectImageListener?) {
        onSelectImageListener = listener
    }
}
