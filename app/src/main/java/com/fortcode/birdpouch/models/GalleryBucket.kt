package com.fortcode.birdpouch.models

data class GalleryBucket(
        val id: String,
        val name: String) {

    override fun toString(): String {
        return name
    }
}