package com.example.navigationdrawerapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.navigationdrawerapp.databinding.ItemFixtureMatchRowBinding // <-- KESİNLİKLE BU Binding sınıfı
import com.example.navigationdrawerapp.model.MatchResult
import java.text.SimpleDateFormat // Tarih formatlamak için
import java.util.Locale // Tarih formatlamak için

class MatchResultAdapter(private var matchResults: List<MatchResult>) :
    RecyclerView.Adapter<MatchResultAdapter.MatchResultViewHolder>() {

    class MatchResultViewHolder(val binding: ItemFixtureMatchRowBinding) : // <-- Burası da doğru Binding olmalı
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchResultViewHolder {
        val binding = ItemFixtureMatchRowBinding.inflate(LayoutInflater.from(parent.context), parent, false) // <-- Burası da doğru Binding olmalı
        return MatchResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MatchResultViewHolder, position: Int) {
        val match = matchResults[position]
        holder.binding.apply {
            tvHomeTeamName.text = match.homeTeam
            tvAwayTeamName.text = match.awayTeam

            // Skorları ata ve debug log ekle
            val scoreText = if (match.score.isNullOrEmpty()) {
                "Sonuç yok"
            } else {
                match.score
            }
            tvScore.text = scoreText
            
            // Debug log ekle
            Log.d("MATCH_RESULT_ADAPTER", "Position $position: ${match.homeTeam} vs ${match.awayTeam}, Score: '${match.score}', Displayed: '$scoreText'")

            // Tarihi formatla ve ata
            // API'den gelen format: "2025-05-30T17:00:00+03:00" (image_441604.png)
            // İstediğimiz format: "30 Mayıs Cuma 14:00" (image_3995e0.png)
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
            // NOT: Saat dilimini ayarlarken +03:00'ü göz önünde bulundurun.
            // Eğer API'den gelen saat yerel saatinize göre değilse, dönüşümde farklılıklar olabilir.
            // Görselde 17:00 olmasına rağmen 14:00 görünüyor, bu muhtemelen API'den gelen saatin UTC olup
            // cihazın saat dilimine göre otomatik dönüştürülmesi veya görseldeki saatin temsilidir.
            // Biz şimdilik API'den geleni direkt çevirelim.
            val outputFormat = SimpleDateFormat("dd MMMM EEEE HH:mm", Locale("tr", "TR")) // Türkçe ay ve gün adı

            try {
                val date = match.date?.let { inputFormat.parse(it) }
                // Eğer parse işlemi başarılı olursa formatlı tarihi, yoksa ham tarihi göster.
                tvMatchDate.text = date?.let { outputFormat.format(it) } ?: match.date
            } catch (e: Exception) {
                // Herhangi bir hata durumunda (örn. tarih formatı uyumsuzluğu) ham tarihi göster
                tvMatchDate.text = match.date
                e.printStackTrace()
            }
        }
    }

    override fun getItemCount(): Int = matchResults.size

    fun updateMatchResults(newMatchResults: List<MatchResult>) {
        this.matchResults = newMatchResults
        notifyDataSetChanged()
    }
}