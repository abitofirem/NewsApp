package com.example.navigationdrawerapp.model

data class GoalKingResponse(
    val success: Boolean,
    val result: List<GoalKing>?, //API'den null gelebileceği için nullable yaptım
    val message: String? = null //Yeni eklendi: API'den gelen mesajı tutmak için
)