package com.aisuluaiva.android.accessibility.feedback.vibration
import android.content.Context
import android.os.Vibrator
import android.os.VibrationEffect
import android.util.Log

/**
* Successful code to me!
* Created: Sat. Mar. 22 at 17:34
* 
* Responsible for vibration
*/
class VibrationManager(private val context: Context) {
private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
companion object {
const val TAG = "VibrationManager"
}
fun vibrate(time: Long) {
if (vibrator.hasVibrator() && time > 0) {
val effect = VibrationEffect.createOneShot(time, 157)
//VibrationEffect.DEFAULT_AMPLITUDE)
vibrator.vibrate(effect)
} else {
Log.i(TAG, "Device hasn't got a vibrator.")
}
}
fun cancel() {
vibrator.cancel()
}
}
