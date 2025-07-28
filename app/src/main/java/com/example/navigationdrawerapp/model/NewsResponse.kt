package com.example.navigationdrawerapp.model

import com.example.navigationdrawerapp.model.News
import com.google.gson.annotations.SerializedName

//API'den gelen genel yanıt yapısını temsil eden data class
data class NewsResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("result")
    val result: List<News> // Haber listesini içerecek
)