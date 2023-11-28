package fr.valerii.aeon.model

import com.google.gson.annotations.SerializedName

data class AeonError(
    @SerializedName("error_code")
    val errorCode: Int? = null,
    @SerializedName("error_msg")
    val errorMsg: String? = null
)