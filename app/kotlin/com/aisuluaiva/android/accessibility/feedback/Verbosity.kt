package com.aisuluaiva.android.accessibility.feedback
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ListView
import android.graphics.Color

class Verbosity : Activity() {
private lateinit var prefs: SharedPreferences
private lateinit var editor: SharedPreferences.Editor
companion object {
val constants = arrayOf(AppConstants.PREFS_SPEAK_NODE_TYPES_BOOL,
AppConstants.PREFS_SPEAK_WINDOW_TITLES_BOOL,
AppConstants.PREFS_SPEAK_PASSWORDS_AS_YOU_TYPE_BOOL,
AppConstants.PREFS_SPEAK_PANE_TITLES_BOOL,
AppConstants.PREFS_SPEAK_NODE_IDS_BOOL,
AppConstants.PREFS_ANNOUNCE_TIME_WHEN_SCREEN_TURNS_ON_BOOL,
AppConstants.PREFS_ANNOUNCE_DATE_WHEN_SCREEN_TURNS_ON_BOOL,
AppConstants.PREFS_NOTIFY_WHEN_SCREEN_TURNS_OFF_BOOL,
AppConstants.PREFS_NOTIFY_ABOUT_SCREEN_ORIENTATION_CHANGE_BOOL,
AppConstants.PREFS_SPEAK_BATTERY_LEVEL_BOOL)
val labels = arrayOf(R.string.speak_node_types, R.string.speak_window_titles,
R.string.speak_passwords_as_you_type, R.string.speak_pane_titles,
R.string.speak_node_ids, R.string.announce_time_when_screen_turns_on,
R.string.announce_date_when_screen_turns_on, R.string.notify_when_screen_turns_off,
R.string.notify_about_screen_orientation_change, R.string.speak_battery_level)
}
fun getItemNames(): List<String> {
val items = mutableListOf<String>()
for (label in labels) {
items.add(getString(label))
}
return items
}
override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)
prefs = getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
editor = prefs.edit()
val listView = ListView(this)
val items = object : ArrayAdapter<String>(this, R.layout.checkbox_item, getItemNames()) {
override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
val view = super.getView(position, convertView, parent) as CheckBox
val key = constants[position]
view.isChecked = prefs.getBoolean(key, true)
view.setOnCheckedChangeListener { _, isChecked ->
editor.putBoolean(key, isChecked).apply()
}
return view
}
}


listView.adapter = items
setContentView(listView)
}
}
