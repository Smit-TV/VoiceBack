package com.aisuluaiva.android.accessibility.feedback.speech
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.aisuluaiva.android.accessibility.feedback.AppConstants
import java.util.Locale

class TTSReceiver(private val context: Context,
private val tts: TTS) : BroadcastReceiver() {
override fun onReceive(context: Context, intent: Intent) {
when (intent.action) {
AppConstants.INTENT_SPOKEN_LANGUAGE -> languageChanged(intent)
}
}
fun languageChanged(intent: Intent) {
tts.setLanguage(Locale(
intent.getStringExtra(AppConstants.EXTRA_LANGUAGE) ?: return))
}
}
