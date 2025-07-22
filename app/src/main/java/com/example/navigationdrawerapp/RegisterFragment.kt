package com.example.navigationdrawerapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.navigationdrawerapp.databinding.FragmentRegisterBinding //View Binding için
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    //Google Sign-In için sabit bir request kodu
    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "RegisterFragment" //Logcat için TAG
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Firebase Auth ve Google Sign-In istemcisini başlat
        firebaseAuth = FirebaseAuth.getInstance()

        //Google Sign-In seçeneklerini yapılandır
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) //Google Services Json'dan gelir
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Kayıt Ol butonu tıklama dinleyicisi
        binding.registerButton.setOnClickListener {
            registerUser()
        }

        //Google ile Kayıt Ol butonu tıklama dinleyicisi
        binding.googleSigninButtonRegister.setOnClickListener {
            signInWithGoogle()
        }

        //Zaten hesabınız var mı? metni tıklama dinleyicisi
        binding.loginNowText.setOnClickListener {
            // LoginFragment'a geçiş yap
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .addToBackStack(null) //Geri tuşu ile RegisterFragment'a dönebilmek için
                .commit()
        }
    }

    //E-posta ve şifre ile kullanıcı kaydı
    private fun registerUser() {
        val name = binding.nameEditText.text.toString().trim()
        val email = binding.emailRegisterEditText.text.toString().trim()
        val password = binding.passwordRegisterEditText.text.toString().trim()

        //Giriş doğrulamaları
        if (name.isEmpty()) {
            binding.nameInputLayout.error = "Ad Soyad boş bırakılamaz"
            binding.nameEditText.requestFocus()
            return
        }
        if (email.isEmpty()) {
            binding.emailRegisterInputLayout.error = "E-posta boş bırakılamaz"
            binding.emailRegisterEditText.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailRegisterInputLayout.error = "Geçerli bir e-posta adresi girin"
            binding.emailRegisterEditText.requestFocus()
            return
        }
        if (password.isEmpty()) {
            binding.passwordRegisterInputLayout.error = "Şifre boş bırakılamaz"
            binding.passwordRegisterEditText.requestFocus()
            return
        }
        if (password.length < 6) {
            binding.passwordRegisterInputLayout.error = "Şifre en az 6 karakter olmalı"
            binding.passwordRegisterEditText.requestFocus()
            return
        }

        //Firebase ile yeni kullanıcı oluşturma
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    //Kayıt başarılı, kullanıcı bilgisini güncelleyelim (isteğe bağlı)
                    val user = firebaseAuth.currentUser
                    user?.updateProfile(
                        com.google.firebase.auth.UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()
                    )?.addOnCompleteListener { profileUpdateTask ->
                        if (profileUpdateTask.isSuccessful) {
                            Log.d(TAG, "Kullanıcı adı başarıyla ayarlandı.")
                        } else {
                            Log.w(TAG, "Kullanıcı adı ayarlanamadı", profileUpdateTask.exception)
                        }
                    }

                    Toast.makeText(requireContext(), "Kayıt başarılı!", Toast.LENGTH_SHORT).show()
                    (requireActivity() as? MainActivity)?.updateNavigationMenu()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, CFragment())
                        .commit()

                } else {
                    //Kayıt başarısız
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(requireContext(), "Kayıt başarısız: ${task.exception?.message}",
                        Toast.LENGTH_LONG).show()
                }
            }
    }

    //Google ile giriş başlatma
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    //Google giriş sonucu
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Google Sign In sonucunu işleme
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                //Google Sign In başarılı, Firebase ile kimlik doğrula
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                //Google Sign In başarısız oldu
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(requireContext(), "Google ile giriş başarısız: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    //Firebase ile Google kimlik doğrulama
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    //Firebase Auth başarılı
                    val user = firebaseAuth.currentUser
                    Toast.makeText(requireContext(), "Google ile giriş başarılı! Hoş geldiniz, ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    (requireActivity() as? MainActivity)?.updateNavigationMenu()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, CFragment())
                        .commit()

                } else {
                    //Firebase Auth başarısız
                    Toast.makeText(requireContext(), "Google ile giriş başarısız: ${task.exception?.message}",
                        Toast.LENGTH_LONG).show()
                    Log.w(TAG, "Firebase Auth with Google failed", task.exception)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null //Bellek sızıntısını önlemek için binding'i temizle
    }
}