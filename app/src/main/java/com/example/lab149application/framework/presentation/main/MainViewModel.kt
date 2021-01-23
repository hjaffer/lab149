package com.example.lab149application.framework.presentation.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.lab149application.business.domain.models.SnapDao
import com.example.lab149application.business.domain.state.DataState
import com.example.lab149application.business.interactors.GetSnap
import com.example.lab149application.business.interactors.PostSnap
import com.example.lab149application.framework.network.model.snap.SnapMatch
import com.example.lab149application.framework.presentation.livedata.SingleLiveEvent
import com.example.lab149application.framework.presentation.util.DateUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

@ExperimentalCoroutinesApi
class MainViewModel
@ViewModelInject
constructor(
    private val getSnap: GetSnap,
    private val postSnap: PostSnap
) : ViewModel() {
    private val _timerValue = MutableLiveData("")
    internal val timerValue:LiveData<String> = _timerValue
    private val timerKeepRunning = AtomicBoolean(true)
    private var timerEndtTime: Long = 0
    private var timerJob: Job? = null
    private val timerUpdateMilliseconds: Long = 100

    private val _getSnapsDataState: MutableLiveData<DataState<List<SnapDao>>> = MutableLiveData()
    internal val getSnapsDataState: LiveData<DataState<List<SnapDao>>>
        get() = _getSnapsDataState

    private val _snapResult: MutableLiveData<DataState<SnapMatch>> = MutableLiveData()
    internal val snapResult: LiveData<DataState<SnapMatch>>
        get() = _snapResult

    private val _timerExpired: SingleLiveEvent<Boolean> = SingleLiveEvent()
    internal val timerExpired: LiveData<Boolean> = _timerExpired

    fun fetchSnaps() {
        setStateEvent(MainStateEvent.GetSnapsEvent)
    }

    fun postASnap(ImageLabel: String, Image: String) {
        viewModelScope.launch {
            postSnap.execute(ImageLabel, Image)
                .onEach { snapMatch ->
                    _snapResult.value = snapMatch
                }
                .launchIn(viewModelScope)
        }
    }

    fun startTimer(totalTime: Long) {
        stopTimer()
        timerKeepRunning.set(true)
        _timerExpired.value = false
        timerEndtTime = System.currentTimeMillis() + totalTime
        timerJob = viewModelScope.launch {
            while(timerKeepRunning.get()) {
                delay(timerUpdateMilliseconds)
                val timeRemaining = timerEndtTime - System.currentTimeMillis()
                if (timeRemaining > 0) {
                    _timerValue.postValue(DateUtil.converter(timeRemaining))
                } else {
                    timerKeepRunning.set(false)
                    _timerExpired.postValue(true)
                }
            }
        }
    }

    private fun stopTimer() {
        timerKeepRunning.set(false)
        timerJob?.cancel()
    }

    private fun setStateEvent(mainStateEvent: MainStateEvent) {
        when (mainStateEvent) {
            is MainStateEvent.GetSnapsEvent -> {
                viewModelScope.launch {
                    getSnap.execute()
                        .onEach { dataState ->
                            _getSnapsDataState.value = dataState
                        }
                        .launchIn(viewModelScope)
                }
            }
            is MainStateEvent.None -> {
                // no action
            }
        }
    }
}

sealed class MainStateEvent {
    object GetSnapsEvent : MainStateEvent()
    object None : MainStateEvent()
}
