package com.aisuluaiva.android.accessibility.feedback
import android.view.accessibility.AccessibilityNodeInfo

class Reading(val text: CharSequence,
var forward: Boolean,
val node: AccessibilityNodeInfo) {
var currentIndex = if (forward) {
0
} else {
text.length
}

fun characters(): String? {
val char = if (forward && currentIndex < text.length && currentIndex >= 0) {
text[currentIndex++]
} else if (!forward && currentIndex > 0 && currentIndex <= text.length) {
text[--currentIndex]
} else { null }
return char?.toString()
}
fun paragraphs(): String? {
val sb = StringBuilder()
var char: String? = ""
while (char != null) {
char = (characters() ?: break).toString()
if (char == "\n" && sb.isNotBlank()) {
if (forward) {
currentIndex--
} else {
currentIndex++
}
break
}
sb.append(char)
}
if (sb.isEmpty()) {
return null
}
val paragraph = sb.toString()
return if (forward) { 
paragraph
} else {
paragraph.reversed()
}
}
fun words(): String? {
val sb = StringBuilder()
var char: String? = ""
while (char != null) {
char = (characters() ?: break).toString()
if (!char[0].isLetterOrDigit() && sb.isNotBlank()
&& sb.toString().any { it.isLetterOrDigit() }
&& char != "_") {
if (forward) {
currentIndex--
} else {
currentIndex++
}
break
}
sb.append(char)
}
if (sb.isEmpty()) {
return null
}
val word = sb.toString()
return if (forward) { 
word
} else {
word.reversed()
}
}

}
