package com.aisuluaiva.android.accessibility.feedback
import android.content.BroadcastReceiver
import android.content.Context
import android.content.res.Configuration
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.aisuluaiva.android.accessibility.utils.DateTimeUtils
import com.aisuluaiva.android.accessibility.feedback.speech.TTS

class FeedbackReceiver(private val context: Context,
private val tts: TTS,
private val feedbackManager: FeedbackManager) : BroadcastReceiver() {
companion object {
fun getIntentFilter(): IntentFilter {
return IntentFilter().apply {
addAction(Intent.ACTION_CONFIGURATION_CHANGED)
addAction(Intent.ACTION_POWER_DISCONNECTED)
addAction(Intent.ACTION_POWER_CONNECTED)
addAction(Intent.ACTION_SCREEN_OFF)
addAction(Intent.ACTION_SCREEN_ON)
}
}
}
private val prefs = context.getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
private val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
override fun onReceive(context: Context, intent: Intent) {
when (intent.action) {
Intent.ACTION_CONFIGURATION_CHANGED -> onConfigurationChanged()
Intent.ACTION_POWER_DISCONNECTED,
Intent.ACTION_POWER_CONNECTED -> onPower()
Intent.ACTION_SCREEN_OFF -> screenOff()
Intent.ACTION_SCREEN_ON -> screenOn()
}
}
fun screenOff() {
if (prefs.getBoolean(AppConstants.PREFS_NOTIFY_WHEN_SCREEN_TURNS_OFF_BOOL, true)) {
tts.speak(R.string.screen_off)
}
feedbackManager.onEvent(FeedbackManager.EVENT_SCREEN_OFF)
}
fun screenOn() {
val sb = StringBuilder()
if (prefs.getBoolean(AppConstants.PREFS_ANNOUNCE_TIME_WHEN_SCREEN_TURNS_ON_BOOL, true)) {
sb.append(DateTimeUtils.getTime()).append(" ")
}
if (prefs.getBoolean(AppConstants.PREFS_ANNOUNCE_DATE_WHEN_SCREEN_TURNS_ON_BOOL, true)) {
sb.append(DateTimeUtils.getDate()).append(" ")
}
tts.speak(sb.toString(), TTS.QUEUE_ADD)
feedbackManager.onEvent(FeedbackManager.EVENT_SCREEN_ON)
}
fun onConfigurationChanged() {
if (prefs.getBoolean(AppConstants.PREFS_NOTIFY_ABOUT_SCREEN_ORIENTATION_CHANGE_BOOL, true)) {
val orientation = context.resources.configuration.orientation
if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
tts.speak(R.string.orientation_landscape, TTS.QUEUE_ADD)
} else {
tts.speak(R.string.orientation_portrait, TTS.QUEUE_ADD)
}
}
}
fun onPower() {
if (!prefs.getBoolean(AppConstants.PREFS_SPEAK_BATTERY_LEVEL_BOOL, true)) {
return
}
val percentage = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
tts.speak("$percentage %")
}
}
