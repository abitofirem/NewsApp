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
| <img src="https://github.com/user-attachments/assets/eb37c79c-ce33-4f49-89f6-30a1dcbf8403" width="200" alt="Giriş Ekranı"> | <img src="https://github.com/user-attachments/assets/88e55a1a-af99-49f2-a662-46566cd936ac" width="200" alt="Kayıt Ekranı"> | <img src="https://github.com/user-attachments/assets/1de3f288-a985-4030-a134-3aeb1cb45969" width="200" alt="Haberler Ana Sayfa"> |

| Kaydedilen Haberler | Finans 1 | Finans 2 |
|---|---|---|
| <img src="https://github.com/user-attachments/assets/37f08f15-db28-4de6-ad75-8ff6bbc61b50" width="200" alt="Favoriler"> | <img src="https://github.com/user-attachments/assets/2f40e126-e523-4657-8307-38d2b23e5989" width="200" alt="Finans Sayfası 1"> | <img src="https://github.com/user-attachments/assets/15c181ef-b66d-4e20-93ac-1d8492616ba4" width="200" alt="Finans Sayfası 2"> |

| Futbol 1 | Futbol 2 | Hava Durumu |
|---|---|---|
| <img src="https://github.com/user-attachments/assets/7aa789be-f5d3-4d4a-aa6e-ae193a81fcfc" width="200" alt="Futbol Sayfası 1"> | <img src="https://github.com/user-attachments/assets/4ac46b01-8aaf-40a4-afd3-97134ce0411c" width="200" alt="Futbol Sayfası 2"> | <img src="https://github.com/user-attachments/assets/d84d93d0-c973-4a45-ab1a-86e354581282" width="200" alt="Hava Durumu"> |

| Eczane 1 | Eczane 2 | Ayarlar |
|---|---|---|
| <img src="https://github.com/user-attachments/assets/bcdda2c3-7ebb-4ae6-8322-a0317116abae" width="200" alt="Eczane Listesi"> | <img src="https://github.com/user-attachments/assets/92a16165-355b-4d8e-b517-05a31dd99943" width="200" alt="Eczane Haritası"> | <img src="https://github.com/user-attachments/assets/a8efe76c-0443-4dc1-8d14-131a3c67b7a4" width="200" alt="Ayarlar"> |

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
