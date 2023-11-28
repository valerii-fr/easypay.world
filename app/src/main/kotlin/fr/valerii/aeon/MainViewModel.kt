package fr.valerii.aeon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.valerii.aeon.data.auth.TokenStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(): ViewModel() {
    val uiState: StateFlow<MainUiState> get() = _uiState.asStateFlow()
    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.LoggedOut)

    init {
        viewModelScope.launch {
            TokenStorage.tokenState.collect {
                if (it != null) {
                    _uiState.update { MainUiState.LoggedIn }
                } else {
                    _uiState.update { MainUiState.LoggedOut }
                }
            }
        }
    }
}