package fr.valerii.aeon.data.auth

import fr.valerii.aeon.model.AeonToken

sealed interface AuthApiState {
    data object Loading: AuthApiState
    data object Waiting: AuthApiState
    data class ApiResult(
        val result: Result<AeonToken>,
        val updated: Long = System.currentTimeMillis()
    ): AuthApiState
}