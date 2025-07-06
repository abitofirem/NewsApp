package com.example.navigationdrawerapp.ui.fragments // Projenizin Fragment paketi

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.navigationdrawerapp.adapter.LeagueAdapter
import com.example.navigationdrawerapp.databinding.FragmentBBinding // BFragment'ın layout'u için View Binding
import com.example.navigationdrawerapp.model.League // League modelini import ettik
import com.example.navigationdrawerapp.ui.viewmodel.LeagueViewModel // LeagueViewModel'ı import ettik

class BFragment : Fragment() {

    //ViewBinding için bir private var oluşturalım.
    //_binding null olabilir çünkü Fragment'ın yaşam döngüsü boyunca set/unset edilecek.
    private var _binding: FragmentBBinding? = null
    //View'a erişmek için kullanılan public val. Sadece binding null değilse erişilebilmeli.
    private val binding get() = _binding!! // !! operatörü, binding'in null olmadığını garanti eder

    //ViewModel'ı başlatmak için viewModels delegasyonu kullanıyoruz.
    //Bu, ViewModel'ın Fragment'ın yaşam döngüsüne bağlı olmasını sağlar.
    private val viewModel: LeagueViewModel by viewModels()

    //RecyclerView için adaptörümüzü lazy olarak başlatıyoruz.
    //Başlangıçta boş bir liste ile başlatılır.
    private lateinit var leagueAdapter: LeagueAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //View Binding'i kullanarak layout'u şişiriyoruz
        _binding = FragmentBBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //RecyclerView ve Adaptör Kurulumu
        setupRecyclerView()

        //ViewModel'dan gelen lig verilerini gözlemle
        observeViewModel()

        //API'den lig verilerini çekmek için ViewModel'ı tetikle
        //Sadece bir kez çağrıldığından emin olmak için LiveData veya başka bir mekanizma kullanılabilir.
        //Şimdilik doğrudan çağırıyoruz.
        viewModel.fetchLeagues()
    }

    private fun setupRecyclerView() {
        //Adaptörü başlatırken tıklama olayını tanımlıyoruz
        leagueAdapter = LeagueAdapter(emptyList()) { league ->
            //Lig öğesine tıklandığında yapılacak işlemler burada tanımlanır.
            //Örneğin: Toast mesajı göster, Lig Detay Fragment'ına geç vb.
            Log.d("BFragment", "Tıklanan Lig: ${league.leagueName}, Key: ${league.leagueKey}")
            //İleride buraya Navigation Component ile LeagueDetailFragment'a geçiş kodu gelecek.
        }

        binding.rvLeagues.apply {
            layoutManager = LinearLayoutManager(context) // Dikey bir liste düzeni
            adapter = leagueAdapter //Adaptörü RecyclerView'a ata
        }
    }

    private fun observeViewModel() {
        //ViewModel'daki lig listesi LiveData'sını gözlemle
        viewModel.leagues.observe(viewLifecycleOwner) { leagues ->
            if (leagues != null) {
                //Eğer lig listesi başarıyla gelirse adaptörü güncelle
                leagueAdapter.updateLeagues(leagues)
            } else {
                Log.e("BFragment", "Ligler yüklenemedi veya boş döndü.")
                //Hata durumunda kullanıcıya bilgi verebilirsin (örn: Toast mesajı)
            }
        }

        // Yükleme durumu LiveData'sını gözlemle (İsteğe bağlı: loading spinner göstermek için)
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            //Örneğin bir ProgressBar'ı göstermek/gizlemek için kullanılabilir
            //binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Hata mesajı LiveData'sını gözlemle (İsteğe bağlı: hata mesajı göstermek için)
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Log.e("BFragment", "Hata: $errorMessage")
                //Toast.makeText(context, "Hata: $errorMessage", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //Bellek sızıntılarını önlemek için binding'i null yapıyoruz
        _binding = null
    }
}