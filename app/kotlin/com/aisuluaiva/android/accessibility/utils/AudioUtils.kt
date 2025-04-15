package com.aisuluaiva.android.accessibility.utils
import android.os.SystemClock
import android.content.Context
import android.media.AudioManager
import android.media.AudioDeviceInfo
import android.view.KeyEvent
import com.aisuluaiva.android.accessibility.feedback.FeedbackManager

class AudioUtils(private val context: Context,
private val feedbackManager: FeedbackManager) {
companion object {
const val TAG = "AudioUtils"
const val ACTION_VOLUME_UP = true
const val ACTION_VOLUME_DOWN = false
}
private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
fun getFinalStream(stream: Int): Int {
if (stream == AudioManager.STREAM_ACCESSIBILITY && (isHeadphonesConnected() || 
audioManager.isWiredHeadsetOn)) {
return AudioManager.STREAM_MUSIC
}
return stream
}
fun isHeadphonesConnected(): Boolean {
val devs = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
for (dev in devs) {
if (dev.type == 
AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
dev.type == 
AudioDeviceInfo.TYPE_WIRED_HEADSET) {
return true
}
}
return false
}
fun setVolume(prefStream: Int, action: Boolean) {
val stream = getFinalStream(prefStream)
val maxVolume = audioManager.getStreamMaxVolume(stream)
val currentVolume = audioManager.getStreamVolume(stream)
val newVolume = if (action == ACTION_VOLUME_UP) {
currentVolume + 1
} else {
currentVolume - 1
}
if (newVolume <= maxVolume &&
newVolume > 0) {
audioManager.setStreamVolume(stream, newVolume, 0)
} else {
feedbackManager.onEvent(FeedbackManager.EVENT_VOLUME_LIMIT)
}
}
fun increaseSpeakerVolume() {
setVolume(AudioManager.STREAM_ACCESSIBILITY, ACTION_VOLUME_UP)
}
fun decreaseSpeakerVolume() {
setVolume(AudioManager.STREAM_ACCESSIBILITY, ACTION_VOLUME_DOWN)
}

fun sendMediaKeyEvent(keyCode: Int) {
val actions = listOf(KeyEvent.ACTION_DOWN,
KeyEvent.ACTION_UP)
for (action in actions) {
val time = SystemClock.uptimeMillis()
val event = KeyEvent(time, time, action, keyCode, 0)
audioManager.dispatchMediaKeyEvent(event)
}
}
fun nextTrack() {
sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_NEXT)
}
fun previousTrack() {
sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS)
}
}
