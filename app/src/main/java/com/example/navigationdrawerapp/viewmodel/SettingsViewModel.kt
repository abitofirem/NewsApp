package com.example.navigationdrawerapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class SettingsViewModel : ViewModel() {
    
    private val auth = FirebaseAuth.getInstance()
    
    private val _logoutResult = MutableLiveData<LogoutResult>()
    val logoutResult: LiveData<LogoutResult> = _logoutResult
    
    private val _currentUser = MutableLiveData<String>()
    val currentUser: LiveData<String> = _currentUser
    
    init {
        loadCurrentUser()
    }
    
    private fun loadCurrentUser() {
        val user = auth.currentUser
        _currentUser.value = user?.email ?: "Unknown User"
    }
    
    fun logout() {
        auth.signOut()
        _logoutResult.value = LogoutResult.Success
    }
    
    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }
    
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
    
    sealed class LogoutResult {
        object Success : LogoutResult()
        data class Error(val message: String) : LogoutResult()
    }
} 