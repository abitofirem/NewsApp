package com.example.navigationdrawerapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.navigationdrawerapp.R
import com.example.navigationdrawerapp.model.Pharmacy
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PharmacyAdapter(
    private var pharmacies: List<Pharmacy>,
    private val onMapButtonClick: (Pharmacy) -> Unit //Harita butonuna tıklama dinleyicisi
) : RecyclerView.Adapter<PharmacyAdapter.PharmacyViewHolder>() {

    //Her bir öğenin genişleyip genişlemediğini takip etmek için bir Set
    private val expandedPositions = mutableSetOf<Int>()

    //ViewHolder sınıfı: Her bir eczane öğesinin görünümünü tutar
    inner class PharmacyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPharmacyName: TextView = itemView.findViewById(R.id.tv_pharmacy_name)
        val tvCity: TextView = itemView.findViewById(R.id.tv_city)
        val tvDistrict: TextView = itemView.findViewById(R.id.tv_district)
        val tvPhone: TextView = itemView.findViewById(R.id.tv_phone)
        val tvAddress: TextView = itemView.findViewById(R.id.tv_address)
        val ivExpandToggle: ImageView = itemView.findViewById(R.id.iv_expand_toggle)
        val llExpandableDetails: LinearLayout = itemView.findViewById(R.id.ll_expandable_details)
        val btnShowOnMap: com.google.android.material.button.MaterialButton = itemView.findViewById(R.id.btn_show_on_map)

        fun bind(pharmacy: Pharmacy, position: Int) {
            tvPharmacyName.text = pharmacy.name
            tvCity.text = pharmacy.name //Şehir bilgisi doğrudan pharmacy.name'den geliyorsa
            //Eğer Pharmacy modelinde ayrı bir city alanı varsa, onu kullanın
            //Örneğin: tvCity.text = pharmacy.city
            tvDistrict.text = pharmacy.dist
            tvPhone.text = pharmacy.phone
            tvAddress.text = pharmacy.address

            //Genişletilebilir detayların görünürlüğünü ayarla
            val isExpanded = expandedPositions.contains(position)
            llExpandableDetails.visibility = if (isExpanded) View.VISIBLE else View.GONE
            ivExpandToggle.setImageResource(if (isExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down)

            //Genişlet/Daralt butonuna tıklama dinleyicisi
            ivExpandToggle.setOnClickListener {
                if (isExpanded) {
                    expandedPositions.remove(position) //Genişletilmişse daralt
                } else {
                    expandedPositions.add(position) //Daraltılmışsa genişlet
                }
                notifyItemChanged(position) //Sadece bu öğeyi güncelle
            }

            //Haritada Göster butonuna tıklama dinleyicisi
            btnShowOnMap.setOnClickListener {
                onMapButtonClick.invoke(pharmacy) //Lambda fonksiyonunu çağır
            }
        }
    }

    //ViewHolder oluşturulduğunda çağrılır (Layout'u şişirir)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PharmacyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pharmacy, parent, false)
        return PharmacyViewHolder(view)
    }

    //ViewHolder'a veri bağlandığında çağrılır
    override fun onBindViewHolder(holder: PharmacyViewHolder, position: Int) {
        holder.bind(pharmacies[position], position)
    }

    //Listedeki toplam öğe sayısını döndürür
    override fun getItemCount(): Int {
        return pharmacies.size
    }

    //Eczane listesini güncellemek ve RecyclerView'ı yenilemek için metod
    fun updateData(newPharmacies: List<Pharmacy>) {
        pharmacies = newPharmacies
        expandedPositions.clear() // Yeni veri geldiğinde tüm kartları daralt
        notifyDataSetChanged() // Tüm liste değiştiği için adaptörü tamamen yenile
    }
}