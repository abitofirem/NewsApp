package com.example.navigationdrawerapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigationdrawerapp.model.WeatherResponse
import com.example.navigationdrawerapp.repository.WeatherRepository
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository(com.example.navigationdrawerapp.api.RetrofitClient.weatherApiService)

    // LiveData'lar
    private val _weatherData = MutableLiveData<WeatherResponse?>()
    val weatherData: LiveData<WeatherResponse?> = _weatherData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Hava durumu verilerini çeken fonksiyon
    fun fetchWeatherData(city: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val response = repository.getWeatherForecast(city)
                if (response.isSuccessful) {
                    _weatherData.value = response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = "Hava durumu çekilemedi: ${response.code()} - $errorBody"
                    _weatherData.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "Hata oluştu: ${e.localizedMessage}"
                _weatherData.value = null
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}