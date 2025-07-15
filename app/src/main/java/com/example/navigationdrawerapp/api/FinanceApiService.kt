package com.example.navigationdrawerapp.api

import com.example.navigationdrawerapp.model.BistResponse
import com.example.navigationdrawerapp.model.CurrencyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface FinanceApiService {

    //BIST100 için
    @GET("economy/borsaIstanbul")
    suspend fun getBistData(
        @Header("content-type") contentType: String = "application/json"
    ): Response<BistResponse>

    //Döviz Kurları için
    @GET("economy/allCurrency")
    suspend fun getAllCurrencyData(
        @Header("content-type") contentType: String = "application/json"
    ): Response<CurrencyResponse>


}