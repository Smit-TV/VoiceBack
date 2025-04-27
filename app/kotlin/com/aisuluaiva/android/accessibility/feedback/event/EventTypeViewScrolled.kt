package com.aisuluaiva.android.accessibility.feedback.event
import android.view.accessibility.AccessibilityEvent
import com.aisuluaiva.android.accessibility.feedback.FeedbackManager
import com.aisuluaiva.android.accessibility.feedback.speech.TTS

/**
* Handles TYPE_VIEW_SCROLLED
*/
class EventTypeViewScrolled(private val tts: TTS,
private val feedbackManager: FeedbackManager) {

fun handle(event: AccessibilityEvent) {
if (event.eventTime - (EventTypeWindowsChanged.lastEvent?.eventTime ?: 0) < 300
|| event.itemCount == event.toIndex) {
return
}
feedbackManager.onScrolled(event.eventType, event.fromIndex, event.itemCount)
}
}
