package com.example.navigationdrawerapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigationdrawerapp.model.TeamStanding //BURAYI TeamStanding olarak import et!
import com.example.navigationdrawerapp.api.FootballApiService
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import com.example.navigationdrawerapp.model.MatchResult
import com.example.navigationdrawerapp.model.MatchResultResponse


class LeagueDetailViewModel : ViewModel() {

    private val API_KEY = "2gmUrMjHzi3aQLY6FYXbhE:078zdz0PXeIEbP5VbRNstp"
    private val BASE_URL = "https://api.collectapi.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("Content-Type", "application/json")
                .header("Authorization", "apikey $API_KEY")
            val request = requestBuilder.build()
            chain.proceed(request)
        }
        .build()

    private val footballApiService: FootballApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FootballApiService::class.java)
    }

    private val _standings = MutableLiveData<List<TeamStanding>?>() //BURASI List<TeamStanding> OLDU
    val standings: LiveData<List<TeamStanding>?> = _standings

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _matchResults = MutableLiveData<List<MatchResult>?>() // Burayı MatchResult? olarak güncelledim
    val matchResults: LiveData<List<MatchResult>?> = _matchResults // Burayı MatchResult? olarak güncelledim

    fun fetchStandings(leagueKey: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val response = footballApiService.getLeagueStandings(leagueKey)
                if (response.isSuccessful) {
                    _standings.value = response.body()?.result //response.body()?.result zaten List<TeamStanding> olacak
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = "Puan durumu çekilemedi: ${response.code()} - ${errorBody ?: "Bilinmeyen hata"}"
                    _standings.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "Hata oluştu: ${e.localizedMessage}"
                _standings.value = null
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Fikstür verilerini çekecek yeni fonksiyon
    fun fetchMatchResults(leagueKey: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val response = footballApiService.getMatchResults(leagueKey)
                if (response.isSuccessful) {
                    _matchResults.value = response.body()?.result
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = "Fikstür çekilemedi: ${response.code()} - ${errorBody ?: "Bilinmeyen hata"}"
                    _matchResults.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "Hata oluştu: ${e.localizedMessage}"
                _matchResults.value = null
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}