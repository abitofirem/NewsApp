package com.example.navigationdrawerapp.ui.fragments

import android.app.AlertDialog
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
import com.example.navigationdrawerapp.R
import com.example.navigationdrawerapp.adapter.CriptoAdapter
import com.example.navigationdrawerapp.adapter.CurrencyAdapter
import com.example.navigationdrawerapp.adapter.EmtiaAdapter
import com.example.navigationdrawerapp.adapter.PreciousMetalAdapter
import com.example.navigationdrawerapp.model.BistResult
import com.example.navigationdrawerapp.model.PreciousMetal
import com.example.navigationdrawerapp.viewmodel.FinanceViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import com.example.navigationdrawerapp.adapter.CurrencySelectionAdapter //Yeni import


class FinanceFragment : Fragment() {


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


    //Emtia
    private lateinit var rvEmtia: androidx.recyclerview.widget.RecyclerView
    private lateinit var emtiaAdapter: EmtiaAdapter
    private lateinit var tvShowAllEmtia: TextView //YENİ EKLENDİ


    //Döviz Dönüştürücü
    private lateinit var etAmountFrom: android.widget.EditText
    private lateinit var btnFromCurrency: com.google.android.material.button.MaterialButton
    private lateinit var btnToCurrency: com.google.android.material.button.MaterialButton
    private lateinit var btnSwapCurrencies: android.widget.ImageButton
    private lateinit var btnConvert: android.widget.Button
    private lateinit var tvAmountTo: android.widget.TextView
    private lateinit var tvRateFrom: android.widget.TextView
    private lateinit var tvRateTo: android.widget.TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //ViewModel'i başlat
        financeViewModel = ViewModelProvider(this).get(FinanceViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_finance, container, false)
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

        //Emtia RecyclerView'ını başlat
        emtiaAdapter = EmtiaAdapter(emptyList()) // Boş bir liste ile başlatıyoruz
        rvEmtia.adapter = emtiaAdapter
        rvEmtia.layoutManager = LinearLayoutManager(context)

        //Emtia verilerini çek
        financeViewModel.fetchEmtiaData()


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

        //Emtia RecyclerView'ını bağla
        rvEmtia = view.findViewById(R.id.rv_emtia)
        tvShowAllEmtia = view.findViewById(R.id.tv_show_all_emtia)


        //Döviz Dönüştürücü bileşenlerini bağla
        etAmountFrom = view.findViewById(R.id.et_amount_from)
        btnFromCurrency = view.findViewById(R.id.btn_from_currency)
        btnToCurrency = view.findViewById(R.id.btn_to_currency)
        btnSwapCurrencies = view.findViewById(R.id.btn_swap_currencies)
        btnConvert = view.findViewById(R.id.btn_convert)
        tvAmountTo = view.findViewById(R.id.tv_amount_to)
        tvRateFrom = view.findViewById(R.id.tv_rate_from)
        tvRateTo = view.findViewById(R.id.tv_rate_to)

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


        //Emtia verilerini gözlemcisi
        financeViewModel.emtiaData.observe(viewLifecycleOwner) { emtiaResponse ->
            emtiaResponse?.result?.let { emtiaList ->
                //Sadece ilk 3'ü göster
                emtiaAdapter.updateData(emtiaList.take(3))

                //Eğer 3'ten fazla eleman varsa "Tümünü Göster"i görünür yap
                if (emtiaList.size > 3) {
                    tvShowAllEmtia.visibility = View.VISIBLE
                } else {
                    tvShowAllEmtia.visibility = View.GONE
                }
            }
        }

        //Dönüştürme sonucu gözlemcisi
        financeViewModel.exchangeData.observe(viewLifecycleOwner) { exchangeResponse ->
            exchangeResponse?.let {
                // Dönüşüm başarılıysa sonuçları göster
                val data = it.result.data.firstOrNull()
                if (data != null) {
                    tvAmountTo.text = String.format("%.2f", data.calculated)
                    tvRateFrom.text = "1 ${it.result.base} = ${data.rate} ${data.code}"
                    tvRateTo.text = "1 ${data.code} = %.4f ${it.result.base}".format(1.0 / data.rate.toDouble())
                }
            }
        }






        //"Tümünü Göster" butonuna tıklama dinleyicisi
        tvShowAllCurrency.setOnClickListener {
            val isCurrentlyExpanded = currencyAdapter.isExpanded
            currencyAdapter.setExpanded(!isCurrentlyExpanded)
            tvShowAllCurrency.text = if (!isCurrentlyExpanded) {
                getString(R.string.finance_show_less)
            } else {
                getString(R.string.finance_show_all)
            }
        }

        //Kripto Paralar için "Tümünü Göster" butonunun tıklama dinleyicisi
        tvShowAllCripto.setOnClickListener {
            val currentText = tvShowAllCripto.text.toString()
            val showAllText = getString(R.string.finance_show_all)
            val isExpanded = currentText == showAllText

            val criptoList = financeViewModel.criptoData.value?.result ?: emptyList()
            if (isExpanded) {
                criptoAdapter.updateData(criptoList) //Tüm listeyi göster
                tvShowAllCripto.text = getString(R.string.finance_show_less)
            } else {
                criptoAdapter.updateData(criptoList.take(3)) //Sadece ilk 3'ü göster
                tvShowAllCripto.text = showAllText
            }
        }

        //Emtia için "Tümünü Göster" butonunun tıklama dinleyicisi
        tvShowAllEmtia.setOnClickListener {
            val currentText = tvShowAllEmtia.text.toString()
            val showAllText = getString(R.string.finance_show_all)
            val isExpanded = currentText == showAllText
            
            val emtiaList = financeViewModel.emtiaData.value?.result ?: emptyList()
            if (isExpanded) {
                emtiaAdapter.updateData(emtiaList) // Tüm listeyi göster
                tvShowAllEmtia.text = getString(R.string.finance_show_less)
            } else {
                emtiaAdapter.updateData(emtiaList.take(3)) // Sadece ilk 3'ü göster
                tvShowAllEmtia.text = showAllText
            }
        }



        //"Dönüştür" butonu tıklama dinleyicisi
        btnConvert.setOnClickListener {
            performConversion() // Yeni oluşturduğumuz yardımcı fonksiyonu çağır
        }


        btnFromCurrency.setOnClickListener {
            showCurrencySelectionDialog(true)
        }

        btnToCurrency.setOnClickListener {
            showCurrencySelectionDialog(false)
        }

        //"Para birimlerini değiştir" butonu tıklama dinleyicisi
        btnSwapCurrencies.setOnClickListener {
            val tempFrom = btnFromCurrency.text
            val tempTo = btnToCurrency.text
            btnFromCurrency.text = tempTo
            btnToCurrency.text = tempFrom

            //Para birimleri değiştirildiğinde dönüşüm işlemini otomatik olarak tekrarla
            performConversion()
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
            val bistResult = bistResults[0] //Listenin ilk elemanını alıyoruz

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

        //Altın verilerini ekle
        financeViewModel.goldData.value?.result?.forEach { goldResult ->
            if (goldResult.name == "Gram Altın" || goldResult.name == "ONS Altın") { //'ONS Altın' olarak güncelledik
                preciousMetalList.add(
                    PreciousMetal(
                        name = goldResult.name,
                        buying = goldResult.buying.toString(),
                        selling = goldResult.selling.toString(),
                        rate = goldResult.rate //Rate bilgisini direkt atıyoruz
                    )
                )
            }
        }

        //Gümüş verisini ekle
        financeViewModel.silverData.value?.result?.let { silverResult ->
            //Gümüş rate bilgisini String'den Double'a çeviriyoruz
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

        //Listeyi adapter'a gönder
        if (preciousMetalList.isNotEmpty()) {
            preciousMetalAdapter.updateData(preciousMetalList)
        }
    }


    /**
     * Para birimi seçim dialogunu gösterir.
     * @param isFromButton true ise "btn_from_currency", false ise "btn_to_currency" butonu için.
     */
    private fun showCurrencySelectionDialog(isFromButton: Boolean) {
        // `currencyData` zaten mevcut bir LiveData
        val currencyList = financeViewModel.currencyData.value?.result ?: emptyList()

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_currency_selection, null)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.rv_currency_list)
        val dialogAdapter = CurrencySelectionAdapter(currencyList)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = dialogAdapter

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Para Birimi Seç")
            .setView(dialogView)
            .create()

        dialogAdapter.onItemClick = { selectedCurrency ->
            if (isFromButton) {
                btnFromCurrency.text = selectedCurrency.code
            } else {
                btnToCurrency.text = selectedCurrency.code // Burada `code` yerine `name` kullanıyoruz
            }
            dialog.dismiss()

            // Seçim yapıldığında otomatik dönüşüm yap
            performConversion()
        }

        dialog.show()
    }

    /**
     * Dönüşüm işlemini gerçekleştirir ve UI'ı günceller.
     */
    private fun performConversion() {
        val fromCurrency = btnFromCurrency.text.toString()
        val toCurrency = btnToCurrency.text.toString()
        val amount = etAmountFrom.text.toString()

        if (amount.isNotEmpty() && fromCurrency.isNotEmpty() && toCurrency.isNotEmpty()) {
            financeViewModel.convertCurrency(fromCurrency, toCurrency, amount)
        }
    }

}
