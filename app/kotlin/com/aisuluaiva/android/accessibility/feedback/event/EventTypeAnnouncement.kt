package com.aisuluaiva.android.accessibility.feedback.event
import android.view.accessibility.AccessibilityEvent
import android.util.Log
import com.aisuluaiva.android.accessibility.feedback.FeedbackManager
import com.aisuluaiva.android.accessibility.feedback.speech.TTS
import com.aisuluaiva.android.accessibility.feedback.speech.ProgressListener

/**
* Handles TYPE_ANNOUNCEMENT events
*/
class EventTypeAnnouncement(private val tts: TTS,
private val feedbackManager: FeedbackManager) {
private var lastText = mutableListOf<CharSequence>()
private var lastEventTime = 0L
fun handle(event: AccessibilityEvent) {
if (lastEventTime > System.currentTimeMillis() - 1000
&& lastText == event.text) {
return
}
lastEventTime = System.currentTimeMillis()
lastText = event.text

for (i in 0 until event.text.size) {
val text = event.text[i] ?: continue
val utterance = "${TTS.UTTERANCE_ANNOUNCEMENT}$i"
tts.addProgressListener(utterance,
object : ProgressListener() {
override fun onStart() {
feedbackManager.onEvent(event.eventType)
tts.removeProgressListener(utterance)
}
})
tts.speak(text,
TTS.QUEUE_ADD, 
utterance)
}
}

}
