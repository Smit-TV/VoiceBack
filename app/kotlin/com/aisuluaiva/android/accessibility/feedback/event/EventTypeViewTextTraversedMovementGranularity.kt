package com.aisuluaiva.android.accessibility.feedback.event
import android.content.Context
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.aisuluaiva.android.accessibility.feedback.FeedbackManager
import com.aisuluaiva.android.accessibility.feedback.R
import com.aisuluaiva.android.accessibility.feedback.speech.TTS
import com.aisuluaiva.android.accessibility.feedback.TextEditor

/**
* Handles TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY
*/
class EventTypeViewTextTraversedMovementGranularity(private val tts: TTS,
private val feedbackManager: FeedbackManager,
private val context: Context) {
fun handle(event: AccessibilityEvent) {

val text = event.text.getOrNull(0) ?: return
val toIndex = event.toIndex
val fromIndex = event.fromIndex
val node = event.source ?: return
val textSelEnd = node.textSelectionEnd
val textSelStart = node.textSelectionStart
val action = event.action
val currentText = text.substring(fromIndex, toIndex)
android.util.Log.i("select", "fromIndex $fromIndex toIndex $toIndex selStart $textSelStart selEnd $textSelEnd")
tts.speak(currentText)
}


}

