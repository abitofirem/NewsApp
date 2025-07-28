package com.example.navigationdrawerapp.repository

import com.example.navigationdrawerapp.api.NewsApiService
import com.example.navigationdrawerapp.model.NewsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class NewsRepository(private val newsApiService: NewsApiService) {
    
    suspend fun getNews(country: String = "tr", tag: String = "general", paging: Int = 0): NewsResponse {
        return withContext(Dispatchers.IO) {
            newsApiService.getNews(country, tag, paging)
        }
    }
} 