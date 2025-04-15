package com.aisuluaiva.android.accessibility.feedback
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo
object FocusHistory {
val history = mutableMapOf<AccessibilityWindowInfo, AccessibilityNodeInfo?>()
fun reset() {
history.clear()
}
fun get(window: AccessibilityWindowInfo): AccessibilityNodeInfo? {
return history[window]
}
}
