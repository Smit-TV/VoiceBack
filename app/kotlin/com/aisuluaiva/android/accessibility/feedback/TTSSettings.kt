package com.aisuluaiva.android.accessibility.feedback
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.SharedPreferences
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.PopupMenu
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.aisuluaiva.android.accessibility.feedback.speech.TTS

 class TTSSettings : Activity(), TextToSpeech.OnInitListener {
private lateinit var prefs: SharedPreferences
private lateinit var editor: SharedPreferences.Editor
private lateinit var tts: TextToSpeech
private lateinit var engine: TextView
private lateinit var rate: TextView
private val receiver = object : BroadcastReceiver() {
override fun onReceive(context: Context, intent: Intent) {
rate.text = intent.getStringExtra(AppConstants.EXTRA_SPEECH_RATE) ?: getCurrentSpeechRate()
}
}
private lateinit var localBroadcastManager: LocalBroadcastManager
override fun onStart() {
super.onStart()
localBroadcastManager = LocalBroadcastManager.getInstance(this)
localBroadcastManager.registerReceiver(receiver, IntentFilter(AppConstants.INTENT_SPEECH_RATE_CHANGED))
}
override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)
setContentView(R.layout.activity_tts)
prefs = getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
editor = prefs.edit()
tts = TextToSpeech(this, this,
prefs.getString(AppConstants.PREFS_ENGINE_PACKAGE_STR, null))
}
override fun onInit(status: Int) {
val engineList = tts.getEngines()
val engines = findViewById<ListView>(R.id.list_view)
val currentEngine = findViewById<LinearLayout>(R.id.current_engine)
engine = currentEngine.findViewById<TextView>(R.id.engine)
engine.text = getCurrentEngineName()
currentEngine.setOnClickListener {
val engines = tts.getEngines()
val popup = PopupMenu(this, engine)
for (i in 0 until engines.size) {
val engine = engines[i]
popup.menu.add(0, i, 0, engine.label ?: engine.name)
}
popup.setOnMenuItemClickListener { item ->
editor.putString(AppConstants.PREFS_ENGINE_PACKAGE_STR, engines[item.getItemId()].name).apply()
engine.text = getCurrentEngineName()
TTS.instance?.updateConfiguration()
false
}
popup.show()
}
val speechRate = findViewById<LinearLayout>(R.id.speech_rate)
rate = speechRate.findViewById<TextView>(R.id.rate)
rate.text = getCurrentSpeechRate()
speechRate.setOnClickListener {
val popup = PopupMenu(this, rate)
for (i in 0..100) {
popup.menu.add(0, i, 0, "$i%")
}
popup.setOnMenuItemClickListener { item ->
val r = item.getItemId() * 10f / 100
editor.putFloat(AppConstants.PREFS_SPEECH_RATE_FLOAT, r).apply()
rate.text = getCurrentSpeechRate()
TTS.instance?.tts?.setSpeechRate(r)
false
}
popup.show()
}
val spokenLanguage = findViewById<ListView>(R.id.languages)
val languages = mutableListOf<String>()
val locales = mutableListOf<String>()
for (lang in tts.getAvailableLanguages()) {
languages.add("${lang.displayLanguage} (${lang.displayCountry})")
locales.add("${lang.getLanguage()}-${lang.getCountry()}")
}
spokenLanguage.adapter = ArrayAdapter(this, R.layout.menu_item, languages)
spokenLanguage.setOnItemClickListener { _, _, position, _ ->
val intent = Intent(AppConstants.INTENT_SPOKEN_LANGUAGE)
intent.putExtra(AppConstants.EXTRA_LANGUAGE, locales[position])
localBroadcastManager.sendBroadcast(intent)
}
}
fun getCurrentSpeechRate(): String {
val rate = prefs.getFloat(AppConstants.PREFS_SPEECH_RATE_FLOAT, 2f)
return "${(rate * 10).toInt()}%"
}
fun getCurrentEngine(): TextToSpeech.EngineInfo {
val packageName = prefs.getString(AppConstants.PREFS_ENGINE_PACKAGE_STR, tts.getDefaultEngine())
var currentEngine = tts.getEngines()[0]
for (engine in tts.getEngines()) {
if (engine.name != packageName) {
continue
}
currentEngine = engine
}
return currentEngine
}
fun getCurrentEngineName(): String {
val engine = getCurrentEngine()
 return engine.label ?: engine.name
}
override fun onStop() {
super.onStop()
localBroadcastManager.unregisterReceiver(receiver)
}
override fun onDestroy() {
super.onDestroy()
tts.shutdown()
}
}
