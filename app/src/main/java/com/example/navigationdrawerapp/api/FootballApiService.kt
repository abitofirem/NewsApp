package com.example.navigationdrawerapp.api // Projenizin API paketi

import com.example.navigationdrawerapp.model.LeagueResponse
import retrofit2.http.GET
import retrofit2.http.Headers

interface FootballApiService {

    @Headers(
        "content-type: application/json",
 //     "authorization: apikey 2gmUrMjHzi3aQLY6FYXbhE:078zdz0PXeIEbP5VbRNstp" // API_KEY'in doğru olduğunu varsayıyorum
    )
    @GET("football/leaguesList")
    suspend fun getLeagues(): LeagueResponse
}