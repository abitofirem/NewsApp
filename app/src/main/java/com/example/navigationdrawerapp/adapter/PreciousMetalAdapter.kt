package com.example.navigationdrawerapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.navigationdrawerapp.R
import com.example.navigationdrawerapp.model.PreciousMetal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class PreciousMetalAdapter(private var preciousMetalList: List<PreciousMetal>) :
    RecyclerView.Adapter<PreciousMetalAdapter.PreciousMetalViewHolder>() {

    class PreciousMetalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.tv_metal_name)
        val buyingTextView: TextView = view.findViewById(R.id.tv_metal_buying)
        val sellingTextView: TextView = view.findViewById(R.id.tv_metal_selling)
        val rateTextView: TextView = view.findViewById(R.id.tv_metal_rate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreciousMetalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gold_silver, parent, false)
        return PreciousMetalViewHolder(view)
    }

    override fun onBindViewHolder(holder: PreciousMetalViewHolder, position: Int) {
        val metal = preciousMetalList[position]

        //Ondalık format
        val decimalFormat = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale("tr", "TR")))

        holder.nameTextView.text = metal.name
        val context = holder.itemView.context

        //Alış fiyatı boş değilse göster
        if (metal.buying.isNotEmpty() && metal.buying != "-") {
            holder.buyingTextView.text = "${context.getString(R.string.finance_buying)}: ${decimalFormat.format(metal.buying.toDouble())}"
            holder.buyingTextView.visibility = View.VISIBLE
        } else {
            holder.buyingTextView.visibility = View.GONE
        }

        //Satış fiyatı boş değilse göster
        if (metal.selling.isNotEmpty() && metal.selling != "-") {
            holder.sellingTextView.text = "${context.getString(R.string.finance_selling)}: ${decimalFormat.format(metal.selling.toDouble())}"
            holder.sellingTextView.visibility = View.VISIBLE
        } else {
            holder.sellingTextView.visibility = View.GONE
        }

        //Değişim oranını ayarla ve rengini değiştir
        metal.rate?.let { rate ->
            holder.rateTextView.text = String.format(Locale.US, "%.2f%%", rate)
            when {
                rate > 0 -> holder.rateTextView.setTextColor(Color.GREEN) //veya kaynak dosyasından al
                rate < 0 -> holder.rateTextView.setTextColor(Color.RED) //veya kaynak dosyasından al
                else -> holder.rateTextView.setTextColor(Color.GRAY) //veya kaynak dosyasından al
            }
            holder.rateTextView.visibility = View.VISIBLE
        } ?: run {
            holder.rateTextView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return preciousMetalList.size
    }

    fun updateData(newPreciousMetalList: List<PreciousMetal>) {
        this.preciousMetalList = newPreciousMetalList
        notifyDataSetChanged()
    }
}