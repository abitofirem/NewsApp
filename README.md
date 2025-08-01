# 📱 NewsApp - Çok Fonksiyonlu Haber Uygulaması

[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com/)
[![MVVM](https://img.shields.io/badge/Architecture-MVVM-blue?style=for-the-badge)](https://developer.android.com/jetpack/guide)

---

## 📋 İçindekiler

- [🎯 Proje Hakkında](#-proje-hakkında)
- [✨ Özellikler](#-özellikler)
- [📱 Ekran Görüntüleri](#-ekran-görüntüleri)
- [🛠️ Teknolojiler](#-teknolojiler)
- [🚀 Kurulum](#-kurulum)
- [🔌 API Yapılandırması](#-api-yapılandırması)
- [🧱 Proje Yapısı](#-proje-yapısı)
- [📖 Kullanım](#-kullanım)

---

## 🎯 Proje Hakkında

**NewsApp**, modern Android geliştirme yaklaşımları kullanılarak geliştirilen çok modüllü bir haber uygulamasıdır. Kullanıcılara haber, finans, futbol, hava durumu ve eczane bilgileri gibi çeşitli hizmetleri bir arada sunar.

### 🏗️ Mimari

- MVVM (Model-View-ViewModel)
- Repository Pattern
- LiveData & ViewModel
- ViewBinding

---

## ✨ Özellikler

### 📰 Haberler
- Sonsuz kaydırma
- Firebase ile favorilere ekleme/çıkarma
- Detay sayfası
- Türkçe/İngilizce dil desteği
- Açık/Koyu tema desteği

### 💸 Finans
- BIST 100, döviz, altın/gümüş, kripto para, emtia fiyatları
- Para birimi dönüştürücü

### ⚽ Futbol
- Ligler, puan durumu, fikstür, gol krallığı
- Favori lig ekleme

### 🌤️ Hava Durumu
- Şehir arama (otomatik tamamlama)
- Günlük ve haftalık tahminler
- Sıcaklık, nem, rüzgar bilgileri

### 💊 Eczane
- Nöbetçi eczane listesi
- Harita ile gösterim
- Şehir/ilçe filtreleme

### 👤 Kullanıcı Yönetimi
- E-posta/şifre & Google ile giriş
- Şifre sıfırlama
- Kullanıcı profili

---

## 📱 Ekran Görüntüleri

| Giriş | Kayıt | Haberler (Ana) |
|---|---|---|
| ![Giriş Ekranı](https://via.placeholder.com/200x400/FF5722/FFFFFF?text=Giriş) | ![Kayıt Ekranı](https://via.placeholder.com/200x400/795548/FFFFFF?text=Kayıt) | ![Haberler Ana Sayfa](https://via.placeholder.com/200x400/2196F3/FFFFFF?text=Haberler) |

| Kaydedilen Haberler | Finans 1 | Finans 2 |
|---|---|---|
| ![Favoriler](https://via.placeholder.com/200x400/1976D2/FFFFFF?text=Favoriler) | ![Finans Sayfası 1](https://via.placeholder.com/200x400/FF9800/FFFFFF?text=Finans+1) | ![Finans Sayfası 2](https://via.placeholder.com/200x400/F57C00/FFFFFF?text=Finans+2) |

| Futbol 1 | Futbol 2 | Hava Durumu |
|---|---|---|
| ![Futbol Sayfası 1](https://via.placeholder.com/200x400/4CAF50/FFFFFF?text=Futbol+1) | ![Futbol Sayfası 2](https://via.placeholder.com/200x400/388E3C/FFFFFF?text=Futbol+2) | ![Hava Durumu](https://via.placeholder.com/200x400/03A9F4/FFFFFF?text=Hava+Durumu) |

| Eczane 1 | Eczane 2 | Ayarlar |
|---|---|---|
| ![Eczane Listesi](https://via.placeholder.com/200x400/9C27B0/FFFFFF?text=Eczane+1) | ![Eczane Haritası](https://via.placeholder.com/200x400/7B1FA2/FFFFFF?text=Eczane+2) | ![Ayarlar](https://via.placeholder.com/200x400/607D8B/FFFFFF?text=Ayarlar) |

---

## 🛠️ Teknolojiler

### Backend
- Firebase Authentication
- Firebase Firestore
- CollectAPI

### Android
- Kotlin
- MVVM Architecture
- Retrofit
- Glide
- RecyclerView
- ViewBinding
- Navigation Component

---

## 🚀 Kurulum

### Gereksinimler

- Android Studio Arctic Fox+
- Android SDK 21+
- JDK 11+

### Adımlar

```bash
# Projeyi klonla ve klasöre gir
git clone https://github.com/kullaniciadi/NewsApp.git
cd NewsApp
```

Firebase Console'dan yeni bir proje oluşturun, Android uygulaması ekleyin ve
`google-services.json` dosyasını indirip `app/` klasörüne koyun.

CollectAPI'den API anahtarınızı alın.

```kotlin
// app/src/main/java/com/example/navigationdrawerapp/api/RetrofitClient.kt dosyasında
private const val API_KEY = "your_collectapi_key_here"
```

Projeyi derleyip çalıştırın:

```bash
./gradlew build
./gradlew installDebug
```

---

### API Endpoint Örnekleri:

```
GET /news/getNews?country=tr&tag=general&paging=0
GET /economy/borsaIstanbul
GET /economy/allCurrency
GET /economy/goldPrice
GET /economy/silverPrice
GET /economy/cripto
GET /economy/emtia
GET /economy/exchange?base=USD&to=EUR&int=100
GET /football/leaguesList
GET /football/league?data.league=super-lig
GET /football/results?data.league=super-lig
GET /football/goalKings?data.league=super-lig
GET /weather/getWeather?city=Ankara
GET /health/dutyPharmacy?il=Ankara&ilce=Çankaya
```
