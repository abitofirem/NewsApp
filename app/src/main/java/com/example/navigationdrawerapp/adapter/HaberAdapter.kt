package com.example.navigationdrawerapp.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.navigationdrawerapp.Haber
import com.example.navigationdrawerapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast

// RecyclerView için verileri bağlayacak adaptör
class HaberAdapter(
    private var haberList: List<Haber>, //Mutable list olarak tanımladık ki updateNews ile değiştirebilelim
    private val onItemClick: (Haber) -> Unit //Tıklama için lambda fonksiyonu
) : RecyclerView.Adapter<HaberAdapter.HaberViewHolder>() {

    //Haber öğesi tıklama için arayüz (opsiyonel, lambda kullanıldığı için çok zorunlu değil ama iyi bir pratik)
    interface OnItemClickListener {
        fun onItemClick(haber: Haber)
    }

    //Her bir haber öğesinin görünümlerini tutar
    class HaberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.haberImageView)
        val baslikTextView: TextView = itemView.findViewById(R.id.haberBaslikTextView)
        val icerikTextView: TextView = itemView.findViewById(R.id.haberIcerikTextView)
    }

    //ViewHolder oluşturulduğunda çağrılır
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HaberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_haber, parent, false)
        return HaberViewHolder(view)
    }

    private fun urlToKey(url: String): String = url.hashCode().toString()

    private val savedSet = mutableSetOf<String>()

    fun setSavedSet(urls: Set<String>) {
        savedSet.clear()
        savedSet.addAll(urls)
        notifyDataSetChanged()
    }

    //ViewHolder'a veri bağlandığında çağrılır
    override fun onBindViewHolder(holder: HaberViewHolder, position: Int) {
        val haber = haberList[position]
        holder.baslikTextView.text = haber.baslik
        holder.icerikTextView.text = haber.icerik

        //Glide ile görsel yükleme
        Glide.with(holder.itemView.context)
            .load(haber.gorselUrl)
            .placeholder(R.drawable.placeholder_image) // Resim yüklenene kadar göster
            .error(R.drawable.error_image) // Hata olursa göster
            .into(holder.imageView)

        //Kaydet butonunun görünürlüğü
        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
        val saveNewsView = holder.itemView.findViewById<ImageView>(R.id.iv_save_news)
        saveNewsView.visibility = if (isLoggedIn) View.VISIBLE else View.GONE

        val uniqueKey = urlToKey(haber.haberUrl)
        val isSaved = savedSet.contains(uniqueKey)
        saveNewsView.setImageResource(if (isSaved) R.drawable.ic_save_filled else R.drawable.ic_save)

        if (isLoggedIn) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val db = FirebaseFirestore.getInstance()
            val savedRef = db.collection("users").document(userId).collection("savedNews").document(uniqueKey)

            saveNewsView.setOnClickListener {
                if (isSaved) {
                    savedRef.delete().addOnSuccessListener {
                        savedSet.remove(uniqueKey)
                        notifyItemChanged(position)
                    }
                } else {
                    val newsData = hashMapOf(
                        "id" to haber.id,
                        "baslik" to haber.baslik,
                        "icerik" to haber.icerik,
                        "gorselUrl" to haber.gorselUrl,
                        "haberUrl" to haber.haberUrl,
                        "kaynak" to haber.kaynak
                    )
                    savedRef.set(newsData).addOnSuccessListener {
                        savedSet.add(uniqueKey)
                        notifyItemChanged(position)
                    }
                }
            }
        } else {
            saveNewsView.setOnClickListener(null)
        }

        //Haber öğesine tıklama olayını ayarla
        holder.itemView.setOnClickListener {
            onItemClick(haber) //Dışarıdan gelen lambda fonksiyonunu çağır
        }
    }

    //Listedeki toplam öğe sayısını döndürür
    override fun getItemCount(): Int {
        return haberList.size
    }

    //Yeni haber listesi geldiğinde adaptörü güncelleyen metod
    fun updateNews(newNewsList: List<Haber>) {
        this.haberList = newNewsList
        notifyDataSetChanged() //Tüm liste değiştiğinde RecyclerView'ı yeniden çiz (DiffUtil daha iyidir)
    }
}