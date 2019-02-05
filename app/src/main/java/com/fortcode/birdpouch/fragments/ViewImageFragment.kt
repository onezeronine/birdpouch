package com.fortcode.birdpouch.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.fortcode.birdpouch.R
import com.fortcode.birdpouch.models.GalleryImage
import com.fortcode.birdpouch.utilities.IntentConstants
import com.google.gson.Gson
import com.squareup.picasso.Picasso

class ViewImageFragment : Fragment() {
    private lateinit var fullScreenImageView: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_full_screen_image, container, false)

        if(activity == null) { return view }

        val imageAsJson = arguments?.getString(IntentConstants.GALLERY_IMAGE)
        val image = Gson().fromJson(imageAsJson, GalleryImage::class.java)

        fullScreenImageView = view.findViewById(R.id.full_screen_imageview)
        Picasso.get().load(image.uriFilePath).into(fullScreenImageView)

        return view
    }
}