package com.fortcode.birdpouch.models

import android.net.Uri
import java.io.File

data class GalleryImage(
    val id: String,
    val bucketId: String,
    val bucketName: String,
    val displayName: String,
    val imageUri: String,
    val dateAdded: Int
) {
    val uriFilePath: Uri
        get() {
            val file = File(this.imageUri)
            return Uri.fromFile(file)
        }
}
