package com.aisuluaiva.android.accessibility.feedback.event
import android.view.accessibility.AccessibilityEvent
import com.aisuluaiva.android.accessibility.feedback.TouchController
import com.aisuluaiva.android.accessibility.feedback.FeedbackManager
import com.aisuluaiva.android.accessibility.feedback.FeedbackService

/**
* Handles the TYPE_TOUCH_EXPLORATION_GESTURE_END
*/
class EventTypeTouchExplorationGestureEnd(private val service: FeedbackService,
private val feedbackManager: FeedbackManager) {
fun handle(event: AccessibilityEvent) {
PackageVariables.fingerOnScreen = false
}
}
