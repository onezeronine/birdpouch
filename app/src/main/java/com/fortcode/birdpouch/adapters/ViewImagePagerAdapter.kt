package com.fortcode.birdpouch.adapters

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.fortcode.birdpouch.fragments.ViewImageFragment
import com.fortcode.birdpouch.models.GalleryImage
import com.fortcode.birdpouch.utilities.IntentConstants
import com.google.gson.Gson

class ViewImagePagerAdapter(
        fragmentManager: FragmentManager,
        private val galleryImages: ArrayList<GalleryImage>) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        val image = galleryImages[position]
        val bundle = Bundle().apply { putString(IntentConstants.GALLERY_IMAGE, Gson().toJson(image)) }
        return ViewImageFragment().apply { arguments = bundle }
    }

    override fun getCount(): Int {
        return galleryImages.size
    }

    internal fun getImage(position: Int): GalleryImage? {
        if(position >= galleryImages.size) { return null }
        return galleryImages[position]
    }

    internal fun getImagePosition(img: GalleryImage): Int {
        return galleryImages.indexOf(img)
    }

    internal fun replaceImages(images: List<GalleryImage>) {
        galleryImages.clear()
        galleryImages.addAll(images)
        notifyDataSetChanged()
    }
}