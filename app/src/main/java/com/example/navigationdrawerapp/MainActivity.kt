package com.example.navigationdrawerapp

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.navigationdrawerapp.ui.fragments.BFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var bottomNavigationView: BottomNavigationView

    //--- Dil Ayarları İçin Eklemeler BAŞLANGIÇ ---
    //Bu metod, Activity'nin context'i oluşturulmadan önce çağrılır ve dil ayarını yapar.
    override fun attachBaseContext(newBase: Context?) {
        val language = getSavedLanguagePreference(newBase) //Kaydedilen dili al
        val context = newBase?.let { updateResources(it, language) } ?: newBase
        super.attachBaseContext(context)
    }

    //SharedPreferences'tan kaydedilen dil tercihini okuyan metod
    private fun getSavedLanguagePreference(context: Context?): String {
        val sharedPref = context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        //Eğer kaydedilmiş bir dil yoksa, varsayılan olarak cihazın dilini kullan
        //Veya "tr" gibi bir varsayılan dil atayabilirsiniz.
        return sharedPref?.getString("app_language", Locale.getDefault().language) ?: "tr"
    }

    //Kaynakları belirtilen dile göre güncelleyen metod
    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config) //Yeni context oluştur
    }
    //--- Dil Ayarları İçin Eklemeler SONU ---

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //Uygulama her başlatıldığında (uygulamadan çıkıp tekrar girdiğinizde), kullanıcının en son seçtiği dil otomatik olarak aktif olacak.
        //Dil tercihini SharedPreferences'tan yükle ve uygula
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val savedLanguage = sharedPref.getString("app_language", Locale.getDefault().language)
        if (savedLanguage != null) {
            val locale = Locale(savedLanguage)
            Locale.setDefault(locale)
            val config = Configuration(resources.configuration)
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)
        }


        toolbar = findViewById(R.id.toolbar) // Bu satır yeterli
        setSupportActionBar(toolbar)
        toolbar.title = "" // Toolbar başlığını boş yap

        drawerLayout = findViewById(R.id.drawer_layout)
        // val toolbar: Toolbar = findViewById(R.id.toolbar) // Bu satırı silin
        // setSupportActionBar(toolbar) // Bu setSupportActionBar da gereksiz, yukarıdaki yeterli

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_a -> {
                    drawerLayout.openDrawer(GravityCompat.START)
                    toolbar.title = getString(R.string.title_home)
                    true
                }
                R.id.navigation_b -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, BFragment())
                        .commit()
                    toolbar.title = getString(R.string.title_settings)
                    true
                }
                R.id.navigation_c -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, CFragment())
                        .commit()
                    setToolbarTitle(getString(R.string.title_news)) //BURADA DEĞİŞİKLİK
                    true
                }
                R.id.navigation_d -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, DFragment())
                        .commit()
                    toolbar.title = getString(R.string.title_about)
                    true
                }
                R.id.navigation_e -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, EFragment())
                        .commit()
                    toolbar.title = getString(R.string.title_logout)
                    true
                }
                else -> false
            }
        }

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_nav,
            R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            // Uygulama ilk açıldığında Haber Listesini (FragmentC) göster
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CFragment()) // BURADA DEĞİŞİKLİK
                .commit()
            // Yan menüde ve alt menüde ilgili öğeyi seçili yapın
            navigationView.setCheckedItem(R.id.nav_c) //Eğer yan menünüzde nav_c varsa
            bottomNavigationView.selectedItemId = R.id.navigation_c //Eğer alt menünüzde bu id varsa
            setToolbarTitle(getString(R.string.title_news)) //BURADA DEĞİŞİKLİK: Haberler başlığı
        }
    }

    //Bu, fragment'ların MainActivity'deki Toolbar'ın başlığını dinamik olarak değiştirebilmesi için bir yardımcı metottur.
    fun setToolbarTitle(title: String) {
        toolbar.title = title
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_a -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment())
                    .commit()
                toolbar.title = getString(R.string.title_home)
            }
            R.id.nav_b -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, SettingsFragment())
                    .commit()
                toolbar.title = getString(R.string.title_settings)
            }
            R.id.nav_c -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ShareFragment())
                    .commit()
                toolbar.title = getString(R.string.title_share)
            }
            R.id.nav_d -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AboutFragment())
                    .commit()
                toolbar.title = getString(R.string.title_about)
            }
            R.id.nav_e -> {
                Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show()
                toolbar.title = getString(R.string.title_logout)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        // BURASI EKLENDİ/DEĞİŞTİRİLDİ: Fragment yığınını kontrol etme
        else if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            // Not: Geri dönülen fragment'ın başlığını dinamik olarak ayarlamak daha karmaşık olabilir.
            // Şimdilik varsayılan bir başlık ayarlayabilir veya bu kısmı boş bırakabilirsiniz.
            // Eğer fragment'lar kendi başlıklarını setToolbarTitle ile ayarlıyorsa,
            // buraya özel bir şey eklemeniz gerekmeyebilir.
            setToolbarTitle(getString(R.string.app_name)) // Uygulama adını varsayılana ayarlarız
        }
        else {
            super.onBackPressed()
        }

    }
    /**
     * Uygulamanın temasını ayarlar ve Activity'yi yeniden oluşturur.
     * @param isDarkTheme Eğer true ise Koyu Tema, false ise Açık Tema uygulanır.
     */
    fun applyAppTheme(isDarkTheme: Boolean) {
        if (isDarkTheme) {
            // Koyu Tema modunu ayarla
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            // Açık Tema modunu ayarla
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        // Temayı uygulamak için Activity'yi yeniden oluştur
        recreate()
    }

    /**
     * Uygulama başladığında veya yeniden oluşturulduğunda kaydedilen tema tercihini yükler ve uygular.
     */
    private fun loadAndApplyTheme() {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isDarkThemeEnabled = sharedPref.getBoolean("is_dark_theme", false) // Varsayılan: açık tema

        if (isDarkThemeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}