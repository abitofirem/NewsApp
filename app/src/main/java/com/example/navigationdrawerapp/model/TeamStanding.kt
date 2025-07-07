package com.example.navigationdrawerapp.model // Projenizin model paketi

import com.google.gson.annotations.SerializedName

data class TeamStanding(
    @SerializedName("rank")
    val rank: String?,
    @SerializedName("lose")
    val lose: String?, //String
    @SerializedName("win")
    val win: String?, //String
    @SerializedName("play") //Burası değişti! Önceki `played` yerine `play`
    val played: String?, //String
    @SerializedName("point") //Burası değişti! Önceki `points` yerine `point`
    val points: String?, //String
    @SerializedName("team")
    val team: String? //String
)