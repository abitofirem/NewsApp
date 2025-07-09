package com.example.navigationdrawerapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigationdrawerapp.api.WeatherApiService // WeatherApiService import edildi
import com.example.navigationdrawerapp.model.WeatherResponse // Model import edildi
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor // Loglama için
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class WeatherViewModel : ViewModel() {

    private val API_KEY = "apikey 2gmUrMjHzi3aQLY6FYXbhE:078zdz0PXeIEbP5VbRNstp" // ÖNEMLİ: KENDİ ANAHTARINIZLA DEĞİŞTİRİN!

    //CollectAPI'nin ortak BASE_URL'i
    private val BASE_URL = "https://api.collectapi.com/"

    //Bu ViewModel için özel OkHttpClient.
    //Sadece bu ViewModel'in API isteklerine API anahtarını ekler.
    private val weatherOkHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { //Loglama Interceptor'ı
                setLevel(HttpLoggingInterceptor.Level.BODY)
            })
            .addInterceptor { chain -> //API anahtarını ekleyen Interceptor
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("content-type", "application/json")
                    .header("authorization", API_KEY) //Bu servise özel API anahtarını ekle
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // WeatherApiService'i, bu ViewModel'e özel OkHttpClient ile oluşturuyoruz
    private val weatherApiService: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(weatherOkHttpClient) // Kendi özel OkHttpClient'ımızı kullanıyoruz
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }

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