package com.aisuluaiva.android.accessibility.feedback.event
import android.content.SharedPreferences
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityWindowInfo
import com.aisuluaiva.android.accessibility.feedback.R
import com.aisuluaiva.android.accessibility.feedback.AppConstants
import com.aisuluaiva.android.accessibility.feedback.FeedbackManager
import com.aisuluaiva.android.accessibility.feedback.speech.TTS

/**
* Handles CONTENT_CHANGE_TYPE_* events 
*/
class EventTypeWindowStateChanged(private val tts: TTS,
private val feedbackManager: FeedbackManager,
private val prefs: SharedPreferences) {
companion object {
var lastEvent: AccessibilityEvent? = null
var mWindow: AccessibilityWindowInfo? = null
}
fun handle(event: AccessibilityEvent) {
val eventTime = event.eventTime
val contentChangeTypes = event.contentChangeTypes
val node = event.source ?: return
val paneTitle = node.paneTitle ?: event.text.getOrNull(0) ?: return
val window = node.window  ?: return
if (!window.isActive
|| eventTime-  (lastEvent?.eventTime ?: 0) < 500
|| eventTime - (EventTypeWindowsChanged.lastEvent?.eventTime ?: 0) < 500
|| paneTitle.isEmpty()
|| paneTitle == window.title) {
return
}
if (contentChangeTypes != AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_DISAPPEARED) {
eventCompleted(paneTitle)
lastEvent = event
mWindow = window
}
}
fun eventCompleted(paneTitle: CharSequence) {
if (prefs.getBoolean(AppConstants.PREFS_SPEAK_PANE_TITLES_BOOL, true)) {
tts.speak(paneTitle, TTS.QUEUE_ADD)
}
feedbackManager.onEvent(
AccessibilityEvent.TYPE_WINDOWS_CHANGED)
}
}
