package com.example.a2faproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Local guest mode (no Firebase, works offline)
    private val _isLocalGuest = MutableStateFlow(false)
    private val localGuestUid = "local_guest_user"

    val currentUid: String?
        get() = if (_isLocalGuest.value) localGuestUid else auth.currentUser?.uid

    val isAnonymous: Boolean
        get() = _isLocalGuest.value || auth.currentUser?.isAnonymous == true

    val isLoggedIn: Boolean
        get() = _isLocalGuest.value || auth.currentUser != null

    fun clearError() {
        _errorMessage.value = null
    }

    private fun getSignInErrorMessage(e: Exception): String {
        return when (e) {
            is FirebaseAuthInvalidUserException -> "No account found with this email"
            is FirebaseAuthInvalidCredentialsException -> {
                when {
                    e.message?.contains("password") == true -> "Incorrect password"
                    e.message?.contains("email") == true -> "Please enter a valid email address"
                    else -> "Invalid email or password"
                }
            }
            else -> {
                when {
                    e.message?.contains("network") == true -> "No internet connection. Please try again"
                    e.message?.contains("blocked") == true -> "Too many attempts. Please try again later"
                    else -> "Unable to sign in. Please try again"
                }
            }
        }
    }

    private fun getSignUpErrorMessage(e: Exception): String {
        return when (e) {
            is FirebaseAuthWeakPasswordException -> "Password must be at least 6 characters"
            is FirebaseAuthInvalidCredentialsException -> "Please enter a valid email address"
            is FirebaseAuthUserCollisionException -> "An account with this email already exists"
            else -> {
                when {
                    e.message?.contains("network") == true -> "No internet connection. Please try again"
                    else -> "Unable to create account. Please try again"
                }
            }
        }
    }

    fun signIn(email: String, password: String, onSuccess: (uid: String) -> Unit) {
        if (email.isBlank()) {
            _errorMessage.value = "Please enter your email"
            return
        }
        if (password.isBlank()) {
            _errorMessage.value = "Please enter your password"
            return
        }

        viewModelScope.launch {
            _errorMessage.value = null
            _isLoading.value = true
            try {
                val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
                val uid = result.user?.uid
                if (uid != null) onSuccess(uid) else _errorMessage.value = "Unable to sign in. Please try again"
            } catch (e: Exception) {
                _errorMessage.value = getSignInErrorMessage(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signUp(email: String, password: String, onSuccess: (uid: String) -> Unit) {
        if (email.isBlank()) {
            _errorMessage.value = "Please enter your email"
            return
        }
        if (password.isBlank()) {
            _errorMessage.value = "Please enter a password"
            return
        }
        if (password.length < 6) {
            _errorMessage.value = "Password must be at least 6 characters"
            return
        }

        viewModelScope.launch {
            _errorMessage.value = null
            _isLoading.value = true
            try {
                val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
                val uid = result.user?.uid
                if (uid != null) onSuccess(uid) else _errorMessage.value = "Unable to create account. Please try again"
            } catch (e: Exception) {
                _errorMessage.value = getSignUpErrorMessage(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signInAsGuest(onSuccess: (uid: String) -> Unit) {
        // Local guest mode - works completely offline, no Firebase call
        _errorMessage.value = null
        _isLocalGuest.value = true
        onSuccess(localGuestUid)
    }

    fun signOut() {
        _isLocalGuest.value = false
        auth.signOut()
    }
}
