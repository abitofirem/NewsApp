package com.example.navigationdrawerapp.api // Projenizin API paketi

import com.example.navigationdrawerapp.model.LeagueResponse
import com.example.navigationdrawerapp.model.StandingResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response //<-- Retrofit2'den Response

interface FootballApiService {

    @GET("football/leaguesList")
    suspend fun getLeagues(): Response<LeagueResponse> //Response<LeagueResponse> döndürmeli

    @GET("football/league")
    suspend fun getLeagueStandings(
        @Query("data.league") leagueKey: String
    ): Response<StandingResponse> //Response<StandingResponse> döndürmeli
}