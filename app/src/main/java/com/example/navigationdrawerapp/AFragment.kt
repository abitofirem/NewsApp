package com.example.navigationdrawerapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.navigationdrawerapp.adapter.CriptoAdapter
import com.example.navigationdrawerapp.adapter.CurrencyAdapter
import com.example.navigationdrawerapp.adapter.PreciousMetalAdapter
import com.example.navigationdrawerapp.model.BistResult
import com.example.navigationdrawerapp.model.PreciousMetal
import com.example.navigationdrawerapp.viewmodel.FinanceViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class AFragment : Fragment() {


    private lateinit var financeViewModel: FinanceViewModel

    //XML'deki UI elemanlarını buraya tanımladık.
    //BIST
    private lateinit var tvBistCurrent: TextView
    private lateinit var tvBistChangeRate: TextView
    private lateinit var tvBistMin: TextView
    private lateinit var tvBistMax: TextView
    private lateinit var tvBistTime: TextView
    private lateinit var mainProgressBar: ProgressBar
    private lateinit var mainErrorMessage: TextView

    //Currency
    private lateinit var rvAllCurrency: androidx.recyclerview.widget.RecyclerView
    private lateinit var currencyAdapter: CurrencyAdapter
    private lateinit var tvShowAllCurrency: TextView

    //Değerli Madenler
    private lateinit var rvGoldSilver: androidx.recyclerview.widget.RecyclerView
    private lateinit var preciousMetalAdapter: PreciousMetalAdapter

    //Kripto değişkenleri
    private lateinit var rvCripto: androidx.recyclerview.widget.RecyclerView
    private lateinit var criptoAdapter: CriptoAdapter
    private lateinit var tvShowAllCripto: TextView //YENİ EKLENDİ



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //ViewModel'i başlat
        financeViewModel = ViewModelProvider(this).get(FinanceViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_a, container, false)
        initViews(view)
        setupObservers()

        //RecyclerView'i başlat
        currencyAdapter = CurrencyAdapter(emptyList()) //Boş bir liste ile başlatıyoruz
        rvAllCurrency.adapter = currencyAdapter
        rvAllCurrency.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

        //Hem BIST hem de döviz verilerini çek
        financeViewModel.fetchBistData()
        financeViewModel.fetchAllCurrencyData()

        //Altın ve gümüş verilerini çek
        financeViewModel.fetchGoldPrice()
        financeViewModel.fetchSilverPrice()

        financeViewModel.fetchCriptoPrice()


        return view
    }

    //UI elemanlarını bağlayan yardımcı fonksiyon
    private fun initViews(view: View) {
        tvBistCurrent = view.findViewById(R.id.tv_bist_current)
        tvBistChangeRate = view.findViewById(R.id.tv_bist_change_rate)
        tvBistMin = view.findViewById(R.id.tv_bist_min)
        tvBistMax = view.findViewById(R.id.tv_bist_max)
        tvBistTime = view.findViewById(R.id.tv_bist_time)
        mainProgressBar = view.findViewById(R.id.main_progress_bar)
        mainErrorMessage = view.findViewById(R.id.main_error_message)

        rvAllCurrency = view.findViewById(R.id.rv_all_currency)
        tvShowAllCurrency = view.findViewById(R.id.tv_show_all_currency)


        rvGoldSilver = view.findViewById(R.id.rv_gold_silver)
        preciousMetalAdapter = PreciousMetalAdapter(emptyList())
        rvGoldSilver.adapter = preciousMetalAdapter
        rvGoldSilver.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)


        //Kripto Paralar RecyclerView
        rvCripto = view.findViewById(R.id.rv_cripto)
        criptoAdapter = CriptoAdapter()
        rvCripto.adapter = criptoAdapter
        rvCripto.layoutManager = LinearLayoutManager(context)
        tvShowAllCripto = view.findViewById(R.id.tv_show_all_cripto) //YENİ EKLENDİ


    }

    //LiveData'ları gözlemleyen fonksiyon
    private fun setupObservers() {
        financeViewModel.bistData.observe(viewLifecycleOwner) { bistResponse ->
            bistResponse?.let {
                updateBistUi(it.result)
                mainErrorMessage.visibility = View.GONE
            } ?: run {
                mainErrorMessage.visibility = View.VISIBLE
            }
        }

        //DÖVİZ KURLARI GÖZLEMCİSİ
        financeViewModel.currencyData.observe(viewLifecycleOwner) { currencyResponse ->
            currencyResponse?.let {
                currencyAdapter.updateData(it.result)
                //YENİ GÜNCELLEME: 3'ten fazla eleman varsa butonu göster
                if (it.result.size > 3) {
                    tvShowAllCurrency.visibility = View.VISIBLE
                }
            }
        }

        financeViewModel.goldData.observe(viewLifecycleOwner) { goldResponse ->
            goldResponse?.let {
                updatePreciousMetalsUi()
            }
        }

        financeViewModel.silverData.observe(viewLifecycleOwner) { silverResponse ->
            silverResponse?.let {
                updatePreciousMetalsUi()
            }
        }


        //Kripto Paralar Gözlemcisi
        financeViewModel.criptoData.observe(viewLifecycleOwner) { criptoResponse ->
            criptoResponse?.result?.let { criptoList ->
                //Yalnızca ilk 3'ü göster
                criptoAdapter.updateData(criptoList.take(3)) //BU SATIRI DÜZENLEME GEREKİYOR
                //Eğer 3'ten fazla eleman varsa "Tümünü Göster"i görünür yap
                if (criptoList.size > 3) {
                    tvShowAllCripto.visibility = View.VISIBLE
                } else {
                    tvShowAllCripto.visibility = View.GONE
                }
            }
        }


        //"Tümünü Göster" butonuna tıklama dinleyicisi
        tvShowAllCurrency.setOnClickListener {
            val isExpanded = tvShowAllCurrency.text.toString().startsWith("Tümünü Göster")
            currencyAdapter.setExpanded(isExpanded)
            tvShowAllCurrency.text = if (isExpanded) "<< Daha az göster" else "Tümünü Göster >>"
        }

        // YENİ EKLENDİ: Kripto Paralar için "Tümünü Göster" butonunun tıklama dinleyicisi
        tvShowAllCripto.setOnClickListener {
            val isExpanded = tvShowAllCripto.text.toString().startsWith("Tümünü Göster")
            val criptoList = financeViewModel.criptoData.value?.result ?: emptyList()
            if (isExpanded) {
                criptoAdapter.updateData(criptoList) //Tüm listeyi göster
                tvShowAllCripto.text = "<< Daha az göster"
            } else {
                criptoAdapter.updateData(criptoList.take(3)) //Sadece ilk 3'ü göster
                tvShowAllCripto.text = "Tümünü Göster >>"
            }
        }


        financeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            mainProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) mainErrorMessage.visibility = View.GONE
        }

        financeViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                mainErrorMessage.text = it
                mainErrorMessage.visibility = View.VISIBLE
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            } ?: run {
                mainErrorMessage.visibility = View.GONE
            }
        }
    }

    //BIST UI'ını güncelleyen yardımcı fonksiyon
    private fun updateBistUi(bistResults: List<BistResult>) {
        if (bistResults.isNotEmpty()) {
            val bistResult = bistResults[0] // Listenin ilk elemanını alıyoruz

            //DecimalFormat kullanarak sayıları doğru formatta gösterdik
            val decimalFormat = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale("tr", "TR")))

            tvBistCurrent.text = decimalFormat.format(bistResult.current)
            tvBistMin.text = "Min: ${decimalFormat.format(bistResult.min)}"
            tvBistMax.text = "Max: ${decimalFormat.format(bistResult.max)}"
            tvBistTime.text = bistResult.time

            //Değişim oranını ve rengini ayarladık
            val changeRate = bistResult.changerate
            val changeRateFormatted = String.format(Locale.US, "%.2f%%", changeRate)
            tvBistChangeRate.text = changeRateFormatted

            if (changeRate > 0) {
                tvBistChangeRate.setTextColor(
                    resources.getColor(
                        R.color.positive_change_color,
                        null
                    )
                )
            } else if (changeRate < 0) {
                tvBistChangeRate.setTextColor(
                    resources.getColor(
                        R.color.negative_change_color,
                        null
                    )
                )
            } else {
                tvBistChangeRate.setTextColor(resources.getColor(R.color.secondary_text, null))
            }
        }
    }

    private fun updatePreciousMetalsUi() {
        val preciousMetalList = mutableListOf<PreciousMetal>()

        // Altın verilerini ekle
        financeViewModel.goldData.value?.result?.forEach { goldResult ->
            if (goldResult.name == "Gram Altın" || goldResult.name == "ONS Altın") { // 'ONS Altın' olarak güncelledik
                preciousMetalList.add(
                    PreciousMetal(
                        name = goldResult.name,
                        buying = goldResult.buying.toString(),
                        selling = goldResult.selling.toString(),
                        rate = goldResult.rate // Rate bilgisini direkt atıyoruz
                    )
                )
            }
        }

        // Gümüş verisini ekle
        financeViewModel.silverData.value?.result?.let { silverResult ->
            // Gümüş rate bilgisini String'den Double'a çeviriyoruz
            val silverRate = try {
                silverResult.rate.replace("%", "").replace(",", ".").trim().toDoubleOrNull()
            } catch (e: Exception) {
                null
            }

            preciousMetalList.add(
                PreciousMetal(
                    name = "Gümüş Fiyatı",
                    buying = silverResult.buying.toString(),
                    selling = silverResult.selling.toString(),
                    rate = silverRate // Dönüştürülmüş rate'i atıyoruz
                )
            )
        }

        // Listeyi adapter'a gönder
        if (preciousMetalList.isNotEmpty()) {
            preciousMetalAdapter.updateData(preciousMetalList)
        }
    }

}