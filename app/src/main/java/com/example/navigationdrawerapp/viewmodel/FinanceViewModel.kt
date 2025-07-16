package com.example.navigationdrawerapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigationdrawerapp.api.FinanceApiService
import com.example.navigationdrawerapp.api.RetrofitClient
import com.example.navigationdrawerapp.model.BistResponse
import com.example.navigationdrawerapp.model.CriptoResponse
import com.example.navigationdrawerapp.model.CurrencyResponse
import com.example.navigationdrawerapp.model.EmtiaResponse
import com.example.navigationdrawerapp.model.ExchangeResponse
import com.example.navigationdrawerapp.model.GoldResponse
import com.example.navigationdrawerapp.model.SilverResponse
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

    //Kıymetli Madenler LiveData'ları
    private val _goldData = MutableLiveData<GoldResponse?>()
    val goldData: LiveData<GoldResponse?> = _goldData

    private val _silverData = MutableLiveData<SilverResponse?>()
    val silverData: LiveData<SilverResponse?> = _silverData

    //Kripto Paralar
    private val _criptoData = MutableLiveData<CriptoResponse>()
    val criptoData: LiveData<CriptoResponse> get() = _criptoData

    //Emtia
    private val _emtiaData = MutableLiveData<EmtiaResponse?>()
    val emtiaData: LiveData<EmtiaResponse?> get() = _emtiaData

    //Dönüşüm için
    private val _exchangeData = MutableLiveData<ExchangeResponse?>()
    val exchangeData: LiveData<ExchangeResponse?> get() = _exchangeData


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
                    _errorMessage.value =
                        "BIST verileri çekilemedi: ${response.code()} - $errorBody"
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
                    _errorMessage.value =
                        "Döviz verileri çekilemedi: ${response.code()} - $errorBody"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Hata oluştu: ${e.localizedMessage}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    //Altın ve Gümüş verilerini çeken fonksiyonlar
    fun fetchGoldPrice() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getGoldPrice()
                if (response.isSuccessful) {
                    _goldData.value = response.body()
                } else {
                    _errorMessage.value = "Altın verileri alınamadı: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Hata: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchSilverPrice() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getSilverPrice()
                if (response.isSuccessful) {
                    _silverData.value = response.body()
                } else {
                    _errorMessage.value = "Gümüş verileri alınamadı: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Hata: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }

    }


    //Kripto Para verilerini çeken fonksiyon
    fun fetchCriptoPrice() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getCriptoPrice()
                if (response.isSuccessful) {
                    _criptoData.value = response.body()
                } else {
                    _errorMessage.value = "Kripto verileri alınamadı: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Hata: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    //Emtia verilerini çeken fonksiyon
    fun fetchEmtiaData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = apiService.getEmtiaData()
                if (response.isSuccessful) {
                    _emtiaData.value = response.body()
                } else {
                    _errorMessage.value = "Emtia verileri yüklenemedi: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Hata: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    /**
     * Para birimi dönüştürme işlemini gerçekleştirir.
     *
     * @param baseCurrency Dönüştürülecek ana para birimi kodu (örn: "USD").
     * @param toCurrency Dönüştürülecek hedef para birimi kodu (örn: "TRY").
     * @param amount Dönüştürülecek miktar.
     */


    fun convertCurrency(baseCurrency: String, toCurrency: String, amount: String) {
        // Miktar boş veya sıfırsa işlemi yapma
        if (amount.isNullOrEmpty() || amount.toDoubleOrNull() == 0.0) {
            _exchangeData.value = null
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response =
                    apiService.convertCurrency(baseCurrency, toCurrency, amount)
                if (response.isSuccessful) {
                    _exchangeData.value = response.body()
                } else {
                    _errorMessage.value = "Dönüştürme işlemi başarısız: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Hata: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
