package com.tmp.thermaquil.activities

import android.util.Log
import androidx.arch.core.executor.DefaultTaskExecutor
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tmp.thermaquil.common.Event
import com.tmp.thermaquil.data.models.SEND_STATE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor() : ViewModel() {
    private var _sendState = MutableLiveData(SEND_STATE.SUCCESS)
    val sendState: LiveData<SEND_STATE>
        get() = _sendState

    private var _resume = MutableLiveData(false)
    val resume: LiveData<Boolean>
        get() = _resume

    private var _ready = MutableLiveData<Boolean>()
    val ready: LiveData<Boolean>
        get() = _ready

    fun setResume(s: Boolean) {
        _resume.postValue(s)
    }

    fun setSendState(s: SEND_STATE) {
        _sendState.postValue(s)
    }

    fun setReadyState(s: Boolean) {
        _ready.postValue(s)
        Log.d("Trung", "ready: $s")
    }
}