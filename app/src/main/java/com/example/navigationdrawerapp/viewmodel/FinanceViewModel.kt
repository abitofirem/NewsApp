package com.example.navigationdrawerapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigationdrawerapp.api.FinanceApiService
import com.example.navigationdrawerapp.model.BistResponse
import com.example.navigationdrawerapp.model.CurrencyResponse
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class FinanceViewModel : ViewModel() {


    //API key için
    private val API_KEY = "apikey 1He3fTb1Yz28ySCsfPnGhT:1p4LcRShJjq1eDw90XE0M1"
    private val BASE_URL = "https://api.collectapi.com/"

    //API çağrılarını yönetecek Retrofit servisi
    private val apiService: FinanceApiService by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY //Ağ isteklerinin loglarını görmek için
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("content-type", "application/json")
                    .header("authorization", API_KEY)
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FinanceApiService::class.java)
    }

    //LiveData'lar
    private val _bistData = MutableLiveData<BistResponse?>()
    val bistData: LiveData<BistResponse?> = _bistData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage


    //DÖVİZ KURLARI İÇİN YENİ LİVEDATA DEĞİŞKENLERİ
    private val _currencyData = MutableLiveData<CurrencyResponse?>()
    val currencyData: LiveData<CurrencyResponse?> = _currencyData

    //BIST 100 verilerini çeken fonksiyon
    fun fetchBistData() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val response = apiService.getBistData()
                if (response.isSuccessful) {
                    _bistData.value = response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = "BIST verileri çekilemedi: ${response.code()} - $errorBody"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Hata oluştu: ${e.localizedMessage}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    //DÖVİZ KURLARI İÇİN YENİ FONKSİYON
    fun fetchAllCurrencyData() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val response = apiService.getAllCurrencyData()
                if (response.isSuccessful) {
                    _currencyData.value = response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = "Döviz verileri çekilemedi: ${response.code()} - $errorBody"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Hata oluştu: ${e.localizedMessage}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }



}