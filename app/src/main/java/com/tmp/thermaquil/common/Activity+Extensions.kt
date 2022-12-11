package com.tmp.thermaquil.common

import android.app.Activity
import com.tmp.thermaquil.CustomApplication

val Activity.customApplication: CustomApplication
get() = application as CustomApplication