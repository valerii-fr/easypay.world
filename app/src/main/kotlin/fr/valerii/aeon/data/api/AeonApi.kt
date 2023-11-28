package fr.valerii.aeon.data.api

import fr.valerii.aeon.model.AeonResponse
import fr.valerii.aeon.model.AuthModel
import fr.valerii.aeon.model.PaymentAeonResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AeonApi {
    @GET("api-test/payments")
    fun getPayments() : Call<Result<PaymentAeonResponse>>

    /**
     * @param payload JSON with login and password for auth
     */
    @POST("api-test/login")
    fun auth(@Body payload: AuthModel) : Call<Result<AeonResponse>>
}