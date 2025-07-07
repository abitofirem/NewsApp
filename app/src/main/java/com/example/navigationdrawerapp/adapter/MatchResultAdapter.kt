package com.example.navigationdrawerapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.navigationdrawerapp.databinding.ItemMatchResultRowBinding //Binding sınıfını kullanıyoruz
import com.example.navigationdrawerapp.model.MatchResult

class MatchResultAdapter(private val matchResults: List<MatchResult>) :
    RecyclerView.Adapter<MatchResultAdapter.MatchResultViewHolder>() {

    class MatchResultViewHolder(val binding: ItemMatchResultRowBinding) : //ViewBinding kullanmak daha temiz
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchResultViewHolder {
        val binding = ItemMatchResultRowBinding.inflate(LayoutInflater.from(parent.context), parent, false) //Doğru Binding sınıfı olmalı
        return MatchResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MatchResultViewHolder, position: Int) {
        val match = matchResults[position]
        holder.binding.tvHomeTeamName.text = match.homeTeam
        holder.binding.tvAwayTeamName.text = match.awayTeam
        holder.binding.tvScore.text = match.score
        holder.binding.tvMatchDate.text = match.date
    }

    override fun getItemCount(): Int = matchResults.size
}