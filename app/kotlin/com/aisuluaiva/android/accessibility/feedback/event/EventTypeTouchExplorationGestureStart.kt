package com.aisuluaiva.android.accessibility.feedback.event
import android.view.accessibility.AccessibilityEvent

/**
* Handles the TYPE_TOUCH_EXPLORATION_GESTURE_START
*/
class EventTypeTouchExplorationGestureStart {
fun handle(event: AccessibilityEvent) {
PackageVariables.fingerOnScreen = true
}
}
