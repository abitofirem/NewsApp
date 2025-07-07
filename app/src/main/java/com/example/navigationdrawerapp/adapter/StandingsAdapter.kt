package com.example.navigationdrawerapp.adapter // Adaptörlerinizin bulunduğu paket

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.navigationdrawerapp.databinding.ItemStandingRowBinding // ViewBinding için
import com.example.navigationdrawerapp.model.TeamStanding // Oluşturduğumuz TeamStanding modelini import edin

class StandingsAdapter(
    private var standingsList: List<TeamStanding> //Puan durumu listesi
) : RecyclerView.Adapter<StandingsAdapter.StandingViewHolder>() {

    //ViewHolder sınıfımız
    class StandingViewHolder(val binding: ItemStandingRowBinding) : RecyclerView.ViewHolder(binding.root)

    //ViewHolder oluşturma metodu
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StandingViewHolder {
        val binding = ItemStandingRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StandingViewHolder(binding)
    }

    // ViewHolder'ı verilerle bağlama metodu
    override fun onBindViewHolder(holder: StandingViewHolder, position: Int) {
        val teamStanding = standingsList[position]
        holder.binding.apply {
            tvTeamRank.text = teamStanding.rank
            tvTeamName.text = teamStanding.team
            tvTeamPlayed.text = "O:${teamStanding.played}" //"O:" ön eki ekleyerek daha okunaklı
            tvTeamWin.text = "G:${teamStanding.win}"     //"G:" ön eki
            tvTeamLose.text = "M:${teamStanding.lose}"    //"M:" ön eki
            tvTeamPoints.text = teamStanding.points       //Puanı doğrudan göster
        }
    }

    // Listedeki öğe sayısı
    override fun getItemCount(): Int {
        return standingsList.size
    }

    //Puan durumu listesini güncellemek için yardımcı metod
    fun updateStandings(newStandingsList: List<TeamStanding>) {
        this.standingsList = newStandingsList
        notifyDataSetChanged() //Veri değiştiğinde RecyclerView'ı güncelle
    }
}