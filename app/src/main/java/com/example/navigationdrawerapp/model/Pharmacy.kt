package com.example.navigationdrawerapp.model

import com.google.gson.annotations.SerializedName

data class Pharmacy(
    @SerializedName("name") val name: String,
    @SerializedName("dist") val dist: String, // İlçe bilgisi (Bu kalmalı)
    @SerializedName("address") val address: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("loc") val loc: String // Enlem,boylam (latitude,longitude)
)