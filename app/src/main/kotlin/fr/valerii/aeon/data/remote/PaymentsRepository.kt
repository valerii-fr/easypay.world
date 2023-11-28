package fr.valerii.aeon.data.remote

import fr.valerii.aeon.model.PaymentAeonResponse
import kotlinx.coroutines.flow.StateFlow

interface PaymentsRepository {
    val result: StateFlow<Result<PaymentAeonResponse>>
    fun getPayments()
}