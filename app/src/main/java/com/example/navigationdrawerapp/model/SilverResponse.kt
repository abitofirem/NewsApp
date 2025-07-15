package com.example.navigationdrawerapp.model

import com.google.gson.annotations.SerializedName

data class SilverResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("result")
    val result: SilverResult
)