package com.example.navigationdrawerapp.api

import com.example.navigationdrawerapp.model.PharmacyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface PharmacyApiService {

    @GET("health/dutyPharmacy")
    suspend fun getDutyPharmacies(

        @Header("authorization") authorization: String = "apikey 1He3fTb1Yz28ySCsfPnGhT:1p4LcRShJjq1eDw90XE0M1", // API Anahtarınız
        @Header("content-type") contentType: String = "application/json",

        @Query("il") il: String, //<<< Bu API'nin beklediği isim: 'il' ve tipi String (zorunlu)
        @Query("ilce") ilce: String? = null //<<< Bu API'nin beklediği isim: 'ilce' ve tipi String? (opsiyonel)
    ): Response<PharmacyResponse>
}