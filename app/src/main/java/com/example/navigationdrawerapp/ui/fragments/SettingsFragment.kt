package com.example.navigationdrawerapp.ui.fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.example.navigationdrawerapp.MainActivity
import com.example.navigationdrawerapp.R
import com.example.navigationdrawerapp.databinding.FragmentSettingsBinding
import com.example.navigationdrawerapp.viewmodel.NewsViewModel
import java.util.Locale

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!


    // NewsViewModel'i ekle
    private val newsViewModel: NewsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // SharedPreferences'tan mevcut tema tercihini yükle
        val sharedPref = activity?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isDarkThemeEnabled = sharedPref?.getBoolean("is_dark_theme", false) ?: false

        // Switch'in başlangıç durumunu ayarla
        binding.themeSwitch.isChecked = isDarkThemeEnabled

        // TextView'ın metnini başlangıç durumuna göre güncelle
        updateThemeText(isDarkThemeEnabled)

        // Switch'in durum değişimini dinle
        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            // TextView metnini güncelle
            updateThemeText(isChecked)

            // Yeni tema tercihini SharedPreferences'a kaydet
            sharedPref?.edit()?.putBoolean("is_dark_theme", isChecked)?.apply()

            // Activity'e tema değişikliğini bildir
            (activity as? MainActivity)?.applyAppTheme(isChecked)

        }

        //--- Dil Ayarları ---
        // SharedPreferences'tan mevcut dil tercihini yükle
        val currentLanguage = sharedPref?.getString("app_language", Locale.getDefault().language) ?: Locale.getDefault().language

        // Radyo butonlarının başlangıç durumunu ayarla
        if (currentLanguage == "tr") {
            binding.radioTurkish.isChecked = true
        } else {
            binding.radioEnglish.isChecked = true
        }

        // RadioGroup'un durum değişimini dinle
        binding.languageRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioTurkish -> {
                    // Türkçe seçildiğinde
                    saveAndApplyLanguage("tr")
                }
                R.id.radioEnglish -> {
                    // İngilizce seçildiğinde
                    saveAndApplyLanguage("en")
                }
            }
        }
    }

    /**
     * Switch'in durumuna göre tema metnini günceller.
     * @param isDarkTheme Eğer true ise "Açık Tema", false ise "Koyu Tema" yazar.
     */
    private fun updateThemeText(isDarkTheme: Boolean) {
        if (isDarkTheme) {
            binding.themeStatusTextView.text = getString(R.string.light_theme)
        } else {
            binding.themeStatusTextView.text = getString(R.string.dark_theme)
        }
    }


    /**
     * Seçilen dili SharedPreferences'a kaydeder ve uygulamanın dilini değiştirir.
     * @param langCode Kaydedilecek dil kodu (örn. "tr", "en").
     */

    //kullanıcının dil tercihini kaydeder, uygulamanın iç dil ayarlarını değiştirir ve ardından tüm kullanıcı arayüzünün seçilen yeni dilde görünmesini sağlamak için uygulamayı yeniden başlatır.
    private fun saveAndApplyLanguage(langCode: String) {
        val sharedPref = activity?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPref?.edit()?.putString("app_language", langCode)?.apply()

        //NewsViewModel'e dil değişikliğini bildir
        newsViewModel.updateLanguage(langCode)

        //Dil değişikliğini uygulamak için gerekli kod
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration) // Mevcut konfigürasyonu kopyala
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        //Activity'i yeniden oluşturarak dil değişikliğini tüm UI'a yansıt
        //Bu, Fragment içindeyken Activity'i yeniden başlatmanın tipik yoludur.
        activity?.recreate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
