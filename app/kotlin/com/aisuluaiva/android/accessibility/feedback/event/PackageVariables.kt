package com.aisuluaiva.android.accessibility.feedback.event

/**
* Global variables for this package
*/
object PackageVariables {
 var _fingerOnScreen = false
var fingerOnScreen: Boolean 
get() = _fingerOnScreen
set(value) {
for (fingerListener in fingerListeners) {
fingerListener.onChanged(value)
}
_fingerOnScreen = value
}
private val fingerListeners = mutableListOf<OnFingerOnScreenChangeListener>()
fun setOnFingerOnScreenChangeListener(l: OnFingerOnScreenChangeListener) {
this.fingerListeners.add(l)
}
}

fun interface OnFingerOnScreenChangeListener {
fun onChanged(fON: Boolean)
}
