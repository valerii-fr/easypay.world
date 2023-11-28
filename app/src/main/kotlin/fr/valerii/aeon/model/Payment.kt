package fr.valerii.aeon.model

import com.google.gson.annotations.SerializedName

data class Payment(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("amount")
    val amount: String? = null,
    @SerializedName("created")
    val created: Long? = null
)