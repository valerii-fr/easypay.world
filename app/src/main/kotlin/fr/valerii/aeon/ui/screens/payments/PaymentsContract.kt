package fr.valerii.aeon.ui.screens.payments

import fr.valerii.aeon.model.PaymentAeonResponse

sealed interface PaymentsUiState {
    data object Loading: PaymentsUiState
    data class Error(val error: Throwable) : PaymentsUiState
    data class Success(val response: PaymentAeonResponse) : PaymentsUiState
}

sealed interface PaymentsUiAction {
    data object Logout : PaymentsUiAction
}