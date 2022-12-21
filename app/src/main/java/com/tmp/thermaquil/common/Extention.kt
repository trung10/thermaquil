package com.tmp.thermaquil.common

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.tmp.thermaquil.R

fun Activity.toast(s: String) {
    Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(s: String) {
    Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
}

fun Activity.getPhone(): String? {
    val sharedPrefBLE =
        this.getSharedPreferences(getString(R.string.ble_device_key), Context.MODE_PRIVATE)
    return sharedPrefBLE.getString("phone", null)
}

fun Fragment.getPhone(): String? {
    val sharedPrefBLE =
        requireActivity().getSharedPreferences(getString(R.string.ble_device_key), Context.MODE_PRIVATE)
    return sharedPrefBLE.getString("phone", null)
}

fun Fragment.setPhone(s: String) {
    val sharedPref: SharedPreferences =
        requireActivity().getSharedPreferences(
            getString(R.string.ble_device_key),
            Context.MODE_PRIVATE
        )

    val prefBleDeviceEditor = sharedPref.edit()
    prefBleDeviceEditor.putString("phone", s)
    prefBleDeviceEditor.apply()
}


