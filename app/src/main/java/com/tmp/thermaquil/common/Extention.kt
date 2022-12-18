package com.tmp.thermaquil.common

import android.app.Activity
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Activity.toast(s: String) {
    Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(s: String) {
    Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
}
