package com.example.navigationdrawerapp.model

import com.google.gson.annotations.SerializedName

data class ExchangeResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("result")
    val result: ExchangeResult
)