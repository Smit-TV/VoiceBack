package com.aisuluaiva.android.accessibility.feedback
import android.content.Context
import android.content.SharedPreferences
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.aisuluaiva.android.accessibility.extensions.*
import com.aisuluaiva.android.accessibility.feedback.sound.SoundManager
import com.aisuluaiva.android.accessibility.feedback.vibration.VibrationManager

/**
* Good luck to me!
* File created at Sat. Mar. 22 17:00
*/
class FeedbackManager(private val context: Context) {
private val sm = SoundManager(context)
private val vm = VibrationManager(context)
private val prefs = context.getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
companion object {
const val EVENT_SERVICE_STARTED = -1
const val EVENT_SERVICE_STOPPED = -2
const val EVENT_SUPER_NODE_FOCUSED = -3
const val EVENT_PLAIN_NODE_FOCUSED = -4
const val EVENT_NOT_FOCUSABLE_NODE = -5
const val EVENT_TRAVERSAL_END = -6
const val EVENT_VOLUME_LIMIT = -7
const val EVENT_TEXT_END = -8
const val EVENT_SYSTEM_GESTURE_DETECTION_START = -9
const val EVENT_SYSTEM_GESTURE_DETECTION_END = -10
const val EVENT_NAV_TYPE_CHANGED = -11
const val EVENT_TEXT_DELETED = -12
const val EVENT_NOT_EDITABLE = -13
const val EVENT_SCREEN_ON = -14
const val EVENT_SCREEN_OFF = -15
val eventIds = listOf(-1, -2, -3, -4, -5, -6,
AccessibilityEvent.TYPE_ANNOUNCEMENT, AccessibilityEvent.TYPE_VIEW_SCROLLED,
AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED, -7, -8, -9, -10,
AccessibilityEvent.TYPE_VIEW_CLICKED, AccessibilityEvent.TYPE_VIEW_LONG_CLICKED,
-11, AccessibilityEvent.TYPE_WINDOWS_CHANGED, -12, -13,
-14, -15)
const val TAG = "FeedbackManager"

fun getIntensityByEventType(eventType: Int): Long {
return when (eventType) {
EVENT_PLAIN_NODE_FOCUSED -> 13
EVENT_NOT_FOCUSABLE_NODE,  
EVENT_TEXT_DELETED,
EVENT_SYSTEM_GESTURE_DETECTION_END -> 0
else -> 21
}
}

/**
* Get event type by constant value
*/
fun eventTypeToString(eventType: Int): String {
return when (eventType) {
EVENT_SERVICE_STARTED -> "EVENT_SERVICE_STARTED"
EVENT_SERVICE_STOPPED -> "EVENT_SERVICE_STOPPED"
EVENT_SUPER_NODE_FOCUSED -> "EVENT_SUPER_NODE_FOCUSED"
EVENT_PLAIN_NODE_FOCUSED -> "EVENT_PLAIN_NODE_FOCUSED"
EVENT_NOT_FOCUSABLE_NODE -> "EVENT_NOT_FOCUSABLE_NODE"
EVENT_TRAVERSAL_END -> "EVENT_TRAVERSAL_END"
EVENT_VOLUME_LIMIT -> "EVENT_VOLUME_LIMIT"
EVENT_TEXT_END -> "EVENT_TEXT_END"
EVENT_SYSTEM_GESTURE_DETECTION_END -> "EVENT_SYSTEM_GESTURE_DETECTION_END"
EVENT_SYSTEM_GESTURE_DETECTION_START -> "EVENT_SYSTEM_GESTURE_DETECTION_START"
EVENT_NAV_TYPE_CHANGED -> "EVENT_NAV_TYPE_CHANGED"
EVENT_TEXT_DELETED -> "EVENT_TEXT_DELETED"
EVENT_NOT_EDITABLE -> "EVENT_NOT_EDITABLE"
EVENT_SCREEN_ON -> "EVENT_SCREEN_ON"
EVENT_SCREEN_OFF -> "EVENT_SCREEN_OFF"
else -> AccessibilityEvent.eventTypeToString(eventType)
}
}
/**
* Get sound by event type
*/
fun getSoundByEventType(eventType: Int): Int? {
return when (eventType) {
EVENT_SERVICE_STARTED -> R.raw.service_enabled
EVENT_SERVICE_STOPPED -> R.raw.service_disabled
EVENT_PLAIN_NODE_FOCUSED -> R.raw.focus2
EVENT_SUPER_NODE_FOCUSED -> R.raw.focus1
EVENT_NOT_FOCUSABLE_NODE -> R.raw.bad_focus
EVENT_TRAVERSAL_END,
EVENT_TEXT_END,
EVENT_NOT_EDITABLE,
EVENT_VOLUME_LIMIT -> R.raw.volume_limit
EVENT_SYSTEM_GESTURE_DETECTION_END -> R.raw.system_gesture_detection_end
EVENT_SYSTEM_GESTURE_DETECTION_START -> R.raw.system_gesture_detection_start
EVENT_NAV_TYPE_CHANGED -> R.raw.change_nav_type
EVENT_TEXT_DELETED -> R.raw.text_deleted
EVENT_SCREEN_ON -> R.raw.screen_on
EVENT_SCREEN_OFF -> R.raw.screen_off
AccessibilityEvent.TYPE_ANNOUNCEMENT -> R.raw.announcement
AccessibilityEvent.TYPE_VIEW_CLICKED -> R.raw.clicked
AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> R.raw.long_clicked
AccessibilityEvent.TYPE_VIEW_SCROLLED, 
AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> R.raw.scrolled
AccessibilityEvent.TYPE_WINDOWS_CHANGED -> R.raw.windows_changed
else -> null
}
}
}

/**
* Reacts to event and processes it
*/
fun onEvent(eventType: Int, rate: Float = 1.0f) {
val soundIsOn = prefs.getBoolean(AppConstants.PREFS_SOUND_BOOL, true) 
if (soundIsOn) {
val sound = prefs.getString("SOUND_${eventTypeToString(eventType)}", null) ?: getSoundByEventType(eventType)
sound?.let {
sm.play(it,
prefs.getFloat(AppConstants.PREFS_SOUND_VOLUME_FLOAT, 1.0f), 
rate)
}
}
val vibrationIsOn = prefs.getBoolean(AppConstants.PREFS_VIBRATION_BOOL, true)
if (!vibrationIsOn) {
return
}
val time = prefs.getLong("VIBRATION_${eventTypeToString(eventType)}", getIntensityByEventType(eventType))
vm.vibrate(time)
}
fun onScrolled(eventType: Int, currentItem: Int, itemCount: Int) {
if (itemCount == -1 || currentItem == -1) {
return
}
val percent = currentItem.toFloat() / itemCount
val rate = 0.1f + (percent * (2.0f - 0.1f)) 
onEvent(eventType, rate.toFloat())
}
fun onA11yFocused(node: AccessibilityNodeInfo) {
val event = if (node.hasFocusable || node.hasAnyClickable || node.isSeekBar) {
EVENT_SUPER_NODE_FOCUSED
}  else {
EVENT_PLAIN_NODE_FOCUSED
}
onEvent(event)
}
fun recycle() {
sm.recycle()
}
}
