package com.example.navigationdrawerapp.model

import com.google.gson.annotations.SerializedName

//JSON yanıtının tamamını temsil eden data class
data class BistResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("result")
    val result: List<BistResult>

)