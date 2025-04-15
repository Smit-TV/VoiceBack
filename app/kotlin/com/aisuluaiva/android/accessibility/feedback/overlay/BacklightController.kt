package com.aisuluaiva.android.accessibility.feedback.overlay
import android.content.Context
import android.os.Looper
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.graphics.Color
import android.graphics.PixelFormat
import com.aisuluaiva.android.accessibility.feedback.AppConstants
import com.aisuluaiva.android.accessibility.feedback.R
import com.aisuluaiva.android.accessibility.feedback.speech.TTS

class BacklightController(private val context: Context,
private val tts: TTS) {
private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
private val prefs = context.getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
private val view = View(context).apply {
setBackgroundColor(Color.BLACK)
}
private val lp = WindowManager.LayoutParams(
WindowManager.LayoutParams.MATCH_PARENT,
WindowManager.LayoutParams.MATCH_PARENT,
WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE 
or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
or WindowManager.LayoutParams.FLAG_FULLSCREEN
                    or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_DIM_BEHIND,
            PixelFormat.TRANSPARENT)
private val handler = Handler(Looper.getMainLooper())

init {
restore()
}

fun restore() {
tune(prefs.getBoolean(AppConstants.PREFS_BACKLIGHT_IS_ENABLED, true))
}
fun set() {
val current = prefs.getBoolean(AppConstants.PREFS_BACKLIGHT_IS_ENABLED, true)
val newState = current != true
prefs.edit().putBoolean(AppConstants.PREFS_BACKLIGHT_IS_ENABLED, newState).apply()
tune(newState)
}

fun tune(isEnabled: Boolean) {
if (isEnabled) {
try {
windowManager.removeView(view)
} catch (e: Exception) {}
tts.speak(R.string.showing_screen)
return
}
tts.speak(R.string.screen_hidden)
handler.post {
windowManager.addView(view, lp)
}
}
}
