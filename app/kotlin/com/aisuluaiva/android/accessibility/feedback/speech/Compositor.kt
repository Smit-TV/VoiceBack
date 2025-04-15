package com.aisuluaiva.android.accessibility.feedback.speech
import android.content.Context
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction
import com.aisuluaiva.android.accessibility.feedback.R
import com.aisuluaiva.android.accessibility.extensions.*
import com.aisuluaiva.android.accessibility.utils.PackageUtils

/**
* Contains text to speak
*/
class Compositor(private val context: Context,
private val event: AccessibilityEvent?,
val node: AccessibilityNodeInfo = event?.source ?: throw IllegalArgumentException("This event hasn't got node")) {
companion object {
var lastWindow: AccessibilityWindowInfo? = null
const val FLAG_INCLUDE_NOT_IMPORTANT_LABELS = 1
const val FLAG_INCLUDE_STATE_DESCRIPTION = 2
const val ALL_FLAGS = 1 or 2
}
val uniqueId: CharSequence = node.viewIdResourceName?.split("/")?.last()?.replace("_", " ") ?: ""
val disableDescription = if (node.isEnabled) {
"" 
} else {
context.getString(R.string.state_disabled)
}
val window = (if (lastWindow != node.window) {
lastWindow = node.window
val appName = PackageUtils.getAppName(context, node.packageName ?: "") ?: ""
"${context.getString(R.string.window)} ${node.window?.title ?: appName}"
} else { "" }).toString()

val expandDescription = node.expandDescription ?: ""
val label = if (node.isEditText || node.isWebView) {
node.text ?: ""
} else if (node.contentDescription?.isNotEmpty() == true) {
node.contentDescription ?: ""
} else if (node.text?.isNotEmpty() == true && node.childCount == 0) {
node.text ?: ""
/*} else if (event?.contentDescription?.isNotEmpty() == true) {
event.contentDescription ?: ""
} else if (node.childCount == 0 && node.stateDescription != event?.text?.getOrNull(0) ?: "") {
event?.text?.getOrNull(0) ?: ""
*/} else {
"" 
}
val stateDescription  = if (node.stateDescription?.isNotEmpty() == true) {
node.stateDescription ?: ""
} else if (node.isCheckable) {
getStateDescription(node)
} else { 
""
}
fun getStateDescription(node: AccessibilityNodeInfo): CharSequence {
return context.getString(
getStateDescription(node.className ?: "", node.isChecked))
}
fun getStateDescription(className: CharSequence, isChecked: Boolean): Int {
    return when (className) {
        "android.widget.CheckBox" -> {
            if (isChecked) R.string.state_checked else R.string.state_not_checked
        }
        "android.widget.Switch" -> {
            if (isChecked) R.string.state_on else R.string.state_off
        }
        "android.widget.RadioButton", "android.widget.CompoundButton", "android.widget.ToggleButton" -> {
            if (isChecked) R.string.state_selected else R.string.state_not_selected
        }
        else -> {
            if (isChecked) R.string.state_checked else R.string.state_not_checked
        }
    }
}

val selectStatus = if (node.isSelected) {
context.getString(R.string.state_selected)
} else {
""
}
var hintText = if (!node.isShowingHintText
 || node.actionList.indexOf(AccessibilityAction.ACTION_SET_TEXT) == -1) {
node.hintText ?: ""
} else {
""
}
var roleDescription = getRole(node.className ?: "")
fun getRole(cln: CharSequence): CharSequence {
if (node.roleDescription?.isNotEmpty() == true) {
return node.roleDescription ?: ""
}
val roles = mutableListOf<Int>()
if (node.isPassword || event?.isPassword == true) {
roles.add(R.string.role_passwd)
}
if (cln == "android.widget.Button" ||
cln == "android.widget.ImageButton" ||
cln == "android.widget.ImageView" && node.hasAnyClickable) {
roles.add(R.string.role_button)
} 
if (cln == "android.widget.ImageView" && !node.hasAnyClickable) {
roles.add(R.string.role_image)
}
if (cln == "android.widget.Switch") {
roles.add(R.string.role_switch)
} 
if (cln == "android.widget.CheckBox") {
roles.add(R.string.role_checkbox)
}
if (cln == "android.widget.RadioButton") {
roles.add(R.string.role_radiobutton)
}
if (node.isHeading) {
roles.add(R.string.role_heading)
}
if (cln == "android.widget.SeekBar" ||
node.actionList.indexOf(AccessibilityAction.ACTION_SET_PROGRESS) != -1) {
roles.add(R.string.role_seekbar)
} else if (cln == "android.widget.ProgressBar"
|| node.rangeInfo != null) {
roles.add(R.string.role_progressbar)
}
if (cln == "android.widget.EditText") {
roles.add(R.string.role_edittext)
}
if (cln == "android.webkit.WebView" || node.isWebView) {
roles.add(R.string.role_webview)
}

if (cln == "android.widget.Spinner" || node.canOpenPopup()) {
roles.add(R.string.role_drop_down_list)
}
val sb = StringBuilder()
for (r in roles) {
sb.append(context.getString(r)).append(" ")
}
return sb.toString()
}

fun getText(flags: Int = ALL_FLAGS): CharSequence {
if (label.isNotEmpty()) {
return label
}
val sb = StringBuilder()
node.text?.let {
sb.append(it)
}
for (i in 0 until node.childCount) {
val child = node.getChild(i) ?: continue
val childC = Compositor(context,event, child)
if (!child.isVisibleToUser ||
child.isSeekBar || child.hasAnyClickable || child.hasFocusable) {
continue
}
if (flags and FLAG_INCLUDE_STATE_DESCRIPTION != 0 &&
childC.stateDescription.isNotEmpty()) {
if (sb.isNotEmpty()) {
sb.append("\n")
}
sb.append(childC.stateDescription)
}
if (childC.getText().isNotEmpty()) {
if (sb.isNotEmpty()) {
sb.append("\n")
}
sb.append(childC.getText(0).toString())
}
if (childC.roleDescription.isNotEmpty() && roleDescription.isEmpty()) {
roleDescription = childC.roleDescription
}
if (childC.hintText.isNotEmpty() && hintText.isEmpty()) {
hintText = childC.hintText
}
}
if (flags and FLAG_INCLUDE_NOT_IMPORTANT_LABELS != 0 &&
sb.isEmpty() && event != null) {
for (text in event.text) {
if (!sb.isEmpty()) {
sb.append("\n")
}
if (text == node.stateDescription) {
continue
}
sb.append(text ?: continue)
}
}
return sb.toString()
}
}
