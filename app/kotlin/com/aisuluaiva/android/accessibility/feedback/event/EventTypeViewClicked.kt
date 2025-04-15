package com.aisuluaiva.android.accessibility.feedback.event
import android.view.accessibility.AccessibilityEvent
import com.aisuluaiva.android.accessibility.feedback.FeedbackService
import com.aisuluaiva.android.accessibility.feedback.speech.TTS
import com.aisuluaiva.android.accessibility.feedback.FeedbackManager
import com.aisuluaiva.android.accessibility.extensions.*

/**
* Handles the TYPE_VIEW_CLICKED
*/
class EventTypeViewClicked(private val tts: TTS,
private val feedbackManager: FeedbackManager,
private val service: FeedbackService) {
fun handle(event: AccessibilityEvent) {
feedbackManager.onEvent(event.eventType)
val node = event.source ?: return

if (!node.isAccessibilityFocused) {
node.performFocus()
}
tts.speak(node.expandDescription ?: return, TTS.QUEUE_ADD)
}
}
