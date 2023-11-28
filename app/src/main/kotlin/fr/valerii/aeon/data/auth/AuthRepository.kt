package fr.valerii.aeon.data.auth

import fr.valerii.aeon.model.AuthModel
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val result: StateFlow<AuthApiState>
    fun auth(auth: AuthModel)
}