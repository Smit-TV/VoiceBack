package com.aisuluaiva.android.accessibility.feedback.event
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo
import com.aisuluaiva.android.accessibility.feedback.speech.TTS
import com.aisuluaiva.android.accessibility.feedback.SpeedNav
import com.aisuluaiva.android.accessibility.feedback.FeedbackManager
import com.aisuluaiva.android.accessibility.feedback.FeedbackService

/**
* Handles the TYPE_VIEW_ACCESSIBILITY_FOCUSED event
*/
class EventTypeViewAccessibilityFocused(private val tts: TTS,
private val feedbackManager: FeedbackManager,
private val service: FeedbackService) {
companion object {
var lastNode: AccessibilityNodeInfo? = null
}
init {
PackageVariables.setOnFingerOnScreenChangeListener { isOnScreen ->
if (!isOnScreen && lastNode != null) {
lastNode?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
lastNode = null
}
}
}

fun handle(event: AccessibilityEvent) {
val node = event.source ?: return
tts.speak(event)
feedbackManager.onA11yFocused(node)
if (node.isTextEntryKey ||
service.findWindowById(node.windowId)?.type == AccessibilityWindowInfo.TYPE_INPUT_METHOD) {
lastNode = node
} else {
lastNode = null
}
SpeedNav.reset()
}
}
