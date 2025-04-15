package com.aisuluaiva.android.accessibility.utils
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtils {
const val PERMISSION_READ_EXTERNAL_STORAGE = 1
const val PERMISSION_READ_MEDIA_AUDIO = 2
fun checkPermission(context: Context, permission: String): Boolean {
return ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
}
fun getPermission(activity: Activity, permission: String, permissionCode: Int) {
ActivityCompat.requestPermissions(activity,
arrayOf(permission), permissionCode)
}
}
