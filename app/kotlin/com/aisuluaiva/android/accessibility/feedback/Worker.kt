package com.aisuluaiva.android.accessibility.feedback
import android.accessibilityservice.AccessibilityService
import android.content.Context
import com.aisuluaiva.android.accessibility.feedback.traversal.Traversal
import com.aisuluaiva.android.accessibility.feedback.speech.TTS
import com.aisuluaiva.android.accessibility.utils.AudioUtils
import com.aisuluaiva.android.accessibility.feedback.overlay.*

/**
* Handles gestures
*/
class Worker(val service: FeedbackService,
val feedbackManager: FeedbackManager,
private val audioUtils: AudioUtils,
private val backlightController: BacklightController,
val tts: TTS) {
private val prefs = service.getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
private val traversal = Traversal(service, feedbackManager)
private val quickMenu = QuickMenu(service, this)
private val speedNav = SpeedNav(this, prefs, tts)
private val touchController = TouchController(service, feedbackManager)
private val textEditor = TextEditor(service, feedbackManager, tts)
private val languageMenu = LanguageMenu(service, tts)
companion object {
const val ACTION_NEXT_NODE = -1
const val ACTION_PREVIOUS_NODE = -2
const val ACTION_INCREASE_SPEAKER_VOLUME = -3
const val ACTION_DECREASE_SPEAKER_VOLUME = -4
const val ACTION_NEXT_TRACK = -5
const val ACTION_PREVIOUS_TRACK = -6
const val ACTION_SET_BACKLIGHT = -7
const val ACTION_NEXT_NAV_TYPE = -8
const val ACTION_PREVIOUS_NAV_TYPE = -9
const val ACTION_NEXT_NAV_ELEMENT = -10
const val ACTION_PREVIOUS_NAV_ELEMENT = -11
const val ACTION_QUICK_MENU = -12
const val ACTION_TAP = -13
const val ACTION_LONG_TAP = -14
const val ACTION_PASS_THROUGH_SYSTEM_GESTURE = -15
const val ACTION_NEXT_SPOKEN_LANGUAGE = -16
const val ACTION_PREVIOUS_SPOKEN_LANGUAGE = -17
const val ACTION_INCREASE_SPEECH_RATE = -18
const val ACTION_DECREASE_SPEECH_RATE = -19
const val ACTION_SELECTION = -20
const val ACTION_COPY = -21
const val ACTION_CUT = -22
const val ACTION_SELECT_ALL = -23
const val ACTION_CURSOR_TO_START = -24
const val ACTION_CURSOR_TO_END = -25
const val ACTION_PASTE = -26
const val ACTION_LANGUAGE_MENU = -27
/** This needs to synchronized with R.array.actions */
val actionIds = listOf(-1, -2, AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS,
AccessibilityService.GLOBAL_ACTION_KEYCODE_HEADSETHOOK, AccessibilityService.GLOBAL_ACTION_BACK,
AccessibilityService.GLOBAL_ACTION_HOME, AccessibilityService.GLOBAL_ACTION_RECENTS,
-3, -4, -5, -6, -7, -8, -9,
-10, -11, -12, -13, -14, -15,
-16, -17, -18, -19, -20, -21, -22, -23,
-24, -25, -26, -27)

fun getActionByGesture(gesture: Int): Int {
return when (gesture) {
AccessibilityService.GESTURE_SWIPE_LEFT -> ACTION_PREVIOUS_NODE
AccessibilityService.GESTURE_SWIPE_RIGHT -> ACTION_NEXT_NODE
AccessibilityService.GESTURE_SWIPE_UP_AND_LEFT -> AccessibilityService.GLOBAL_ACTION_HOME
AccessibilityService.GESTURE_SWIPE_UP_AND_RIGHT -> ACTION_QUICK_MENU
AccessibilityService.GESTURE_SWIPE_LEFT_AND_UP -> AccessibilityService.GLOBAL_ACTION_RECENTS
AccessibilityService.GESTURE_SWIPE_RIGHT_AND_DOWN -> AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS
AccessibilityService.GESTURE_SWIPE_DOWN_AND_LEFT -> AccessibilityService.GLOBAL_ACTION_BACK
AccessibilityService.GESTURE_SWIPE_DOWN_AND_RIGHT -> AccessibilityService.GLOBAL_ACTION_KEYCODE_HEADSETHOOK
AccessibilityService.GESTURE_SWIPE_LEFT_AND_RIGHT -> ACTION_INCREASE_SPEAKER_VOLUME
AccessibilityService.GESTURE_SWIPE_RIGHT_AND_LEFT -> ACTION_DECREASE_SPEAKER_VOLUME
AccessibilityService.GESTURE_SWIPE_UP_AND_DOWN -> ACTION_PREVIOUS_NAV_TYPE
AccessibilityService.GESTURE_SWIPE_DOWN_AND_UP -> ACTION_NEXT_NAV_TYPE
AccessibilityService.GESTURE_SWIPE_UP -> ACTION_PREVIOUS_NAV_ELEMENT
AccessibilityService.GESTURE_SWIPE_DOWN -> ACTION_NEXT_NAV_ELEMENT
AccessibilityService.GESTURE_SWIPE_RIGHT_AND_UP -> ACTION_PASS_THROUGH_SYSTEM_GESTURE
AccessibilityService.GESTURE_4_FINGER_SINGLE_TAP -> ACTION_TAP
AccessibilityService.GESTURE_4_FINGER_DOUBLE_TAP -> ACTION_LONG_TAP
AccessibilityService.GESTURE_4_FINGER_SWIPE_UP -> ACTION_NEXT_SPOKEN_LANGUAGE
AccessibilityService.GESTURE_4_FINGER_SWIPE_DOWN -> ACTION_PREVIOUS_SPOKEN_LANGUAGE
AccessibilityService.GESTURE_4_FINGER_SWIPE_RIGHT -> ACTION_INCREASE_SPEECH_RATE
AccessibilityService.GESTURE_4_FINGER_SWIPE_LEFT -> ACTION_DECREASE_SPEECH_RATE
AccessibilityService.GESTURE_3_FINGER_SINGLE_TAP -> ACTION_COPY
AccessibilityService.GESTURE_3_FINGER_DOUBLE_TAP -> ACTION_CUT
AccessibilityService.GESTURE_3_FINGER_SWIPE_LEFT -> ACTION_SELECTION
AccessibilityService.GESTURE_3_FINGER_SWIPE_RIGHT -> ACTION_SELECT_ALL
AccessibilityService.GESTURE_3_FINGER_SWIPE_UP -> ACTION_CURSOR_TO_START
AccessibilityService.GESTURE_3_FINGER_SWIPE_DOWN -> ACTION_CURSOR_TO_END
AccessibilityService.GESTURE_3_FINGER_TRIPLE_TAP -> ACTION_PASTE
else -> 0
}
}
}

fun exec(gesture: Int, displayId: Int): Boolean {
val action = prefs.getInt("GESTURE${gesture}DISPLAY${displayId}", getActionByGesture(gesture))
work(action, displayId)
return true
}
fun work(action: Int, displayId: Int = 0) {
if (action > 0) {
service.performGlobalAction(action)
return
}
when (action) {
ACTION_NEXT_NODE -> traversal.goToNextNode()
ACTION_PREVIOUS_NODE -> traversal.goToPreviousNode()
ACTION_INCREASE_SPEAKER_VOLUME -> audioUtils.increaseSpeakerVolume()
ACTION_DECREASE_SPEAKER_VOLUME -> audioUtils.decreaseSpeakerVolume()
ACTION_NEXT_TRACK -> audioUtils.nextTrack()
ACTION_PREVIOUS_TRACK -> audioUtils.previousTrack()
ACTION_SET_BACKLIGHT -> backlightController.set()
ACTION_NEXT_NAV_TYPE -> speedNav.nextNavType()
ACTION_PREVIOUS_NAV_TYPE -> speedNav.previousNavType()
ACTION_NEXT_NAV_ELEMENT -> speedNav.navigate(true)
ACTION_PREVIOUS_NAV_ELEMENT -> speedNav.navigate(false)
ACTION_QUICK_MENU -> quickMenu.show(displayId)
ACTION_TAP -> touchController.tap()
ACTION_LONG_TAP -> touchController.tapAndHold()
ACTION_PASS_THROUGH_SYSTEM_GESTURE -> touchController.passThroughSystemGesture(displayId)
ACTION_NEXT_SPOKEN_LANGUAGE -> tts.nextLanguage()
ACTION_PREVIOUS_SPOKEN_LANGUAGE -> tts.previousLanguage()
ACTION_INCREASE_SPEECH_RATE -> tts.increaseSpeechRate()
ACTION_DECREASE_SPEECH_RATE -> tts.decreaseSpeechRate()
ACTION_COPY -> textEditor.copy()
ACTION_CUT -> textEditor.cut()
ACTION_SELECT_ALL -> textEditor.selectAll()
ACTION_PASTE -> textEditor.paste()
ACTION_CURSOR_TO_END -> textEditor.moveCursorToEnd()
ACTION_CURSOR_TO_START -> textEditor.moveCursorToStart()
ACTION_LANGUAGE_MENU -> languageMenu.show(displayId)
ACTION_SELECTION -> textEditor.startOrEndSelectionMode()
}
}
}
