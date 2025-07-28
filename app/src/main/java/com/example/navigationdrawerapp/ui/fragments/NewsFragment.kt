package com.example.navigationdrawerapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.navigationdrawerapp.MainActivity
import com.example.navigationdrawerapp.R
import com.example.navigationdrawerapp.adapter.NewsAdapter
import com.example.navigationdrawerapp.databinding.FragmentNewsBinding
import com.example.navigationdrawerapp.viewmodel.NewsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class NewsFragment : Fragment() {
    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    //NewsViewModel'i oluşturun (Fragment KTX'in viewModels delegesi ile)
    private val newsViewModel: NewsViewModel by viewModels()
    private lateinit var newsAdapter: NewsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toolbar başlığını bu fragment açıldığında güncelleyelim (isteğe bağlı)
        // Toolbar başlığını bu fragment açıldığında güncelleyelim
        (activity as? MainActivity)?.setToolbarTitle(getString(R.string.title_news)) // BURADA DEĞİŞİKLİK
        setupRecyclerView()

        observeViewModel() //ViewModel'i gözlemlemeye başla

        // Firestore'dan kaydedilen haber url'lerini çek
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(user.uid).collection("savedNews")
                .get()
                .addOnSuccessListener { result ->
                    fun urlToKey(url: String): String = url.hashCode().toString()
                    val savedUrls = result.documents.mapNotNull { it.getString("haberUrl") }
                    val savedKeys = savedUrls.map { urlToKey(it) }.toSet()
                    newsAdapter.setSavedSet(savedKeys)
                }
        }
    }

    override fun onResume() {
        super.onResume()
        newsViewModel.fetchNews()
    }

    private fun setupRecyclerView() {

        //Adaptörü boş bir liste ile başlatıyoruz, çünkü veriler ViewModel'den gelecek
        newsAdapter = NewsAdapter(emptyList(), { news ->
            //Haber öğesine tıklandığında NewsDetailFragment'ı aç
            val detailFragment = NewsDetailFragment().apply {
                arguments = Bundle().apply {
                    // Haber objesindeki 'id' artık String, buna dikkat!
                    putString("haber_id", news.id)
                    putString("haber_baslik", news.title)
                    putString("haber_icerik", news.content)
                    putString("haber_gorsel_url", news.imageUrl)
                }
            }

            //Fragment'ı değiştirmek için FragmentTransaction kullan
            //fragment_container, activity_main.xml'deki FrameLayout ID'si olmalı
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null) // Geri tuşuyla listeye dönmek için
                .commit()
        })

        binding.recyclerViewHaberler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = newsAdapter
            // Sonsuz kaydırma için RecyclerView kaydırma dinleyicisini ekle
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    // Listenin sonuna yaklaşıldığında ve zaten yükleme yapılmıyorsa ve daha fazla haber varsa
                    if (!newsViewModel.isLoading.value!! && newsViewModel.hasMoreNews) {
                        // Son 5 öğeden biri görünüyorsa yükleme yap
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5
                            && firstVisibleItemPosition >= 0) {
                            newsViewModel.loadMoreNews()
                        }
                    }
                }
            })
        }
    }


    private fun observeViewModel() {
        newsViewModel.newsList.observe(viewLifecycleOwner) { news ->
            // ViewModel'den yeni haber listesi geldiğinde adaptörü güncelle
            newsAdapter.updateNews(news)
        }

        newsViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Yükleme göstergesini görünür yap/gizle
            // progressBar id'si fragment_c.xml'de tanımlı olmalı!
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        newsViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            // Hata mesajı geldiğinde Toast göster
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                //Hata gösterildikten sonra hata mesajını temizleyebiliriz
                //newsViewModel._errorMessage.value = null // Eğer MutableLiveData'ya doğrudan erişim varsa
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
