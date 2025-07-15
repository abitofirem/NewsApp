package com.example.navigationdrawerapp.model

import com.google.gson.annotations.SerializedName

//"result" objesinin içindeki verileri temsil eden data class
data class BistResult(
    @SerializedName("currentstr")
    val currentstr: String,
    @SerializedName("current")
    val current: Double,
    @SerializedName("changeratestr")
    val changeratestr: String,
    @SerializedName("changerate")
    val changerate: Double,
    @SerializedName("minstr")
    val minstr: String,
    @SerializedName("min")
    val min: Double,
    @SerializedName("maxstr")
    val maxstr: String,
    @SerializedName("max")
    val max: Double,
    @SerializedName("openingstr")
    val openingstr: String,
    @SerializedName("opening")
    val opening: Double,
    @SerializedName("closingstr")
    val closingstr: String,
    @SerializedName("closing")
    val closing: Double,
    @SerializedName("time")
    val time: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("datetime")
    val datetime: String
)