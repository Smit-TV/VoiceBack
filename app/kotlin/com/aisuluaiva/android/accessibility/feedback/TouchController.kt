package com.aisuluaiva.android.accessibility.feedback
import android.accessibilityservice.AccessibilityService.GestureResultCallback
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.view.ViewConfiguration
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.Region
import android.hardware.display.DisplayManager
import android.util.DisplayMetrics
import android.util.Log

class TouchController(private val service: FeedbackService,
private val feedbackManager: FeedbackManager) {
private val displayManager = service.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
fun emulateTap(time: Long) {
val node = service.findFocus(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY) ?: return
val rect = Rect()
node.getBoundsInScreen(rect)
emulateTapXY(rect.centerX().toFloat(), rect.centerY().toFloat(), time)
} fun emulateTapXY(x: Float, y: Float, time: Long) {
val point = PointF(x, y)
    val tap = GestureDescription.StrokeDescription(path(point), 0, time)
    val builder = GestureDescription.Builder()
    builder.addStroke(tap)
    service.dispatchGesture(builder.build(), object : GestureResultCallback() {
override fun onCancelled(gesture: GestureDescription) {
enableExploreByTouch()
Log.e(TAG, "Gesture cancelled.")
}
override fun onCompleted(gesture: GestureDescription) {
enableExploreByTouch()
Log.i(TAG, "Gesture completed.")
}
}, null)
}
private fun path(point: PointF): Path {
    val path = Path()
    path.moveTo(point.x, point.y)
    return path
}
fun disableExploreByTouch() {
val info = service.serviceInfo
info.flags = info.flags and AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE.inv()
service.serviceInfo = info
}
fun enableExploreByTouch() {
val info = service.serviceInfo
info.flags = info.flags or AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE
service.serviceInfo = info
}
fun tap() {
disableExploreByTouch()
emulateTap(ViewConfiguration.getTapTimeout().toLong())
}
fun tapAndHold() {
disableExploreByTouch()
emulateTap(ViewConfiguration.getLongPressTimeout().toLong() * 2)
}
fun passThroughSystemGesture(displayId: Int) {
val display = displayManager.getDisplay(displayId) ?: return
val metrics = DisplayMetrics()
display.getMetrics(metrics)
val rect = Rect(0, 0, metrics.widthPixels * 100, metrics.heightPixels * 100)
val region = Region(rect)
val l = object : FeedbackService.OnAccessibilityEventListener {
override fun onAccessibilityEvent(event: AccessibilityEvent) {
if (event.eventType != AccessibilityEvent.TYPE_TOUCH_INTERACTION_END) {
return
}
service.removeAccessibilityEventListener(this)
service.setTouchExplorationPassthroughRegion(displayId, Region())
feedbackManager.onEvent(FeedbackManager.EVENT_SYSTEM_GESTURE_DETECTION_END)
}
}
service.addAccessibilityEventListener(l)
service.setTouchExplorationPassthroughRegion(displayId, region)
feedbackManager.onEvent(FeedbackManager.EVENT_SYSTEM_GESTURE_DETECTION_START)
}
companion object {
const val TAG = "TouchController"
}
}
