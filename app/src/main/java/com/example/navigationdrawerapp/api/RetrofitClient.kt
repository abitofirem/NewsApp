package com.example.navigationdrawerapp.api

import com.example.navigationdrawerapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://api.collectapi.com/" //Tüm CollectAPI servisleri için ortak URL

    //API anahtarlarını BuildConfig'den alıyoruz
    //Bu anahtarlar build.gradle.kts dosyanızdan gelecek
    private val NEWS_API_KEY = BuildConfig.NEWS_API_KEY
    private val PHARMACY_API_KEY = BuildConfig.PHARMACY_API_KEY
    private val FINANCE_API_KEY = BuildConfig.FINANCE_API_KEY
    private val FOOTBALL_API_KEY = BuildConfig.FOOTBALL_API_KEY
    private val WEATHER_API_KEY = BuildConfig.WEATHER_API_KEY


    //Her servis için özel bir OkHttpClient oluşturacak yardımcı fonksiyon
    //Bu, her servisin kendi API anahtarını kullanmasını sağlar
    private fun createHttpClientWithAuth(apiKey: String): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                //Sadece DEBUG build'lerinde HTTP istek ve yanıtlarını logla
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            })
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestBuilder = originalRequest.newBuilder()
                    .header("content-type", "application/json")
                    // API anahtarını Authorization header'ına ekle
                    .header("authorization", "apikey $apiKey") //İlgili servisin API anahtarı eklendi
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    //Haberler Servisi
    val newsApiService: NewsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createHttpClientWithAuth(NEWS_API_KEY)) // Haberler için özel HTTP istemcisi ve anahtar
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java)
    }

    //Eczane Servisi
    val pharmacyApiService: PharmacyApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createHttpClientWithAuth(PHARMACY_API_KEY)) // Eczane için özel HTTP istemcisi ve anahtar
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PharmacyApiService::class.java)
    }

    //Finans Servisi
    val financeApiService: FinanceApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createHttpClientWithAuth(FINANCE_API_KEY)) // Finans için özel HTTP istemcisi ve anahtar
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FinanceApiService::class.java)
    }

    //Futbol Servisi
    val footballApiService: FootballApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createHttpClientWithAuth(FOOTBALL_API_KEY)) //Futbol için özel HTTP istemcisi ve anahtar
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FootballApiService::class.java)
    }

    //Hava Durumu Servisi
    val weatherApiService: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createHttpClientWithAuth(WEATHER_API_KEY)) //Hava Durumu için özel HTTP istemcisi ve anahtar
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}