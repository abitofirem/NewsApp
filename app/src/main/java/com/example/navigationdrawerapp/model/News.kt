package com.example.navigationdrawerapp.model

import java.io.Serializable
import com.google.gson.annotations.SerializedName

data class News(
    @SerializedName("key")
    val id: String = "",
    @SerializedName("name")
    val title: String = "",
    @SerializedName("description")
    val content: String = "",
    @SerializedName("image")
    val imageUrl: String = "",
    @SerializedName("url")
    val newsUrl: String = "",
    @SerializedName("source")
    val source: String = ""
) : Serializable 