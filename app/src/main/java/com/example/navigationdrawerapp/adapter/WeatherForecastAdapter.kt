package com.example.navigationdrawerapp.adapter

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
import java.text.SimpleDateFormat
import java.util.Locale

//Adaptörün constructor'ına bir lambda fonksiyonu (onItemClick) ekliyoruz.
class WeatherForecastAdapter(
    private val onItemClick: (WeatherForecast) -> Unit //Tıklanan WeatherForecast nesnesini döndürecek lambda
) : ListAdapter<WeatherForecast, WeatherForecastAdapter.WeatherForecastViewHolder>(WeatherForecastDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherForecastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_weather_forecast_day, parent, false)
        //ViewHolder'ı oluştururken lambda'yı da ona iletiyoruz
        return WeatherForecastViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: WeatherForecastViewHolder, position: Int) {
        val forecast = getItem(position)
        holder.bind(forecast)
    }

    inner class WeatherForecastViewHolder(itemView: View, private val onItemClick: (WeatherForecast) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val tvDaySmall: TextView = itemView.findViewById(R.id.tv_day_small)
        private val ivWeatherIconSmall: ImageView = itemView.findViewById(R.id.iv_weather_icon_small)
        private val tvMinDegreeSmall: TextView = itemView.findViewById(R.id.tv_min_degree_small)

        fun bind(forecast: WeatherForecast) {
            //itemView'a (yani tüm kartın kendisine) tıklama listener'ı ekliyoruz
            itemView.setOnClickListener {
                onItemClick(forecast) //Tıklanan öğeyi lambda aracılığıyla dışarıya bildiriyoruz
            }

            // Tarih metninden gün ismini hesapla ve ata
            try {
                val inputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val date = inputFormat.parse(forecast.date)
                val outputFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                tvDaySmall.text = date?.let { outputFormat.format(it) } ?: forecast.day
            } catch (e: Exception) {
                tvDaySmall.text = forecast.day // Hata olursa modeldeki ham veriyi kullan
            }

            tvMinDegreeSmall.text = "${forecast.degree.toDouble().toInt()}°C"

            Glide.with(itemView.context)
                .load(forecast.icon)
                .into(ivWeatherIconSmall)
        }
    }
}

class WeatherForecastDiffCallback : DiffUtil.ItemCallback<WeatherForecast>() {
    override fun areItemsTheSame(oldItem: WeatherForecast, newItem: WeatherForecast): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: WeatherForecast, newItem: WeatherForecast): Boolean {
        return oldItem == newItem
    }
}