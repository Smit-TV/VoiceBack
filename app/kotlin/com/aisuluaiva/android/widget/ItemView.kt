package com.aisuluaiva.android.widget
import android.content.Context
import android.os.Bundle
import androidx.appcompat.widget.AppCompatTextView
import android.view.View
import android.view.accessibility.*
import android.util.AttributeSet
import android.util.Log
import com.aisuluaiva.android.accessibility.feedback.R
import com.aisuluaiva.android.accessibility.feedback.sound.SoundManager

class ItemView(private val context: Context,
attrs: AttributeSet?) : AppCompatTextView(context, attrs) {
private val soundManager = SoundManager(context)
override fun onInitializeAccessibilityEvent(event: AccessibilityEvent) {
if (event.eventType == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) {
if (text != context.getString(R.string.default_value)) {
soundManager.play(text)
}
}
super.onInitializeAccessibilityEvent(event)
}
}
