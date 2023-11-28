package fr.valerii.aeon.domain

import fr.valerii.aeon.data.auth.AuthRepository
import fr.valerii.aeon.model.AuthModel
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val authRepo: AuthRepository
) {
    operator fun invoke(login: String, password: String) {
        val auth = AuthModel(login, password)
        authRepo.auth(auth)
    }
}