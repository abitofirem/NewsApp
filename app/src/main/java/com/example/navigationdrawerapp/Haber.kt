package com.example.navigationdrawerapp

import java.io.Serializable //Fragmentlar arası aktarım için
import com.google.gson.annotations.SerializedName

data class Haber(
    @SerializedName("key") //API'deki 'key' alanını 'id'ye eşleştiriyoruz
    val id: String = "", //'key' string olarak geliyor, bu yüzden Int yerine String yaptık
    @SerializedName("name") //API'deki 'name' alanını 'baslik'a eşleştiriyoruz
    val baslik: String = "",
    @SerializedName("description") //API'deki 'description' alanını 'icerik'e eşleştiriyoruz
    val icerik: String = "",
    @SerializedName("image") //API'deki 'image' alanını 'gorselUrl'e eşleştiriyoruz
    val gorselUrl: String = "",
    @SerializedName("url") //Haber URL'si
    val haberUrl: String = "",
    @SerializedName("source") //Haber kaynağı
    val kaynak: String = ""
) : Serializable //Fragmentlar arası veri aktarımı için (safe args kullanmıyorsanız)