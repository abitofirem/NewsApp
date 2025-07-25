package com.example.navigationdrawerapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigationdrawerapp.model.League
import com.example.navigationdrawerapp.api.FootballApiService // import et!
import com.example.navigationdrawerapp.api.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class LeagueViewModel : ViewModel() {

    private val footballApiService: FootballApiService = RetrofitClient.footballApiService // <-- Burayı değiştirdik




    private val _leagues = MutableLiveData<List<League>?>()
    val leagues: LiveData<List<League>?> = _leagues

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun fetchLeagues() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val response = footballApiService.getLeagues()
                if (response.isSuccessful) {
                    _leagues.value = response.body()?.result
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = "Ligler çekilemedi: ${response.code()} - $errorBody"
                    _leagues.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "Hata oluştu: ${e.localizedMessage}"
                _leagues.value = null
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}