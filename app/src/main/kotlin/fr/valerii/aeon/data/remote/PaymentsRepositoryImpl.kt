package fr.valerii.aeon.data.remote

import fr.valerii.aeon.data.api.AeonApi
import fr.valerii.aeon.data.api.AeonNotLoadedException
import fr.valerii.aeon.data.auth.TokenStorage
import fr.valerii.aeon.model.PaymentAeonResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class PaymentsRepositoryImpl @Inject constructor(
    private val api: AeonApi
) : PaymentsRepository {
    override val result: StateFlow<Result<PaymentAeonResponse>> get() = _result.asStateFlow()
    private val _result: MutableStateFlow<Result<PaymentAeonResponse>> = MutableStateFlow(
        Result.failure(AeonNotLoadedException())
    )

    private val paymentsCallback = object : Callback<Result<PaymentAeonResponse>> {
        override fun onResponse(
            call: Call<Result<PaymentAeonResponse>>,
            response: Response<Result<PaymentAeonResponse>>
        ) {
            val body = response.body()
            body?.let {
                _result.update { body }
            }
        }

        override fun onFailure(call: Call<Result<PaymentAeonResponse>>, t: Throwable) {
            TokenStorage.setToken(null)
            _result.update { Result.failure(t) }
        }
    }

    override fun getPayments() = api.getPayments().enqueue(paymentsCallback)
}