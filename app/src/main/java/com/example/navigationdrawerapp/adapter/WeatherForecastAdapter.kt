package com.example.navigationdrawerapp.adapter // Paket adınızın doğru olduğundan emin olun

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.navigationdrawerapp.R
import com.example.navigationdrawerapp.model.WeatherForecast

class WeatherForecastAdapter : ListAdapter<WeatherForecast, WeatherForecastAdapter.WeatherForecastViewHolder>(WeatherForecastDiffCallback()) {

    //Her bir liste öğesinin (item_weather_forecast_day.xml) görünümünü oluşturur
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherForecastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_weather_forecast_day, parent, false)
        return WeatherForecastViewHolder(view)
    }

    //Verileri liste öğesine (ViewHolder) bağlar
    override fun onBindViewHolder(holder: WeatherForecastViewHolder, position: Int) {
        val forecast = getItem(position)
        holder.bind(forecast)
    }

    //Her bir liste öğesinin görünümlerini tutan ve verileri bağlayan iç sınıf
    inner class WeatherForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDaySmall: TextView = itemView.findViewById(R.id.tv_day_small)
        private val ivWeatherIconSmall: ImageView = itemView.findViewById(R.id.iv_weather_icon_small)
        private val tvMinDegreeSmall: TextView = itemView.findViewById(R.id.tv_min_degree_small)

        fun bind(forecast: WeatherForecast) {
            tvDaySmall.text = forecast.day //Gün adını ayarla (örn: "Salı")
            tvMinDegreeSmall.text = "${forecast.degree.toDouble().toInt()}°C"

            //Glide kütüphanesi ile hava durumu ikonunu URL'den yükle
            Glide.with(itemView.context)
                .load(forecast.icon)
                .into(ivWeatherIconSmall)
        }
    }
}

//ListAdapter'ın verimli güncellemeler yapmasını sağlayan DiffUtil Callback'i
class WeatherForecastDiffCallback : DiffUtil.ItemCallback<WeatherForecast>() {
    override fun areItemsTheSame(oldItem: WeatherForecast, newItem: WeatherForecast): Boolean {
        // Öğeler aynı mı (genellikle benzersiz ID veya tarih-gün kombinasyonu)
        return oldItem.date == newItem.date && oldItem.day == newItem.day
    }

    override fun areContentsTheSame(oldItem: WeatherForecast, newItem: WeatherForecast): Boolean {
        //Öğelerin içerikleri aynı mı (tüm özelliklerin eşitliği)
        return oldItem == newItem
    }
}