package fr.valerii.aeon.ui.screens.payments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.valerii.aeon.data.remote.PaymentsRepository
import fr.valerii.aeon.domain.GetPaymentsUseCase
import fr.valerii.aeon.domain.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentsViewModel @Inject constructor(
    private val paymentsRepo: PaymentsRepository,
    private val getPaymentsUseCase: GetPaymentsUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    val paymentsUiState get() = _paymentsUiState.asStateFlow()
    private val _paymentsUiState = MutableStateFlow<PaymentsUiState>(PaymentsUiState.Loading)

    init {
        viewModelScope.launch {
            paymentsRepo.result.collect { result ->
                result.onSuccess { success ->
                    _paymentsUiState.update { PaymentsUiState.Success(success) }
                }.onFailure { error ->
                    _paymentsUiState.update { PaymentsUiState.Error(error) }
                }
            }
        }
        _paymentsUiState.update { PaymentsUiState.Loading }
        getPaymentsUseCase()
    }

    fun consumeAction(action: PaymentsUiAction) {
        when (action) {
            is PaymentsUiAction.Logout -> logoutUseCase()
        }
    }
}