package com.example.navigationdrawerapp.adapter // Projenizin adaptör paketi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide //Takım/Lig logolarını yüklemek için Glide
import com.example.navigationdrawerapp.R
import com.example.navigationdrawerapp.databinding.ItemLeagueBinding //ViewBinding için
import com.example.navigationdrawerapp.model.League //League modelini import ettik
import com.google.firebase.auth.FirebaseAuth
import android.view.View
import android.util.Log

// Lig listesini RecyclerView'da göstermek için adaptör
class LeagueAdapter(
    private var leagueList: List<League>, // Şu an ekranda gösterilen liste
    private val onItemClick: (League) -> Unit //Lig öğesine tıklandığında çalışacak lambda
) : RecyclerView.Adapter<LeagueAdapter.LeagueViewHolder>() {

    private var fullLeagueList: List<League> = leagueList // Tüm liglerin tam kopyası (filtre için)
    // Favori durumunu UI için tutan set
    private val favoriteSet = mutableSetOf<String>() // Lig id'leri ile tutuluyor

    //Her bir lig öğesinin görünümlerini tutan ViewHolder
    //View Binding'i kullanarak View'lara daha güvenli erişim sağlıyoruz
    class LeagueViewHolder(val binding: ItemLeagueBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeagueViewHolder {
        //item_league.xml layoutunu şişir ve bir ViewHolder oluştur
        //View Binding ile layout'u şişiriyoruz
        val binding = ItemLeagueBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeagueViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeagueViewHolder, position: Int) {
        val league = leagueList[position] //Mevcut lig objesini al
        val isFavorite = favoriteSet.contains(league.leagueKey)
        Log.d("LEAGUE_ADAPTER", "onBindViewHolder: ${league.leagueKey} isFavorite=$isFavorite")
        holder.binding.apply {
            tvLeagueName.text = league.leagueName //Lig adını TextView'a bağla

            //CollectAPI'nin leaguesList endpoint'i ülke adı döndürmediği için
            //şimdilik statik olarak "Türkiye" olarak belirledik.
            //İleride bu bilgi API'den gelirse veya başka bir yerden alınırsa güncellenebilir.
            tvCountryName.text = "Türkiye" //Geçici olarak ülke adı

            //Lig bayrağını yükle
            //CollectAPI'nin leaguesList'inde bayrak URL'si yok, bu yüzden şimdilik sabit bir ikon kullanacağız.
            //Glide kullanmaya gerek yok çünkü statik bir drawable kullanıyoruz.
            ivLeagueFlag.setImageResource(R.drawable.ic_football) //Varsayılan bir bayrak ikonu

            //Favori yıldızını yönet (şimdilik her zaman dış hatlı yıldız)
            // Favori durumu kontrolü
            if (isFavorite) {
                ivFavoriteStar.setImageResource(R.drawable.ic_star_filled)
            } else {
                ivFavoriteStar.setImageResource(R.drawable.ic_star_outline)
            }

            //İleride favori ligler özelliği eklenirse, bu kısım güncellenecek.
            //Örneğin: ivFavoriteStar.setImageResource(if (league.isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_outline)

            // Yıldız ikonunun görünürlüğü
            val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
            ivFavoriteStar.visibility = if (isLoggedIn) View.VISIBLE else View.GONE

            ivFavoriteStar.setOnClickListener {
                if (isFavorite) {
                    favoriteSet.remove(league.leagueKey)
                    Log.d("LEAGUE_ADAPTER", "Removed: ${league.leagueKey}")
                } else {
                    favoriteSet.add(league.leagueKey)
                    Log.d("LEAGUE_ADAPTER", "Added: ${league.leagueKey}")
                }
                notifyItemChanged(position)
            }

            //Tüm CardView öğesine tıklama dinleyicisi ekle (item_league.xml'deki root view)
            root.setOnClickListener {
                onItemClick(league) //Lig objesini geri döndürerek onItemClick lambda'sını çağır
            }
        }
    }

    override fun getItemCount(): Int {
        return leagueList.size //Listedeki toplam lig sayısı
    }

    //Yeni lig listesi geldiğinde adaptörü güncellemek için metod
    fun updateLeagues(newLeagueList: List<League>) {
        this.leagueList = newLeagueList
        this.fullLeagueList = newLeagueList // Tam listeyi de güncelle
        notifyDataSetChanged()
    }

    // Arama için filtreleme fonksiyonu
    fun filter(query: String) {
        leagueList = if (query.isBlank()) {
            fullLeagueList
        } else {
            fullLeagueList.filter { it.leagueName.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }
}