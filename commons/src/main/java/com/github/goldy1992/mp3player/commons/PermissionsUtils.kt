package com.github.goldy1992.mp3player.commons

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

object PermissionsUtils {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val TIRAMISU_PERMISSIONS =  arrayOf(
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.READ_MEDIA_AUDIO,
        Manifest.permission.READ_MEDIA_IMAGES)

    private val STANDARD_PERMISSIONS = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    fun getAppPermissions() : Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            TIRAMISU_PERMISSIONS
        } else {
            STANDARD_PERMISSIONS
        }
    }

    fun hasPermission(permission : String, context : Context) : Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun appHasPermissions(context: Context) : Boolean {
        val permissions = getAppPermissions()
        for (permission in permissions) {
            if (!hasPermission(permission, context)) {
                return false
            }
        }
        return true
    }
}