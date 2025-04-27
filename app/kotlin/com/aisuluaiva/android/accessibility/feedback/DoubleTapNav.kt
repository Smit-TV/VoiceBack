package com.aisuluaiva.android.accessibility.feedback
import android.text.Spanned
import android.text.style.ClickableSpan
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import com.aisuluaiva.android.accessibility.feedback.overlay.LinksMenu
import com.aisuluaiva.android.accessibility.feedback.overlay.ActionsMenu
import com.aisuluaiva.android.accessibility.feedback.speech.TTS

class DoubleTapNav(private val service: FeedbackService,
private val tts: TTS,
private val feedbackManager: FeedbackManager) {
fun getFocusedNode(): AccessibilityNodeInfo? {
return service.findFocus(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY)
}
private var currentLink: ClickableSpan? = null
private var currentAction = AccessibilityNodeInfo.AccessibilityAction(4, null)
fun onClickLink() {
currentLink?.onClick(View(service))
}
fun reset() {
val links = LinksMenu.getClickableSpans(getFocusedNode() ?: return)
if (links.isNotEmpty()) {
currentLink = links[0]
}
val actions = ActionsMenu.getActions(getFocusedNode() ?: return)
if (actions.isNotEmpty()) {
currentAction = actions[0]
}
}
fun onAction() {
getFocusedNode()?.performAction(currentAction.id)
}
fun <T> getList(forward: Boolean, actionList: List<T>): List<T> {
return if (forward) {
actionList
} else {
actionList.reversed()
}
}
fun getIndex(currentPosition: Int, length: Int, forward: Boolean): Int {
if (length == currentPosition) {
if (forward) {
return 0
} else {
return length - 1
}
} else {
return currentPosition
}
}

fun actions(forward: Boolean) {
val actionList = ActionsMenu.getActions(getFocusedNode() ?: return)
if (actionList.isEmpty()) {
return
}
val actions = getList(forward, actionList).toMutableList()
var currentActionPosition = actions.indexOf(currentAction)
if (currentActionPosition == -1) {
currentActionPosition = 0
}
if (actions.size > 1) {
actions.remove(actions[currentActionPosition])
}
currentAction = actions[getIndex(currentActionPosition, actions.size, forward)]
tts.speak(currentAction.label ?: "")
feedbackManager.onEvent(FeedbackManager.EVENT_NAV_TYPE_CHANGED)
}
fun links(forward: Boolean) {
val linkList = LinksMenu.getClickableSpans(getFocusedNode() ?: return)
if (linkList.isEmpty()) {
return
}
val links = getList(forward, linkList).toMutableList()
var currentLinkPosition = links.indexOf(currentLink)
if (currentLinkPosition == -1) {
currentLinkPosition = 0
}
if (links.size > 1) {
links.remove(links[currentLinkPosition])
}
currentLink = links[getIndex(currentLinkPosition, links.size, forward)]
val text = getFocusedNode()?.text ?: return
val spanned = text as Spanned
val spanStart = spanned.getSpanStart(currentLink) ?: -1
val spanEnd = spanned.getSpanEnd(currentLink) ?: -1
val link = if (spanEnd > -1 && spanStart > -1) {
text.substring(spanStart, spanEnd)
} else {
""
}
tts.speak(link)
feedbackManager.onEvent(FeedbackManager.EVENT_NAV_TYPE_CHANGED)
}
}
