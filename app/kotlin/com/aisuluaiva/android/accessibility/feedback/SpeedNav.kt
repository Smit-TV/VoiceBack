package com.aisuluaiva.android.accessibility.feedback
import android.accessibilityservice.AccessibilityService
import android.content.SharedPreferences
import android.os.Bundle
import android.view.accessibility.AccessibilityNodeInfo
import android.text.style.ClickableSpan
import com.aisuluaiva.android.accessibility.extensions.*
import com.aisuluaiva.android.accessibility.feedback.speech.TTS
import com.aisuluaiva.android.accessibility.feedback.speech.Compositor

class SpeedNav(private val worker: Worker,
private val prefs: SharedPreferences,
private val tts: TTS) {
private val service = worker.service
private val doubleTapNav = DoubleTapNav(service, tts)
private val feedbackManager = worker.feedbackManager
init {
instance = this
service.addGestureListener { gesture ->
if (gesture == AccessibilityService.GESTURE_DOUBLE_TAP) {
when (currentNavType) {
NAV_LINKS -> doubleTapNav.onClickLink()
NAV_ACTIONS -> doubleTapNav.onAction()
}
}
}
}
companion object {
var instance: SpeedNav? = null
fun reset() {
instance?.reset()
}
const val NAV_MOVE_FOCUS = 0
const val NAV_BACKLIGHT = 1
const val NAV_VOICEBACK_VOLUME = 2
const val NAV_CHARACTERS = 3
const val NAV_WORDS = 4
const val NAV_PARAGRAPHS = 5
const val NAV_SLIDER = 6
const val NAV_LANGUAGE = 7
const val NAV_RATE = 8
const val NAV_ACTIONS = 9
const val NAV_LINKS = 10
fun navToStringR(nav: Int): Int {
return when (nav) {
NAV_MOVE_FOCUS -> R.string.move_focus
NAV_BACKLIGHT -> R.string.screen_backlight
NAV_VOICEBACK_VOLUME -> R.string.voiceback_volume
NAV_CHARACTERS -> R.string.characters
NAV_WORDS -> R.string.words
NAV_PARAGRAPHS -> R.string.paragraphs
NAV_SLIDER -> R.string.adjust_slider
NAV_LANGUAGE -> R.string.spoken_language
NAV_RATE -> R.string.speech_rate
NAV_ACTIONS -> R.string.actions
NAV_LINKS -> R.string.links
else -> R.string.unknown_error
}
}
fun getDefaultTypes(): String = "0:1:2:3:4:5:6:7:8:9:10"
}

fun navigate(direction: Boolean) {
when (currentNavType) {
NAV_MOVE_FOCUS -> moveFocus(direction)
NAV_BACKLIGHT -> backlight()
NAV_VOICEBACK_VOLUME -> voiceBackVolume(direction)
NAV_CHARACTERS -> read(AccessibilityNodeInfo.MOVEMENT_GRANULARITY_CHARACTER, direction, false)
NAV_WORDS -> read(AccessibilityNodeInfo.MOVEMENT_GRANULARITY_WORD, direction, false)
NAV_PARAGRAPHS -> read(AccessibilityNodeInfo.MOVEMENT_GRANULARITY_PARAGRAPH, direction, false)
NAV_SLIDER -> adjustSlider(direction)
NAV_LANGUAGE -> changeLanguage(direction)
NAV_RATE -> changeSpeechRate(direction)
NAV_LINKS -> links(direction)
NAV_ACTIONS -> actions(direction)
}
}
var currentNavType: Int
get() {
val n =  prefs.getInt(AppConstants.PREFS_CURRENT_NAV_TYPE, NAV_MOVE_FOCUS)
val availableTypes = getNavTypes()
if (availableTypes.indexOf(n) != -1) {
return n
}
return availableTypes[0]
}
set(value) {
prefs.edit().putInt(AppConstants.PREFS_CURRENT_NAV_TYPE, value).apply()
setHandlesDoubleTap()
if (value == NAV_LINKS || value == NAV_ACTIONS) {
doubleTapNav.reset()
}
}
fun setHandlesDoubleTap() {
val n = currentNavType
val node = service.getFocusedNode() ?: run {
service.disableServiceHandlesDoubleTap()
return
}

if (n == NAV_ACTIONS && node.hasSpecialActions() || n == NAV_LINKS && node.hasLinks()) {
service.enableServiceHandlesDoubleTap()
} else {

service.disableServiceHandlesDoubleTap()
}
}

fun getNavTypes(): List<Int> {
val types = (prefs.getString(AppConstants.PREFS_NAV_TYPES, null) ?: getDefaultTypes()).split(":").map {
it.toInt()
}.toMutableList()
val focusedNode = service.getFocusedNode()
if (types.indexOf(NAV_LINKS) != -1 && focusedNode?.hasLinks() != true && types.size > 1) {
types.remove(NAV_LINKS)
}
if (types.indexOf(NAV_ACTIONS) != -1 && focusedNode?.hasSpecialActions() != true && types.size > 1) {
types.remove(NAV_ACTIONS)
}
if (types.indexOf(NAV_SLIDER) != -1 && focusedNode?.isSeekBar != true && types.size > 1) {
types.remove(NAV_SLIDER)
}
return types
}
fun reset() {
setHandlesDoubleTap()
doubleTapNav.reset()
}


fun nextNavType() {
feedbackManager.onEvent(FeedbackManager.EVENT_NAV_TYPE_CHANGED)
val types = getNavTypes()
var currentNavTypePosition = types.indexOf(currentNavType) + 1
if (currentNavTypePosition >= types.size) {
currentNavTypePosition = 0
}
currentNavType = types[currentNavTypePosition]
tts.speak(navToStringR(currentNavType))
setHandlesDoubleTap()
}
fun previousNavType() {
feedbackManager.onEvent(FeedbackManager.EVENT_NAV_TYPE_CHANGED)
val types = getNavTypes()
var currentNavTypePosition = types.indexOf(currentNavType) - 1
if (currentNavTypePosition < 0) {
currentNavTypePosition = types.size - 1
}
currentNavType = types[currentNavTypePosition]
tts.speak(navToStringR(currentNavType))
setHandlesDoubleTap()
}

fun moveFocus(direction: Boolean) {
val action = if (direction) {
Worker.ACTION_NEXT_NODE
} else {
Worker.ACTION_PREVIOUS_NODE
}
worker.work(action)
}
fun backlight() {
worker.work(Worker.ACTION_SET_BACKLIGHT)
}
fun voiceBackVolume(direction: Boolean) {
val action = if (direction) {
Worker.ACTION_INCREASE_SPEAKER_VOLUME
} else {
Worker.ACTION_DECREASE_SPEAKER_VOLUME
}
worker.work(action)
}
private var reading: Reading? = null
fun read(granularity: Int, forward: Boolean, extendSelection: Boolean) {
val node = service.findFocus(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY) 
if (node == null || !node!!.refresh()) {
moveFocus(forward)
return
}
val isEditText = node.isEditText
val label = Compositor(service, null, node).getText(0)
if (reading == null || reading?.node != node) {
reading = Reading(label, forward, node)
}
reading!!.forward = forward
if (node.textSelectionEnd >= 0 && isEditText && !node.isShowingHintText) {
reading = Reading(node.text ?: node.hintText ?: "", forward, node)
reading!!.currentIndex = node.textSelectionEnd
}
if (node.isShowingHintText && isEditText) {
reading!!.currentIndex = Int.MAX_VALUE
}

val text = when (granularity) {
AccessibilityNodeInfo.MOVEMENT_GRANULARITY_CHARACTER -> reading!!.characters()
AccessibilityNodeInfo.MOVEMENT_GRANULARITY_PARAGRAPH -> reading!!.paragraphs()
AccessibilityNodeInfo.MOVEMENT_GRANULARITY_WORD -> reading!!.words()
else -> null
} ?: run {
if (node.className != "android.widget.EditText" || node.isShowingHintText) {
moveFocus(forward)
return
}
feedbackManager.onEvent(FeedbackManager.EVENT_TEXT_END)
return
}
tts.speak(text)
if (isEditText && !node.isShowingHintText) {
node.performAction(if (forward) {
AccessibilityNodeInfo.ACTION_NEXT_AT_MOVEMENT_GRANULARITY
} else {
AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY
}, Bundle().apply {
putBoolean(AccessibilityNodeInfo.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN, TextEditor.isSelectionEnabled)
putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT, granularity)
})
}
}
fun adjustSlider(forward: Boolean) {
val action = if (!forward) {
AccessibilityNodeInfo.ACTION_SCROLL_FORWARD
} else {
AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD
}
service.findFocus(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY)?.performAction(action)
}
fun changeLanguage(forward: Boolean) {
val action = if (!forward) {
Worker.ACTION_NEXT_SPOKEN_LANGUAGE
} else {
Worker.ACTION_PREVIOUS_SPOKEN_LANGUAGE
}
worker.work(action)
}
fun changeSpeechRate(forward: Boolean) {
val action = if (!forward) {
Worker.ACTION_INCREASE_SPEECH_RATE
} else {
Worker.ACTION_DECREASE_SPEECH_RATE
}
worker.work(action)
}
fun links(forward: Boolean) {
doubleTapNav.links(forward)
}
fun actions(forward: Boolean) {
doubleTapNav.actions(forward)
}
}
