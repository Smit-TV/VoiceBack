package com.aisuluaiva.android.accessibility.feedback.speech
import android.content.Context
import android.content.SharedPreferences
import android.content.Intent
import android.content.IntentFilter
import android.view.accessibility.AccessibilityEvent
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.speech.tts.UtteranceProgressListener
import android.media.AudioAttributes
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.Locale
import com.aisuluaiva.android.accessibility.extensions.isEditText
import com.aisuluaiva.android.accessibility.feedback.R
import com.aisuluaiva.android.accessibility.feedback.TextEditor
import com.aisuluaiva.android.accessibility.feedback.AppConstants


/**
* Text to speech
*/
class TTS(private val context: Context,
private var initPhrase: CharSequence) : UtteranceProgressListener(), OnInitListener {
private val localBroadcastManager = LocalBroadcastManager.getInstance(context)
private val ttsReceiver = TTSReceiver(context, this)
val prefs = context.getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
var tts = TextToSpeech(context, this,
prefs.getString(AppConstants.PREFS_ENGINE_PACKAGE_STR, null))
private val editor = prefs.edit()
private val progressListeners = mutableMapOf<String, ProgressListener?>()
companion object {
const val QUEUE_ADD = 1
const val QUEUE_FLUSH = 0
const val UTTERANCE_ANNOUNCEMENT = "announcement"
var instance: TTS? = null
}
fun updateConfiguration() {
tts = TextToSpeech(context, this,
prefs.getString(AppConstants.PREFS_ENGINE_PACKAGE_STR, null))
}
init {
localBroadcastManager.registerReceiver(ttsReceiver, IntentFilter(
AppConstants.INTENT_SPOKEN_LANGUAGE))
instance = this
}
override fun onInit(status: Int) {
if (status == TextToSpeech.SUCCESS) {
val isLanguageSupportedStatus = tts.setLanguage(Locale.getDefault())
if (isLanguageSupportedStatus == TextToSpeech.LANG_MISSING_DATA
|| isLanguageSupportedStatus == TextToSpeech.LANG_NOT_SUPPORTED) {
tts.setLanguage(Locale.ENGLISH)
}
} 
val audioAttributes = AudioAttributes.Builder()
.setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
.build()
tts.setAudioAttributes(audioAttributes)
tts.setOnUtteranceProgressListener(this)
tts.setSpeechRate(prefs.getFloat(AppConstants.PREFS_SPEECH_RATE_FLOAT, 2f))

speak(initPhrase)
}
override fun onError(utteranceId: String, errorCode: Int) {}
override fun onError(utteranceId: String) {}
override fun onDone(utteranceId: String) {
progressListeners[utteranceId]?.onDone()
}
override fun onStart(utteranceId: String) {
progressListeners[utteranceId]?.onStart()
}
override fun onStop(utteranceId: String, interrupted: Boolean) {
progressListeners[utteranceId]?.onStop()
}
fun stop() {
tts.stop()
}
fun shutdown() {
progressListeners.clear()
localBroadcastManager.unregisterReceiver(ttsReceiver)
tts.shutdown()
instance = null
}
fun isSpeaking(): Boolean {
return tts.isSpeaking()
}

fun speak(r: Int, queue: Int = QUEUE_FLUSH, utteranceId: String = "simple") {
speak(context.getString(r),
queue, utteranceId)
}
fun speak(text:  CharSequence, queue: Int = TextToSpeech.QUEUE_FLUSH, utteranceId: String = "simple") {
if (queue == TextToSpeech.QUEUE_FLUSH) {
stop()
}
if (text.length == 1) {
val charName = Dictionary.getSymbolDescriptionRes("$text") 
charName?.let {
speak(charName)
return
}
/*if (text == " ") {
speak(R.string.space)
return
} else if (text == "\n") {
speak(R.string.new_line)
return
}*/
}
val stringProcessor = StringProcessor(text, TextToSpeech.getMaxSpeechInputLength(), utteranceId).getStrArray()
for (str in stringProcessor) {
tts.setPitch(str.pitch)
tts.speak(str.text, TextToSpeech.QUEUE_ADD, null, str.utteranceId)
tts.setPitch(1.0f)
}
}
fun addProgressListener(utteranceId: String, lst: ProgressListener) {
progressListeners[utteranceId] = lst
}
fun removeProgressListener(utteranceId: String) {
progressListeners[utteranceId] = null
}
fun speak(event: AccessibilityEvent) {
val compositor = Compositor(context, event)
val label = compositor.getText()
speak(compositor.selectStatus)
if (compositor.node.isEditText && TextEditor.isSelectionEnabled) {
speak(R.string.selection_mode_on)
}
speak(compositor.expandDescription, QUEUE_ADD)
speak(compositor.stateDescription, QUEUE_ADD)
speak(label, QUEUE_ADD)
if (label.isEmpty() && prefs.getBoolean(AppConstants.PREFS_SPEAK_NODE_IDS_BOOL, true)) {
speak(compositor.uniqueId, QUEUE_ADD)
}
speak(compositor.hintText, QUEUE_ADD)
speak(compositor.disableDescription, QUEUE_ADD)
if (prefs.getBoolean(AppConstants.PREFS_SPEAK_NODE_TYPES_BOOL, true)) {
speak(compositor.roleDescription, QUEUE_ADD)
}
if (prefs.getBoolean(AppConstants.PREFS_SPEAK_WINDOW_TITLES_BOOL, true)) {
speak(compositor.window, QUEUE_ADD)
}
}
fun getAvailableLanguages(): List<Locale> {
val voices = tts.getVoices().toMutableList()
val languages = mutableSetOf<Locale>()
for (voice in voices) {
val features = voice.getFeatures() ?: hashSetOf<String>()
if (features.contains(TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED)) {
continue
}
languages.add(voice.getLocale())
}
return languages.toList().sortedBy {
it.displayLanguage
}
}
fun changeLanguage(forward: Boolean) {
val languages = getAvailableLanguages()
if (languages.size < 2) {
speak(R.string.no_items)
return
}
val currentLocale = tts.getVoice().getLocale()
val locales = if (forward) {
languages
} else {
languages.reversed()
}.toMutableList()
val localePosition = locales.indexOf(currentLocale)
locales.remove(currentLocale)
val newLocale = if (locales.size > localePosition) {
locales[localePosition]
} else {
locales[0]
}
setLanguage(newLocale)
}
fun setLanguage(newLocale: Locale) {
tts.setLanguage(newLocale)

speak("${newLocale.displayLanguage} ${newLocale.displayCountry}")
localBroadcastManager.sendBroadcast(Intent(AppConstants.INTENT_SPOKEN_LANGUAGE))
}
fun nextLanguage() {
changeLanguage(true)
}
fun previousLanguage() {
changeLanguage(false)
}
fun changeRate(increase: Boolean) {
val current = prefs.getFloat(AppConstants.PREFS_SPEECH_RATE_FLOAT, 2f)
val new = if (increase) {
current + 0.25f
} else {
current - 0.25f
}
if (new > 10f) {
speak(R.string.maximum)
} else if (new < 0f) {
speak(R.string.minimum)
} else {
editor.putFloat(AppConstants.PREFS_SPEECH_RATE_FLOAT, new).apply()
tts.setSpeechRate(new)
speak("${context.getString(R.string.speech_rate)} ${(new * 10f).toInt()}%")
val intent = Intent(AppConstants.INTENT_SPEECH_RATE_CHANGED)
intent.putExtra(AppConstants.EXTRA_SPEECH_RATE, "${(new * 10f).toInt()}%")
localBroadcastManager.sendBroadcast(intent)
}
}
fun increaseSpeechRate() {
changeRate(true)
}
fun decreaseSpeechRate() {
changeRate(false)
}


}
