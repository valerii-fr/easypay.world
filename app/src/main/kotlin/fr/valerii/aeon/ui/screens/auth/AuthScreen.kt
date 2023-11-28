package fr.valerii.aeon.ui.screens.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import fr.valerii.aeon.R
import fr.valerii.aeon.theme.paddings
import fr.valerii.aeon.theme.spacers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.authUiState.collectAsState()
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    val error = state is AuthUiState.Error
    val badCredentials = state is AuthUiState.BadCredentials
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(stringResource(R.string.auth_title))
                }
            )
        }
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            TextField(
                value = login,
                onValueChange = { login = it },
                label = { Text(stringResource(R.string.auth_login)) },
                isError = error || badCredentials,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier
                    .padding(horizontal = MaterialTheme.paddings.medium)
                    .fillMaxWidth()
            )
            Spacer(Modifier.height(MaterialTheme.spacers.medium))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.auth_password)) },
                visualTransformation = if (!showPassword) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
                isError = error || badCredentials,
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (!showPassword) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                            contentDescription = null
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .padding(horizontal = MaterialTheme.paddings.medium)
                    .fillMaxWidth()
            )
            Spacer(Modifier.height(MaterialTheme.spacers.medium))
            Button(
                modifier = Modifier
                    .padding(horizontal = MaterialTheme.paddings.medium)
                    .fillMaxWidth(),
                onClick = { viewModel.consumeAction(AuthUiAction.Login(login, password)) },
                enabled = !error && login.isNotBlank() && password.isNotBlank()
            ) {
                Text(
                    text = stringResource(R.string.auth_title),
                    color = if (!error && login.isNotBlank() && password.isNotBlank()) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            if (badCredentials) {
                Text(
                    text = stringResource(R.string.auth_bad_credentials),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(MaterialTheme.paddings.small)
                )
            }
            if (error) {
                Text(
                    text = (state as AuthUiState.Error).error.message.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(MaterialTheme.paddings.small)
                )
            }
        }
    }
}