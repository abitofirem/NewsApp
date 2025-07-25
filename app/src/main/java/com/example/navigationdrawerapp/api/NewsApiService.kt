package com.example.navigationdrawerapp.api

import com.example.navigationdrawerapp.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("news/getNews") // API'nizin temel URL'si üzerine eklenecek yol
    suspend fun getNews(
        @Query("country") country: String, //Ülke kodu (örn: "tr")
        @Query("tag") tag: String,         //Haber kategorisi (örn: "general", "spor", "ekonomi")
        @Query("paging") paging: Int      //Sayfa numarası (CollectAPI'de "paging" olarak geçiyor)
    ): NewsResponse // Beklenen yanıt tipi: NewsResponse objesi
}