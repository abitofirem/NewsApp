package com.example.navigationdrawerapp.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.navigationdrawerapp.R
import com.example.navigationdrawerapp.model.CriptoResult
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class CriptoAdapter : RecyclerView.Adapter<CriptoAdapter.CriptoViewHolder>() {

    private var criptoList = emptyList<CriptoResult>()

    class CriptoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCriptoName: TextView = view.findViewById(R.id.tv_cripto_name)
        val tvCriptoPrice: TextView = view.findViewById(R.id.tv_cripto_price)
        val tvCriptoChangeDay: TextView = view.findViewById(R.id.tv_cripto_change_day)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CriptoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cripto, parent, false)
        return CriptoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CriptoViewHolder, position: Int) {
        val cripto = criptoList[position]
        holder.tvCriptoName.text = "${cripto.name} (${cripto.code})"

        //Fiyatı formatla
        val priceFormat = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.US))
        holder.tvCriptoPrice.text = priceFormat.format(cripto.price) + " " + cripto.currency

        //Değişim oranını renklendir
        val changeDay = cripto.changeDay
        holder.tvCriptoChangeDay.text = String.format(Locale.US, "%.2f%%", changeDay)

        if (changeDay > 0) {
            holder.tvCriptoChangeDay.setTextColor(Color.parseColor("#4CAF50")) //Yeşil
        } else if (changeDay < 0) {
            holder.tvCriptoChangeDay.setTextColor(Color.parseColor("#F44336")) //Kırmızı
        } else {
            holder.tvCriptoChangeDay.setTextColor(Color.parseColor("#808080")) //Gri
        }
    }

    override fun getItemCount() = criptoList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newCriptoList: List<CriptoResult>) {
        criptoList = newCriptoList
        notifyDataSetChanged()
    }
}