package fr.valerii.aeon.data.api

import java.io.IOException

class AeonInitException : IOException() {
    override val message: String = "Value is initialized by default and isn't set yet"
}

class AeonAuthException(
    val code: Int?,
    override val message: String?
) : IOException()

class AeonNotLoadedException() : IOException()