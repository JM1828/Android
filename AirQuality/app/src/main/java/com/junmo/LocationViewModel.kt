package com.junmo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// 위치 정보를 저장하고 공유하기 위한 ViewModel
class LocationViewModel : ViewModel() {
    private val _latitude = MutableLiveData<Double>()
    val latitude: LiveData<Double>
        get() = _latitude

    private val _longitude = MutableLiveData<Double>()
    val longitude: LiveData<Double>
        get() = _longitude

    fun setLatitude(latitude: Double) {
        _latitude.value = latitude
    }

    fun setLongitude(longitude: Double) {
        _longitude.value = longitude
    }
}