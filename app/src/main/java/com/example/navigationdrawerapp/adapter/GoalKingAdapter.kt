package com.example.navigationdrawerapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.navigationdrawerapp.R
import com.example.navigationdrawerapp.model.GoalKing // GoalKing modelini import etmeyi unutma

class GoalKingAdapter(private var goalKings: List<GoalKing>) :
    RecyclerView.Adapter<GoalKingAdapter.GoalKingViewHolder>() {

    class GoalKingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRank: TextView = itemView.findViewById(R.id.tv_player_rank)
        val ivPlayerPhoto: ImageView = itemView.findViewById(R.id.iv_player_photo)
        val tvName: TextView = itemView.findViewById(R.id.tv_player_name)
        val tvGoals: TextView = itemView.findViewById(R.id.tv_player_goals)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalKingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_goal_king_display_row, parent, false) // Yeni layout'u kullanıyoruz
        return GoalKingViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalKingViewHolder, position: Int) {
        val goalKing = goalKings[position]

        holder.tvRank.text = "${position + 1}." // Sıralamayı pozisyona göre otomatik verelim
        holder.tvName.text = goalKing.name ?: ""
        holder.tvGoals.text = goalKing.goals ?: ""

        // Oyuncu fotoğrafı için Glide/Picasso gibi bir kütüphane kullanabilirsin.
        // Şimdilik sadece placeholder gösterelim.
        // Glide.with(holder.itemView.context).load(goalKing.playerImageUrl).into(holder.ivPlayerPhoto)
    }

    override fun getItemCount(): Int {
        return goalKings.size
    }

    fun updateGoalKings(newGoalKings: List<GoalKing>) {
        this.goalKings = newGoalKings
        notifyDataSetChanged()
    }
}