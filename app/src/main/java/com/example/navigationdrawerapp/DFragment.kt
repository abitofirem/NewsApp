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
import com.example.navigationdrawerapp.adapter.WeatherForecastAdapter // Adaptörü import et
import com.example.navigationdrawerapp.model.WeatherForecast // WeatherForecast modelini import et
import com.example.navigationdrawerapp.viewmodel.WeatherViewModel // ViewModel'i import et
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DFragment : Fragment() {

    private lateinit var weatherViewModel: WeatherViewModel

    //fragment_d.xml'deki ana UI elemanları
    private lateinit var tvCityHeader: TextView
    private lateinit var llSearchBar: LinearLayout
    private lateinit var actvCitySearch: MaterialAutoCompleteTextView
    private lateinit var ivSearchIconInBar: ImageView
    private lateinit var recyclerViewForecast: RecyclerView //Haftalık tahminler RecyclerView'ı
    private lateinit var progressBar: ProgressBar
    private lateinit var tvErrorMessage: TextView

    //item_weather_forecast_today.xml (include edilen) içindeki UI elemanları
    //Bu elemanları bir kere bind ederek tekrar tekrar findViewById yapmamak için tanımlıyoruz
    private lateinit var tvDayToday: TextView
    private lateinit var tvDateToday: TextView
    private lateinit var ivWeatherIconToday: ImageView
    private lateinit var tvDescriptionToday: TextView
    private lateinit var tvCurrentDegreeToday: TextView
    private lateinit var tvMinDegreeTodayDetails: TextView
    private lateinit var tvMaxDegreeTodayDetails: TextView
    private lateinit var tvHumidityTodayDetails: TextView

    //Adapter'ı burada tanımlıyoruz
    private lateinit var weatherForecastAdapter: WeatherForecastAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_d, container, false)
        initViews(view)        //UI elemanlarını initialize et
        setupRecyclerView()   //RecyclerView'ı ayarla (burada adapter'ı başlatıyoruz)
        setupListeners()      //Click listener'ları ayarla
        setupObservers()      //LiveData gözlemcilerini ayarla

        //Uygulama ilk açıldığında varsayılan bir şehir için hava durumu çek
        //Bu, ekranın başlangıçta boş kalmamasını sağlar.
        weatherViewModel.fetchWeatherData("Ankara")

        return view
    }

    //Tüm UI elemanlarını bağlar (findViewById işlemleri)
    private fun initViews(view: View) {
        tvCityHeader = view.findViewById(R.id.tv_city_header)
        llSearchBar = view.findViewById(R.id.ll_search_bar)
        actvCitySearch = view.findViewById(R.id.actv_city_search)
        ivSearchIconInBar = view.findViewById(R.id.iv_search_icon_in_bar)

        recyclerViewForecast = view.findViewById(R.id.rv_other_days_weather)
        progressBar = view.findViewById(R.id.progress_bar)
        tvErrorMessage = view.findViewById(R.id.tv_error_message)

        //item_weather_forecast_today.xml'deki elemanlara card_today_weather include'u üzerinden erişim
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

    //RecyclerView ve Adapter'ı ayarlar
    private fun setupRecyclerView() {
        //Adapter'ı başlatırken onItemClick lambda'sını sağlıyoruz
        //Bu lambda, WeatherForecastAdapter'daki bir öğeye tıklandığında çalışacak
        weatherForecastAdapter = WeatherForecastAdapter { clickedForecast ->
            //Tıklanan WeatherForecast nesnesi ile büyük kartı güncelle
            updateMainWeatherCard(clickedForecast)
        }
        recyclerViewForecast.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewForecast.adapter = weatherForecastAdapter
    }

    //Kullanıcı etkileşimleri için listener'ları ayarlar
    private fun setupListeners() {
        ivSearchIconInBar.setOnClickListener {
            val city = actvCitySearch.text.toString().trim()
            if (city.isNotEmpty()) {
                tvCityHeader.text = city //Şehir başlığını güncelle
                weatherViewModel.fetchWeatherData(city) //Yeni şehre göre veri çek
                //Klavyeyi gizle
                val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view?.windowToken, 0)
                actvCitySearch.clearFocus() // EditText'ten odağı kaldır
            } else {
                Toast.makeText(context, "Lütfen bir şehir adı girin.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //LiveData'ları gözlemleyerek UI güncellemelerini yapar
    private fun setupObservers() {
        weatherViewModel.weatherData.observe(viewLifecycleOwner) { weatherResponse ->
            weatherResponse?.let {
                val allForecasts = it.result //Tüm tahminler listesi (WeatherResponse'tan)

                //Ana kartı her zaman API'den gelen ilk günün verileriyle güncelle
                val todayForecast = allForecasts.firstOrNull()
                todayForecast?.let { today ->
                    updateMainWeatherCard(today)
                }

                //RecyclerView'daki haftalık tahminleri güncelle
                //İlk günü hariç tutuyoruz, çünkü o ana kartta gösteriliyor
                val weeklyForecast = if (allForecasts.size > 1) allForecasts.subList(1, allForecasts.size) else emptyList()
                weatherForecastAdapter.submitList(weeklyForecast)

                tvErrorMessage.visibility = View.GONE
            } ?: run {
                // Hata durumunda veya boş veri gelirse
                tvErrorMessage.text = "Hava durumu verisi alınamadı veya boş geldi."
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

    //Büyük hava durumu kartındaki UI elemanlarını güncelleyen yardımcı fonksiyon
    private fun updateMainWeatherCard(forecast: WeatherForecast) {
        //"Bugün" veya gün adını belirle
        val todayDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
        tvDayToday.text = if (forecast.date == todayDate) "Bugün" else SimpleDateFormat("EEEE", Locale("tr", "TR")).format(SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(forecast.date))

        //Tarihi formatla
        try {
            val inputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val date = inputFormat.parse(forecast.date)
            val outputFormat = SimpleDateFormat("d MMMM, EEEE", Locale("tr", "TR"))
            tvDateToday.text = date?.let { outputFormat.format(it) } ?: forecast.date
        } catch (e: Exception) {
            tvDateToday.text = forecast.date // Hata olursa ham tarihi göster
        }

        //Derece, açıklama, min/max ve nem bilgilerini ata
        //String'den Double'a çevirip sonra Int'e dönüştürme (ondalıkları atmak için)
        tvCurrentDegreeToday.text = "${forecast.degree.toDouble().toInt()}°C"
        tvDescriptionToday.text = forecast.description.replaceFirstChar { char ->
            if (char.isLowerCase()) char.titlecase() else char.toString()
        }
        tvMinDegreeTodayDetails.text = "Min: ${forecast.min.toDouble().toInt()}°C"
        tvMaxDegreeTodayDetails.text = "Max: ${forecast.max.toDouble().toInt()}°C"
        tvHumidityTodayDetails.text = "Nem: ${forecast.humidity.toDouble().toInt()}%"

        // Hava durumu ikonunu yükle
        Glide.with(this)
            .load(forecast.icon)
            .into(ivWeatherIconToday)
    }
}