package com.example.navigationdrawerapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigationdrawerapp.model.TeamStanding
import com.example.navigationdrawerapp.model.GoalKing
import com.example.navigationdrawerapp.model.MatchResult
import com.example.navigationdrawerapp.repository.FootballRepository
import kotlinx.coroutines.launch
import java.io.IOException
import retrofit2.HttpException

class LeagueDetailViewModel : ViewModel() {

    private val repository = FootballRepository(com.example.navigationdrawerapp.api.RetrofitClient.footballApiService)

    // Puan Durumu LiveData'ları
    private val _standings = MutableLiveData<List<TeamStanding>?>()
    val standings: LiveData<List<TeamStanding>?> = _standings

    // Fikstür LiveData'ları
    private val _matchResults = MutableLiveData<List<MatchResult>?>()
    val matchResults: LiveData<List<MatchResult>?> = _matchResults

    // Gol Krallığı LiveData'ları
    private val _goalKings = MutableLiveData<List<GoalKing>?>()
    val goalKings: LiveData<List<GoalKing>?> = _goalKings

    // Ortak Yükleme ve Hata LiveData'ları
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun fetchStandings(leagueKey: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val response = repository.getStandings(leagueKey)
                if (response.isSuccessful) {
                    _standings.value = response.body()?.result
                    if (response.body()?.result.isNullOrEmpty()) {
                        _errorMessage.value = "Puan durumu verisi bulunamadı."
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = "Puan durumu çekilemedi: ${response.code()} - ${errorBody ?: "Bilinmeyen hata"}"
                    _standings.value = null
                }
            } catch (e: IOException) {
                _errorMessage.value = "Ağ bağlantı hatası: ${e.localizedMessage}"
                _standings.value = null
                e.printStackTrace()
            } catch (e: HttpException) {
                _errorMessage.value = "API hatası: ${e.code()} - ${e.message()}"
                _standings.value = null
                e.printStackTrace()
            } catch (e: Exception) {
                _errorMessage.value = "Beklenmedik bir hata oluştu: ${e.localizedMessage}"
                _standings.value = null
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchMatchResults(leagueKey: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val response = repository.getMatchResults(leagueKey)
                if (response.isSuccessful) {
                    val matchResultResponse = response.body()
                    Log.d("API_RESPONSE", "Match Results Raw Response: ${matchResultResponse.toString()}")
                    
                    if (matchResultResponse?.success == true && !matchResultResponse.result.isNullOrEmpty()) {
                        _matchResults.value = matchResultResponse.result
                        Log.d("LeagueDetailViewModel", "Fikstür verisi başarıyla çekildi: ${matchResultResponse.result.size} maç.")
                        
                        // İlk birkaç maçın skorlarını logla
                        matchResultResponse.result.take(3).forEach { match ->
                            Log.d("MATCH_SCORE_DEBUG", "Maç: ${match.homeTeam} vs ${match.awayTeam}, Skor: '${match.score}', Tarih: '${match.date}'")
                        }
                    } else {
                        _errorMessage.value = matchResultResponse?.message ?: "Fikstür verisi bulunamadı."
                        _matchResults.value = emptyList()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = "Fikstür çekilemedi: ${response.code()} - ${errorBody ?: "Bilinmeyen hata"}"
                    _matchResults.value = null
                }
            } catch (e: IOException) {
                _errorMessage.value = "Ağ bağlantı hatası: ${e.localizedMessage}"
                _matchResults.value = null
                e.printStackTrace()
            } catch (e: HttpException) {
                _errorMessage.value = "API hatası: ${e.code()} - ${e.message()}"
                _matchResults.value = null
                e.printStackTrace()
            } catch (e: Exception) {
                _errorMessage.value = "Beklenmedik bir hata oluştu: ${e.localizedMessage}"
                _matchResults.value = null
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchGoalKings(leagueKey: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val response = repository.getGoalKings(leagueKey)
                if (response.isSuccessful) {
                    val goalKingResponse = response.body()
                    Log.d("API_RESPONSE", "Goal Kings Raw Response: ${goalKingResponse.toString()}")

                    if (goalKingResponse?.success == true && !goalKingResponse.result.isNullOrEmpty()) {
                        _goalKings.value = goalKingResponse.result
                        Log.d("LeagueDetailViewModel", "Gol krallığı verisi başarıyla çekildi: ${goalKingResponse.result.size} oyuncu.")
                    } else {
                        _errorMessage.value = goalKingResponse?.message ?: "Gol krallığı verisi bulunamadı veya API tarafından desteklenmiyor."
                        _goalKings.value = emptyList()
                        Log.e("LeagueDetailViewModel", "API'den gol krallığı boş veya başarısız geldi: ${goalKingResponse?.message}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = "Gol krallığı çekilemedi (HTTP ${response.code()}): ${errorBody ?: "Bilinmeyen hata"}"
                    _goalKings.value = emptyList()
                    Log.e("LeagueDetailViewModel", "API HTTP hata kodu: ${response.code()}, Hata mesajı: ${errorBody}")
                }
            } catch (e: IOException) {
                _errorMessage.value = "Ağ bağlantı hatası. İnternet bağlantınızı kontrol edin: ${e.localizedMessage}"
                _goalKings.value = emptyList()
                e.printStackTrace()
                Log.e("LeagueDetailViewModel", "Ağ bağlantı hatası: ${e.localizedMessage}", e)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                _errorMessage.value = "API hatası (${e.code()}): ${errorBody ?: e.message()}"
                _goalKings.value = emptyList()
                e.printStackTrace()
                Log.e("LeagueDetailViewModel", "HTTP hatası: ${e.code()}, Mesaj: ${errorBody}", e)
            } catch (e: Exception) {
                _errorMessage.value = "Beklenmedik bir hata oluştu: ${e.localizedMessage}"
                _goalKings.value = emptyList()
                e.printStackTrace()
                Log.e("LeagueDetailViewModel", "Genel hata: ${e.localizedMessage}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}