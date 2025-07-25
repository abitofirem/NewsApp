package com.example.navigationdrawerapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigationdrawerapp.api.RetrofitClient
import com.example.navigationdrawerapp.api.WeatherApiService // WeatherApiService import edildi
import com.example.navigationdrawerapp.model.WeatherResponse // Model import edildi
import kotlinx.coroutines.launch


class WeatherViewModel : ViewModel() {

    private val weatherApiService: WeatherApiService = RetrofitClient.weatherApiService // <-- Burayı değiştirin




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
                //API anahtarını zaten OkHttpClient interceptor'ı eklediği için
                //getWeather fonksiyonuna authorization parametresi göndermiyoruz.
                val response = weatherApiService.getWeather(city = city) //authorization parametresi yok!
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