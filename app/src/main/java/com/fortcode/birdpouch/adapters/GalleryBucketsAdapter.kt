package com.fortcode.birdpouch.adapters

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.fortcode.birdpouch.R
import com.fortcode.birdpouch.models.GalleryBucket

class GalleryBucketsAdapter(
        private val activity: Activity,
        private val galleryBuckets: List<GalleryBucket>) : RecyclerView.Adapter<GalleryBucketsAdapter.ViewHolder>() {

    private var onSelectGalleryBucketListener: OnSelectGalleryBucketListener? = null

    interface OnSelectGalleryBucketListener {
        fun onSelectGalleryBucket(galleryBucket: GalleryBucket)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ViewHolder {
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.template_gallery_buckets, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return galleryBuckets.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val bucket = galleryBuckets[position]
        viewHolder.nameTextView.text = bucket.name
        viewHolder.bucketLayout.setOnClickListener {
            onSelectGalleryBucketListener?.onSelectGalleryBucket(bucket)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.gallery_bucket_item_name)
        val bucketLayout: LinearLayout = itemView.findViewById(R.id.gallery_bucket_item_layout)
    }

    internal fun setOnSelectGalleryBucketListener(listener: OnSelectGalleryBucketListener?) {
        onSelectGalleryBucketListener = listener
    }
}
