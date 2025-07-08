package com.example.navigationdrawerapp.model

import com.google.gson.annotations.SerializedName

data class GoalKing(
    @SerializedName("play")
    val play: String?,
    @SerializedName("goals")
    val goals: String?,
    @SerializedName("name")
    val name: String?
)