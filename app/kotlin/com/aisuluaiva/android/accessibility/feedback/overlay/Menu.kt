package com.aisuluaiva.android.accessibility.feedback.overlay
import android.app.AlertDialog
import android.content.Context
import android.os.Looper
import android.os.Handler
import android.view.LayoutInflater
import android.view.Gravity
import android.view.WindowManager.LayoutParams
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.util.Log
import com.aisuluaiva.android.accessibility.feedback.R

open class Menu(private val context: Context,
private val title: Int,
 val handler: Handler = Handler(Looper.getMainLooper())) {
protected val layoutInflater = LayoutInflater.from(context)
protected val dialogView = layoutInflater.inflate(R.layout.menu, null)
protected val listView: ListView = dialogView.findViewById(R.id.list_view)
protected lateinit var dialog: AlertDialog
init {
handler.post {
dialog = AlertDialog.Builder(context, R.style.activity)
.setTitle(title)
.setView(dialogView)
.create()
dialog.getWindow()?.let {
it.setLayout(LayoutParams.MATCH_PARENT,
LayoutParams.WRAP_CONTENT)
it.setGravity(Gravity.CENTER)
val lp = it.getAttributes()
lp.type = LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
}
val cancel = dialogView.findViewById<Button>(R.id.cancel)
cancel.setOnClickListener {
dismiss()
}
}
}
var displayId = 0
open fun dismiss() = dialog.dismiss()
open fun show(displayId: Int = 0) {
this.displayId = displayId
try {
handler.post {
 dialog.show()
}
} catch (e: Exception) {
Log.e("Menu", "$e")
}
}
}
