package com.example.navigationdrawerapp.api // Projenizin API paketi

import com.example.navigationdrawerapp.model.GoalKingResponse
import com.example.navigationdrawerapp.model.LeagueResponse
import com.example.navigationdrawerapp.model.MatchResultResponse
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

    //Gelecek maçları çekmek için (fikstür)
    //Endpoint: https://api.collectapi.com/football/results?data.league=super-lig
    @GET("football/results")
    suspend fun getMatchResults(
        @Query("data.league") leagueKey: String //Lig anahtarını query parametresi olarak gönderiyoruz
    ): Response<MatchResultResponse> //Beklenen yanıt tipi: MatchResultResponse objesi

    //Geçmiş maçları çekmek için (sonuçlar)
    //Endpoint: https://api.collectapi.com/football/fixtures?data.league=super-lig
    @GET("football/fixtures")
    suspend fun getPastMatchResults(
        @Query("data.league") leagueKey: String //Lig anahtarını query parametresi olarak gönderiyoruz
    ): Response<MatchResultResponse> //Beklenen yanıt tipi: MatchResultResponse objesi

    @GET("football/goalKings") // <-- BURAYI DÜZELTTİK!
    suspend fun getGoalKings(@Query("data.league") league: String): Response<GoalKingResponse> // <-- PARAMETRE ADINI DÜZELTTİK!

}