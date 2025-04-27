package com.aisuluaiva.android.accessibility.feedback
import android.content.ClipData
import android.content.Context
import android.content.ClipboardManager
import android.os.Bundle
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import com.aisuluaiva.android.accessibility.extensions.isEditText
import com.aisuluaiva.android.accessibility.feedback.speech.TTS
import com.aisuluaiva.android.accessibility.feedback.speech.Compositor

class TextEditor(private val service: FeedbackService,
private val feedbackManager: FeedbackManager,
private val tts: TTS) {
companion object {
var isSelectionEnabled = false
}
private val am = service.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
private val clipboardManager = service.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
private fun getFocusedNode(): AccessibilityNodeInfo? {
return service.findFocus(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY)
}
private fun isEditText(): Boolean {
val node = getFocusedNode() ?: return false
return node.isEditText
}
fun copy() {
val result = getFocusedNode()?.performAction(AccessibilityNodeInfo.ACTION_COPY) == true
if (result) {
successfulCopied()
return
}
val text = Compositor(service, null, getFocusedNode() ?: return).getText(0)
val clip = ClipData.newPlainText("label", text)
clipboardManager.setPrimaryClip(clip)
successfulCopied()
}
fun successfulCopied() {
val event = AccessibilityEvent(AccessibilityEvent.TYPE_ANNOUNCEMENT)
event.text.add(service.getString(R.string.copied_to_clipboard))
am.sendAccessibilityEvent(event)
}
fun paste() {
val node = getFocusedNode() ?: return
if (!node.performAction(AccessibilityNodeInfo.ACTION_PASTE)) {
notEditable()
}
}
fun selectAll() {
if (!isEditText()) {
notEditable()
return
}
val node = getFocusedNode() ?: return
val text = node.text ?: return
val args = Bundle().apply {
putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT, text.length)
putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT, 0)
}
if (!node.performAction(AccessibilityNodeInfo.ACTION_SET_SELECTION, args)) {
notEditable()
return
}
tts.speak("${service.getString(R.string.state_selected)} ${node.text}")
}
fun moveCursorToStart() {
if (!isEditText()) {
notEditable()
return
}
val node = getFocusedNode() ?: return
val args = Bundle().apply {
putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT, 0)
putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT, 0)
}
if (!node.performAction(AccessibilityNodeInfo.ACTION_SET_SELECTION, args)) {
notEditable()
} else {
tts.speak(R.string.beginning_of_field)
}
}
fun moveCursorToEnd() {
if (!isEditText()) {
notEditable()
return
}
val node = getFocusedNode() ?: return
val text = node.text ?: return
val args = Bundle().apply {
putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT, text.length)
putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT, text.length)
}
if (!node.performAction(AccessibilityNodeInfo.ACTION_SET_SELECTION, args)) {
notEditable()
} else {
tts.speak(R.string.end_of_field)
}
}
fun cut() {
if (!isEditText()) {
notEditable()
return
}
getFocusedNode()?.performAction(AccessibilityNodeInfo.ACTION_CUT)
}
fun notEditable() {
tts.speak(R.string.not_editable)
feedbackManager.onEvent(FeedbackManager.EVENT_NOT_EDITABLE)
}
fun startOrEndSelectionMode() {
isSelectionEnabled = isSelectionEnabled != true
val notification = if (isSelectionEnabled) {
R.string.selection_mode_on
} else {
R.string.selection_mode_off
}
tts.speak(notification)
}
}
