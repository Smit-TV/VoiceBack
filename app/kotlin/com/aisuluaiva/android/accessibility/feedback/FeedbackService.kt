package com.aisuluaiva.android.accessibility.feedback
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.AccessibilityGestureEvent
import android.accessibilityservice.InputMethod
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Build
import android.view.MotionEvent
import android.view.InputDevice
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo
import android.util.Log
import android.graphics.Color
import com.aisuluaiva.android.accessibility.feedback.event.EventProcessor
import com.aisuluaiva.android.accessibility.feedback.speech.TTS
import com.aisuluaiva.android.accessibility.utils.AudioUtils
import com.aisuluaiva.android.accessibility.feedback.overlay.BacklightController

/**
* Good luck for me!
* Project created at Sat. mar. 22. 16:39
*/
open class FeedbackService : AccessibilityService() {
private lateinit var feedbackManager: FeedbackManager
private lateinit var eventProcessor: EventProcessor
private lateinit var tts: TTS
private lateinit var worker: Worker
private lateinit var audioUtils: AudioUtils
private lateinit var backlightController: BacklightController
private lateinit var prefs: SharedPreferences
private lateinit var receiver: FeedbackReceiver
private val accessibilityEventListeners = mutableListOf<OnAccessibilityEventListener>()
companion object {
var instance: FeedbackService? = null
const val TAG = "FeedbackService"
lateinit var res: Resources
val gestureIds = listOf(
    AccessibilityService.GESTURE_SWIPE_UP,
    AccessibilityService.GESTURE_SWIPE_RIGHT,
    AccessibilityService.GESTURE_SWIPE_DOWN,
    AccessibilityService.GESTURE_SWIPE_LEFT,
    AccessibilityService.GESTURE_SWIPE_UP_AND_DOWN,
    AccessibilityService.GESTURE_SWIPE_DOWN_AND_UP,
    AccessibilityService.GESTURE_SWIPE_LEFT_AND_RIGHT,
    AccessibilityService.GESTURE_SWIPE_RIGHT_AND_LEFT,
    AccessibilityService.GESTURE_SWIPE_UP_AND_LEFT,
    AccessibilityService.GESTURE_SWIPE_UP_AND_RIGHT,
    AccessibilityService.GESTURE_SWIPE_DOWN_AND_LEFT,
    AccessibilityService.GESTURE_SWIPE_DOWN_AND_RIGHT,
    AccessibilityService.GESTURE_SWIPE_RIGHT_AND_UP,
    AccessibilityService.GESTURE_SWIPE_RIGHT_AND_DOWN,
    AccessibilityService.GESTURE_SWIPE_LEFT_AND_UP,
    AccessibilityService.GESTURE_SWIPE_LEFT_AND_DOWN,
    AccessibilityService.GESTURE_2_FINGER_SINGLE_TAP,
    AccessibilityService.GESTURE_2_FINGER_DOUBLE_TAP,
    AccessibilityService.GESTURE_2_FINGER_DOUBLE_TAP_AND_HOLD,
    AccessibilityService.GESTURE_2_FINGER_TRIPLE_TAP,
    AccessibilityService.GESTURE_2_FINGER_TRIPLE_TAP_AND_HOLD,
    AccessibilityService.GESTURE_3_FINGER_SINGLE_TAP,
    AccessibilityService.GESTURE_3_FINGER_SINGLE_TAP_AND_HOLD,
    AccessibilityService.GESTURE_3_FINGER_DOUBLE_TAP,
    AccessibilityService.GESTURE_3_FINGER_DOUBLE_TAP_AND_HOLD,
    AccessibilityService.GESTURE_3_FINGER_TRIPLE_TAP,
    AccessibilityService.GESTURE_3_FINGER_TRIPLE_TAP_AND_HOLD,
    AccessibilityService.GESTURE_3_FINGER_SWIPE_UP,
    AccessibilityService.GESTURE_3_FINGER_SWIPE_RIGHT,
    AccessibilityService.GESTURE_3_FINGER_SWIPE_DOWN,
    AccessibilityService.GESTURE_3_FINGER_SWIPE_LEFT,
    AccessibilityService.GESTURE_4_FINGER_SINGLE_TAP,
    AccessibilityService.GESTURE_4_FINGER_DOUBLE_TAP,
    AccessibilityService.GESTURE_4_FINGER_DOUBLE_TAP_AND_HOLD,
    AccessibilityService.GESTURE_4_FINGER_TRIPLE_TAP,
    AccessibilityService.GESTURE_4_FINGER_SWIPE_UP,
    AccessibilityService.GESTURE_4_FINGER_SWIPE_RIGHT,
    AccessibilityService.GESTURE_4_FINGER_SWIPE_DOWN,
    AccessibilityService.GESTURE_4_FINGER_SWIPE_LEFT,
)


}
override fun onServiceConnected() {
super.onServiceConnected()
res = resources
instance = this
val info = serviceInfo
info.flags = (info.flags
or AccessibilityServiceInfo.FLAG_REQUEST_MULTI_FINGER_GESTURES
or AccessibilityServiceInfo.FLAG_REQUEST_2_FINGER_PASSTHROUGH
or AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE
or AccessibilityServiceInfo.FLAG_INPUT_METHOD_EDITOR)
serviceInfo = info
prefs = getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
feedbackManager = FeedbackManager(this)
audioUtils = AudioUtils(this, feedbackManager)
tts = TTS(this, getString(R.string.service_enabled))
backlightController = BacklightController(this, tts)
eventProcessor = EventProcessor(tts, feedbackManager, this)
worker = Worker(this, feedbackManager, audioUtils, backlightController, tts)
receiver = FeedbackReceiver(this, tts, feedbackManager)
registerReceiver(receiver, FeedbackReceiver.getIntentFilter())
feedbackManager.onEvent(FeedbackManager.EVENT_SERVICE_STARTED)
if (Build.VERSION.SDK_INT > 30) {
setAccessibilityFocusAppearance(prefs.getInt(AppConstants.PREFS_FOCUS_THICKNESS_INT, 20),
prefs.getInt(AppConstants.PREFS_FOCUS_COLOR_INT, Color.BLUE))
}
Log.i(TAG, "Service started.")
val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as android.view.accessibility.AccessibilityManager
Log.i("insa11ys", "${am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_AUDIBLE)}")
}
override fun onDestroy() {
tts.speak(getString(R.string.service_disabled))
feedbackManager.onEvent(FeedbackManager.EVENT_SERVICE_STOPPED)
accessibilityEventListeners.clear()
gestureListeners.clear()
feedbackManager.recycle()
unregisterReceiver(receiver)
Log.i(TAG, "Service stopped.")
super.onDestroy()
}
override fun onInterrupt() {
Log.i(TAG, "Service on interrupted.")
}
fun interface OnAccessibilityEventListener {
fun onAccessibilityEvent(event: AccessibilityEvent)
}

override fun onAccessibilityEvent(event: AccessibilityEvent) {
eventProcessor.handle(event)
Thread {
sendAccessibilityEvent(event)
}.start()
}
fun sendAccessibilityEvent(event: AccessibilityEvent) {
for (l in accessibilityEventListeners) {
l.onAccessibilityEvent(event)
}
}
fun addAccessibilityEventListener(l: OnAccessibilityEventListener) {
accessibilityEventListeners.add(l)
}
fun removeAccessibilityEventListener(l: OnAccessibilityEventListener) {
accessibilityEventListeners.remove(l)
}
fun addGestureListener(gestureListener: OnGestureListener) {
gestureListeners.add(gestureListener)
}
private val gestureListeners = mutableListOf<OnGestureListener>()
fun interface OnGestureListener {
fun onGesture(gesture: Int)
}

override fun onGesture(gesture: Int): Boolean {
for (gestureListener in gestureListeners) {
gestureListener.onGesture(gesture)
}
return worker.exec(gesture, 0)
}
override fun onGesture(event: AccessibilityGestureEvent): Boolean {
for (gestureListener in gestureListeners) {
gestureListener.onGesture(event.gestureId)
}
return worker.exec(event.gestureId, event.displayId)
}
fun findWindowById(windowId: Int): AccessibilityWindowInfo? {
windows.forEach { 
if (it.id == windowId) {
return it
}
}
return null
}
override fun onCreateInputMethod(): InputMethod {
return FeedbackInputMethod(this)
}
fun isServiceHandlesDoubleTap(): Boolean {
return serviceInfo.flags and AccessibilityServiceInfo.FLAG_SERVICE_HANDLES_DOUBLE_TAP != 0
}
fun enableServiceHandlesDoubleTap() {
if (isServiceHandlesDoubleTap()) {
return
}
val info = serviceInfo
info.flags = info.flags or AccessibilityServiceInfo.FLAG_SERVICE_HANDLES_DOUBLE_TAP
serviceInfo = info
}
fun disableServiceHandlesDoubleTap() {
if (!isServiceHandlesDoubleTap()) {
return
}
val info = serviceInfo
info.flags = info.flags and AccessibilityServiceInfo.FLAG_SERVICE_HANDLES_DOUBLE_TAP.inv()
serviceInfo = info
}
fun getFocusedNode(): AccessibilityNodeInfo? {
return findFocus(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY)
}
}
