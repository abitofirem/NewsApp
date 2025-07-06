package com.example.navigationdrawerapp.ui.viewmodel // Projenizin ViewModel paketi

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope //Coroutine Scope için
import com.example.navigationdrawerapp.api.RetrofitClient //RetrofitClient'ı import ettik
import com.example.navigationdrawerapp.model.League //League modelini import ettik
import kotlinx.coroutines.launch //Coroutine başlatmak için
import java.io.IOException //Ağ hataları için
import retrofit2.HttpException //HTTP hataları için

class LeagueViewModel : ViewModel() {

    //Lig listesini tutan ve Fragment tarafından gözlemlenecek LiveData
    private val _leagues = MutableLiveData<List<League>?>()
    val leagues: LiveData<List<League>?> get() = _leagues //Dışarıdan sadece okunabilir

    //Yükleme durumunu gösteren LiveData (opsiyonel: ProgressBar için)
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    //Hata mesajlarını tutan LiveData
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    //Lig verilerini API'den çeken metod
    fun fetchLeagues() {
        // Eğer zaten yükleniyorsa tekrar istek gönderme
        if (_isLoading.value == true) {
            return
        }

        _isLoading.value = true //Yüklemeye başladığımızı belirt
        _errorMessage.value = null //Önceki hata mesajını temizle

        //Coroutine başlat: Ağ isteğini ana iş parçacığından ayır
        viewModelScope.launch {
            try {
                //RetrofitClient üzerinden FootballApiService'a eriş ve ligleri çek
                val response = RetrofitClient.footballApiService.getLeagues()

                if (response.success) { //API yanıtı başarılıysa
                    _leagues.value = response.result //Gelen lig listesini LiveData'ya ata
                } else { //API yanıtı başarısızsa (success: false)
                    val errorMsg = "API'den hata döndü: Ligler çekilemedi."
                    _errorMessage.value = errorMsg
                    Log.e("LeagueViewModel", errorMsg)
                    _leagues.value = null //Hata durumunda listeyi boşalt veya null yap
                }
            } catch (e: HttpException) { //HTTP ile ilgili hatalar (404, 500 vb.)
                val errorMsg = "HTTP Hatası: ${e.message()}"
                _errorMessage.value = errorMsg
                Log.e("LeagueViewModel", errorMsg, e)
                _leagues.value = null
            } catch (e: IOException) { //Ağ bağlantısı hataları (internet yok vb.)
                val errorMsg = "Ağ Hatası: İnternet bağlantınızı kontrol edin. ${e.message}"
                _errorMessage.value = errorMsg
                Log.e("LeagueViewModel", errorMsg, e)
                _leagues.value = null
            } catch (e: Exception) { //Diğer tüm bilinmeyen hatalar
                val errorMsg = "Bilinmeyen Hata: ${e.message}"
                _errorMessage.value = errorMsg
                Log.e("LeagueViewModel", errorMsg, e)
                _leagues.value = null
            } finally {
                _isLoading.value = false //Yükleme tamamlandı veya hata oluştu
            }
        }
    }
}