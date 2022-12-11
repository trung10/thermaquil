package com.tmp.thermaquil.common

import android.util.Log
import com.tmp.thermaquil.BuildConfig

object Logger {

    fun log(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message)
        }
    }

}