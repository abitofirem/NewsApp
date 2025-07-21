package com.example.navigationdrawerapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.navigationdrawerapp.R
import com.example.navigationdrawerapp.model.Currency

class CurrencyAdapter(private var currencyList: List<Currency>) :
    RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {

    var isExpanded: Boolean = false
        private set // Dışarıdan sadece okunabilir olmasını sağlar

    class CurrencyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.tv_currency_name)
        val buyingTextView: TextView = view.findViewById(R.id.tv_currency_buying)
        val sellingTextView: TextView = view.findViewById(R.id.tv_currency_selling)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_currency, parent, false)
        return CurrencyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        val currency = currencyList[position]
        holder.nameTextView.text = currency.name
        val context = holder.itemView.context
        holder.buyingTextView.text = "${context.getString(R.string.finance_buying)}: ${currency.buying}"
        holder.sellingTextView.text = "${context.getString(R.string.finance_selling)}: ${currency.selling}"
    }

    override fun getItemCount(): Int {
        return if (isExpanded) currencyList.size else minOf(currencyList.size, 3)
    }

    fun setExpanded(expanded: Boolean) {
        if (this.isExpanded != expanded) {
            this.isExpanded = expanded
            notifyDataSetChanged()
        }
    }

    fun updateData(newCurrencyList: List<Currency>) {
        this.currencyList = newCurrencyList
        notifyDataSetChanged()
    }
}