package com.example.navigationdrawerapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigationdrawerapp.api.PharmacyApiService
import com.example.navigationdrawerapp.api.RetrofitClient
import com.example.navigationdrawerapp.model.Pharmacy
import kotlinx.coroutines.launch


class PharmacyViewModel : ViewModel() {

    //RetrofitClient'tan API servis örneğini alıyoruz
    private val apiService: PharmacyApiService = RetrofitClient.pharmacyApiService

    //Nöbetçi eczane listesini tutacak LiveData
    private val _pharmacies = MutableLiveData<List<Pharmacy>>()
    val pharmacies: LiveData<List<Pharmacy>> = _pharmacies

    //Yüklenme durumunu (loading) tutacak LiveData
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    //Hata mesajlarını tutacak LiveData
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    //Belirtilen şehir ve isteğe bağlı olarak ilçe için nöbetçi eczaneleri getirir.
    fun fetchDutyPharmacies(city: String?, district: String?) {
        //Eğer zaten yükleniyorsa, tekrar istek gönderme
        if (_isLoading.value == true) {
            return
        }

        //UI'dan gelen şehir girdisinin null veya boş olup olmadığını kontrol et.
        //Eğer öyleyse, bir hata göster ve dur.
        if (city.isNullOrBlank()) {
            _errorMessage.value = "Lütfen bir şehir adı giriniz."
            _pharmacies.value = emptyList() //Listeyi boşalt
            return
        }

        _isLoading.value = true //Yüklemeyi başlat
        _errorMessage.value = null //Önceki hataları temizle

        viewModelScope.launch {
            try {
                //city.isNullOrBlank() kontrolünü zaten yaptığımız için,
                //Kotlin'in akıllı dönüştürmesi (smart-cast) 'city'yi burada null olamayan bir String olarak algılayacaktır.
                //'city' değerini doğrudan 'il' parametresine ve 'district' değerini 'ilce' parametresine iletiyoruz.
                val response = apiService.getDutyPharmacies(il = city, ilce = district)

                if (response.isSuccessful) {
                    response.body()?.let { pharmacyResponse ->
                        if (pharmacyResponse.success) {
                            _pharmacies.value = pharmacyResponse.result
                        } else {
                            _errorMessage.value = "API hatası: ${pharmacyResponse.result}"
                            _pharmacies.value = emptyList()
                        }
                    } ?: run {
                        //Boş API yanıtı geldiğinde
                        _errorMessage.value = "Boş API yanıtı."
                        _pharmacies.value = emptyList()
                    }
                } else {
                    //HTTP isteği başarısız olursa (örn: 404, 500 hataları)
                    _errorMessage.value = "Sunucu hatası: ${response.code()} - ${response.message()}"
                    _pharmacies.value = emptyList()
                }
            } catch (e: Exception) {
                //Ağ bağlantısı veya diğer beklenmeyen hatalar oluştuğunda
                _errorMessage.value = "Bir hata oluştu: ${e.localizedMessage ?: "Bilinmeyen Hata"}"
                _pharmacies.value = emptyList()
            } finally {
                _isLoading.value = false //Yüklemeyi bitir (hata olsa da olmasa da)
            }
        }
    }
}