package com.example.navigationdrawerapp.viewmodel
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // Coroutine Scope için
import com.example.navigationdrawerapp.Haber
import com.example.navigationdrawerapp.api.RetrofitClient // Retrofit istemcimizi import edin
import com.example.navigationdrawerapp.model.NewsResponse // NewsResponse modelimizi import edin
import kotlinx.coroutines.launch // Coroutine başlatmak için
import java.util.Locale

//ViewModel yerine AndroidViewModel kullanıyoruz, çünkü Context'e ihtiyacımız var.
class NewsViewModel(application: Application) : AndroidViewModel(application) { // <-- Burayı değiştir

    //Uygulama context'ine erişim için
    private val app = application

    //Haber listesi için LiveData (UI'ın gözlemleyeceği ve otomatik güncelleneceği veri)
    private val _newsList = MutableLiveData<List<Haber>>()
    val newsList: LiveData<List<Haber>> = _newsList

    //Yükleme durumu için LiveData (UI'da ProgressBar göstermek/gizlemek için)
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    //Hata mesajı için LiveData (UI'da Toast veya Snackbar göstermek için)
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: MutableLiveData<String?> = _errorMessage

    //Sayfalama için değişkenler
    private var currentPage = 0 //CollectAPI'nin "paging" parametresi varsayılan 0'dan başlıyor
    private val pageSize = 10 //Her seferinde kaç haber çekileceği (API'de belirtilmemiş, biz varsayılan 10 alıyoruz)
    var hasMoreNews = true //Daha fazla haber olup olmadığını kontrol eder (false olduğunda daha fazla yüklemeyi durdururuz)

    //Dil ve ülke ayarları
    private lateinit var currentLanguage: String // <-- Başlangıçta değer atamayacağız, init bloğunda okuyacağız
    private val defaultTag = "general" //Genel haberler için 'general'


    init {
        // ViewModel ilk oluşturulduğunda kaydedilmiş dil tercihini yükle
        currentLanguage = getSavedLanguagePreference() // <-- Yeni eklenen metod ile dili oku
        // İlk haber çekme işlemi
        fetchNews()
    }

    //SharedPreferences'tan kaydedilen dil tercihini okuyan metod
    private fun getSavedLanguagePreference(): String {
        val sharedPref = app.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        //Eğer kaydedilmiş bir dil yoksa, varsayılan olarak cihazın dilini kullan
        //Veya "tr" gibi bir varsayılan dil atayabilirsiniz.
        return sharedPref.getString("app_language", Locale.getDefault().language) ?: "tr" // <-- Değiştirildi
    }

    //Dil değişikliğinde çağrılacak metod
    fun updateLanguage(language: String) {
        if (currentLanguage != language) {
            currentLanguage = language
            //Dil değiştiğinde listeyi sıfırla ve yeniden başla
            resetNewsList()
            fetchNews()
        }
    }

    //Haber listesini sıfırla ve sayfalama değişkenlerini resetle
    private fun resetNewsList() {
        _newsList.value = emptyList()
        currentPage = 0
        hasMoreNews = true
    }


    //Haberleri API'den çeken ana metod
    fun fetchNews() {
        //Eğer zaten yükleniyorsa veya daha fazla haber yoksa tekrar çekme
        if (_isLoading.value == true || !hasMoreNews) {
            return
        }

        _isLoading.value = true //Yükleme başladı
        _errorMessage.value = null //Önceki hata mesajını temizle

        viewModelScope.launch { //Coroutine'i ViewModel'in yaşam döngüsüne bağla
            try {
                // Dil koduna göre ülke kodunu belirle
                val country = when (currentLanguage) {
                    "en" -> "en" // İngilizce için ABD
                    "tr" -> "tr" // Türkçe için Türkiye
                    else -> "tr" // Varsayılan olarak Türkiye
                }
                //RetrofitClient üzerinden API çağrısı yap
                //API key'i NewsApiService'de @Headers ile sabitlendiği için burada göndermiyoruz.
                val response: NewsResponse = RetrofitClient.instance.getNews(
                    country = country,
                    tag = defaultTag,
                    paging = currentPage
                )

                if (response.success) { //API yanıtı başarılı mı kontrol et
                    val fetchedNews = response.result //Haber listesini al

                    if (fetchedNews.isNotEmpty()) {
                        //Mevcut listeye yeni haberleri ekle
                        val currentNews = _newsList.value.orEmpty().toMutableList()
                        currentNews.addAll(fetchedNews)
                        _newsList.value = currentNews //LiveData'yı güncelle
                        currentPage++ //Sonraki sayfa için sayfa numarasını arttır
                    } else {
                        hasMoreNews = false //API'den boş liste geldiyse veya başarıyla sonuçlandıysa daha fazla haber yok demektir
                    }
                } else {
                    //API success: false döndürdüyse
                    _errorMessage.value = "API'den başarısız yanıt: Haberler yüklenemedi."
                    hasMoreNews = false //Hata durumunda daha fazla yüklemeyi durdur
                }

            } catch (e: Exception) {
                //Ağ bağlantısı, JSON parse hatası vb. yakala
                _errorMessage.value = "Haberler yüklenirken bir hata oluştu: ${e.message}"
                hasMoreNews = false //Hata durumunda daha fazla yüklemeyi durdur
            } finally {
                _isLoading.value = false //Yükleme bitti
            }
        }
    }

    //RecyclerView'ın sonuna gelindiğinde daha fazla haber yüklemek için çağrılır
    fun loadMoreNews() {
        fetchNews()
    }
}