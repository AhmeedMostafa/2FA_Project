package com.example.a2faproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.data.TokenRepository
import com.utils.TimeBasedOTP
import com.model.Token
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TokenViewModel(private val repository: TokenRepository) : ViewModel() {

    // All tokens from database
    val allTokens: StateFlow<List<Token>> = repository.allTokens
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current time remaining in the 30-second window (30 -> 0)
    private val _timeRemaining = MutableStateFlow(calculateTimeRemaining())
    val timeRemaining: StateFlow<Int> = _timeRemaining.asStateFlow()

    // Map of token IDs to their current OTP codes
    private val _otpCodes = MutableStateFlow<Map<Int, String>>(emptyMap())
    val otpCodes: StateFlow<Map<Int, String>> = _otpCodes.asStateFlow()

    init {
        // Start the countdown timer
        viewModelScope.launch {
            while (true) {
                val remaining = calculateTimeRemaining()
                _timeRemaining.value = remaining

                // Regenerate codes when timer resets
                if (remaining == 30 || _otpCodes.value.isEmpty()) {
                    regenerateAllCodes()
                }

                delay(1000L)
            }
        }

        // Observe tokens and generate codes for new ones
        viewModelScope.launch {
            allTokens.collect { tokens ->
                regenerateCodesForTokens(tokens)
            }
        }
    }

    private fun calculateTimeRemaining(): Int {
        val currentSeconds = (System.currentTimeMillis() / 1000) % 30
        return (30 - currentSeconds).toInt()
    }

    private fun regenerateAllCodes() {
        val tokens = allTokens.value
        regenerateCodesForTokens(tokens)
    }

    private fun regenerateCodesForTokens(tokens: List<Token>) {
        val newCodes = tokens.associate { token ->
            token.id to TimeBasedOTP.generateCurrentCode(token.secretKey)
        }
        _otpCodes.value = newCodes
    }

    fun getCodeForToken(token: Token): String {
        return _otpCodes.value[token.id] ?: TimeBasedOTP.generateCurrentCode(token.secretKey)
    }

    fun addToken(issuer: String, accountName: String, secretKey: String) {
        viewModelScope.launch {
            val token = Token(
                issuer = issuer,
                accountName = accountName,
                secretKey = secretKey.trim().replace(" ", "").uppercase()
            )
            repository.insert(token)
        }
    }

    fun deleteToken(token: Token) {
        viewModelScope.launch {
            repository.delete(token)
        }
    }

    // Factory for creating ViewModel with Repository
    class Factory(private val repository: TokenRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TokenViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TokenViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

