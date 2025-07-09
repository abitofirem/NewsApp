package com.example.navigationdrawerapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.navigationdrawerapp.R
import com.example.navigationdrawerapp.adapter.WeatherForecastAdapter
import com.example.navigationdrawerapp.viewmodel.WeatherViewModel
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import java.text.SimpleDateFormat
import java.util.Locale

class DFragment : Fragment() {

    private lateinit var weatherViewModel: WeatherViewModel

    //UI elemanları
    private lateinit var tvCityHeader: TextView
    private lateinit var llSearchBar: LinearLayout
    private lateinit var actvCitySearch: MaterialAutoCompleteTextView

    // YENİ ARAMA İKONUNU TANIMLA:
    private lateinit var ivSearchIconInBar: ImageView


    //item_weather_forecast_today.xml içindeki UI elemanları
    private lateinit var tvDayToday: TextView
    private lateinit var tvDateToday: TextView
    private lateinit var ivWeatherIconToday: ImageView
    private lateinit var tvDescriptionToday: TextView
    private lateinit var tvCurrentDegreeToday: TextView
    private lateinit var tvHumidityTodayDetails: TextView

    private lateinit var tvMinDegreeTodayDetails: TextView
    private lateinit var tvMaxDegreeTodayDetails: TextView

    //Haftalık RecyclerView
    private lateinit var recyclerViewForecast: RecyclerView
    private lateinit var weatherAdapter: WeatherForecastAdapter

    //Diğer UI elemanları
    private lateinit var progressBar: ProgressBar
    private lateinit var tvErrorMessage: TextView

    private var isSearchBarVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_d, container, false)
        initViews(view)
        setupRecyclerView()
        setupObservers()
        setupListeners()

        //Başlangıçta hava durumu verilerini çek (varsayılan şehir Ankara)
        weatherViewModel.fetchWeatherData("Ankara")

        return view
    }

    private fun initViews(view: View) {
        // fragment_d.xml ana layout'taki elemanlar
        tvCityHeader = view.findViewById(R.id.tv_city_header)
        llSearchBar = view.findViewById(R.id.ll_search_bar)
        actvCitySearch = view.findViewById(R.id.actv_city_search)
        ivSearchIconInBar = view.findViewById(R.id.iv_search_icon_in_bar)

        recyclerViewForecast = view.findViewById(R.id.rv_other_days_weather)
        progressBar = view.findViewById(R.id.progress_bar)
        tvErrorMessage = view.findViewById(R.id.tv_error_message)

        //item_weather_forecast_today.xml içindeki elemanlara erişim (included layout üzerinden)
        val todayWeatherCard = view.findViewById<View>(R.id.card_today_weather)
        tvDayToday = todayWeatherCard.findViewById(R.id.tv_day_today)
        tvDateToday = todayWeatherCard.findViewById(R.id.tv_date_today)
        ivWeatherIconToday = todayWeatherCard.findViewById(R.id.iv_weather_icon_today)
        tvDescriptionToday = todayWeatherCard.findViewById(R.id.tv_description_today)
        tvCurrentDegreeToday = todayWeatherCard.findViewById(R.id.tv_current_degree_today)
        tvMinDegreeTodayDetails = todayWeatherCard.findViewById(R.id.tv_min_degree_today_details)
        tvMaxDegreeTodayDetails = todayWeatherCard.findViewById(R.id.tv_max_degree_today_details)
        tvHumidityTodayDetails = todayWeatherCard.findViewById(R.id.tv_humidity_today_details)
    }

    private fun setupRecyclerView() {
        weatherAdapter = WeatherForecastAdapter()
        recyclerViewForecast.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewForecast.adapter = weatherAdapter
    }

    private fun setupObservers() {
        //Hava durumu verilerini gözlemle
        weatherViewModel.weatherData.observe(viewLifecycleOwner) { weatherResponse ->
            weatherResponse?.let {
                val todayForecast = it.result.firstOrNull()

                todayForecast?.let { today ->
                    tvDayToday.text = "Bugün"
                    //Tarih formatını ayarlayalım
                    try {
                        val inputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                        val date = inputFormat.parse(today.date)
                        val outputFormat = SimpleDateFormat(
                            "d MMMM, EEEE",
                            Locale("tr", "TR")
                        ) // Örn: 8 Temmuz, Salı
                        tvDateToday.text = date?.let { outputFormat.format(it) } ?: today.date
                    } catch (e: Exception) {
                        tvDateToday.text = today.date // Hata olursa orijinal formatı kullan
                    }

                    tvCurrentDegreeToday.text = "${today.degree.toDouble().toInt()}°C"
                    tvDescriptionToday.text = today.description.replaceFirstChar { char ->
                        if (char.isLowerCase()) char.titlecase() else char.toString()
                    }
                    //BURASI DEĞİŞTİRİLDİ: Min ve Max değerlerini ayrı TextView'lere atıyoruz
                    tvMinDegreeTodayDetails.text = "Min: ${today.min}°C"
                    tvMaxDegreeTodayDetails.text = "Max: ${today.max}°C"
                    tvHumidityTodayDetails.text = "Nem: ${today.humidity}%"

                    Glide.with(this)
                        .load(today.icon)
                        .into(ivWeatherIconToday)
                }

                //Haftalık tahminleri RecyclerView'a gönder (ilk günü çıkar)
                val weeklyForecast =
                    if (it.result.size > 1) it.result.subList(1, it.result.size) else emptyList()
                weatherAdapter.submitList(weeklyForecast)

                tvErrorMessage.visibility = View.GONE
            } ?: run {
                tvErrorMessage.text = "Hava durumu verisi boş geldi."
                tvErrorMessage.visibility = View.VISIBLE
            }
        }

        //Yükleme durumunu gözlemle
        weatherViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) tvErrorMessage.visibility = View.GONE
        }

        //Hata mesajlarını gözlemle
        weatherViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                tvErrorMessage.text = it
                tvErrorMessage.visibility = View.VISIBLE
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            } ?: run {
                tvErrorMessage.visibility = View.GONE
            }
        }
    }

    private fun setupListeners() {


        ivSearchIconInBar.setOnClickListener { //YENİ İKON ÜZERİNDEN ARAMA
            val city = actvCitySearch.text.toString().trim()
            if (city.isNotEmpty()) {
                tvCityHeader.text = city // Başlığı aranan şehir yap
                weatherViewModel.fetchWeatherData(city)
                // Arama başarılıysa klavyeyi gizleyebilirsiniz (isteğe bağlı)
                val imm =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view?.windowToken, 0)
                actvCitySearch.clearFocus() // EditText'ten odağı kaldır
            } else {
                Toast.makeText(context, "Lütfen bir şehir adı girin.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}