package com.aisuluaiva.android.accessibility.feedback.event
import android.content.Context
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo
import com.aisuluaiva.android.accessibility.feedback.AppConstants
import com.aisuluaiva.android.accessibility.feedback.FeedbackService
import com.aisuluaiva.android.accessibility.feedback.FeedbackManager
import com.aisuluaiva.android.accessibility.feedback.speech.TTS

/**
* Handles TYPE_WINDOWS_CHANGED
*/
class EventTypeWindowsChanged(private val tts: TTS,
private val feedbackManager: FeedbackManager,
private val service: FeedbackService) {
private val prefs = service.getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
companion object {
var lastEvent: AccessibilityEvent? = null
var mWindow: AccessibilityWindowInfo? = null
}
fun handle(event: AccessibilityEvent) {
//lastEvent = event
val windowChanges = event.windowChanges
val windowId = event.windowId
val window = service.findWindowById(windowId) ?: return
mWindow = window
if (!window.isActive) {
return
}
lastEvent = event
val title = window.title ?: return
when (windowChanges) {
AccessibilityEvent.WINDOWS_CHANGE_TITLE,
AccessibilityEvent.WINDOWS_CHANGE_ADDED -> {
if (prefs.getBoolean(AppConstants.PREFS_SPEAK_PANE_TITLES_BOOL, true)) {
tts.speak(title, 1)
}
feedbackManager.onEvent(event.eventType)
}


}
}
}
