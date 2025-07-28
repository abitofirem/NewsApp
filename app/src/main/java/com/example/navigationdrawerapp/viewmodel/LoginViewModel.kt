package com.example.navigationdrawerapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginViewModel : ViewModel() {
    
    private val auth = FirebaseAuth.getInstance()
    
    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    fun loginWithEmail(email: String, password: String) {
        _isLoading.value = true
        
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _loginResult.value = LoginResult.Success(task.result?.user)
                } else {
                    _loginResult.value = LoginResult.Error(task.exception?.message ?: "Login failed")
                }
            }
    }
    
    fun loginWithGoogle(idToken: String) {
        _isLoading.value = true
        
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _loginResult.value = LoginResult.Success(task.result?.user)
                } else {
                    _loginResult.value = LoginResult.Error(task.exception?.message ?: "Google login failed")
                }
            }
    }
    
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
    
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
    
    sealed class LoginResult {
        data class Success(val user: FirebaseUser?) : LoginResult()
        data class Error(val message: String) : LoginResult()
    }
} 