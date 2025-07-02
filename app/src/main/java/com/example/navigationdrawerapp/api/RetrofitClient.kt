package com.example.navigationdrawerapp.api

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor // Loglama için
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://api.collectapi.com/"

    //lazy initialization ile sadece ilk erişimde Retrofit instance'ını oluşturur
    val instance: NewsApiService by lazy {
        //HTTP İstek ve Yanıtlarını Logcat'te görmek için Interceptor (Geliştirme için çok faydalıdır)
        val logging = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY) // İstek ve yanıt detaylarını logla
        }

        //OkHttpClient ile ağ isteklerini özelleştirme (zaman aşımı vb.)
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(logging) // Logging interceptor'ı ekle
            .connectTimeout(30, TimeUnit.SECONDS) //Bağlantı zaman aşımı
            .readTimeout(30, TimeUnit.SECONDS) //Okuma zaman aşımı
            .writeTimeout(30, TimeUnit.SECONDS) // azma zaman aşımı
            .build()

        // Retrofit Builder ile Retrofit istemcisini oluştur
        Retrofit.Builder()
            .baseUrl(BASE_URL) //Temel URL'yi ayarla
            .addConverterFactory(GsonConverterFactory.create()) //JSON'dan Kotlin nesnelerine çevirmek için Gson'ı kullan
            .client(httpClient) //Oluşturulan OkHttpClient'ı Retrofit'e ata (logging, timeout için)
            .build() //Retrofit nesnesini inşa et
            .create(NewsApiService::class.java) //NewsApiService arayüzünden bir uygulama (instance) oluştur
    }
}