package com.aisuluaiva.android.accessibility.feedback.overlay
import android.content.Context
import android.widget.ArrayAdapter
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log
import android.text.Spanned
import android.text.style.ClickableSpan
import com.aisuluaiva.android.accessibility.feedback.R

open class LinksMenu(private val context: Context,
private val node: AccessibilityNodeInfo) : Menu(context, R.string.links) {
init {
val adapter = ArrayAdapter(context, R.layout.menu_item, getLinkNames())
listView.adapter = adapter
val spans = getClickableSpans(node)
listView.setOnItemClickListener { _, _, position, _ ->
super.dismiss()
spans[position].onClick(android.view.View(context))
}
}
companion object {
fun getClickableSpans(node: AccessibilityNodeInfo): List<ClickableSpan> {
val text = node.text
if (text is Spanned) {
val spanned = text as Spanned
return spanned.getSpans(0, text.length, ClickableSpan::class.java).toList()
}
return listOf<ClickableSpan>()
}
}
fun getLinkNames(): List<CharSequence> {
val spans = getClickableSpans(node)
val links = mutableListOf<CharSequence>()
val text = node.text
val spanned = (text as? Spanned) ?: return listOf()
for (span in spans) {
val spanStart = spanned.getSpanStart(span) ?: -1
val spanEnd = spanned.getSpanEnd(span) ?: -1
val spanName = if (spanEnd > -1 && spanStart > -1) {
text.substring(spanStart, spanEnd)
} else {
""
}
links.add(spanName)
}
return links
}
}