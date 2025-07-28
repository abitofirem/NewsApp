package com.example.navigationdrawerapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.navigationdrawerapp.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn //Google ile giriş yapmak için ana sınıf
import com.google.android.gms.auth.api.signin.GoogleSignInClient //GoogleSignIn işlemlerini yöneten istemci
import com.google.android.gms.auth.api.signin.GoogleSignInOptions //GoogleSignIn yapılandırma seçenekleri
import com.google.android.gms.common.api.ApiException //GoogleSignIn hatalarını yakalamak için
import com.google.firebase.auth.FirebaseAuth //Firebase kimlik doğrulama API'si
import com.google.firebase.auth.GoogleAuthProvider //Google kimlik bilgilerini Firebase için dönüştürmek için
import android.app.AlertDialog
import android.widget.EditText
import android.widget.Button
import com.example.navigationdrawerapp.MainActivity
import com.example.navigationdrawerapp.R

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    //Firebase kimlik doğrulama nesnesi
    private lateinit var firebaseAuth: FirebaseAuth
    //Google Giriş istemcisi nesnesi
    private lateinit var googleSignInClient: GoogleSignInClient

    //Sabitler için Companion Object. Google Giriş işlemi için request kodu ve Logcat TAG'i içerir.
    companion object {
        private const val RC_SIGN_IN = 9002 //Google Giriş işlemi için benzersiz bir istek kodu
        private const val TAG = "LoginFragment" //Logcat çıktılarında bu Fragment'ı tanımlamak için kullanılan etiket
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Firebase Auth örneğini alıyoruz. Bu, tüm kimlik doğrulama işlemlerini yönetecek.
        firebaseAuth = FirebaseAuth.getInstance()

        //Google Giriş Seçeneklerini yapılandırıyoruz.
        //DEFAULT_SIGN_IN: Varsayılan giriş davranışını kullanır.
        //requestIdToken: Firebase ile doğrulamak için Google Kimlik Token'ı ister.
        //getString(R.string.default_web_client_id) değeri, google-services.json dosyasından gelir.
        //requestEmail: Kullanıcının e-posta adresini ister.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        //GoogleSignInClient nesnesini oluşturuyoruz. Bu, Google giriş akışını başlatmak için kullanılır.
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    //Görünüm hiyerarşisi oluşturulduktan sonra çağrılan metod. UI elemanlarına tıklama dinleyicileri eklemek gibi işlemler burada yapılır.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //"Giriş Yap" butonuna tıklama dinleyicisi ekliyoruz. Tıklandığında loginUser() fonksiyonu çalışır.
        binding.loginButton.setOnClickListener {
            loginUser()
        }

        //"Google ile Giriş Yap" butonuna tıklama dinleyicisi ekliyoruz. Tıklandığında signInWithGoogle() fonksiyonu çalışır.
        binding.googleSigninButton.setOnClickListener {
            signInWithGoogle()
        }

        //"Hesabınız yok mu? Şimdi Kayıt Ol!" metnine tıklama dinleyicisi ekliyoruz.
        //Tıklandığında RegisterFragment'a geçiş yapar.
        binding.registerNowText.setOnClickListener {
            // Fragment geçişini manuel olarak FragmentManager kullanarak yapıyoruz.
            // replace: Mevcut fragment'ı (LoginFragment) belirtilen fragment (RegisterFragment) ile değiştirir.
            // addToBackStack(null): Geri tuşuna basıldığında bu fragment'a geri dönülmesini sağlar.
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }

        //"Şifrenizi mi unuttunuz?" metnine tıklama dinleyicisi (isteğe bağlı).
        //Şu an sadece bir Toast mesajı gösteriyor, ancak buraya şifre sıfırlama Fragment'ı eklenebilir.
        binding.forgotPasswordText.setOnClickListener {
            showForgotPasswordDialog()
        }
    }

    //E-posta ve şifre ile kullanıcı girişi işlemini yapar.
    private fun loginUser() {
        //E-posta ve şifre alanlarındaki metinleri alıp boşlukları temizleriz.
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        // Giriş alanları için doğrulama (validasyon) kuralları
        if (email.isEmpty()) {
            binding.emailInputLayout.error = "E-posta boş bırakılamaz" //Hata mesajı göster
            binding.emailEditText.requestFocus() //Klavyeyi ilgili alana odakla
            return //Fonksiyondan çık
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { //E-posta formatını kontrol et
            binding.emailInputLayout.error = "Geçerli bir e-posta adresi girin"
            binding.emailEditText.requestFocus()
            return
        }
        if (password.isEmpty()) {
            binding.passwordInputLayout.error = "Şifre boş bırakılamaz"
            binding.passwordEditText.requestFocus()
            return
        }
        // Şifre uzunluğu kontrolü (isteğe bağlı olarak min. 6 karakter şartı kaldırılabilir)
        // if (password.length < 6) {
        //     binding.passwordInputLayout.error = "Şifre en az 6 karakter olmalı"
        //     binding.passwordEditText.requestFocus()
        //     return
        // }


        //Firebase ile kullanıcının kimliğini doğrula (giriş yap)
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                // İşlem tamamlandığında (başarılı veya başarısız)
                if (task.isSuccessful) {
                    // Giriş başarılı
                    Log.d(TAG, "signInWithEmail:success") //Logcat'e başarı mesajı yaz
                    val user = firebaseAuth.currentUser //Giriş yapan kullanıcının bilgilerini al
                    Toast.makeText(requireContext(), "Giriş başarılı! Hoş geldiniz, ${user?.email}", Toast.LENGTH_SHORT).show()
                    (requireActivity() as? MainActivity)?.updateNavigationMenu()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, NewsFragment())
                        .commit()



                } else {
                    //Giriş başarısız
                    Log.w(TAG, "signInWithEmail:failure", task.exception) // Logcat'e hata mesajı yaz
                    Toast.makeText(requireContext(), "Giriş başarısız: ${task.exception?.message}", //Hata mesajını kullanıcıya göster
                        Toast.LENGTH_LONG).show()
                }
            }
    }

    // Google ile giriş akışını başlatan fonksiyon.
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent //GoogleSignInClient'tan giriş Intent'ini al
        startActivityForResult(signInIntent, RC_SIGN_IN) //Bu Intent'i başlat ve sonucunu RC_SIGN_IN koduyla bekle
    }

    //startActivityForResult ile başlatılan bir aktivitenin sonucunu yakalayan deprecated metod.
    //Yeni projelerde Activity Result API (registerForActivityResult) kullanılması önerilir.
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Eğer sonuç Google Giriş işlemimizden (RC_SIGN_IN) geldiyse
        if (requestCode == RC_SIGN_IN) {
            //GoogleSignIn.getSignedInAccountFromIntent(data) ile Google giriş sonucunu alırız.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                //Task başarılıysa, Google hesabı bilgilerini alırız.
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                //Google Kimlik Token'ı ile Firebase'de kimlik doğrulaması yaparız.
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                //Google Giriş işlemi başarısız olursa
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(requireContext(), "Google ile giriş başarısız: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    //Google Kimlik Token'ı ile Firebase'de kimlik doğrulama işlemi.
    private fun firebaseAuthWithGoogle(idToken: String) {
        //Google Kimlik Token'ını Firebase Kimlik Bilgilerine dönüştürürüz.
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        //Firebase ile bu kimlik bilgileriyle giriş yaparız.
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    //Firebase Kimlik Doğrulama başarılı
                    val user = firebaseAuth.currentUser
                    Toast.makeText(requireContext(), "Google ile giriş başarılı! Hoş geldiniz, ${user?.displayName ?: user?.email}", Toast.LENGTH_SHORT).show()
                    (requireActivity() as? MainActivity)?.updateNavigationMenu()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, NewsFragment())
                        .commit()

                } else {
                    //Firebase Kimlik Doğrulama başarısız
                    Toast.makeText(requireContext(), "Google ile giriş başarısız: ${task.exception?.message}",
                        Toast.LENGTH_LONG).show()
                    Log.w(TAG, "Firebase Auth with Google failed", task.exception)
                }
            }
    }

    // Şifremi unuttum dialog ve sıfırlama fonksiyonu
    private fun showForgotPasswordDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_forgot_password, null)
        val emailEditText = dialogView.findViewById<EditText>(R.id.etForgotPasswordEmail)
        val btnSend = dialogView.findViewById<Button>(R.id.btnForgotPasswordSend)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnForgotPasswordCancel)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnSend.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(requireContext(), "Geçerli bir e-posta adresi girin", Toast.LENGTH_SHORT).show()
            } else {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "Şifre sıfırlama bağlantısı e-posta adresinize gönderildi.", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(requireContext(), "Hata: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                        dialog.dismiss()
                    }
            }
        }
        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    //Fragment'ın görünümü yok edildiğinde çağrılan metod.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
