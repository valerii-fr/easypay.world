package fr.valerii.aeon.domain

import fr.valerii.aeon.data.remote.PaymentsRepository
import javax.inject.Inject

class GetPaymentsUseCase @Inject constructor(
    private val paymentsRepo: PaymentsRepository
) {
    operator fun invoke() = paymentsRepo.getPayments()
}