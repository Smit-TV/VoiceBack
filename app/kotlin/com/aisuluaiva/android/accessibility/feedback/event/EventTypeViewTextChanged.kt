package com.aisuluaiva.android.accessibility.feedback.event
import android.content.Context
import android.view.accessibility.AccessibilityEvent
import android.util.Log
import com.aisuluaiva.android.accessibility.feedback.R
import com.aisuluaiva.android.accessibility.feedback.AppConstants
import com.aisuluaiva.android.accessibility.feedback.FeedbackManager
import com.aisuluaiva.android.accessibility.feedback.speech.TTS

/**
* Handles the TYPE_VIEW_TEXT_CHANGED
*/
class EventTypeViewTextChanged(private val tts: TTS,
private val feedbackManager: FeedbackManager,
private val context: Context) {
private val prefs = context.getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
fun handle(event: AccessibilityEvent) {
android.util.Log.i("textsel", "$event")
if ((event.isPassword || event.source?.isPassword == true)
&& !prefs.getBoolean(AppConstants.PREFS_SPEAK_PASSWORDS_AS_YOU_TYPE_BOOL, true)) {
return
}
val node = event.source ?: return
val text = event.text.getOrNull(0) ?: return
val beforeText = event.beforeText ?: ""
val added = event.addedCount - event.removedCount
val beforeTextLength = beforeText.length
val fromIndex = event.fromIndex
if (added >= 0) {
val addedText = text.substring(fromIndex, fromIndex + added)
tts.speak(addedText)
}
val removed = event.removedCount - event.addedCount
if (removed >= 0) {
val removedText = beforeText.substring(fromIndex, fromIndex + removed)
tts.speak(removedText)
tts.speak(context.getString(R.string.deleted), TTS.QUEUE_ADD)
feedbackManager.onEvent(FeedbackManager.EVENT_TEXT_DELETED)
}
}
}
