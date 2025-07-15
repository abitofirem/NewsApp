package com.example.navigationdrawerapp.api

import com.example.navigationdrawerapp.model.BistResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface FinanceApiService {
    @GET("economy/borsaIstanbul")
    suspend fun getBistData(
        @Header("content-type") contentType: String = "application/json"
    ): Response<BistResponse>

    //İleride diğer API çağrıları buraya eklenecek

}