package com.example.navigationdrawerapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigationdrawerapp.model.*
import com.example.navigationdrawerapp.repository.FinanceRepository
import kotlinx.coroutines.launch

class FinanceViewModel : ViewModel() {

    private val repository = FinanceRepository(com.example.navigationdrawerapp.api.RetrofitClient.financeApiService)

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

    // BIST 100 verilerini çeken fonksiyon
    fun fetchBistData() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val response = repository.getBistRates()
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

    // DÖVİZ KURLARI İÇİN YENİ FONKSİYON
    fun fetchAllCurrencyData() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val response = repository.getCurrencyRates()
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

    // Altın ve Gümüş verilerini çeken fonksiyonlar
    fun fetchGoldPrice() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getGoldRates()
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
                val response = repository.getSilverRates()
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

    // Kripto Para verilerini çeken fonksiyon
    fun fetchCriptoPrice() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getCryptoRates()
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

    // Emtia verilerini çeken fonksiyon
    fun fetchEmtiaData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = repository.getEmtiaRates()
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

    // Para birimi dönüştürme işlemi
    fun convertCurrency(baseCurrency: String, toCurrency: String, amount: String) {
        if (amount.isNullOrEmpty() || amount.toDoubleOrNull() == 0.0) {
            _exchangeData.value = null
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = repository.convertCurrency(baseCurrency, toCurrency, amount)
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