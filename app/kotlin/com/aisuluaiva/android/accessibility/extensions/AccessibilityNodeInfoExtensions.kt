package com.aisuluaiva.android.accessibility.extensions
import android.os.Build
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction
import android.util.Log
import com.aisuluaiva.android.accessibility.feedback.R
import com.aisuluaiva.android.accessibility.feedback.FeedbackService
import com.aisuluaiva.android.accessibility.feedback.overlay.*

fun AccessibilityNodeInfo.hasLinks(): Boolean {
return LinksMenu.getClickableSpans(this).isNotEmpty()
}
fun AccessibilityNodeInfo.hasSpecialActions(): Boolean {
return ActionsMenu.getActions(this).isNotEmpty()
}

val AccessibilityNodeInfo.canScroll: Boolean 
get() {
val cln = className ?: ""
return isScrollable && ( (cln.indexOf("android.") == 0
|| cln.indexOf("androidx.") == 0))
}

val AccessibilityNodeInfo.isEditText: Boolean
get() {
return (className == "android.widget.EditText" ||
actionList.indexOf(AccessibilityAction.ACTION_PASTE) != -1 ||
actionList.indexOf(AccessibilityAction.ACTION_COPY) != -1 ||
actionList.indexOf(AccessibilityAction.ACTION_CUT) != -1 ||
actionList.indexOf(AccessibilityAction.ACTION_SET_TEXT) != -1)
}

val AccessibilityNodeInfo.roleDescription: CharSequence?
get() = extras.getString("AccessibilityNodeInfo.roleDescription", null)

val AccessibilityNodeInfo.expandDescription: CharSequence?
get() {
val r = if (actionList.indexOf(AccessibilityAction.ACTION_COLLAPSE) != -1) {
 R.string.state_expanded
} else if (actionList.indexOf(AccessibilityAction.ACTION_EXPAND) != -1) {
 R.string.state_collapsed
} else {
return null
}
return FeedbackService.res.getString(r)
}

val AccessibilityNodeInfo.hasClickable: Boolean
get() {
return isClickable || actionList.indexOf(AccessibilityAction.ACTION_CLICK) != -1
}
val AccessibilityNodeInfo.hasLongClickable: Boolean
get() {
return isLongClickable || actionList.indexOf(AccessibilityAction.ACTION_LONG_CLICK) != -1
}
val AccessibilityNodeInfo.hasAnyClickable: Boolean
get() = hasClickable || hasLongClickable

val AccessibilityNodeInfo.hasFocusable: Boolean
get() {
return isFocusable || actionList.indexOf(AccessibilityAction.ACTION_FOCUS) != -1
}

val AccessibilityNodeInfo.isSeekBar: Boolean
get() {
return (
rangeInfo != null
|| className == "android.widget.SeekBar"
|| className == "android.widget.ProgressBar"
 || actionList.indexOf(AccessibilityAction.ACTION_SET_PROGRESS) != -1)
}

val AccessibilityNodeInfo.isCollection: Boolean
get() {
val cln = className ?: ""
return collectionInfo != null || cln.indexOf("android") == 0 &&
(cln.indexOf("RecyclerView") != -1
|| cln.indexOf("ListView") != -1
|| cln.indexOf("GridView") != -1)
}

val AccessibilityNodeInfo.isScrollView: Boolean
get() {
if (isSeekBar || className == "android.widget.Spinner") {
return false
}
val cln = className ?: ""
if (cln.indexOf("android") == 0 && (
cln.indexOf("ScrollView") != -1 || cln.indexOf("ViewPager") != -1)
|| isScrollable) {
return true
}
    val scrollActions = listOf(
        AccessibilityAction.ACTION_SCROLL_UP,
        AccessibilityAction.ACTION_SCROLL_DOWN,
        AccessibilityAction.ACTION_SCROLL_LEFT,
        AccessibilityAction.ACTION_SCROLL_RIGHT,
        AccessibilityAction.ACTION_SCROLL_FORWARD,
        AccessibilityAction.ACTION_SCROLL_BACKWARD,
        if (Build.VERSION.SDK_INT > 33) AccessibilityAction.ACTION_SCROLL_IN_DIRECTION else AccessibilityAction.ACTION_SCROLL_UP,
        AccessibilityAction.ACTION_SCROLL_TO_POSITION
    )

    for (action in scrollActions) {
        if (actionList.indexOf(action) != -1) {
return true
        }
    }
return false
}

val AccessibilityNodeInfo.hasLabel: Boolean
get() = contentDescription?.isNotEmpty() == true || text?.isNotEmpty() == true

val AccessibilityNodeInfo.isHTMLElement: Boolean
get() = !isWebView && (actionList.indexOf(AccessibilityAction.ACTION_NEXT_HTML_ELEMENT) != -1 || actionList.indexOf(AccessibilityAction.ACTION_PREVIOUS_HTML_ELEMENT) != -1)

val AccessibilityNodeInfo.hasAvailableNode: Boolean
get() {
if (isCollection || isScrollView) {
return false
}
for (i in 0 until childCount) {
val child = getChild(i) ?: continue
if (child.isScrollView || child.isCollection
|| !child.isVisibleToUser
|| child.hasAnyClickable || child.hasFocusable || child.isSeekBar
|| child.childCount > 0 && !child.hasAvailableNode && !child.hasLabel) {
continue
}
if (child.childCount == 0 && !child.hasLabel && child.stateDescription?.isNotEmpty() != true) {
continue
}
return true
}
return false
}

val AccessibilityNodeInfo.isWebView: Boolean
get() = extras.getString("AccessibilityNodeInfo.chromeRole", "") == "rootWebArea"

val AccessibilityNodeInfo.isAccessibilityFocusable: Boolean
get() {
if (isHTMLElement) {
return !isWebView && ( hasAnyClickable || hasLabel || hasFocusable)
}
if (childCount > 0 && !hasAvailableNode && !hasLabel
|| childCount == 0 && !hasLabel && !hasAnyClickable && !hasFocusable && !isSeekBar
|| !isVisibleToUser || isWebView) {
return false
}
if (isScrollView || isCollection && childCount == 0) {
return hasAnyClickable || hasLabel 
}
return (
hasAnyClickable || hasFocusable
|| hasLabel && stateDescription?.isNotEmpty() == true
|| (hasLabel || stateDescription != null) && logicParent == null
|| isSeekBar  || parent?.isScrollView == true || parent?.isCollection == true)
}

val AccessibilityNodeInfo.logicParent: AccessibilityNodeInfo?
get() {
var p = parent
while (p != null) {
if (p.isAccessibilityFocusable) {
return p
}
p = p.parent
}
return null
}

fun AccessibilityNodeInfo.performFocus(): Boolean = performAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS)
