package com.example.navigationdrawerapp.model

import com.example.navigationdrawerapp.Haber
import com.google.gson.annotations.SerializedName

//API'den gelen genel yanıt yapısını temsil eden data class
data class NewsResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("result")
    val result: List<Haber> // Haber listesini içerecek
)