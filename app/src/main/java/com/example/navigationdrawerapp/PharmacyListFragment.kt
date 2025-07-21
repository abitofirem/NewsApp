package com.example.navigationdrawerapp // Paket yolunuzu kontrol edin ve gerekirse düzenleyin
import android.os.Bundle
import android.util.Log // Debug için Logcat'e çıktı basmak için
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView // Geri butonu için MaterialCardView importu
import com.example.navigationdrawerapp.adapter.PharmacyAdapter // PharmacyAdapter'ı import et!
import com.example.navigationdrawerapp.viewmodel.PharmacyViewModel // PharmacyViewModel'ı import et!
import com.example.navigationdrawerapp.model.Pharmacy // Pharmacy modelini import et!

//EFragment'tan gelecek argümanlar için sabit anahtarlar
private const val ARG_CITY = "city"
private const val ARG_DISTRICT = "district"

class PharmacyListFragment : Fragment() {

    private lateinit var viewModel: PharmacyViewModel
    private lateinit var pharmacyAdapter: PharmacyAdapter

    //Layout'taki View bileşenleri
    private lateinit var tvPharmacyInfoTitle: TextView
    private lateinit var errorMessageTextView: TextView
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var pharmacyRecyclerView: RecyclerView
    private lateinit var cardBackButton: MaterialCardView //MaterialCardView olarak tanımlıyoruz

    //EFragment'tan alacağımız şehir ve ilçe bilgileri
    private var selectedCity: String? = null
    private var selectedDistrict: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedCity = it.getString(ARG_CITY)
            selectedDistrict = it.getString(ARG_DISTRICT)
        }
        //Debug için Logcat'e çıktı basalım:
        Log.d("PharmacyListFragment", "onCreate - Alınan Şehir: $selectedCity, İlçe: $selectedDistrict")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //fragment_pharmacy_list.xml layout'unu şişiriyoruz
        val view = inflater.inflate(R.layout.fragment_pharmacy_list, container, false)

        //Görünümleri bağla (findViewById)
        tvPharmacyInfoTitle = view.findViewById(R.id.tv_pharmacy_info_title)
        errorMessageTextView = view.findViewById(R.id.errorMessageTextView)
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)
        pharmacyRecyclerView = view.findViewById(R.id.rv_pharmacies)
        cardBackButton = view.findViewById(R.id.card_back_button)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //ViewModel'ı başlat
        viewModel = ViewModelProvider(this).get(PharmacyViewModel::class.java)

        //RecyclerView ve Adapter'ı ayarla
        //Başlangıçta boş bir liste ile adaptörü oluştur
        pharmacyAdapter = PharmacyAdapter(emptyList()) { pharmacy ->
            //Eczane listesindeki bir öğeye tıklandığında ne olacağını burada tanımla
            //Örneğin, harita uygulamasını açabiliriz
            Toast.makeText(context, "${pharmacy.name} adresine gidiliyor...", Toast.LENGTH_SHORT).show()
            //Harita açma fonksiyonu burada çağrılabilir
            openMapForPharmacy(pharmacy) //Pharmacy modelindeki 'loc' alanı enlem/boylam içeriyor
        }
        pharmacyRecyclerView.layoutManager = LinearLayoutManager(context)
        pharmacyRecyclerView.adapter = pharmacyAdapter

        //LiveData'ları gözlemle (ViewModel'dan gelen güncellemeleri dinle)
        viewModel.pharmacies.observe(viewLifecycleOwner, Observer { pharmacies ->
            if (pharmacies.isNullOrEmpty()) {
                //Eczane listesi boşsa veya null ise
                pharmacyRecyclerView.visibility = View.GONE
                if (errorMessageTextView.visibility != View.VISIBLE) {
                    //Eğer zaten bir hata mesajı görünmüyorsa, "veri bulunamadı" mesajını göster
                    errorMessageTextView.text = "Seçilen konumda nöbetçi eczane bulunamadı."
                    errorMessageTextView.visibility = View.VISIBLE
                }
            } else {
                //Eczane listesi varsa
                pharmacyRecyclerView.visibility = View.VISIBLE
                pharmacyAdapter.updateData(pharmacies) // Adapter'ı güncelle
                errorMessageTextView.visibility = View.GONE // Hata mesajını gizle

                //Başlık metnini dinamik olarak ayarla

                //BAŞLIK METNİ DEĞİŞİKLİĞİ BURADA
                val titleText = if (!selectedDistrict.isNullOrBlank()) {
                    "${selectedCity} - ${selectedDistrict}\nNöbetçi Eczaneler" //İl-İlçe alt alta
                } else {
                    "${selectedCity}\nNöbetçi Eczaneler" //Sadece il için
                }
                tvPharmacyInfoTitle.text = titleText
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            //Yüklenme durumuna göre ProgressBar'ı göster/gizle
            loadingProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            //Yüklenirken RecyclerView'ı gizleyebilir veya etkileşimini kapatabiliriz
            pharmacyRecyclerView.visibility = if (isLoading) View.GONE else {
                if (!viewModel.pharmacies.value.isNullOrEmpty()) View.VISIBLE else View.GONE
            }
            errorMessageTextView.visibility = if (isLoading) View.GONE else {
                if (viewModel.errorMessage.value != null || viewModel.pharmacies.value.isNullOrEmpty()) View.VISIBLE else View.GONE
            }
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMessage ->
            if (errorMessage != null) {
                //Hata mesajı varsa göster
                errorMessageTextView.text = errorMessage
                errorMessageTextView.visibility = View.VISIBLE
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                pharmacyRecyclerView.visibility = View.GONE //Hata varsa listeyi gizle
            } else {
                //Hata yoksa gizle
                errorMessageTextView.visibility = View.GONE
            }
        })

        //Geri butonuna tıklama dinleyicisi
        cardBackButton.setOnClickListener {
            //Fragment yığınından önceki fragment'a geri dön
            parentFragmentManager.popBackStack()
        }

        //Fragment oluşturulduğunda veya onViewCreated'da hemen API çağrısını yap
        //selectedCity'nin null olmadığından emin ol
        selectedCity?.let { city ->
            Log.d("PharmacyListFragment", "API çağrısı yapılıyor: Şehir=$city, İlçe=$selectedDistrict")
            viewModel.fetchDutyPharmacies(city, selectedDistrict)
        } ?: run {
            //Eğer şehir bilgisi gelmediyse hata göster (Bu durum pek olmamalı)
            val errorMsg = "Şehir bilgisi eksik. Lütfen geri dönüp şehir seçin."
            errorMessageTextView.text = errorMsg
            errorMessageTextView.visibility = View.VISIBLE
            pharmacyRecyclerView.visibility = View.GONE
            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
        }
    }

    //Harita uygulamasını açmak için yardımcı fonksiyon
    private fun openMapForPharmacy(pharmacy: Pharmacy) { // Parametre olarak tüm Pharmacy nesnesini alıyoruz
        val location = pharmacy.loc.replace(" ", "") // Konumdaki boşlukları kaldır
        val pharmacyName = pharmacy.name // Eczane adını al

        //'loc' formatı "latitude,longitude" olmalı
        if (location.isNotBlank()) {
            try {
                // Konumu ve etiketi içeren bir URI oluştur
                val gmmIntentUri = android.net.Uri.parse("geo:0,0?q=$location($pharmacyName)")

                // DEBUG: Oluşturulan URI'ı Logcat'e yazdıralım
                Log.d("PharmacyListFragment", "Oluşturulan Harita URI: $gmmIntentUri")

                val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, gmmIntentUri)

                if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(mapIntent)
                } else {
                    // Eğer hiçbir harita uygulaması bulunamazsa
                    Toast.makeText(context, "Harita uygulaması bulunamadı.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("PharmacyListFragment", "Harita açılırken hata oluştu: ${e.message}")
                Toast.makeText(context, "Harita açılamadı: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Eczane konumu mevcut değil.", Toast.LENGTH_SHORT).show()
        }
    }

    //Bu factory metodu, EFragment'tan PharmacyListFragment'ı oluştururken
    //argümanları güvenli bir şekilde geçirmek için kullanılır.
    companion object {
        @JvmStatic
        fun newInstance(city: String, district: String?) =
            PharmacyListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CITY, city)
                    putString(ARG_DISTRICT, district) //İlçe boşsa null olarak geçebilir
                }
            }
    }
}