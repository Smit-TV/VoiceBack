package com.aisuluaiva.android.accessibility.feedback.event
import android.content.Context
import android.view.accessibility.AccessibilityEvent
import android.util.Log
import com.aisuluaiva.android.accessibility.feedback.R
import com.aisuluaiva.android.accessibility.feedback.FeedbackManager
import com.aisuluaiva.android.accessibility.feedback.speech.TTS

/**
* Handles the TYPE_VIEW_TEXT_SELECTION_CHANGED
*/
class EventTypeViewTextSelectionChanged(private val tts: TTS,
private val feedbackManager: FeedbackManager,
private val context: Context) {
fun handle(event: AccessibilityEvent) {

}
}
