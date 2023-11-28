package fr.valerii.aeon

sealed interface MainUiState {
    data object LoggedOut : MainUiState
    data object LoggedIn : MainUiState
}