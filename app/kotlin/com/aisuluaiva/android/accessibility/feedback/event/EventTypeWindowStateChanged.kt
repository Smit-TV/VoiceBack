package com.aisuluaiva.android.accessibility.feedback.event
import android.os.CountDownTimer
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.aisuluaiva.android.accessibility.feedback.R
import com.aisuluaiva.android.accessibility.feedback.FeedbackManager
import com.aisuluaiva.android.accessibility.feedback.speech.TTS

/**
* Handles CONTENT_CHANGE_TYPE_* events 
*/
class EventTypeWindowStateChanged(private val tts: TTS,
private val feedbackManager: FeedbackManager) {
private var lastEvent: AccessibilityEvent? = null
private var timer: CountDownTimer? = null
private var lastPaneTitle: CharSequence = ""
fun handle(event: AccessibilityEvent) {
val contentChangeTypes = event.contentChangeTypes
val node = event.source ?: return
val paneTitle = event.text.getOrNull(0) ?: node.paneTitle ?: return
val window = node.window
if (window?.isActive != true
|| event.eventTime - (lastEvent?.eventTime ?: 0) < 300
|| paneTitle == lastPaneTitle
|| paneTitle == node.window?.title) {
lastPaneTitle = paneTitle
return
}
lastEvent = event
lastPaneTitle = paneTitle
timer?.cancel()
when (contentChangeTypes) {
AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_TITLE,
AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_APPEARED -> {
eventCompleted(paneTitle)
}
else -> {
eventCompleted(paneTitle)
}
}
}
fun eventCompleted(paneTitle: CharSequence) {
tts.speak(paneTitle, TTS.QUEUE_ADD)
feedbackManager.onEvent(
AccessibilityEvent.TYPE_WINDOWS_CHANGED)
}
}
