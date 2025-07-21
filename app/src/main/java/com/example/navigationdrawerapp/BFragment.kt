package com.example.navigationdrawerapp.ui.fragments // Projenizin Fragment paketi

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
// import androidx.navigation.fragment.findNavController // BU SATIRI SİLİNDİ!
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.navigationdrawerapp.R // R.id.fragment_container için gerekli
import com.example.navigationdrawerapp.adapter.LeagueAdapter
import com.example.navigationdrawerapp.databinding.FragmentBBinding // BFragment'ın layout'u için View Binding
import com.example.navigationdrawerapp.ui.LeagueDetailFragment // LeagueDetailFragment'ı import et!
import com.example.navigationdrawerapp.ui.viewmodel.LeagueViewModel // LeagueViewModel'ı import ettik


class BFragment : Fragment() {

    //ViewBinding için bir private var oluşturalım.
    private var _binding: FragmentBBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LeagueViewModel by viewModels()

    private lateinit var leagueAdapter: LeagueAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        viewModel.fetchLeagues()

        // Arama kutusu ve ikonunu bağla
        binding.ivSearchIcon.setOnClickListener {
            val query = binding.etSearch.text.toString()
            leagueAdapter.filter(query)
        }

        // Klavyeden arama tuşuna basınca da filtrele
        binding.etSearch.setOnEditorActionListener { v, actionId, event ->
            val query = binding.etSearch.text.toString()
            leagueAdapter.filter(query)
            false
        }

        // Yazdıkça anlık filtreleme (isteğe bağlı, kaldırılabilir)
        binding.etSearch.addTextChangedListener {
            val query = it?.toString() ?: ""
            leagueAdapter.filter(query)
        }
    }

    private fun setupRecyclerView() {
        leagueAdapter = LeagueAdapter(emptyList()) { league ->
            Log.d("BFragment", "Tıklanan Lig: ${league.leagueName}, Key: ${league.leagueKey}")

            //Tıklanan ligin verilerini Bundle'a ekle
            val bundle = Bundle().apply {
                putString("leagueKey", league.leagueKey)
                putString("leagueName", league.leagueName)
            }

            //LeagueDetailFragment'ı oluştur ve argümanları ata
            val leagueDetailFragment = LeagueDetailFragment()
            leagueDetailFragment.arguments = bundle

            //Fragment'ı fragment_container'a yükle
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, leagueDetailFragment)
                .addToBackStack(null) // Geri tuşu ile BFragment'a dönebilmek için
                .commit()
        }
        binding.rvLeagues.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = leagueAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.leagues.observe(viewLifecycleOwner) { leagues ->
            if (leagues != null) {
                leagueAdapter.updateLeagues(leagues)
            } else {
                Log.e("BFragment", "Ligler yüklenemedi veya boş döndü.")
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Yükleme durumu için ProgressBar gösterebilirsin
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Log.e("BFragment", "Hata: $errorMessage")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}