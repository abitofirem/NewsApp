package com.example.navigationdrawerapp.api

import com.example.navigationdrawerapp.model.PharmacyResponse
import retrofit2.Response
import retrofit2.http.GET
// import retrofit2.http.Header // Artık doğrudan burada Header kullanmayacağız
import retrofit2.http.Query


interface PharmacyApiService {

    @GET("health/dutyPharmacy")
    suspend fun getDutyPharmacies(

        @Query("il") il: String, //<<< Bu API'nin beklediği isim: 'il' ve tipi String (zorunlu)
        @Query("ilce") ilce: String? = null //<<< Bu API'nin beklediği isim: 'ilce' ve tipi String? (opsiyonel)
    ): Response<PharmacyResponse>
}