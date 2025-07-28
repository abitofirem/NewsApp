package com.example.navigationdrawerapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class RegisterViewModel : ViewModel() {
    
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    private val _registerResult = MutableLiveData<RegisterResult>()
    val registerResult: LiveData<RegisterResult> = _registerResult
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    fun registerWithEmail(email: String, password: String, name: String) {
        _isLoading.value = true
        
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    user?.let { firebaseUser ->
                        // Kullanıcı bilgilerini Firestore'a kaydet
                        val userData = hashMapOf(
                            "name" to name,
                            "email" to email,
                            "createdAt" to com.google.firebase.Timestamp.now()
                        )
                        
                        firestore.collection("users")
                            .document(firebaseUser.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                _isLoading.value = false
                                _registerResult.value = RegisterResult.Success(firebaseUser)
                            }
                            .addOnFailureListener { e ->
                                _isLoading.value = false
                                _registerResult.value = RegisterResult.Error(e.message ?: "Failed to save user data")
                            }
                    }
                } else {
                    _isLoading.value = false
                    _registerResult.value = RegisterResult.Error(task.exception?.message ?: "Registration failed")
                }
            }
    }
    
    fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    fun validatePassword(password: String): Boolean {
        return password.length >= 6
    }
    
    fun validateName(name: String): Boolean {
        return name.trim().isNotEmpty()
    }
    
    sealed class RegisterResult {
        data class Success(val user: FirebaseUser) : RegisterResult()
        data class Error(val message: String) : RegisterResult()
    }
} 