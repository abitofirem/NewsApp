package com.example.navigationdrawerapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.navigationdrawerapp.R // R.drawable.ic_football için gerekli
import com.example.navigationdrawerapp.adapter.StandingsAdapter
import com.example.navigationdrawerapp.databinding.FragmentLeagueDetailBinding
import com.example.navigationdrawerapp.ui.viewmodel.LeagueDetailViewModel

class LeagueDetailFragment : Fragment() {

    private var _binding: FragmentLeagueDetailBinding? = null
    private val binding get() = _binding!!

    //NavArgs kullanımını kaldırdık, argümanları Bundle'dan alacağız.
    //private val args: LeagueDetailFragmentArgs by navArgs()

    private val viewModel: LeagueDetailViewModel by viewModels()

    private lateinit var standingsAdapter: StandingsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeagueDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Argümanları al (manuel yöntem)
        val leagueKey = arguments?.getString("leagueKey")
        val leagueName = arguments?.getString("leagueName")

        if (leagueKey == null || leagueName == null) {
            Toast.makeText(context, "Lig bilgileri eksik!", Toast.LENGTH_SHORT).show()
            //Eğer eksikse geri gidebiliriz (isteğe bağlı, uygulamanın akışına göre değişir)
            parentFragmentManager.popBackStack()
            return
        }

        Log.d("LeagueDetailFragment", "Alınan League Key: $leagueKey, League Name: $leagueName")

        // UI elemanlarını doldur
        binding.tvLeagueDetailName.text = leagueName
        binding.tvCurrentLeagueNameHeader.text = leagueName // Haftalık spinner'ın yanındaki başlık
        binding.ivLeagueDetailLogo.setImageResource(R.drawable.ic_football) // Lig logosuna futbol topu

        //Puan Durumu RecyclerView'ını ayarla
        setupStandingsRecyclerView()

        //Puan Durumu çekme işlemini başlat (varsayılan olarak puan durumu sekmesi seçili)
        viewModel.fetchStandings(leagueKey)

        //ViewModel'dan LiveData'ları gözlemle
        observeViewModel()

        //Sekme tıklama dinleyicilerini ayarla
        setupTabListeners(leagueKey)

        //Haftalık header'ı ve spinner'ı başlangıçta gizle (sadece fikstür için gerekli olacak)
        binding.clWeekHeader.visibility = View.GONE
        binding.spinnerWeekSelection.visibility = View.GONE
        binding.ivMenuIcon.visibility = View.GONE
    }

    private fun setupStandingsRecyclerView() {
        standingsAdapter = StandingsAdapter(emptyList()) //Boş liste ile başlat
        binding.rvStandings.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = standingsAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.standings.observe(viewLifecycleOwner) { standings ->
            if (standings != null) {
                //standings artık List<TeamStanding> tipinde olacak
                standingsAdapter.updateStandings(standings)
                Log.d("LeagueDetailFragment", "Puan durumu güncellendi: ${standings.size} takım.")
            } else {
                Log.e("LeagueDetailFragment", "Puan durumu yüklenemedi veya boş geldi.")
                Toast.makeText(context, "Puan durumu çekilemedi.", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            Log.d("LeagueDetailFragment", "Yükleme durumu: $isLoading")
            //Yükleme göstergesi için burası kullanılabilir
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("LeagueDetailFragment", "Hata: $errorMessage")
                //Hata mesajı gösterildikten sonra ViewModel'daki hatayı temizlemek isterseniz:
                //viewModel.clearErrorMessage() //Eğer ViewModel'a bu metodu eklediyseniz kullanın
            }
        }
    }

    private fun setupTabListeners(leagueKey: String) {
        binding.toggleButtonGroupTabs.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btn_standings -> {
                        Log.d("LeagueDetailFragment", "Puan Durumu sekmesi seçildi.")
                        binding.clWeekHeader.visibility = View.GONE
                        viewModel.fetchStandings(leagueKey)
                    }
                    R.id.btn_fixtures -> {
                        Log.d("LeagueDetailFragment", "Fikstür sekmesi seçildi.")
                        binding.clWeekHeader.visibility = View.VISIBLE
                        Toast.makeText(context, "Fikstür verileri yakında!", Toast.LENGTH_SHORT).show()
                        standingsAdapter.updateStandings(emptyList()) //Puan durumunu temizle
                    }
                    R.id.btn_stats -> {
                        Log.d("LeagueDetailFragment", "İstatistik sekmesi seçildi.")
                        binding.clWeekHeader.visibility = View.GONE
                        Toast.makeText(context, "İstatistik verileri yakında!", Toast.LENGTH_SHORT).show()
                        standingsAdapter.updateStandings(emptyList()) //Puan durumunu temizle
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}