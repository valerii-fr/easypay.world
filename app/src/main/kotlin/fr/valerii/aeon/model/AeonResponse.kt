package fr.valerii.aeon.model

import com.google.gson.annotations.SerializedName

sealed interface AeonResponse {
    val success: Boolean
}

data class AuthSuccessAeonResponse(
    @SerializedName("success")
    override val success: Boolean,
    @SerializedName("response")
    val response: AeonToken
): AeonResponse

data class AuthFailAeonResponse(
    @SerializedName("success")
    override val success: Boolean,
    @SerializedName("error")
    val error: AeonError
): AeonResponse

data class AeonToken(
    @SerializedName("token")
    val token: String
)

data class PaymentAeonResponse(
    @SerializedName("success")
    override val success: Boolean,
    @SerializedName("response")
    val response: List<Payment>
) : AeonResponse