package com.example.navigationdrawerapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.navigationdrawerapp.R
import com.example.navigationdrawerapp.model.EmtiaResult

class EmtiaAdapter(private var emtiaList: List<EmtiaResult>) :
    RecyclerView.Adapter<EmtiaAdapter.EmtiaViewHolder>() {

    class EmtiaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEmtiaName: TextView = view.findViewById(R.id.tv_emtia_name)
        val tvEmtiaSelling: TextView = view.findViewById(R.id.tv_emtia_selling)
        val tvEmtiaRate: TextView = view.findViewById(R.id.tv_emtia_rate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmtiaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_emtia, parent, false)
        return EmtiaViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmtiaViewHolder, position: Int) {
        val emtia = emtiaList[position]

        holder.tvEmtiaName.text = emtia.text
        holder.tvEmtiaSelling.text = "Satış: ${String.format("%.2f USD", emtia.selling)}"

        //Değişim oranına göre rengi ayarla
        val rate = emtia.rate
        holder.tvEmtiaRate.text = String.format("%.2f%%", rate)
        if (rate > 0) {
            holder.tvEmtiaRate.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.positive_change_color))
        } else if (rate < 0) {
            holder.tvEmtiaRate.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.negative_change_color))
        } else {
            holder.tvEmtiaRate.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.secondary_text))
        }
    }

    override fun getItemCount() = emtiaList.size

    fun updateData(newEmtiaList: List<EmtiaResult>) {
        this.emtiaList = newEmtiaList
        notifyDataSetChanged()
    }
}