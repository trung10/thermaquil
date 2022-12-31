package com.tmp.thermaquil.ui.phone

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.flow

class PhoneViewModel : ViewModel() {

    private val _state = MutableLiveData(1)
    val state
        get() = _state

    private val _timer = MutableLiveData(0)
    val time
        get() = _timer

    private var timer: CountDownTimer? = null

    fun getState() = _state.value
    fun updatePhone(phone: String) {
        _state.value = 2
        timer?.cancel()
    }

    fun checkOtp(otp: String) {

    }

    fun setTimer() {
        timer = object: CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _timer.postValue((millisUntilFinished/1000).toInt())
            }

            override fun onFinish() {
                _timer.postValue(-1)
            }
        }
        timer?.start()
    }

    override fun onCleared() {
        timer?.cancel()
        super.onCleared()
    }
}