package com.tmp.thermaquil.ui.treament

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tmp.thermaquil.data.models.Data
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class TreatmentViewModel @Inject constructor() : ViewModel() {

    var timer: CountDownTimer? = null

    var currentCycle= Data.defaultTreatment.cycles[0]
    var count = 0

    var isHot = true

    private var currentTimeDown: Long = 0
    private val _time = MutableLiveData("")
    val time: LiveData<String>
        get() = _time

    private var _temp = MutableLiveData<Float>()
    val temp: LiveData<Float>
        get() = _temp

    private var _dataTempSend = MutableLiveData<Float>()
    val dataTempSend: LiveData<Float>
        get() = _dataTempSend

    private var _dataTimeSend = MutableLiveData<Int>()
    val dataTimeSend: LiveData<Int>
        get() = _dataTimeSend

    private var _endTreatment = MutableLiveData<Boolean>()
    val endTreatment: LiveData<Boolean>
        get() = _endTreatment


    var isChanging = false

    fun startTreatment() {
        Log.d("Trung", "startTreatment entry")
        _temp.postValue(currentCycle.hotSetPont)
        currentTimeDown = currentCycle.hotTime*1000.toLong()
        createTimer()
    }

    private var timeChange: Long = 0L
    fun changeTime(t: Int){
        isChanging = true
        if (timeChange == 0L)
            timeChange = currentTimeDown + t * 1000
        else {
            timeChange += t * 1000
        }
        sendTimeDebounced((timeChange / 1000).toInt())

        val m: Int = (timeChange/(60 * 1000)).toInt()
        val s = timeChange/1000 - m * 60
        _time.postValue(String.format(Locale.US, "%02d:%02d", m, s))

        //createTimer()
    }

    fun changeDegree(t: Float){
        isChanging = true
        sendTempDebounced(_temp.value!!.plus(t))
        _temp.postValue(_temp.value?.plus(t))
    }



    fun createTimer() {
        timer?.cancel()
        timer = object: CountDownTimer(currentTimeDown, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                currentTimeDown = millisUntilFinished

                val m: Int = (millisUntilFinished/(60 * 1000)).toInt()
                val s = millisUntilFinished/1000 - m * 60

                if (!isChanging)
                    _time.postValue(String.format(Locale.US, "%02d:%02d", m, s))
            }

            override fun onFinish() {
                skip()
            }
        }
        timer?.start()
    }

    private var sendJob: Job? = null
    fun sendTempDebounced(f: Float) {
        sendJob?.cancel()
        sendJob = viewModelScope.launch {
            delay(500)
            _dataTempSend.postValue(f)
        }
    }

    private var sendTimeJob: Job? = null
    fun sendTimeDebounced(t: Int) {
        sendTimeJob?.cancel()
        sendTimeJob = viewModelScope.launch {
            delay(500)
            _dataTimeSend.postValue(t)
        }
    }

    fun sendSuccess() {
        isChanging = false
        if (timeChange > 0){
            currentTimeDown = (timeChange * 1000)
            createTimer()
            timeChange = 0L
        }
    }

    fun sendFail() {
        isChanging = false
        timeChange = 0L
    }

    fun skip() {
        count++

        if (count == 2){
            _endTreatment.postValue(true)
        }

        isHot = !isHot
        if (isHot) {
            currentTimeDown = (currentCycle.hotTime * 1000).toLong()
            _temp.postValue(currentCycle.hotSetPont)
        } else {
            _temp.postValue(currentCycle.coldSetPont)
            currentTimeDown = (currentCycle.coldTime * 1000).toLong()
        }

        createTimer()
    }

    fun resume() {
        createTimer()
    }

    fun pause() {
        timer?.cancel()
    }
}