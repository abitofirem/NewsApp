package com.example.navigationdrawerapp.model

import com.google.gson.annotations.SerializedName

data class EmtiaResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("result")
    val result: List<EmtiaResult>
)