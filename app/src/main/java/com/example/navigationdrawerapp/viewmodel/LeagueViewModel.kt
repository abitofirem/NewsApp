package com.example.navigationdrawerapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigationdrawerapp.model.League
import com.example.navigationdrawerapp.repository.FootballRepository
import kotlinx.coroutines.launch

class LeagueViewModel : ViewModel() {

    private val repository = FootballRepository(com.example.navigationdrawerapp.api.RetrofitClient.footballApiService)

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
                val response = repository.getLeagues()
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