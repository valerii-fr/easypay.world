package fr.valerii.aeon.data.auth

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object TokenStorage {
    val tokenState: StateFlow<String?> get() = _tokenState.asStateFlow()
    private val _tokenState = MutableStateFlow<String?>(null)
    fun setToken(token: String?) {
        Log.i("TOKEN", "updated: $token")
        _tokenState.update { token }
    }
}