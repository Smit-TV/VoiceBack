package com.aisuluaiva.android.accessibility.feedback
import android.accessibilityservice.TouchInteractionController
import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.GestureDetector

@SuppressLint("NewApi")
class TouchInteractionHelper(private val service: FeedbackService,
private val feedbackManager: FeedbackManager,
val displayId: Int) : GestureDetector.SimpleOnGestureListener(), TouchInteractionController.Callback {
private val touchController = TouchController(service, feedbackManager)
private val tic = service.getTouchInteractionController(displayId)
private val gestureDetector = GestureDetector(service, this)
private val motionEvents = mutableListOf<MotionEvent>()
init {
tic.registerCallback(service.mainExecutor, this)
tic.requestTouchExploration()
}
override fun onMotionEvent(event: MotionEvent) {
when (event.action) {
MotionEvent.ACTION_MOVE -> motionEvents.add(event)
MotionEvent.ACTION_POINTER_UP -> motionEvents.clear()
}
gestureDetector.onTouchEvent(event)
}
override fun onStateChanged(state: Int) {}

}
