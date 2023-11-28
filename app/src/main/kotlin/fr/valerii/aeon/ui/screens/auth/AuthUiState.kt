package fr.valerii.aeon.ui.screens.auth

sealed interface AuthUiState {
    data object Waiting : AuthUiState
    data object Loading : AuthUiState
    data object BadCredentials: AuthUiState
    data class Error(val error: Throwable): AuthUiState
}

sealed interface AuthUiAction {
    data object ResetError : AuthUiAction
    data class Login(val login: String, val password: String) : AuthUiAction
}