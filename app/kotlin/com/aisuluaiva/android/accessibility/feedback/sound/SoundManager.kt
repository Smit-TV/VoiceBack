package com.aisuluaiva.android.accessibility.feedback.sound
import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log

/**
* Successful code to me!
*  Created: Sat. Mar. 22 2025 18:57
*/
class SoundManager(private val context: Context) {
private val audioAttributes = AudioAttributes.Builder()
.setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
.build()
private val soundPool = SoundPool.Builder()
.setMaxStreams(10)
.setAudioAttributes(audioAttributes)
.build()
fun loadFromRaw(resourceId: Int): Int {
return soundPool.load(context, resourceId, 1) ?: 0
}
fun load(soundPath: String): Int {
return soundPool.load(soundPath, 1) ?: 0
}
private val sounds = mutableMapOf<Any, Int>()
fun play(sound: Any, volume: Float = 1.0f, rate: Float = 1.0f) {
if (sounds[sound] == null) {
sounds[sound] = when (sound) {
is Int -> loadFromRaw(sound)
is String -> load(sound)
else -> return
}
soundPool.setOnLoadCompleteListener { _, id, status ->
if (status == 0) {
soundPool.play(id, volume, volume, 1, 0, rate)
} else {
Log.e(TAG, "Fail to load sound!")
}
}
} else {
soundPool.play(sounds[sound] ?: return, volume, volume, 1, 0, rate)
}
}
fun recycle() {
sounds.clear()
}
companion object {
const val TAG = "SoundPlayer"
}
}
