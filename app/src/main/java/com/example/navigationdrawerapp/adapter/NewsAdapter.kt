package com.example.navigationdrawerapp.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.navigationdrawerapp.model.News
import com.example.navigationdrawerapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast

// RecyclerView için verileri bağlayacak adaptör
class NewsAdapter(
    private var newsList: List<News>, //Mutable list olarak tanımladık ki updateNews ile değiştirebilelim
    private val onItemClick: (News) -> Unit, //Tıklama için lambda fonksiyonu
    private val onRemovedFromSaved: (() -> Unit)? = null // EKLENDİ: Silme sonrası callback
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    //News öğesi tıklama için arayüz (opsiyonel, lambda kullanıldığı için çok zorunlu değil ama iyi bir pratik)
    interface OnItemClickListener {
        fun onItemClick(news: News)
    }

    //Her bir news öğesinin görünümlerini tutar
    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.haberImageView)
        val titleTextView: TextView = itemView.findViewById(R.id.haberBaslikTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.haberIcerikTextView)
    }

    //ViewHolder oluşturulduğunda çağrılır
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_haber, parent, false)
        return NewsViewHolder(view)
    }

    private fun urlToKey(url: String): String = url.hashCode().toString()

    private val savedSet = mutableSetOf<String>()

    fun setSavedSet(urls: Set<String>) {
        savedSet.clear()
        savedSet.addAll(urls)
        notifyDataSetChanged()
    }

    //ViewHolder'a veri bağlandığında çağrılır
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList[position]
        holder.titleTextView.text = news.title
        holder.contentTextView.text = news.content

        //Glide ile görsel yükleme
        Glide.with(holder.itemView.context)
            .load(news.imageUrl)
            .placeholder(R.drawable.placeholder_image) // Resim yüklenene kadar göster
            .error(R.drawable.error_image) // Hata olursa göster
            .into(holder.imageView)

        //Kaydet butonunun görünürlüğü
        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
        val saveNewsView = holder.itemView.findViewById<ImageView>(R.id.iv_save_news)
        saveNewsView.visibility = if (isLoggedIn) View.VISIBLE else View.GONE

        val uniqueKey = urlToKey(news.newsUrl)
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
                        onRemovedFromSaved?.invoke() // EKLENDİ: Silme sonrası callback
                    }
                } else {
                    val newsData = hashMapOf(
                        "id" to news.id,
                        "title" to news.title,
                        "content" to news.content,
                        "imageUrl" to news.imageUrl,
                        "newsUrl" to news.newsUrl,
                        "source" to news.source
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

        //News öğesine tıklama olayını ayarla
        holder.itemView.setOnClickListener {
            onItemClick(news) //Dışarıdan gelen lambda fonksiyonunu çağır
        }
    }

    //Listedeki toplam öğe sayısını döndürür
    override fun getItemCount(): Int {
        return newsList.size
    }

    //Yeni news listesi geldiğinde adaptörü güncelleyen metod
    fun updateNews(newNewsList: List<News>) {
        this.newsList = newNewsList
        notifyDataSetChanged() //Tüm liste değiştiğinde RecyclerView'ı yeniden çiz (DiffUtil daha iyidir)
    }
}