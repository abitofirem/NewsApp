package com.example.navigationdrawerapp.model

import com.google.gson.annotations.SerializedName

data class MatchResultResponse (

    @SerializedName("success")
    val success: Boolean, // API isteğinin başarılı olup olmadığını gösterir
    @SerializedName("result")
    val result: List<MatchResult>


)