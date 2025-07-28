package com.example.navigationdrawerapp.repository

import com.example.navigationdrawerapp.api.FootballApiService
import com.example.navigationdrawerapp.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class FootballRepository(private val footballApiService: FootballApiService) {
    
    suspend fun getLeagues(): Response<LeagueResponse> {
        return withContext(Dispatchers.IO) {
            footballApiService.getLeagues()
        }
    }
    
    suspend fun getStandings(leagueKey: String): Response<StandingResponse> {
        return withContext(Dispatchers.IO) {
            footballApiService.getLeagueStandings(leagueKey)
        }
    }
    
    suspend fun getMatchResults(leagueKey: String): Response<MatchResultResponse> {
        return withContext(Dispatchers.IO) {
            footballApiService.getMatchResults(leagueKey)
        }
    }
    
    suspend fun getGoalKings(leagueKey: String): Response<GoalKingResponse> {
        return withContext(Dispatchers.IO) {
            footballApiService.getGoalKings(leagueKey)
        }
    }
} 