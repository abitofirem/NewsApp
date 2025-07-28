package com.example.navigationdrawerapp.repository

import com.example.navigationdrawerapp.api.PharmacyApiService
import com.example.navigationdrawerapp.model.PharmacyResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class PharmacyRepository(private val pharmacyApiService: PharmacyApiService) {
    
    suspend fun getPharmacies(city: String, district: String?): Response<PharmacyResponse> {
        return withContext(Dispatchers.IO) {
            pharmacyApiService.getDutyPharmacies(city, district)
        }
    }
} 