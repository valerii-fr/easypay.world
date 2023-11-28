package fr.valerii.aeon.ui.screens.payments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import fr.valerii.aeon.R
import fr.valerii.aeon.model.Payment
import fr.valerii.aeon.theme.paddings
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsScreen(
    viewModel: PaymentsViewModel = hiltViewModel()
) {
    val state by viewModel.paymentsUiState.collectAsState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(stringResource(R.string.payments_title))
                },
                actions = {
                    IconButton(onClick = { viewModel.consumeAction(PaymentsUiAction.Logout)} ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (state) {
            is PaymentsUiState.Success -> PaymentsListView(
                modifier = Modifier.padding(paddingValues),
                state = state as PaymentsUiState.Success
            )
            is PaymentsUiState.Loading -> {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxWidth()
                ) {
                    CircularProgressIndicator()
                }
            }
            is PaymentsUiState.Error -> {
                LazyColumn(modifier = Modifier.padding(paddingValues)) {
                    item {
                        Text(
                            text = "${(state as PaymentsUiState.Error).error.message}",
                            modifier = Modifier.padding(MaterialTheme.paddings.medium)
                        )
                        Text(
                            text = (state as PaymentsUiState.Error).error.stackTraceToString(),
                            modifier = Modifier.padding(horizontal = MaterialTheme.paddings.medium),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentsListView(
    modifier: Modifier = Modifier,
    state: PaymentsUiState.Success
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        itemsIndexed(state.response.response) { index, payment ->
            PaymentItem(payment = payment, currency = "$")
        }
    }
}

@Composable
private fun PaymentItem(
    payment: Payment,
    currency: String
) {
    if (
        payment.created != null
        && payment.amount?.isNotBlank() == true
        ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.paddings.small)
        ) {
            Text(
                text = payment.title,
                style = MaterialTheme.typography.titleSmall
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                val localDate = LocalDateTime.ofEpochSecond(
                    payment.created.toLong(),
                    0,
                    ZonedDateTime.now().offset
                )
                    .format(
                        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                    )
                Text(
                    text = localDate,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${payment.amount} $currency",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Divider()
        }
    }

}