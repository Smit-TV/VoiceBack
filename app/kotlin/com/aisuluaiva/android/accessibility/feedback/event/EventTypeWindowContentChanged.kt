package com.aisuluaiva.android.accessibility.feedback.event
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.aisuluaiva.android.accessibility.feedback.R
import com.aisuluaiva.android.accessibility.feedback.FeedbackManager
import com.aisuluaiva.android.accessibility.feedback.speech.TTS

/**
* Handles CONTENT_CHANGE_TYPE_* events 
*/
class EventTypeWindowContentChanged(private val tts: TTS,
private val feedbackManager: FeedbackManager) {
fun getQueueMode(liveRegion: Int): Int {
return when (liveRegion) {
View.ACCESSIBILITY_LIVE_REGION_POLITE -> TTS.QUEUE_ADD
else -> TTS.QUEUE_FLUSH
}
}
fun handle(event: AccessibilityEvent) {
val contentChangeTypes = event.contentChangeTypes
val node = event.source
val queueMode = getQueueMode(node?.liveRegion ?: 0)
when (contentChangeTypes) {
AccessibilityEvent.CONTENT_CHANGE_TYPE_DRAG_STARTED -> tts.speak(R.string.drag_started)
AccessibilityEvent.CONTENT_CHANGE_TYPE_DRAG_DROPPED -> tts.speak(R.string.drag_dropped)
AccessibilityEvent.CONTENT_CHANGE_TYPE_DRAG_CANCELLED -> tts.speak(R.string.drag_cancelled)
AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT -> contentChangeTypeText(node ?: return, queueMode)
AccessibilityEvent.CONTENT_CHANGE_TYPE_ERROR -> contentChangeTypeError(node ?: return, queueMode)
AccessibilityEvent.CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION -> contentChangeTypeContentDescription(node ?: return, queueMode)
AccessibilityEvent.CONTENT_CHANGE_TYPE_STATE_DESCRIPTION -> contentChangeTypeStateDescription(node ?: return, queueMode)
AccessibilityEvent.CONTENT_CHANGE_TYPE_ENABLED -> contentChangeTypeEnabled(node ?: return, queueMode)
AccessibilityEvent.CONTENT_CHANGE_TYPE_SUBTREE -> subtree(event)

}
}
fun subtree(event: AccessibilityEvent) {
val node = event.source ?: return
//tts.speak(node.paneTitle ?: return, TTS.QUEUE_ADD)
}

fun isInternalNode(node: AccessibilityNodeInfo): Boolean {
if (node.isAccessibilityFocused) {
return true
}
var p = node.parent
while (p != null) {
if (p.isAccessibilityFocused) {
return true
}
p = p.parent
}
return false
}
fun contentChangeTypeText(node: AccessibilityNodeInfo, queue: Int) {
if (!isInternalNode(node)) {
return
}
tts.speak(node.text ?: return, queue)
}
fun contentChangeTypeContentDescription(node: AccessibilityNodeInfo, queue: Int) {
if (!isInternalNode(node)) {
return
}
tts.speak(node.contentDescription ?: return, queue)
}
fun contentChangeTypeError(node: AccessibilityNodeInfo, queue: Int) {
if (!isInternalNode(node)) {
return
}
tts.speak(node.error ?: return, queue)
}
fun contentChangeTypeStateDescription(node: AccessibilityNodeInfo, queue: Int) {
if (!isInternalNode(node)) {
return
}
val rangeInfo = node.rangeInfo
val state = if (node.stateDescription?.isNotEmpty() == true) {
node.stateDescription
} else if (node.rangeInfo != null) {
val percent = (rangeInfo.current / rangeInfo.max) * 100

"${percent.toInt()}%"
} else {
return
}
if (rangeInfo != null) {
feedbackManager.onScrolled(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
rangeInfo.current.toInt(),
 rangeInfo.max.toInt())
}
tts.speak(state ?: return, queue)
}
fun contentChangeTypeEnabled(node: AccessibilityNodeInfo, queue: Int) {
if (!isInternalNode(node) || node.isEnabled) {
return
}
tts.speak(R.string.state_disabled ?: return, queue)
}
}
