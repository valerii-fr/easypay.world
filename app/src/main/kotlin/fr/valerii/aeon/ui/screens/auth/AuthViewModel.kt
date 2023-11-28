package fr.valerii.aeon.ui.screens.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.valerii.aeon.data.api.AeonAuthException
import fr.valerii.aeon.data.api.AeonInitException
import fr.valerii.aeon.data.auth.AuthApiState
import fr.valerii.aeon.data.auth.AuthRepository
import fr.valerii.aeon.domain.AuthUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val authRepo: AuthRepository
) : ViewModel() {
    val authUiState : StateFlow<AuthUiState> get() = _authUiState.asStateFlow()
    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Waiting)

    init {
        viewModelScope.launch {
            authRepo.result.collect {
                Log.w("AuthViewModel", "$it")
                when(it) {
                    is AuthApiState.ApiResult -> {
                        it.result.onSuccess { success ->
                            Log.w("AuthViewModel_success", "$success")
                        }.onFailure { error ->
                            Log.w("AuthViewModel_failure", "$error")
                            when(error) {
                                is AeonAuthException -> _authUiState.update { AuthUiState.BadCredentials }
                                is AeonInitException -> _authUiState.update { AuthUiState.Waiting }
                                else -> _authUiState.update { AuthUiState.Error(error) }
                            }
                        }
                    }
                    is AuthApiState.Loading -> _authUiState.update { AuthUiState.Loading }
                    is AuthApiState.Waiting -> _authUiState.update { AuthUiState.Waiting }
                }
            }
        }
    }

    fun consumeAction(action: AuthUiAction) {
        when(action) {
            is AuthUiAction.Login -> authUseCase(action.login, action.password)
            is AuthUiAction.ResetError -> _authUiState.update { AuthUiState.Waiting }
        }
    }
}