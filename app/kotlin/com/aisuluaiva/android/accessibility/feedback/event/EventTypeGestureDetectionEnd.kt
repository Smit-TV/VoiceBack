package com.aisuluaiva.android.accessibility.feedback.event
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import com.aisuluaiva.android.accessibility.feedback.TouchController
import com.aisuluaiva.android.accessibility.feedback.FeedbackManager
import com.aisuluaiva.android.accessibility.feedback.FeedbackService

/**
* Handles the TYPE_GESTURE_DETECTION_END
*/
class EventTypeGestureDetectionEnd(private val service: FeedbackService,
private val feedbackManager: FeedbackManager) {
fun handle(event: AccessibilityEvent) {
EventTypeViewAccessibilityFocused.lastNode = null
}
}
