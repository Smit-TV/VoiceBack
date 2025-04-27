package com.aisuluaiva.android.accessibility.feedback
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Build
import android.view.Menu
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.PopupMenu
import android.widget.CheckBox
import android.graphics.Color

class FocusSettings : Activity() {
private lateinit var prefs: SharedPreferences
private lateinit var editor: SharedPreferences.Editor
override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)
setContentView(R.layout.activity_focus)
prefs = getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
editor = prefs.edit()
val focusColor = findViewById<LinearLayout>(R.id.focus_color)
val color = findViewById<TextView>(R.id.color)
color.text = getFocusColorName()
val focusThickness = findViewById<LinearLayout>(R.id.focus_thickness)
val thickness = findViewById<TextView>(R.id.thickness)
thickness.text = "${prefs.getInt(AppConstants.PREFS_FOCUS_THICKNESS_INT, 20)}%"
val sizes = (0..100).toList()
focusThickness.setOnClickListener {
val popup = PopupMenu(this, thickness)
for (i in 0..100) {
popup.menu.add(0, i, 0, i.toString())
}
popup.setOnMenuItemClickListener { menuItem ->
editor.putInt(AppConstants.PREFS_FOCUS_THICKNESS_INT, sizes[menuItem.getItemId()]).apply()

if (Build.VERSION.SDK_INT > 30) {
FeedbackService?.instance?.setAccessibilityFocusAppearance(prefs.getInt(AppConstants.PREFS_FOCUS_THICKNESS_INT, 20),
prefs.getInt(AppConstants.PREFS_FOCUS_COLOR_INT, Color.BLUE))
}
thickness.text = "${prefs.getInt(AppConstants.PREFS_FOCUS_THICKNESS_INT, 20)}%"
false
}
popup.show()
}
val colors = arrayOf(Color.BLUE,
Color.YELLOW, Color.GRAY, Color.RED, Color.GREEN, Color.MAGENTA, 
Color.WHITE, Color.BLACK)
focusColor.setOnClickListener {
val popup = PopupMenu(this, color)
for (i in 0 until colors.size) {
popup.menu.add(0, i, 0, 
getString(getColorR(colors[i])))
}

popup.setOnMenuItemClickListener { menuItem ->
val c = colors[menuItem.getItemId()]
editor.putInt(AppConstants.PREFS_FOCUS_COLOR_INT, c).apply()

if (Build.VERSION.SDK_INT > 30) {
FeedbackService?.instance?.setAccessibilityFocusAppearance(prefs.getInt(AppConstants.PREFS_FOCUS_THICKNESS_INT, 20),
prefs.getInt(AppConstants.PREFS_FOCUS_COLOR_INT, Color.BLUE))
}
color.text = getFocusColorName()
false
}
popup.show()
}
val moveToAnotherWindow = findViewById<CheckBox>(R.id.move_to_another_window)
moveToAnotherWindow.isChecked = prefs.getBoolean(AppConstants.PREFS_MOVE_TO_ANOTHER_WINDOW_BOOL, true)
moveToAnotherWindow.setOnCheckedChangeListener { _, isChecked ->
editor.putBoolean(AppConstants.PREFS_MOVE_TO_ANOTHER_WINDOW_BOOL, isChecked).apply()
}
}
fun getFocusColorName(): String {
val r = prefs.getInt(AppConstants.PREFS_FOCUS_COLOR_INT, Color.BLUE)
return getString(getColorR(r))
}
fun getColorR(r: Int): Int {
return when (r) {
Color.BLUE -> R.string.color_blue
Color.YELLOW -> R.string.color_yellow
Color.GREEN -> R.string.color_green
Color.RED -> R.string.color_red
Color.GRAY -> R.string.color_grey
Color.BLACK -> R.string.color_black
Color.MAGENTA -> R.string.color_magenta
Color.WHITE -> R.string.color_white
else -> R.string.unknown_error
}
}
}
