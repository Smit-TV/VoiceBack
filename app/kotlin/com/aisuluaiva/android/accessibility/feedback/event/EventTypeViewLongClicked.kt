package com.aisuluaiva.android.accessibility.feedback.event
import android.view.accessibility.AccessibilityEvent
import com.aisuluaiva.android.accessibility.feedback.speech.TTS
import com.aisuluaiva.android.accessibility.feedback.FeedbackManager
import com.aisuluaiva.android.accessibility.extensions.*

/**
* Handles the TYPE_VIEW_LONG_CLICKED
*/
class EventTypeViewLongClicked(private val tts: TTS,
private val feedbackManager: FeedbackManager) {
fun handle(event: AccessibilityEvent) {
feedbackManager.onEvent(event.eventType)
val node = event.source ?: return
tts.speak(node.expandDescription ?: return, TTS.QUEUE_ADD)
}
}
