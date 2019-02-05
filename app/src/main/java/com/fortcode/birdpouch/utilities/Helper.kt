package com.fortcode.birdpouch.utilities

import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE

object Helper {
    fun isStoragePermissionGranted(activity: Activity, requestCode: Int): Boolean {
        if(activity.checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true
        }

        ActivityCompat.requestPermissions(activity, arrayOf(WRITE_EXTERNAL_STORAGE), requestCode)
        return false
    }
}
