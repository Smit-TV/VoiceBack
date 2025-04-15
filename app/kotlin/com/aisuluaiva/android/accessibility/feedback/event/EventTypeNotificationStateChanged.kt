package com.aisuluaiva.android.accessibility.feedback.event
import android.app.Notification
import android.view.accessibility.AccessibilityEvent
import com.aisuluaiva.android.accessibility.feedback.FeedbackManager
import com.aisuluaiva.android.accessibility.feedback.speech.TTS
import com.aisuluaiva.android.accessibility.feedback.speech.ProgressListener

/**
* Handles TYPE_NOTIFICATION_STATE_CHANGED
*/
class EventTypeNotificationStateChanged(private val tts: TTS,
private val feedbackManager: FeedbackManager) {
fun handle(event: AccessibilityEvent) {
val notification = event.parcelableData as? Notification
if (notification == null) {
for (i in 0 until event.text.size) {
val utterance = "${TTS.UTTERANCE_ANNOUNCEMENT}$i"
tts.addProgressListener(utterance, object : ProgressListener() {
override fun onStart() {
feedbackManager.onEvent(AccessibilityEvent.TYPE_ANNOUNCEMENT)
tts.removeProgressListener(utterance)
}
})
tts.speak(event.text.getOrNull(i) ?: continue,
 TTS.QUEUE_ADD,
 utterance)
}
}
}

}
