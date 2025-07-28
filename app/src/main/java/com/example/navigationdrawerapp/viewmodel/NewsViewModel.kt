package com.example.navigationdrawerapp.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.navigationdrawerapp.model.News
import com.example.navigationdrawerapp.repository.NewsRepository
import kotlinx.coroutines.launch
import java.util.Locale

class NewsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application
    private val repository = NewsRepository(com.example.navigationdrawerapp.api.RetrofitClient.newsApiService)

    private val _newsList = MutableLiveData<List<News>>()
    val newsList: LiveData<List<News>> = _newsList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: MutableLiveData<String?> = _errorMessage

    private var currentPage = 0
    private val pageSize = 10
    var hasMoreNews = true

    private lateinit var currentLanguage: String
    private val defaultTag = "general"

    init {
        currentLanguage = getSavedLanguagePreference()
        fetchNews()
    }

    private fun getSavedLanguagePreference(): String {
        val sharedPref = app.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString("app_language", Locale.getDefault().language) ?: "tr"
    }

    fun updateLanguage(language: String) {
        if (currentLanguage != language) {
            currentLanguage = language
            resetNewsList()
            fetchNews()
        }
    }

    private fun resetNewsList() {
        _newsList.value = emptyList()
        currentPage = 0
        hasMoreNews = true
    }

    fun fetchNews() {
        if (_isLoading.value == true || !hasMoreNews) {
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val country = when (currentLanguage) {
                    "en" -> "en"
                    "tr" -> "tr"
                    else -> "tr"
                }
                
                val response = repository.getNews(country, defaultTag, currentPage)
                
                if (response.success) {
                    val fetchedNews = response.result ?: emptyList()

                    if (fetchedNews.isNotEmpty()) {
                        val currentNews = _newsList.value.orEmpty().toMutableList()
                        currentNews.addAll(fetchedNews)
                        _newsList.value = currentNews
                        currentPage++
                    } else {
                        hasMoreNews = false
                    }
                } else {
                    _errorMessage.value = "API'den başarısız yanıt: Haberler yüklenemedi."
                    hasMoreNews = false
                }

            } catch (e: Exception) {
                _errorMessage.value = "Haberler yüklenirken bir hata oluştu: ${e.message}"
                hasMoreNews = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMoreNews() {
        fetchNews()
    }
}