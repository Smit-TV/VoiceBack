package com.aisuluaiva.android.accessibility.feedback.traversal
import android.content.Context
import android.os.Bundle
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction
import android.util.Log
import com.aisuluaiva.android.accessibility.extensions.*
import com.aisuluaiva.android.accessibility.feedback.FeedbackService
import com.aisuluaiva.android.accessibility.feedback.FeedbackManager
import com.aisuluaiva.android.accessibility.feedback.AppConstants
import com.aisuluaiva.android.accessibility.feedback.speech.Compositor

class Traversal(private val service: FeedbackService,
private val feedbackManager: FeedbackManager){
private var hasFocusedNode = false
private val prefs = service.getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
fun getLastChild(node: AccessibilityNodeInfo): AccessibilityNodeInfo? {
var child: AccessibilityNodeInfo? = null
for (i in 0 until node.childCount) {
child = node.getChild(i) ?: continue
}
return child
}
fun getFirstChild(node: AccessibilityNodeInfo): AccessibilityNodeInfo? {
var child: AccessibilityNodeInfo? = null
for (i in 0 until node.childCount) {
child = node.getChild(i) ?: continue
break
}
return child
}
fun scrollToPosition(scrollView: AccessibilityNodeInfo, column: Int, row: Int) {
if (!scrollView.isVisibleToUser) {
return
}
scrollView.performAction(AccessibilityAction.ACTION_SCROLL_TO_POSITION.id, Bundle().apply {
putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_COLUMN_INT, column)
putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_ROW_INT, row)
})
scrollView.refresh()
}
fun getFocusedNode(): AccessibilityNodeInfo {
val node = service.findFocus(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY)
node ?: run {
hasFocusedNode = false
return service.rootInActiveWindow ?: run {
return  service.windows[0].root
}
}
hasFocusedNode = true
return node
}
private val  htmlNavigationBundle = Bundle().apply {
putString(AccessibilityNodeInfo.ACTION_ARGUMENT_HTML_ELEMENT_STRING, "genericContainer")
}
fun goToNextNode() {
val focusedNode = getFocusedNode()
if ((focusedNode.isHTMLElement || focusedNode.isWebView) && focusedNode.performAction(AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT, htmlNavigationBundle)) {
return
}
val node = if (focusedNode.childCount == 0) {
focusedNode.parent ?: focusedNode
} else {
focusedNode
}
Thread {
nextNode(node, hasFocusedNode == false)?.performFocus() ?: end(true)
}.start()
}
fun end(forward: Boolean) {
val focusedNode = getFocusedNode()
val pref = prefs.getBoolean(AppConstants.PREFS_MOVE_TO_ANOTHER_WINDOW_BOOL, true)
if (!pref) {
focusedNode?.apply {
performAction(AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS)
performFocus()
}
}
feedbackManager.onEvent(FeedbackManager.EVENT_TRAVERSAL_END)
if (!pref) {
return
}
changeWindow(forward)
}
fun changeWindow(forward: Boolean, isChild: Boolean = false) {
val focusedNode = getFocusedNode()
val windows = if (forward) {
service.windows
} else {
service.windows.reversed()
}.toMutableList()
val currentWindow = (focusedNode.window
 ?: service.findWindowById(focusedNode.windowId)
?: service.rootInActiveWindow?.window ?: return)
var isFound = isChild
for (window in windows) {
if (window == currentWindow) {
isFound = true
continue
}
if (isFound) {
val root = window.root ?: continue
val node = if (forward) {
nextNode(root, true)
} else {
previousNode(root, true)

}
node?.let {
it.performFocus()
return
}
}
}
if (isChild) {
val root = service.rootInActiveWindow ?: return
val node = if (forward) {
nextNode(root, true)
} else {
previousNode(root, true)
}
node?.let {
it.performFocus()
}
return
}
 changeWindow(forward, true)
}
fun isViewPager(node: AccessibilityNodeInfo): Boolean {
val cln = node.className ?: ""
return cln.indexOf("android") == 0 && cln.indexOf("ViewPager") != -1
}
fun nextNode(node: AccessibilityNodeInfo, isChild: Boolean = false, nodeForComparison: AccessibilityNodeInfo? = null, scrollView: AccessibilityNodeInfo? = null): AccessibilityNodeInfo? {
var isFocusFound = isChild || node.isAccessibilityFocused
for (i in 0 until node.childCount) {
val child = node.getChild(i) ?: continue
if (child.isAccessibilityFocused || child == nodeForComparison) {
isFocusFound = true
continue
}
if (child.isHTMLElement) continue
child.collectionItemInfo?.let {
if (isFocusFound) {
scrollToPosition(node, it.columnIndex, it.rowIndex)
}
}
if (isFocusFound && child.childCount > 0 && !child.isVisibleToUser) {
nextNode(child, true)?.let {
return it
}
}
if (isFocusFound && child.isVisibleToUser) {
if (child.isAccessibilityFocusable || child.isWebView) {
return child
}
nextNode(child, true)?.let {
return it
}
}
}
if (!node.isWebView && !isViewPager(node) && node.canScroll && node.isScrollView && scrollView != node) {
while (node.isVisibleToUser && node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD) && node.refresh()) {
waitForScrollingToFinish(node, true)
nextNode(node.findFocus(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY) ?: node, node.findFocus(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY) == null, node.findFocus(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY), node)?.let {
return it
}
}
}
if (isChild || node == scrollView) {
return null
}
return nextNode(node.parent ?: run {
return null
}, false, node, scrollView)
}

fun goToPreviousNode() {
val focusedNode = getFocusedNode()
if ((focusedNode.isHTMLElement || focusedNode.isWebView) && focusedNode.performAction(AccessibilityNodeInfo.ACTION_PREVIOUS_HTML_ELEMENT, htmlNavigationBundle)) {
return
}
Thread {
previousNode(focusedNode.parent ?: focusedNode, hasFocusedNode == false)?.performFocus() ?: end(false)
}.start()
}
fun previousNode(node: AccessibilityNodeInfo, isChild: Boolean = false, nodeForComparison: AccessibilityNodeInfo? = null, scrollView: AccessibilityNodeInfo? = null): AccessibilityNodeInfo? {
var isFocusFound = isChild || node.isAccessibilityFocused
for (i in node.childCount - 1 downTo 0) {
val child = node.getChild(i) ?: continue
if (child.isAccessibilityFocused || child == nodeForComparison) {
isFocusFound = true
continue
}
if (child.isHTMLElement) continue
child.collectionItemInfo?.let {
if (isFocusFound) {
scrollToPosition(node, it.columnIndex, it.rowIndex)
}
}
if (isFocusFound && child.childCount > 0 && !child.isVisibleToUser) {
previousNode(child, true)?.let {
return it
}
}
if (isFocusFound && child.isVisibleToUser) {
previousNode(child, true)?.let {
return it
}
if (child.isAccessibilityFocusable || child.isWebView) {
return child
}
}
}

if (!node.isWebView && !isViewPager(node) && node.canScroll && node.isScrollView && scrollView != node) {
while (node.isVisibleToUser && node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD) && node.refresh()) {
waitForScrollingToFinish(node, false)
previousNode(node.findFocus(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY)?.parent ?: node, node.findFocus(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY) == null, node.findFocus(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY), node)?.let {
return it
}
}
}
if (!node.isAccessibilityFocused && (node.isAccessibilityFocusable || node.isWebView)) {
return node
}

if (isChild || scrollView == node) {
return null
}
return previousNode(node.parent ?: run {
return null
}, false, node, scrollView)
}

fun waitForScrollingToFinish(node: AccessibilityNodeInfo, scrollDirection: Boolean) {

Thread.sleep(500)
node.refresh()
}
}
