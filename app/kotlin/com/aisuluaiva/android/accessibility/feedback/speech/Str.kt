package com.aisuluaiva.android.accessibility.feedback.speech

/**
* This class contains text, utterance id and pitch
*/
class Str(val text: CharSequence,
 val utteranceId: String) {
var pitch = 1.0f
init {
if (text.length == 1) {
if (text[0].isUpperCase()) {
pitch = 1.5f
}
}
}
}
