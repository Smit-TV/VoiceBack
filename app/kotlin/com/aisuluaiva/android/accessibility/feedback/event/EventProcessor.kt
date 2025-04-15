package com.aisuluaiva.android.accessibility.feedback.event
import android.content.Context
import android.os.PowerManager
import android.view.accessibility.AccessibilityEvent
import com.aisuluaiva.android.accessibility.feedback.FeedbackManager
import com.aisuluaiva.android.accessibility.feedback.FeedbackService
import com.aisuluaiva.android.accessibility.feedback.speech.TTS

/**
* Handles all events
*/
class EventProcessor(private val tts: TTS,
private val feedbackManager: FeedbackManager,
private val service: FeedbackService) {
private val powerManager = service.getSystemService(Context.POWER_SERVICE) as PowerManager
private val eventTypeViewHoverEnter = EventTypeViewHoverEnter(tts, feedbackManager)
private val eventTypeViewAccessibilityFocused = EventTypeViewAccessibilityFocused(tts, feedbackManager, service)
private val eventTypeTouchInteractionStart = EventTypeTouchInteractionStart()
private val eventTypeTouchInteractionEnd = EventTypeTouchInteractionEnd()
private val eventTypeViewClicked = EventTypeViewClicked(tts, feedbackManager, service)
private val eventTypeViewLongClicked = EventTypeViewLongClicked(tts, feedbackManager)
private val eventTypeWindowContentChanged = EventTypeWindowContentChanged(tts, feedbackManager)
private val eventTypeAnnouncement = EventTypeAnnouncement(tts, feedbackManager)
private val eventTypeViewScrolled = EventTypeViewScrolled(tts, feedbackManager)
private val eventTypeViewTextTraversedMovementGranularity = EventTypeViewTextTraversedMovementGranularity(tts, feedbackManager, service)
private val eventTypeWindowStateChanged = EventTypeWindowStateChanged(tts, feedbackManager)
private val eventTypeWindowsChanged = EventTypeWindowsChanged(tts, feedbackManager, service) 
private val eventTypeNotificationStateChanged = EventTypeNotificationStateChanged(tts, feedbackManager)
private val eventTypeTouchExplorationGestureStart = EventTypeTouchExplorationGestureStart()
private val eventTypeTouchExplorationGestureEnd = EventTypeTouchExplorationGestureEnd(service, feedbackManager)
private val eventTypeViewTextChanged = EventTypeViewTextChanged(tts, feedbackManager, service)
private val eventTypeViewTextSelectionChanged = EventTypeViewTextSelectionChanged(tts, feedbackManager, service)
private val eventTypeViewHoverExit = EventTypeViewHoverExit(tts, feedbackManager)
private val eventTypeViewSelected = EventTypeViewSelected(tts, feedbackManager, service)
private val eventTypeGestureDetectionEnd = EventTypeGestureDetectionEnd(service, feedbackManager)
fun handle(event: AccessibilityEvent) {
if (shouldDropEvent(event)) {
return
}
val eventType = event.eventType
when (eventType) {
AccessibilityEvent.TYPE_VIEW_HOVER_ENTER -> eventTypeViewHoverEnter.handle(event)
AccessibilityEvent.TYPE_VIEW_HOVER_EXIT -> eventTypeViewHoverExit.handle(event)
AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED -> eventTypeViewAccessibilityFocused.handle(event)
AccessibilityEvent.TYPE_VIEW_CLICKED -> eventTypeViewClicked.handle(event)
AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> eventTypeViewLongClicked.handle(event)
AccessibilityEvent.TYPE_VIEW_SELECTED -> eventTypeViewSelected.handle(event)
AccessibilityEvent.TYPE_VIEW_SCROLLED -> eventTypeViewScrolled.handle(event)
AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY -> eventTypeViewTextTraversedMovementGranularity.handle(event)
AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> eventTypeViewTextChanged.handle(event)
AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED -> eventTypeViewTextSelectionChanged.handle(event)
AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> eventTypeWindowContentChanged.handle(event)
AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> eventTypeWindowStateChanged.handle(event)
AccessibilityEvent.TYPE_WINDOWS_CHANGED -> eventTypeWindowsChanged.handle(event)
AccessibilityEvent.TYPE_GESTURE_DETECTION_END -> eventTypeGestureDetectionEnd.handle(event)
AccessibilityEvent.TYPE_TOUCH_INTERACTION_START -> eventTypeTouchInteractionStart.handle(event)
AccessibilityEvent.TYPE_TOUCH_INTERACTION_END -> eventTypeTouchInteractionEnd.handle(event)
AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END -> eventTypeTouchExplorationGestureEnd.handle(event)
AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START -> eventTypeTouchExplorationGestureStart.handle(event)
AccessibilityEvent.TYPE_ANNOUNCEMENT -> eventTypeAnnouncement.handle(event)
AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> eventTypeNotificationStateChanged.handle(event)
}
}
fun shouldDropEvent(event: AccessibilityEvent): Boolean {
return event.eventType != AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED && !powerManager.isInteractive
}
}
