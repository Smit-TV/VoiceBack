package com.aisuluaiva.android.accessibility.feedback
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Build
import android.view.View
import android.view.LayoutInflater
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.Button
import android.widget.TextView
import android.widget.Spinner
import android.widget.PopupMenu
import android.widget.LinearLayout
import android.hardware.display.DisplayManager
import com.aisuluaiva.android.accessibility.feedback.Worker.Companion.actionIds
import com.aisuluaiva.android.accessibility.feedback.FeedbackService.Companion.gestureIds


class MainActivity : Activity() {
private lateinit var layoutInflater: LayoutInflater
private lateinit var prefs: SharedPreferences
private lateinit var editor: SharedPreferences.Editor
private lateinit var displayManager: DisplayManager
override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)
setContentView(R.layout.activity_main)
layoutInflater = LayoutInflater.from(this)
prefs = getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
editor = prefs.edit()
displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
val ttsSettings = findViewById<Button>(R.id.tts_settings)
ttsSettings.setOnClickListener {
startActivity(Intent(this, TTSSettings::class.java))
}

val soundAndVibration = findViewById<Button>(R.id.sound_and_vibration)
soundAndVibration.setOnClickListener {
startActivity(Intent(this, SoundAndVibration::class.java))
}
val gestures = findViewById<Button>(R.id.gestures)
gestures.setOnClickListener {
val dialogView = layoutInflater.inflate(R.layout.dialog_theme, null) as LinearLayout
val soundBar = dialogView.findViewById<LinearLayout>(R.id.sound_bar)
val action = soundBar.getChildAt(0) as TextView
action.text = getString(R.string.action)
val actions = soundBar.getChildAt(1) as Spinner
val actionArray = resources.getStringArray(R.array.actions)
val actionAdapter = ArrayAdapter(this, 
android.R.layout.simple_spinner_item, 
actionArray)
actions.adapter = actionAdapter
val gestureBar = dialogView.findViewById<LinearLayout>(R.id.event_bar)
val gesture = gestureBar.getChildAt(0) as TextView
gesture.text = getString(R.string.gesture)
val gestureAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
resources.getStringArray(R.array.gestures))
val gestures = dialogView.findViewById<Spinner>(R.id.event)
gestures.adapter = gestureAdapter
val display = TextView(this)
display.text = getString(R.string.display)
display.setLayoutParams(LinearLayout.LayoutParams(
0,
LinearLayout.LayoutParams.WRAP_CONTENT,
0.5f))
val displays = Spinner(this)
displays.setLayoutParams(LinearLayout.LayoutParams(
0,
LinearLayout.LayoutParams.WRAP_CONTENT,
0.5f))
val displayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
getDisplays())
displays.adapter = displayAdapter
val displayBar = LinearLayout(this)
displayBar.setLayoutParams(LinearLayout.LayoutParams(
LinearLayout.LayoutParams.MATCH_PARENT,
LinearLayout.LayoutParams.WRAP_CONTENT))
displayBar.setOrientation(LinearLayout.HORIZONTAL)
displayBar.addView(display)
displayBar.addView(displays)
dialogView.addView(displayBar, 0)
val disable = dialogView.findViewById<CheckBox>(R.id.play)
disable.text = getString(R.string.disable)
val displayIds = getDisplayIds()
var currentDisplay = 0 // Display.DEFAULT_DISPLAY
var currentGesture = gestureIds[0]
var currentAction = actionIds[0]
var gestureKey = "GESTURE${currentGesture}DISPLAY$currentDisplay"
val condition = prefs.getInt(gestureKey, Worker.getActionByGesture(currentGesture)) == 0
disable.isChecked = condition
actions.setEnabled(condition != true)
displays.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
currentDisplay = displayIds[position]
gestureKey = "GESTURE${currentGesture}DISPLAY$currentDisplay"
val action = prefs.getInt(gestureKey, Worker.getActionByGesture(currentGesture))
var selectionPosition = actionIds.indexOf(action)
if (selectionPosition == -1) {
selectionPosition = 0
}
actions.setSelection(selectionPosition)
disable.isChecked = action == 0
}
override fun onNothingSelected(adapterView: AdapterView<*>) {}
})
gestures.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
currentGesture = gestureIds[position]
gestureKey = "GESTURE${currentGesture}DISPLAY$currentDisplay"
val action = prefs.getInt(gestureKey, Worker.getActionByGesture(currentGesture))
var selectionPosition = actionIds.indexOf(action)
if (selectionPosition == -1) {
selectionPosition = 0
}
actions.setSelection(selectionPosition)
disable.isChecked = action == 0
}
override fun onNothingSelected(adapterView: AdapterView<*>) {}
})
actions.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
if (!actions.isEnabled) {
return
}
currentAction = actionIds[position]
editor.putInt(gestureKey, currentAction).apply()
}
override fun onNothingSelected(adapterView: AdapterView<*>) {}
})
disable.setOnCheckedChangeListener { _, isChecked ->
actions.setEnabled(isChecked != true)
val action = if (isChecked) {
actions.setSelection(0)
0
} else {
Worker.getActionByGesture(currentGesture)
}
editor.putInt(gestureKey, action).apply()
if (action != 0 && !isChecked) {
actions.setSelection(actionIds.indexOf(action))
}
}


val dialog = AlertDialog.Builder(this, R.style.light_or_night)
.setTitle(R.string.gestures)
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
val focus = findViewById<Button>(R.id.focus)

focus.setOnClickListener {
startActivity(Intent(this, FocusSettings::class.java))
}
if (Build.VERSION.SDK_INT < 31) {
focus.setEnabled(false)
}
val controls = findViewById<Button>(R.id.reading_controls)
controls.setOnClickListener {
startActivity(Intent(this, MenuSettings::class.java))
}
val menu = findViewById<Button>(R.id.menu)
menu.setOnClickListener {
startActivity(Intent(this, MenuSettings::class.java).apply {
putExtra("EXTRA_MENU_TYPE", 1)
})
}
val verbosity = findViewById<Button>(R.id.verbosity)
verbosity.setOnClickListener {
startActivity(Intent(this, Verbosity::class.java))
}
}

fun getDisplays(): List<String> {
val displays = mutableListOf<String>()
for (display in displayManager.displays) {
displays.add(display.name ?: "ID:${display.displayId}")
}
return displays
}
fun getDisplayIds(): List<Int> {
val displays = mutableListOf<Int>()
for (display in displayManager.displays) {
displays.add(display.displayId)
}
return displays
}

}
