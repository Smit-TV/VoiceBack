package com.aisuluaiva.android.accessibility.feedback.speech

/**
* Contains instances of Str
*/
class StringProcessor(val text: CharSequence,
val maxSpeechInputLength: Int,
val utteranceId: String) {
private val lines = text.chunked(maxSpeechInputLength)
fun getStrArray(): List<Str> {
val strArray = mutableListOf<Str>()
for (line in lines) {
val str = Str(line, utteranceId)
strArray.add(str)
}
return strArray
}
}
