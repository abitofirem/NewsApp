package com.example.navigationdrawerapp.repository

import com.example.navigationdrawerapp.api.FinanceApiService
import com.example.navigationdrawerapp.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class FinanceRepository(private val financeApiService: FinanceApiService) {
    
    suspend fun getCurrencyRates(): Response<CurrencyResponse> {
        return withContext(Dispatchers.IO) {
            financeApiService.getAllCurrencyData()
        }
    }
    
    suspend fun getCryptoRates(): Response<CriptoResponse> {
        return withContext(Dispatchers.IO) {
            financeApiService.getCriptoPrice()
        }
    }
    
    suspend fun getGoldRates(): Response<GoldResponse> {
        return withContext(Dispatchers.IO) {
            financeApiService.getGoldPrice()
        }
    }
    
    suspend fun getSilverRates(): Response<SilverResponse> {
        return withContext(Dispatchers.IO) {
            financeApiService.getSilverPrice()
        }
    }
    
    suspend fun getEmtiaRates(): Response<EmtiaResponse> {
        return withContext(Dispatchers.IO) {
            financeApiService.getEmtiaData()
        }
    }
    
    suspend fun getBistRates(): Response<BistResponse> {
        return withContext(Dispatchers.IO) {
            financeApiService.getBistData()
        }
    }
    
    suspend fun convertCurrency(baseCurrency: String, toCurrency: String, amount: String): Response<ExchangeResponse> {
        return withContext(Dispatchers.IO) {
            financeApiService.convertCurrency(baseCurrency, toCurrency, amount)
        }
    }
} 