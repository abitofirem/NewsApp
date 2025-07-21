package com.example.navigationdrawerapp // Paket yolunuzu kontrol edin ve gerekirse düzenleyin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment

class EFragment : Fragment() {

    private lateinit var actvCitySelection: AutoCompleteTextView
    private lateinit var actvDistrictSelection: AutoCompleteTextView
    private lateinit var btnSearchPharmacies: Button

    //Şehir ve ilçe verileri (sabitleri burada tutmak daha iyi)
    private val cities = listOf("Ankara", "İstanbul", "İzmir", "Adana", "Antalya", "Bursa")
    private val ankaraDistricts = listOf("Çankaya", "Keçiören", "Yenimahalle", "Mamak", "Etimesgut", "Sincan", "Gölbaşı", "Polatlı")
    private val istanbulDistricts = listOf("Kadıköy", "Beşiktaş", "Şişli", "Fatih", "Üsküdar", "Maltepe", "Ataşehir", "Bakırköy")
    private val izmirDistricts = listOf("Konak", "Bornova", "Karşıyaka", "Buca", "Çiğli", "Seferihisar")
    private val adanaDistricts = listOf("Seyhan", "Yüreğir", "Çukurova", "Sarıçam")
    private val antalyaDistricts = listOf("Muratpaşa", "Kepez", "Konyaaltı", "Alanya")
    private val bursaDistricts = listOf("Osmangazi", "Nilüfer", "Yıldırım", "İnegöl")

    //Seçili şehir ve ilçeyi hatırlamak için değişkenler
    private var lastSelectedCity: String? = null
    private var lastSelectedDistrict: String? = null

    //Adaptörleri sınıf seviyesinde tutalım ki onResume'da tekrar kullanabilelim
    private lateinit var cityAdapter: ArrayAdapter<String>
    private lateinit var districtAdapter: ArrayAdapter<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //fragment_e.xml layout'unu şişir
        return inflater.inflate(R.layout.fragment_e, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //XML'deki görünümleri kodda bağla (findViewById)
        actvCitySelection = view.findViewById(R.id.actv_city_selection)
        actvDistrictSelection = view.findViewById(R.id.actv_district_selection)
        btnSearchPharmacies = view.findViewById(R.id.btn_search_pharmacies)

        //Şehir adaptörünü başlat ve ata
        cityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, cities)
        actvCitySelection.setAdapter(cityAdapter)

        //İlçe adaptörünü başlangıçta boş olarak başlat ve ata
        districtAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, emptyList<String>())
        actvDistrictSelection.setAdapter(districtAdapter)


        //Şehir seçimi değiştiğinde ilçe listesini güncelle
        actvCitySelection.setOnItemClickListener { parent, _, position, _ ->
            val selectedCityItem = parent.getItemAtPosition(position).toString()
            Log.d("EFragment", "Seçilen Şehir: $selectedCityItem")
            lastSelectedCity = selectedCityItem //Seçimi kaydet
            lastSelectedDistrict = null //Yeni şehirde ilçe sıfırlanmalı
            updateDistrictAdapter(selectedCityItem) //İlçe adaptörünü güncelle
            actvDistrictSelection.setText("", false) //İlçe seçimini sıfırla
            actvDistrictSelection.clearFocus() //Klavyeyi kapatmak için focusu kaldır
        }

        //İlçe seçimi yapıldığında kaydet
        actvDistrictSelection.setOnItemClickListener { parent, _, position, _ ->
            lastSelectedDistrict = parent.getItemAtPosition(position).toString()
            Log.d("EFragment", "Seçilen İlçe: $lastSelectedDistrict")
        }


        //Arama butonuna tıklama dinleyicisi
        btnSearchPharmacies.setOnClickListener {
            val selectedCity = actvCitySelection.text.toString().trim()
            val selectedDistrict = actvDistrictSelection.text.toString().trim() //İlçe boşsa "" döner

            if (selectedCity.isEmpty()) {
                Toast.makeText(context, "Lütfen bir şehir seçin.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener //Fonksiyondan çık
            }

            Log.d("EFragment", "Arama butonu tıklandı. Şehir: $selectedCity, İlçe: $selectedDistrict")

            //PharmacyListFragment'ın newInstance metodunu kullanarak fragment'ı oluştur
            //ve şehir/ilçe bilgilerini aktar
            val pharmacyListFragment = PharmacyListFragment.newInstance(
                city = selectedCity,
                //Eğer ilçe boşsa (kullanıcı seçmediyse) null olarak gönderiyoruz
                district = selectedDistrict.ifEmpty { null }
            )

            //Fragment'ı R.id.fragment_container'a yükle
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, pharmacyListFragment) //MainActivity'deki ana container'ınızın ID'si
                .addToBackStack(null) //Geri tuşu ile EFragment'a dönebilmek için
                .commit()

            Log.d("EFragment", "PharmacyListFragment'a geçiş başlatıldı.")
        }
    }

    override fun onResume() {
        super.onResume()
        //AutoCompleteTextView'nun filtresini temizlemek ve tüm şehirleri göstermek için adaptörü yeniden ata.
        cityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, cities)
        actvCitySelection.setAdapter(cityAdapter)
        //Alanları temizle
        actvCitySelection.setText("", false)
        actvDistrictSelection.setText("", false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        lastSelectedCity = null
        lastSelectedDistrict = null
    }

    //İlçe adaptörünü güncelleyen yardımcı fonksiyon
    private fun updateDistrictAdapter(city: String) {
        val districtsForSelectedCity = when (city) {
            "Ankara" -> ankaraDistricts
            "İstanbul" -> istanbulDistricts
            "İzmir" -> izmirDistricts
            "Adana" -> adanaDistricts
            "Antalya" -> antalyaDistricts
            "Bursa" -> bursaDistricts
            else -> emptyList() //Diğer şehirler için boş
        }
        districtAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, districtsForSelectedCity)
        actvDistrictSelection.setAdapter(districtAdapter)
    }

}