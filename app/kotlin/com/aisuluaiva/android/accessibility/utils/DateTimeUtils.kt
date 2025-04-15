package com.aisuluaiva.android.accessibility.utils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateTimeUtils {
/** HH:mm */
fun getTime(): String {
val current = LocalDateTime.now()
val format = DateTimeFormatter.ofPattern("HH:mm")
return current.format(format)
}
/** dd.MM.yyyy */
fun getDate(): String {
val current = LocalDateTime.now()
val format = DateTimeFormatter.ofPattern("dd.MM.yyyy")
return current.format(format)
}
}