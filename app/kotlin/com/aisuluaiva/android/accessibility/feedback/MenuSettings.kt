package com.aisuluaiva.android.accessibility.feedback
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.Toast
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.PopupMenu
import android.graphics.Color
import com.aisuluaiva.android.accessibility.feedback.overlay.QuickMenu

class MenuSettings : Activity() {
private lateinit var prefs: SharedPreferences
private lateinit var editor: SharedPreferences.Editor
private var menuType = 0
override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)
setContentView(R.layout.activity_controls)
menuType = intent.getIntExtra("EXTRA_MENU_TYPE", 0)
prefs = getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
editor = prefs.edit()
findViewById<TextView>(R.id.pane_title).apply {
if (menuType == 1) {
text = getString(R.string.customize_menu)
setTitle(text)
}
}
val add = findViewById<Button>(R.id.add)
val controls = findViewById<ListView>(R.id.items)
val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
getNavTypes())
controls.adapter = adapter
controls.setOnItemClickListener { _, _, position, _ ->
val cs = getControls().toMutableList()
if (cs.size > 1) {
cs.removeAt(position)
editor.putString(if (menuType == 0) {
AppConstants.PREFS_NAV_TYPES
} else {
AppConstants.PREFS_QUICK_MENU_ITEMS
}, cs.joinToString(separator=":")).apply()
controls.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, getNavTypes())
}
}
add.setOnClickListener {
val allTypes = (if (menuType == 0) {
SpeedNav.getDefaultTypes()
} else {
QuickMenu.getDefaultItems()
}).split(":").filter {
getControls().indexOf(it.toInt()) == -1
}
if (allTypes.size > 0) {
val popup = PopupMenu(this, add)
for (i in 0 until allTypes.size) {
val t = allTypes[i].toInt()
popup.menu.add(0, t, 0, getString(if (menuType == 0) {
SpeedNav.navToStringR(t)
} else {
QuickMenu.itemToStringR(t)
}))
}
popup.setOnMenuItemClickListener { item ->
val cons = getControls().toMutableList()
cons.add(item.itemId)
editor.putString(if (menuType == 0) {
 AppConstants.PREFS_NAV_TYPES
} else {
AppConstants.PREFS_QUICK_MENU_ITEMS
}, cons.joinToString(":")).apply()
controls.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, getNavTypes())
false
}
popup.show()
} else {
Toast.makeText(this, getString(R.string.no_items), Toast.LENGTH_SHORT).show()
}
}
}
fun getControls(): List<Int> {
return (prefs.getString(if (menuType == 0) {
AppConstants.PREFS_NAV_TYPES
} else {
 AppConstants.PREFS_QUICK_MENU_ITEMS
}, null) ?: if (menuType == 0) {
SpeedNav.getDefaultTypes()
} else {
QuickMenu.getDefaultItems()
}).split(":").map {
it.toInt()
}
}
fun getNavTypes(): List<String> {
val res = getControls()
val list = mutableListOf<String>()
for (r in res) {
val item = if (menuType == 0) {
SpeedNav.navToStringR(r)
} else {
QuickMenu.itemToStringR(r)
}
list.add(getString(item))

}
return list
}
}
