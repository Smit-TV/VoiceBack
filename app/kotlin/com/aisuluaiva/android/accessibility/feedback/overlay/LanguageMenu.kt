package com.aisuluaiva.android.accessibility.feedback.overlay
import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.ArrayAdapter
import com.aisuluaiva.android.accessibility.feedback.R
import com.aisuluaiva.android.accessibility.feedback.speech.TTS
import com.aisuluaiva.android.accessibility.extensions.*

class LanguageMenu(private val context: Context,
private val tts: TTS,
private val menuHandler: Handler = Handler(Looper.getMainLooper())) : Menu(context, R.string.spoken_language, menuHandler) {
init {
try {
val languages = tts.getAvailableLanguages()
val l = mutableListOf<String>()
for (lang in languages) {
l.add("${lang.displayLanguage} (${lang.displayCountry})")
}
val adapter = ArrayAdapter(context, R.layout.menu_item, l)
listView.adapter = adapter
listView.setOnItemClickListener { _, _, position, _ ->
dismiss()
tts.setLanguage(languages[position])

}
} catch (e: Throwable) {}}
}
