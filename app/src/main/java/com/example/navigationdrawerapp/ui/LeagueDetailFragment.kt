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
import com.bumptech.glide.Glide
import com.example.navigationdrawerapp.R
import com.example.navigationdrawerapp.adapter.StandingsAdapter
import com.example.navigationdrawerapp.databinding.FragmentLeagueDetailBinding
import com.example.navigationdrawerapp.adapter.MatchResultAdapter
import com.example.navigationdrawerapp.viewmodel.LeagueDetailViewModel
import com.example.navigationdrawerapp.adapter.GoalKingAdapter

class LeagueDetailFragment : Fragment() {

    private var _binding: FragmentLeagueDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LeagueDetailViewModel by viewModels()

    private lateinit var standingsAdapter: StandingsAdapter
    private lateinit var matchResultAdapter: MatchResultAdapter
    private lateinit var goalKingAdapter: GoalKingAdapter

    private var currentLeagueKey: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeagueDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentLeagueKey = arguments?.getString("leagueKey")
        val leagueName = arguments?.getString("leagueName")
        val leagueLogoUrl = arguments?.getString("leagueLogoUrl")

        if (currentLeagueKey == null || leagueName == null) {
            Toast.makeText(context, "Lig bilgileri eksik!", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        Log.d("LeagueDetailFragment", "Alınan League Key: $currentLeagueKey, League Name: $leagueName")

        binding.tvLeagueDetailName.text = leagueName
        binding.tvCurrentLeagueNameHeader.text = leagueName

        if (!leagueLogoUrl.isNullOrEmpty()) {
            Glide.with(this).load(leagueLogoUrl).into(binding.ivLeagueDetailLogo)
        } else {
            binding.ivLeagueDetailLogo.setImageResource(R.drawable.ic_football)
        }

        setupRecyclerViewAndAdapters()
        observeViewModel()
        setupTabListeners(currentLeagueKey!!)

        // Varsayılan olarak Puan Durumu sekmesini seç
        binding.toggleButtonGroupTabs.check(R.id.btn_standings)

        // Başlangıçta cl_week_header gizli olmalı
        binding.clWeekHeader.visibility = View.GONE
        binding.spinnerWeekSelection.visibility = View.GONE
        binding.ivMenuIcon.visibility = View.GONE
    }

    private fun setupRecyclerViewAndAdapters() {
        binding.rvStandings.layoutManager = LinearLayoutManager(context)

        standingsAdapter = StandingsAdapter(emptyList())
        matchResultAdapter = MatchResultAdapter(emptyList())
        goalKingAdapter = GoalKingAdapter(emptyList())
    }

    private fun observeViewModel() {
        viewModel.standings.observe(viewLifecycleOwner) { standings ->
            if (binding.toggleButtonGroupTabs.checkedButtonId == R.id.btn_standings) {
                if (standings != null && standings.isNotEmpty()) {
                    standingsAdapter.updateStandings(standings)
                    binding.rvStandings.adapter = standingsAdapter
                    Log.d("LeagueDetailFragment", "Puan durumu güncellendi: ${standings.size} takım.")
                } else {
                    Log.e("LeagueDetailFragment", "Puan durumu yüklenemedi veya boş geldi.")
                    Toast.makeText(context, viewModel.errorMessage.value ?: "Puan durumu çekilemedi.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.matchResults.observe(viewLifecycleOwner) { matchResults ->
            if (binding.toggleButtonGroupTabs.checkedButtonId == R.id.btn_fixtures) {
                if (matchResults != null && matchResults.isNotEmpty()) {
                    // Burası artık sorun olmayacak, çünkü MatchResultAdapter'da updateMatchResults metodu var
                    matchResultAdapter.updateMatchResults(matchResults)
                    binding.rvStandings.adapter = matchResultAdapter
                    Log.d("LeagueDetailFragment", "Fikstür güncellendi: ${matchResults.size} maç.")
                } else {
                    Log.e("LeagueDetailFragment", "Fikstür yüklenemedi veya boş geldi.")
                    Toast.makeText(context, viewModel.errorMessage.value ?: "Fikstür çekilemedi.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.goalKings.observe(viewLifecycleOwner) { goalKings ->
            if (binding.toggleButtonGroupTabs.checkedButtonId == R.id.btn_stats) {
                if (goalKings != null && goalKings.isNotEmpty()) {
                    goalKingAdapter.updateGoalKings(goalKings)
                    binding.rvStandings.adapter = goalKingAdapter
                    Log.d("LeagueDetailFragment", "Gol krallığı güncellendi: ${goalKings.size} oyuncu.")
                } else {
                    Log.e("LeagueDetailFragment", "Gol krallığı yüklenemedi veya boş geldi.")
                    Toast.makeText(context, viewModel.errorMessage.value ?: "Gol krallığı çekilemedi.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            Log.d("LeagueDetailFragment", "Yükleme durumu: $isLoading")
            // Yükleme animasyonu gösterme/gizleme mantığı buraya gelebilir
            // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty() && !viewModel.isLoading.value!!) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("LeagueDetailFragment", "Hata: $errorMessage")
            }
        }
    }

    private fun setupTabListeners(leagueKey: String) {
        binding.toggleButtonGroupTabs.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                binding.clWeekHeader.visibility = View.GONE
                binding.spinnerWeekSelection.visibility = View.GONE
                binding.ivMenuIcon.visibility = View.GONE

                when (checkedId) {
                    R.id.btn_standings -> {
                        Log.d("LeagueDetailFragment", "Puan Durumu sekmesi seçildi.")
                        binding.rvStandings.adapter = standingsAdapter
                        viewModel.fetchStandings(leagueKey)
                    }
                    R.id.btn_fixtures -> {
                        Log.d("LeagueDetailFragment", "Fikstür sekmesi seçildi.")
                        binding.clWeekHeader.visibility = View.VISIBLE
                        binding.spinnerWeekSelection.visibility = View.VISIBLE
                        binding.ivMenuIcon.visibility = View.VISIBLE
                        binding.rvStandings.adapter = matchResultAdapter
                        viewModel.fetchMatchResults(leagueKey)
                    }
                    R.id.btn_stats -> {
                        Log.d("LeagueDetailFragment", "İstatistik sekmesi seçildi.")
                        binding.rvStandings.adapter = goalKingAdapter
                        viewModel.fetchGoalKings(leagueKey)
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