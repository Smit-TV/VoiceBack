package com.aisuluaiva.android.accessibility.feedback.overlay
import android.content.Context
import android.widget.ArrayAdapter
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log
import com.aisuluaiva.android.accessibility.feedback.R

open class ActionsMenu(private val context: Context,
private val node: AccessibilityNodeInfo) : Menu(context, R.string.actions) {
init {
val adapter = ArrayAdapter(context, R.layout.menu_item, getActionNames())
listView.adapter = adapter
val actions = getActionIds(node)
listView.setOnItemClickListener { _, _, position, _ ->
super.dismiss()
val action = actions[position]
node.performAction(action)
}
}
companion object {
fun getActionIds(node: AccessibilityNodeInfo): List<Int> {
val actions = mutableListOf<Int>()
for (action in getActions(node)) {
if (action.label != null) {
actions.add(action.id)
}
}
return actions
}
fun getActions(node: AccessibilityNodeInfo): List<AccessibilityNodeInfo.AccessibilityAction> {
val actions = mutableListOf<AccessibilityNodeInfo.AccessibilityAction>()
for (action in node.actionList) {
if (action.label != null) {
actions.add(action)
}
}
return actions
}
}
fun getActionNames(): List<String> {
val actions = getActions(node)
val actionList = node.actionList
val actionNames = mutableListOf<String>()
for (action in actions) {
try {
actionNames.add("${action.label}")
} catch (e: Exception) {}
}
return actionNames
}
}
