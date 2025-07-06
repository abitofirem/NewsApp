package com.example.navigationdrawerapp.api

import com.example.navigationdrawerapp.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NewsApiService {

    //Haberleri çekmek için GET isteği
    //Endpoint: https://api.collectapi.com/news/getNews
    @Headers( //API anahtarınızı burada belirtiyoruz
        "content-type: application/json",
 //       "authorization: apikey 2gmUrMjHzi3aQLY6FYXbhE:078zdz0PXeIEbP5VbRNstp" //API_KEY
    )
    @GET("news/getNews") //API'nizin temel URL'si üzerine eklenecek yol
    suspend fun getNews(
        @Query("country") country: String, //Ülke kodu (örn: "tr")
        @Query("tag") tag: String,         //Haber kategorisi (örn: "general", "spor", "ekonomi")
        @Query("paging") paging: Int      //Sayfa numarası (CollectAPI'de "paging" olarak geçiyor)
    ): NewsResponse // Beklenen yanıt tipi: NewsResponse objesi
}