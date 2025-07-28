package com.example.navigationdrawerapp

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.navigationdrawerapp.ui.fragments.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale
import com.google.firebase.auth.FirebaseAuth
import android.widget.TextView
import com.example.navigationdrawerapp.ui.fragments.FootballFragment

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

    fun refreshMenus() {
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.menu.clear()
        navigationView.inflateMenu(R.menu.nav_menu)
        updateNavigationMenu()

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.menu.clear()
        bottomNavigationView.inflateMenu(R.menu.navbar_menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Kaydedilen temayı yükle ve uygula (setContentView'dan ÖNCE)
        loadAndApplyTheme()

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


        toolbar = findViewById(R.id.toolbar) //Bu satır yeterli
        setSupportActionBar(toolbar)
        toolbar.title = "" // Toolbar başlığını boş yap

        drawerLayout = findViewById(R.id.drawer_layout)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)
        val tvUserName = headerView.findViewById<TextView>(R.id.tvUserName)
        val tvUserEmail = headerView.findViewById<TextView>(R.id.tvUserEmail)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            tvUserName.text = user.displayName ?: "Kullanıcı"
            tvUserEmail.text = user.email ?: ""
        } else {
            tvUserName.text = "Misafir"
            tvUserEmail.text = ""
        }

        navigationView.setNavigationItemSelectedListener(this)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_a -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, FinanceFragment())
                        .commit()
                    setToolbarTitle(getString(R.string.bottom_nav_a)) //Finans
                    true
                }
                R.id.navigation_b -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, FootballFragment())
                        .commit()
                    setToolbarTitle(getString(R.string.bottom_nav_b)) //Futbol
                    true
                }
                R.id.navigation_c -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, NewsFragment())
                        .commit()
                    setToolbarTitle(getString(R.string.bottom_nav_c)) //Haberler
                    true
                }
                R.id.navigation_d -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, WeatherFragment())
                        .commit()
                    setToolbarTitle(getString(R.string.bottom_nav_d)) //Hava
                    true
                }
                R.id.navigation_e -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, PharmacyFragment())
                        .commit()
                    setToolbarTitle(getString(R.string.bottom_nav_e)) //Eczane
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
            //Uygulama ilk açıldığında Haber Listesini (FragmentC) göster
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NewsFragment()) // BURADA DEĞİŞİKLİK
                .commit()
            //Yan menüde ve alt menüde ilgili öğeyi seçili yapın
            navigationView.setCheckedItem(R.id.nav_c) //Eğer yan menünüzde nav_c varsa
            bottomNavigationView.selectedItemId = R.id.navigation_c //Eğer alt menünüzde bu id varsa
            setToolbarTitle(getString(R.string.bottom_nav_c)) //BURADA DEĞİŞİKLİK: Haberler başlığı
        }

        val menu = navigationView.menu

        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null

        //Kaydedilenler (nav_c) sadece giriş yaptıysa görünsün
        menu.findItem(R.id.nav_c)?.isVisible = isLoggedIn

        //Çıkış Yap (nav_e) giriş yaptıysa "Çıkış Yap", yapmadıysa "Giriş Yap" olarak değişsin
        val logoutItem = menu.findItem(R.id.nav_e)
        if (isLoggedIn) {
            logoutItem.title = getString(R.string.nav_logout)
            logoutItem.setIcon(R.drawable.nav_logout)
        } else {
            logoutItem.title = getString(R.string.nav_login)
            logoutItem.setIcon(R.drawable.ic_login)
        }
        refreshMenus() // Menüleri ve başlıkları güncelle
    }

    override fun onResume() {
        super.onResume()
        refreshMenus() // Dil değişikliğinden sonra menü başlıklarını güncelle
    }

    //Bu, fragment'ların MainActivity'deki Toolbar'ın başlığını dinamik olarak değiştirebilmesi için bir yardımcı metottur.
    fun setToolbarTitle(title: String) {
        toolbar.title = title
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_a -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, NewsFragment())
                    .commit()
                setToolbarTitle(getString(R.string.bottom_nav_c))
            }
            R.id.nav_b -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, SettingsFragment())
                    .commit()
                setToolbarTitle(getString(R.string.nav_settings))
            }
            R.id.nav_c -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ShareFragment())
                    .commit()
                setToolbarTitle(getString(R.string.nav_share))
            }
            R.id.nav_d -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AboutFragment())
                    .commit()
                setToolbarTitle(getString(R.string.nav_about))
            }
            R.id.nav_e -> {
                if (FirebaseAuth.getInstance().currentUser != null) {
                    //Çıkış yap
                    FirebaseAuth.getInstance().signOut()
                    updateNavigationMenu()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, NewsFragment())
                        .commit()
                } else {
                    //Giriş ekranına yönlendir
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, LoginFragment())
                        .commit()
                }
                drawerLayout.closeDrawer(GravityCompat.START)
                return true
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
            //Not: Geri dönülen fragment'ın başlığını dinamik olarak ayarlamak daha karmaşık olabilir.

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
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        //Temayı uygulamak için Activity'yi yeniden oluştur (küçük bir gecikmeyle)
        window.decorView.post {
            recreate()
        }
    }

    /**
     * Uygulama başladığında veya yeniden oluşturulduğunda kaydedilen tema tercihini yükler ve uygular.
     */
    private fun loadAndApplyTheme() {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isDarkThemeEnabled = sharedPref.getBoolean("is_dark_theme", false) //Varsayılan: açık tema

        if (isDarkThemeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    fun updateNavigationMenu() {
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val menu = navigationView.menu
        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null

        // Kaydedilenler (nav_c) sadece giriş yaptıysa görünsün
        menu.findItem(R.id.nav_c)?.isVisible = isLoggedIn

        // Çıkış Yap (nav_e) giriş yaptıysa "Çıkış Yap", yapmadıysa "Giriş Yap" olarak değişsin
        val logoutItem = menu.findItem(R.id.nav_e)
        if (isLoggedIn) {
            logoutItem.title = getString(R.string.nav_logout)
            logoutItem.setIcon(R.drawable.nav_logout)
        } else {
            logoutItem.title = getString(R.string.nav_login)
            logoutItem.setIcon(R.drawable.ic_login)
        }

        // Header güncelle
        val headerView = navigationView.getHeaderView(0)
        val tvUserName = headerView.findViewById<TextView>(R.id.tvUserName)
        val tvUserEmail = headerView.findViewById<TextView>(R.id.tvUserEmail)
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            tvUserName.text = user.displayName ?: "Kullanıcı"
            tvUserEmail.text = user.email ?: ""
        } else {
            tvUserName.text = "Misafir"
            tvUserEmail.text = ""
        }
    }
}