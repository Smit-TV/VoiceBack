package com.aisuluaiva.android.accessibility.utils
import android.content.Context
import android.util.Log
import android.content.pm.PackageManager

object PackageUtils {
const val LOG_TAG = "PackageUtils"
fun getAppName(cxt: Context, packageName: CharSequence): CharSequence? {
return try {
val packageManager = cxt.packageManager
val info = packageManager.getApplicationInfo("${packageName}", PackageManager.GET_META_DATA)
packageManager.getApplicationLabel(info)
} catch (e: PackageManager.NameNotFoundException) {
Log.e(LOG_TAG, "Can't access from getAppName")
null
}
}
}
