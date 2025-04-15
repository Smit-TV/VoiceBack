package com.aisuluaiva.android.accessibility.feedback.event
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo
import com.aisuluaiva.android.accessibility.extensions.*
import com.aisuluaiva.android.accessibility.feedback.speech.TTS
import com.aisuluaiva.android.accessibility.feedback.FeedbackManager
import com.aisuluaiva.android.accessibility.feedback.FeedbackService

/**
* Handles the TYPE_VIEW_SELECTED event
*/
class EventTypeViewSelected(private val tts: TTS,
private val feedbackManager: FeedbackManager,
private val service: FeedbackService) {
private var lastEvent: AccessibilityEvent? = null

fun handle(event: AccessibilityEvent) {
if (shouldDropEvent(event)) {
return
}
lastEvent = event
val node = event.source ?: return
val focusedNode = if (node.isAccessibilityFocusable) {
node
} else {
node.logicParent ?: return
}
if (service.findFocus(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY) == null) {
focusedNode.performFocus()
return
}
tts.speak(event)
feedbackManager.onA11yFocused(focusedNode)
}
fun shouldDropEvent(event: AccessibilityEvent): Boolean {
val node = event.source ?: return true
return !node.isVisibleToUser || event.eventTime - 100 < lastEvent?.eventTime ?: 0 ||
node.rangeInfo != null || node.isSeekBar
|| EventTypeWindowsChanged.lastEvent?.eventTime ?: 0 > event.eventTime - 200
}
}
