package com.aisuluaiva.android.accessibility.feedback.event
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.aisuluaiva.android.accessibility.extensions.*
import com.aisuluaiva.android.accessibility.feedback.FeedbackManager
import com.aisuluaiva.android.accessibility.feedback.speech.TTS

/**
* Handles the TYPE_VIEW_HOVER_EXIT
*/
class EventTypeViewHoverExit(private val tts: TTS,
private val feedbackManager: FeedbackManager) {
companion object {
var lastNode = AccessibilityNodeInfo()
}

fun handle(event: AccessibilityEvent) {
lastNode = event.source ?: return
}
}
