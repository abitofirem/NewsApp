package com.example.navigationdrawerapp.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.navigationdrawerapp.R
import com.example.navigationdrawerapp.model.Currency

class CurrencySelectionAdapter(private val currencyList: List<Currency>) :
    RecyclerView.Adapter<CurrencySelectionAdapter.CurrencyViewHolder>() {

    var onItemClick: ((Currency) -> Unit)? = null

    inner class CurrencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCurrencyName: TextView = itemView.findViewById(R.id.tv_currency_name)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(currencyList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dialog_currency, parent, false)
        return CurrencyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        val currency = currencyList[position]
        holder.tvCurrencyName.text = "${currency.name} (${currency.code})" // BURAYI DEĞİŞTİR
    }

    override fun getItemCount() = currencyList.size
}