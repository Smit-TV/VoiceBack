package com.aisuluaiva.android.accessibility.feedback.event
import android.os.CountDownTimer
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.aisuluaiva.android.accessibility.extensions.*
import com.aisuluaiva.android.accessibility.feedback.FeedbackManager
import com.aisuluaiva.android.accessibility.feedback.speech.TTS

/**
* Handles the TYPE_VIEW_HOVER_ENTER event
*/
class EventTypeViewHoverEnter(private val tts: TTS,
private val feedbackManager: FeedbackManager) {
private var skippedNode: AccessibilityNodeInfo? = null
private var lastNode = AccessibilityNodeInfo()
private var lastView: AccessibilityNodeInfo? = null
private var countDownTimer: CountDownTimer? = null
init {
PackageVariables.setOnFingerOnScreenChangeListener { isOnScreen ->
lastView = null
skippedNode = null
}
}
fun handle(event: AccessibilityEvent) {
countDownTimer?.cancel()
skippedNode = null
val node = event.source ?: return
val view = if (node.isAccessibilityFocusable) node else node.logicParent ?: run {
// I don't know :(
// @test("TouchNothing")
if (skippedNode == node) {
return
}
countDownTimer?.cancel()
skippedNode = node
countDownTimer = object : CountDownTimer(3, 1) {
override fun onTick(timeAfter: Long) {}
override fun onFinish() {
if (skippedNode == node) {
feedbackManager.onEvent(FeedbackManager.EVENT_NOT_FOCUSABLE_NODE)
}
}
}.start()
lastNode = node
return
}
skippedNode = null
if (view.isAccessibilityFocused && lastView != view) {
view.performAction(AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS)
}
view.performFocus()
lastView = view
countDownTimer?.cancel()
}
}
