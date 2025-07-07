package com.example.navigationdrawerapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider // ViewModelProvider için gerekli
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.navigationdrawerapp.R // R.drawable.ic_football için gerekli
import com.example.navigationdrawerapp.adapter.StandingsAdapter // Puan durumu adapter'ı
import com.example.navigationdrawerapp.databinding.FragmentLeagueDetailBinding
import com.example.navigationdrawerapp.ui.adapter.MatchResultAdapter // Fikstür adapter'ı için eklendi
import com.example.navigationdrawerapp.ui.viewmodel.LeagueDetailViewModel

class LeagueDetailFragment : Fragment() {

    private var _binding: FragmentLeagueDetailBinding? = null
    //ViewBinding kullanırken null güvenlikli erişim sağlamak için kullanılır
    private val binding get() = _binding!!

    //ViewModel'ı başlatmak için Kotlin'in `by viewModels()` delegasyonunu kullanıyoruz.
    //Bu, ViewModel'ın lifecycle'a duyarlı bir şekilde otomatik olarak başlatılmasını sağlar.
    private val viewModel: LeagueDetailViewModel by viewModels()

    //RecyclerView için adapter'ları tanımlıyoruz
    private lateinit var standingsAdapter: StandingsAdapter
    private lateinit var matchResultAdapter: MatchResultAdapter

    //Fragment'ın görünümünü oluşturur ve binding'i başlatır
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeagueDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    //Görünüm oluşturulduktan sonra UI bileşenlerini başlatır ve veri çekme/gözlemleme işlemlerini yapar
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //DİKKAT: _binding zaten onCreateView'de başlatıldı, burada tekrar bind etmek gerekmez.
        //Eğer onCreateView'de inflate etmeseydin, burada FragmentLeagueDetailBinding.bind(view) kullanabilirdin.

        //ViewModel başlatma: 'by viewModels()' kullandığımız için bu satır GEREKSİZ ve ÇAKIŞIR.
        //viewModel = ViewModelProvider(this).get(LeagueDetailViewModel::class.java)

        //RecyclerView ve Adapter'ların temel kurulumunu yapar
        setupRecyclerViewAndAdapters()

        //Argümanları al (manuel yöntem)
        val leagueKey = arguments?.getString("leagueKey")
        val leagueName = arguments?.getString("leagueName")

        //Lig bilgileri eksikse kullanıcıyı uyar ve fragment'tan çık
        if (leagueKey == null || leagueName == null) {
            Toast.makeText(context, "Lig bilgileri eksik!", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        Log.d("LeagueDetailFragment", "Alınan League Key: $leagueKey, League Name: $leagueName")

        //UI elemanlarını alınan verilerle doldur
        binding.tvLeagueDetailName.text = leagueName
        binding.tvCurrentLeagueNameHeader.text = leagueName // Haftalık spinner'ın yanındaki başlık
        //Lig logosunu varsayılan olarak futbol topuyla ayarla (gerçek uygulamada Glide/Coil ile URL'den yüklenir)
        binding.ivLeagueDetailLogo.setImageResource(R.drawable.ic_football)

        //ViewModel'dan gelen LiveData'ları gözlemlemeyi başlat
        observeViewModel()

        //Sekme tıklama dinleyicilerini ayarlar ve ilk veriyi çeker
        setupTabListeners(leagueKey)

        //Başlangıçta varsayılan olarak Puan Durumu sekmesini seçili hale getir.
        //Bu, setupTabListeners içindeki listener'ı tetikleyerek ilgili veriyi çeker ve UI'ı günceller.
        binding.toggleButtonGroupTabs.check(R.id.btn_standings)

        //Haftalık header'ı, spinner'ı ve menü ikonunu başlangıçta gizle (Puan Durumu varsayılan olduğu için)
        //Bu satırlar, `setupTabListeners` içindeki ilk `check` çağrısıyla da yönetilebilir, ancak burada
        //açıkça belirtmek tutarlılık sağlar.
        binding.clWeekHeader.visibility = View.GONE
        binding.spinnerWeekSelection.visibility = View.GONE
        binding.ivMenuIcon.visibility = View.GONE
    }

    //RecyclerView ve Adapter'ları başlatan yardımcı metot
    private fun setupRecyclerViewAndAdapters() {
        binding.rvStandings.layoutManager = LinearLayoutManager(context)
        standingsAdapter = StandingsAdapter(emptyList()) // Puan durumu adapter'ını boş liste ile başlat
        matchResultAdapter = MatchResultAdapter(emptyList()) // Fikstür adapter'ını boş liste ile başlat
        // Adapter atamasını burada yapmıyoruz, sekmelerde seçime göre yapıyoruz.
    }

    //ViewModel'dan gelen LiveData'ları gözlemleyen yardımcı metot
    private fun observeViewModel() {
        //Puan durumu verilerini gözlemle
        viewModel.standings.observe(viewLifecycleOwner) { standings ->
            if (standings != null) {
                // Sadece puan durumu sekmesi seçiliyse adapter'ı güncelle
                if (binding.toggleButtonGroupTabs.checkedButtonId == R.id.btn_standings) {
                    standingsAdapter.updateStandings(standings)
                    binding.rvStandings.adapter = standingsAdapter // Adapter'ı ata
                    Log.d("LeagueDetailFragment", "Puan durumu güncellendi: ${standings.size} takım.")
                }
            } else {
                Log.e("LeagueDetailFragment", "Puan durumu yüklenemedi veya boş geldi.")
                Toast.makeText(context, "Puan durumu çekilemedi.", Toast.LENGTH_SHORT).show()
            }
        }

        //Fikstür verilerini gözlemle
        viewModel.matchResults.observe(viewLifecycleOwner) { matchResults ->
            if (matchResults != null) {
                // Sadece fikstür sekmesi seçiliyse adapter'ı güncelle
                if (binding.toggleButtonGroupTabs.checkedButtonId == R.id.btn_fixtures) {
                    // MatchResultAdapter'ı güncel verilerle yeniden oluşturup atıyoruz
                    // Not: Daha performanslı bir çözüm için MatchResultAdapter içinde bir update metodunu kullanmak daha iyi olabilir.
                    matchResultAdapter = MatchResultAdapter(matchResults)
                    binding.rvStandings.adapter = matchResultAdapter // RecyclerView'a fikstür adapter'ını ata
                    Log.d("LeagueDetailFragment", "Fikstür güncellendi: ${matchResults.size} maç.")
                }
            } else {
                Log.e("LeagueDetailFragment", "Fikstür yüklenemedi veya boş geldi.")
                Toast.makeText(context, "Fikstür çekilemedi.", Toast.LENGTH_SHORT).show()
            }
        }

        //Yükleme durumu değiştiğinde gözlemle (örneğin ProgressBar göstermek için)
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            Log.d("LeagueDetailFragment", "Yükleme durumu: $isLoading")
            //binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        //Hata mesajlarını gözlemle
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("LeagueDetailFragment", "Hata: $errorMessage")
            }
        }
    }

    //Sekme tıklama dinleyicilerini ayarlayan yardımcı metot
    private fun setupTabListeners(leagueKey: String) {
        binding.toggleButtonGroupTabs.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btn_standings -> {
                        Log.d("LeagueDetailFragment", "Puan Durumu sekmesi seçildi.")
                        binding.clWeekHeader.visibility = View.GONE // Puan durumu için haftalık başlığı gizle
                        binding.spinnerWeekSelection.visibility = View.GONE
                        binding.ivMenuIcon.visibility = View.GONE
                        binding.rvStandings.adapter = standingsAdapter // Puan durumu adapter'ını ayarla
                        viewModel.fetchStandings(leagueKey) // Puan durumu verilerini çek
                    }
                    R.id.btn_fixtures -> {
                        Log.d("LeagueDetailFragment", "Fikstür sekmesi seçildi.")
                        binding.clWeekHeader.visibility = View.VISIBLE // Fikstür için haftalık başlığı göster
                        binding.spinnerWeekSelection.visibility = View.VISIBLE // Spinner'ı göster
                        binding.ivMenuIcon.visibility = View.VISIBLE // Menu ikonunu göster
                        binding.rvStandings.adapter = matchResultAdapter // Fikstür adapter'ını ayarla
                        viewModel.fetchMatchResults(leagueKey) // Fikstür verilerini çek
                    }
                    R.id.btn_stats -> {
                        Log.d("LeagueDetailFragment", "İstatistik sekmesi seçildi.")
                        binding.clWeekHeader.visibility = View.GONE // İstatistik için haftalık başlığı gizle
                        binding.spinnerWeekSelection.visibility = View.GONE
                        binding.ivMenuIcon.visibility = View.GONE
                        // İstatistik için ayrı bir adapter veya boş adapter atayabilirsiniz
                        // binding.rvStandings.adapter = null
                        Toast.makeText(context, "İstatistik verileri yakında!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    //Fragment görünümü yok edildiğinde binding referansını temizle
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}