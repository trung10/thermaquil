package com.tmp.thermaquil.ui.phone

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PhoneViewModel : ViewModel() {

    private val _state = MutableLiveData(1)
    val state
        get() = _state

    fun getState() = _state.value

    fun updatePhone(phone: String) {
        _state.value = 2
    }

    fun checkOtp(otp: String) {

    }
}