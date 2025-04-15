package com.aisuluaiva.android.accessibility.feedback
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.InputMethod
import android.annotation.SuppressLint
import android.util.Log

@SuppressLint("NewApi")
class FeedbackInputMethod(private val service: FeedbackService) : InputMethod(service) {}
