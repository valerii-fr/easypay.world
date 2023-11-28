package fr.valerii.aeon.data.auth

import android.util.Log
import fr.valerii.aeon.data.api.AeonApi
import fr.valerii.aeon.data.api.AeonAuthException
import fr.valerii.aeon.model.AeonResponse
import fr.valerii.aeon.model.AuthFailAeonResponse
import fr.valerii.aeon.model.AuthModel
import fr.valerii.aeon.model.AuthSuccessAeonResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AeonApi
) : AuthRepository {
    override val result: StateFlow<AuthApiState> get() = _result.asStateFlow()
    private val _result: MutableStateFlow<AuthApiState> = MutableStateFlow(AuthApiState.Waiting)

    private val authCallback = object : Callback<Result<AeonResponse>>  {
        override fun onResponse(
            call: Call<Result<AeonResponse>>,
            response: Response<Result<AeonResponse>>
        ) {
            val body = response.body()
            body?.onSuccess { result ->
                when (result) {
                    is AuthSuccessAeonResponse -> {
                        TokenStorage.setToken(result.response.token)
                        _result.update {
                            Log.w("AUTH_success", "$result")
                            AuthApiState.ApiResult(Result.success(result.response)) }
                        }
                    is AuthFailAeonResponse -> {
                        TokenStorage.setToken(null)
                        _result.update {
                            Log.w("AUTH_success_bad", "$result")
                            AuthApiState.ApiResult(
                                Result.failure(
                                    AeonAuthException(
                                        code = result.error.errorCode,
                                        message = result.error.errorMsg
                                    )
                                )
                            )
                        }
                    }
                    else -> { }
                }
            }?.onFailure { cause ->
                cause.printStackTrace()
                TokenStorage.setToken(null)
                _result.update {
                    Log.w("AUTH_failure", "HTTP request failed")
                    AuthApiState.ApiResult(Result.failure(cause))
                }
            }
        }

        override fun onFailure(call: Call<Result<AeonResponse>>, t: Throwable) {
            TokenStorage.setToken(null)
            t.printStackTrace()
            _result.update {
                Log.w("AUTH_failure_cb", "$t")
                AuthApiState.ApiResult(Result.failure(t))
            }
        }
    }

    override fun auth(auth: AuthModel) {
        _result.update { AuthApiState.Loading }
        api.auth(auth).enqueue(authCallback)
    }
}