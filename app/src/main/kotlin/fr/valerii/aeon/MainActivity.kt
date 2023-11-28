package fr.valerii.aeon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import fr.valerii.aeon.theme.AEONTestTaskTheme
import fr.valerii.aeon.ui.screens.auth.AuthScreen
import fr.valerii.aeon.ui.screens.payments.PaymentsScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm : MainViewModel = hiltViewModel()
            val uiState by vm.uiState.collectAsState()
            AEONTestTaskTheme {
                when (uiState) {
                    is MainUiState.LoggedOut -> AuthScreen()
                    is MainUiState.LoggedIn -> PaymentsScreen()
                }
            }
        }
    }
}
