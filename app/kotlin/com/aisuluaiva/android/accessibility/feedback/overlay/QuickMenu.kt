package com.aisuluaiva.android.accessibility.feedback.overlay
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.ArrayAdapter
import android.util.Log
import com.aisuluaiva.android.accessibility.feedback.R
import com.aisuluaiva.android.accessibility.feedback.AppConstants
import com.aisuluaiva.android.accessibility.feedback.Worker
import com.aisuluaiva.android.accessibility.feedback.MainActivity
import com.aisuluaiva.android.accessibility.feedback.TTSSettings
import com.aisuluaiva.android.accessibility.extensions.*

class QuickMenu(private val context: Context,
private val worker: Worker) : Menu(context, R.string.quick_menu) {
private val prefs = context.getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
init {
listView.setOnItemClickListener { _, _, position, _ ->
dismiss()
focusedNode?.performFocus()
Thread {
Thread.sleep(250)
try {
when (getItemIds()[position]) {
ITEM_BACKLIGHT -> worker.work(Worker.ACTION_SET_BACKLIGHT)
ITEM_TTS -> context.startActivity(Intent(context, TTSSettings::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
ITEM_APP -> context.startActivity(Intent(context, MainActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
ITEM_TAP -> worker.work(Worker.ACTION_TAP, displayId)
ITEM_TAP_AND_HOLD -> worker.work(Worker.ACTION_LONG_TAP, displayId)
ITEM_PASS_THROUGH_SYSTEM_GESTURE -> worker.work(Worker.ACTION_PASS_THROUGH_SYSTEM_GESTURE, displayId)
ITEM_LANGUAGE -> LanguageMenu(context, worker.tts, super.handler).show(displayId)
ITEM_ACTIONS -> ActionsMenu(context, focusedNode ?: throw Exception()).show(displayId)
ITEM_LINKS -> LinksMenu(context, focusedNode ?: throw Exception()).show(displayId)
}
} catch (e: Exception) {
Log.e("Menu", "$e")
}
}.start()
}
}
companion object {
fun getDefaultItems(): String {
return "0:1:2:3:4:5:6:7:8"
}
const val ITEM_BACKLIGHT = 0
const val ITEM_TTS = 1
const val ITEM_APP = 2
const val ITEM_TAP = 3
const val ITEM_TAP_AND_HOLD = 4
const val ITEM_PASS_THROUGH_SYSTEM_GESTURE = 5
const val ITEM_LANGUAGE = 6
const val ITEM_LINKS = 7
const val ITEM_ACTIONS = 8
fun itemToStringR(r: Int): Int {
return when (r) {
ITEM_BACKLIGHT -> R.string.screen_backlight
ITEM_TTS -> R.string.tts_settings
ITEM_APP -> R.string.app_settings
ITEM_TAP -> R.string.tap
ITEM_TAP_AND_HOLD -> R.string.tap_and_hold
ITEM_PASS_THROUGH_SYSTEM_GESTURE -> R.string.pass_through_system_gesture
ITEM_LANGUAGE -> R.string.spoken_language
ITEM_ACTIONS -> R.string.actions
ITEM_LINKS -> R.string.links
else -> R.string.unknown_error
}
}
fun getItemName(context: Context, item: Int): String {
return context.getString(itemToStringR(item))
}
}
fun getItemIds(): List<Int> {
val items = (prefs.getString(AppConstants.PREFS_QUICK_MENU_ITEMS, null) ?: getDefaultItems()).split(":").map {
it.toInt()
}.toMutableList()
if (focusedNode == null) {
items.remove(ITEM_TAP)
items.remove(ITEM_TAP_AND_HOLD)
items.remove(ITEM_ACTIONS)
items.remove(ITEM_LINKS)
} else {
if (LinksMenu.getClickableSpans(focusedNode ?: throw Exception("Focused node is null")).isEmpty()) {
items.remove(ITEM_LINKS)
}
if (ActionsMenu.getActions(focusedNode ?: throw Exception("Focused node is null")).isEmpty()) {
items.remove(ITEM_ACTIONS)
}
}
return items
}
fun getItems(): List<String> {
return (getItemIds().map {
if (it != ITEM_BACKLIGHT) {
getItemName(context, it)
} else if (prefs.getBoolean(AppConstants.PREFS_BACKLIGHT_IS_ENABLED, true)) {
context.getString(R.string.action_hide_screen)
} else {
context.getString(R.string.action_show_screen)
}
})
}
var focusedNode: AccessibilityNodeInfo? = null
override fun show(displayId: Int) {

focusedNode = worker.service.findFocus(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY)

val adapter = ArrayAdapter(context, R.layout.menu_item, getItems())
listView.adapter = adapter
super.show(displayId)
}
}
