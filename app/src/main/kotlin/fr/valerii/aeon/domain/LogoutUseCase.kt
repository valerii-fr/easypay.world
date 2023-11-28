package fr.valerii.aeon.domain

import fr.valerii.aeon.data.auth.TokenStorage
import javax.inject.Inject

class LogoutUseCase @Inject constructor() {
    operator fun invoke() = TokenStorage.setToken(null)
}