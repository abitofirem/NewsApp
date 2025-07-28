package com.example.navigationdrawerapp.ui.fragments
import com.example.navigationdrawerapp.R


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.navigationdrawerapp.databinding.FragmentShareBinding // Bu satırı kendi layout dosyanıza göre güncelleyin
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.navigationdrawerapp.adapter.NewsAdapter
import com.example.navigationdrawerapp.model.News

class ShareFragment : Fragment() {

    private var _binding: FragmentShareBinding? = null // Binding sınıfı adını güncelleyin
    private val binding get() = _binding!!

    private lateinit var newsAdapter: NewsAdapter
    private var newsList: List<News> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShareBinding.inflate(inflater, container, false) // Binding sınıfı adını güncelleyin
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsAdapter = NewsAdapter(newsList, { /* tıklama işlemi */ }) { loadSavedNews() }
        binding.recyclerViewKaydedilenler.adapter = newsAdapter
        binding.recyclerViewKaydedilenler.layoutManager = LinearLayoutManager(requireContext())

        loadSavedNews()
    }

    private fun loadSavedNews() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users")
                .document(user.uid)
                .collection("savedNews")
                .get()
                .addOnSuccessListener { result ->
                    val savedNewsList = result.documents.mapNotNull { doc ->
                        doc.toObject(News::class.java)
                    }
                    newsAdapter.updateNews(savedNewsList)
                    // EKLENDİ: Kaydedilenlerin key'lerini adapter'a ilet
                    val savedKeys = savedNewsList.map { it.newsUrl.hashCode().toString() }.toSet()
                    newsAdapter.setSavedSet(savedKeys)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Kaydedilenler yüklenemedi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "Kaydedilenleri görmek için giriş yapmalısınız.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
