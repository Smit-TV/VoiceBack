package com.aisuluaiva.android.accessibility.feedback
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.SeekBar
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.CheckBox
import android.view.View
import android.view.Gravity
import android.view.LayoutInflater
import android.view.accessibility.AccessibilityEvent
import com.aisuluaiva.android.accessibility.utils.MediaUtils
import com.aisuluaiva.android.accessibility.feedback.FeedbackManager.Companion.eventIds


class SoundAndVibration : Activity() {
private lateinit var prefs: SharedPreferences
private lateinit var editor: SharedPreferences.Editor
private lateinit var layoutInflater: LayoutInflater
override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)
setContentView(R.layout.activity_sound_and_vibration)
prefs = this.getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
editor = prefs.edit()
layoutInflater = LayoutInflater.from(this)
val sound = findViewById<CheckBox>(R.id.sound)
sound.isChecked = prefs.getBoolean(AppConstants.PREFS_SOUND_BOOL, true)
sound.setOnCheckedChangeListener { _, isChecked ->
editor.putBoolean(AppConstants.PREFS_SOUND_BOOL, isChecked).apply()

}

val soundVolume = findViewById<LinearLayout>(R.id.sound_volume)
val volume = findViewById<TextView>(R.id.volume)
volume.text = "${getPercentage(prefs.getFloat(AppConstants.PREFS_SOUND_VOLUME_FLOAT, 1.0f))}%"
soundVolume.setOnClickListener {
val alertDialog = AlertDialog.Builder(this, R.style.activity)
.setTitle(R.string.sound_volume)
.setItems(getResources().getStringArray(R.array.sound_volume)) { dialog, which ->
dialog.dismiss()
val item = when (which) {
0 -> 0.25f
1 -> 0.50f
2 -> 0.75f
else -> 1.0f
}
editor.putFloat(AppConstants.PREFS_SOUND_VOLUME_FLOAT, item).apply()
volume.text = "${getPercentage(prefs.getFloat(AppConstants.PREFS_SOUND_VOLUME_FLOAT, 1.0f))}%"
}
.setNegativeButton(R.string.cancel) { dialog, _ ->
dialog.dismiss()
}
.create()
alertDialog.setOnShowListener {
val cancel = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
cancel.setLayoutParams(LinearLayout.LayoutParams(
LinearLayout.LayoutParams.MATCH_PARENT,
LinearLayout.LayoutParams.WRAP_CONTENT))
}

val window = alertDialog.getWindow()
window?.let {
it.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
LinearLayout.LayoutParams.WRAP_CONTENT)
it.setGravity(Gravity.CENTER)
}
alertDialog.show()
}

val vibration = findViewById<CheckBox>(R.id.vibration)
vibration.isChecked = prefs.getBoolean(AppConstants.PREFS_VIBRATION_BOOL, true)
vibration.setOnCheckedChangeListener { _, isChecked ->
editor.putBoolean(AppConstants.PREFS_VIBRATION_BOOL, isChecked).apply()
}

val soundTheme = findViewById<Button>(R.id.sound_theme)
soundTheme.setOnClickListener {
val dialogView = layoutInflater.inflate(R.layout.dialog_theme, null)
val event = dialogView.findViewById<Spinner>(R.id.event)
val adapter = ArrayAdapter(this, R.layout.menu_item, 
resources.getStringArray(R.array.events))
event.adapter = adapter

val sound = dialogView.findViewById<Spinner>(R.id.resource)
val resourceItems = mutableListOf<String>()
resourceItems.addAll(resources.getStringArray(R.array.theme_resources))
prefs.getString(AppConstants.PREFS_RESOURCES_STRING, null)?.let {
resourceItems.addAll(it.split(":"))
}
val audioFiles = MediaUtils(this).getAudioFiles()
audioFiles?.let {
resourceItems.addAll(it)
}
val resAdapter = ArrayAdapter(this, R.layout.audio_item, resourceItems)
resAdapter.setDropDownViewResource(R.layout.audio_item)
sound.adapter = resAdapter
var currentEvent = -1
val play = dialogView.findViewById<CheckBox>(R.id.play)
var eventKey = "SOUND_${FeedbackManager.eventTypeToString(currentEvent)}"
event.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
val item = eventIds[position]
currentEvent = item
eventKey = "SOUND_${FeedbackManager.eventTypeToString(item)}"
val pref = prefs.getString(eventKey, null) ?: resourceItems[0]
val condition = pref != "\\_/" 
play.isChecked = condition
sound.setEnabled(condition)
val selection = resourceItems.indexOf(pref)
if (selection != -1) {
sound.setSelection(selection)
} else {
sound.setSelection(0)
}
}
override fun onNothingSelected(adapterView: AdapterView<*>) {}
})
val pref = prefs.getString(eventKey, null) ?: resourceItems[0]
val selection = resourceItems.indexOf(pref)
if (selection != -1) {
sound.setSelection(selection)
} else {
sound.setSelection(0)
}

var currentSound = ""
sound.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
val item = resourceItems[position]
currentSound = item
if (position != 0) {
editor.putString(eventKey, currentSound)
} else {
editor.remove(eventKey)
//editor.putString(eventKey, "\\_/")
}
editor.apply()
}
override fun onNothingSelected(adapterView: AdapterView<*>) {}
})


play.isChecked = (prefs.getString(eventKey, null) ?: "") != "\\_/"
sound.setEnabled(play.isChecked)
play.setOnCheckedChangeListener { _, isChecked ->
sound.setEnabled(isChecked)
if (!isChecked) {
editor.putString(eventKey, "\\_/")
} else {
editor.remove(eventKey)
}
editor.apply()
sound.setSelection(0)
}

val dialog = AlertDialog.Builder(this, R.style.activity)
.setTitle(R.string.sound_theme)
.setView(dialogView)
.create()
val close = dialogView.findViewById<Button>(R.id.close)
close.setOnClickListener {
dialog.dismiss()
}
val window = dialog.getWindow()
window?.let {
it.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
LinearLayout.LayoutParams.WRAP_CONTENT)
it.setGravity(Gravity.CENTER)
}
dialog.show()
}

val vibrationTheme = findViewById<Button>(R.id.vibration_theme)
vibrationTheme.setOnClickListener {
val dialogView = layoutInflater.inflate(R.layout.dialog_theme, null)
val soundBar = dialogView.findViewById<LinearLayout>(R.id.sound_bar)
val sound = soundBar.getChildAt(0) as TextView
sound.text = getString(R.string.vibration_intensity)
val vibrationIntensity = SeekBar(this)
vibrationIntensity.setLayoutParams(LinearLayout.LayoutParams(
0,
LinearLayout.LayoutParams.MATCH_PARENT,
0.5f))
soundBar.addView(vibrationIntensity)
val intensityRes = dialogView.findViewById<Spinner>(R.id.resource)
val play = dialogView.findViewById<CheckBox>(R.id.play)
play.text = getString(R.string.disable)


var currentEvent = -1
var eventKey = "VIBRATION_${FeedbackManager.eventTypeToString(currentEvent)}"
val eventItems = resources.getStringArray(R.array.events)
val eventAdapter = ArrayAdapter(this, R.layout.menu_item, eventItems)
val event = dialogView.findViewById<Spinner>(R.id.event)
event.adapter = eventAdapter

event.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
currentEvent = eventIds[position]
eventKey = "VIBRATION_${FeedbackManager.eventTypeToString(currentEvent)}"
val vibrationTime = prefs.getLong(eventKey, FeedbackManager.getIntensityByEventType(currentEvent))
vibrationIntensity.setProgress(vibrationTime.toInt())
intensityRes.setSelection(vibrationTime.toInt())
play.isChecked = vibrationTime < -1
}
override fun onNothingSelected(adapter: AdapterView<*>){}
})

var progress = prefs.getLong(eventKey, FeedbackManager.getIntensityByEventType(currentEvent)).toInt()
if (progress < -1) {
vibrationIntensity.setEnabled(false)
}
vibrationIntensity.setProgress(progress)
vibrationIntensity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
override fun onProgressChanged(seekBar: SeekBar, mProgress: Int, fromUser: Boolean) {
if (!fromUser) {
return 
}
editor.putLong(eventKey, mProgress.toLong()).apply()
progress = mProgress
intensityRes.setSelection(mProgress)
play.isChecked = false
}
override fun onStartTrackingTouch(seekBar: SeekBar) {}
override fun onStopTrackingTouch(seekbar: SeekBar) {}
})
val intensityItems = Array(101) { it.toString() + "%" }
intensityRes.adapter = ArrayAdapter(this, R.layout.menu_item, intensityItems)
if (progress > -1) {
intensityRes.setSelection(progress.toInt())
} else {
intensityRes.setSelection(0)
}
intensityRes.setEnabled(vibrationIntensity.isEnabled)
intensityRes.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
if (progress < -1) {
return
}
editor.putLong(eventKey, position.toLong()).apply()
vibrationIntensity.setProgress(position)
}
override fun onNothingSelected(adapter: AdapterView<*>){}
})
val dialog = AlertDialog.Builder(this, R.style.activity)
.setTitle(R.string.vibration_theme)
.setView(dialogView)
.create()
val close = dialogView.findViewById<Button>(R.id.close)
close.setOnClickListener {
dialog.dismiss()
}


play.isChecked = prefs.getLong(eventKey, FeedbackManager.getIntensityByEventType(currentEvent)) < -1
play.setOnCheckedChangeListener { _, isChecked ->
vibrationIntensity.setEnabled(isChecked != true)
intensityRes.setEnabled(isChecked != true)
if (isChecked) {
editor.putLong(eventKey, -2)
progress = -2
vibrationIntensity.setProgress(0)
intensityRes.setSelection(0)
} else {
editor.remove(eventKey)
progress = 0
}
editor.apply()
} 
val window = dialog.getWindow()
window?.let {
it.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
LinearLayout.LayoutParams.WRAP_CONTENT)
it.setGravity(Gravity.CENTER)
}
dialog.show()

}
}
fun getPercentage(percent: Float): Int {
return (percent * 100.0f).toInt()
}
}

