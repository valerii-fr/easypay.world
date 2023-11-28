package fr.valerii.aeon.di

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.valerii.aeon.BuildConfig
import fr.valerii.aeon.data.api.AeonApi
import fr.valerii.aeon.data.auth.AuthRepository
import fr.valerii.aeon.data.auth.AuthRepositoryImpl
import fr.valerii.aeon.data.auth.TokenStorage
import fr.valerii.aeon.data.remote.PaymentsRepository
import fr.valerii.aeon.data.remote.PaymentsRepositoryImpl
import fr.valerii.aeon.model.AeonResponse
import fr.valerii.aeon.model.AuthFailAeonResponse
import fr.valerii.aeon.model.AuthSuccessAeonResponse
import fr.valerii.aeon.model.PaymentAeonResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AeonModule {

    @Provides
    @Singleton
    fun provideAuthRepo(
        api: AeonApi
    ) : AuthRepository = AuthRepositoryImpl(api)

    @Provides
    @Singleton
    fun providePaymentsRepo(
        api: AeonApi
    ) : PaymentsRepository = PaymentsRepositoryImpl(api)

    @Provides
    fun provideApiRetrofit() : AeonApi {
        val gson = GsonBuilder()
            .registerTypeAdapter(AeonResponse::class.java, InterfaceAdapter<AeonResponse>())
            .setPrettyPrinting()
            .create()
        val token = TokenStorage.tokenState.value
        val headersInterceptor = Interceptor {
            val request = it.request().newBuilder()
                .addHeader("app-key", BuildConfig.APP_KEY)
                .addHeader("v", BuildConfig.API_V)
                .apply {
                    if(token != null) addHeader("token", token)
                }
                .build()
            it.proceed(request)
        }
        val loggingInterceptor = HttpLoggingInterceptor {
            Log.i("HTTP", it)
        }.apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        val okhttp = OkHttpClient.Builder()
            .addInterceptor(headersInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
        return Retrofit.Builder()
            .client(okhttp)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(ResultCallAdapterFactory())
            .baseUrl(BuildConfig.BASE_URL)
            .build()
            .create(AeonApi::class.java)
    }
}

internal class ResultCallAdapterFactory : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java || returnType !is ParameterizedType) {
            return null
        }
        val upperBound = getParameterUpperBound(0, returnType)

        return if (upperBound is ParameterizedType && upperBound.rawType == Result::class.java) {
            object : CallAdapter<Any, Call<Result<*>>> {
                override fun responseType(): Type = getParameterUpperBound(0, upperBound)

                @Suppress("UNCHECKED_CAST")
                override fun adapt(call: Call<Any>): Call<Result<*>> =
                    ResultCall(call) as Call<Result<*>>
            }
        } else {
            null
        }
    }
}

internal class ResultCall<T>(private val delegate: Call<T>) :
    Call<Result<T>> {

    override fun enqueue(callback: Callback<Result<T>>) {
        delegate.enqueue(
            object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.isSuccessful) {
                        callback.onResponse(
                            this@ResultCall,
                            Response.success(
                                response.code(),
                                Result.success(response.body()!!)
                            )
                        )
                    } else {
                        callback.onResponse(
                            this@ResultCall,
                            Response.success(
                                Result.failure(
                                    HttpException(response)
                                )
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    val errorMessage = when (t) {
                        is IOException -> "No internet connection"
                        is HttpException -> "Something went wrong!"
                        else -> t.localizedMessage
                    }
                    callback.onResponse(
                        this@ResultCall,
                        Response.success(Result.failure(RuntimeException(errorMessage, t)))
                    )
                }
            }
        )
    }

    override fun isExecuted(): Boolean {
        return delegate.isExecuted
    }

    override fun execute(): Response<Result<T>> {
        return Response.success(Result.success(delegate.execute().body()!!))
    }

    override fun cancel() {
        delegate.cancel()
    }

    override fun isCanceled(): Boolean {
        return delegate.isCanceled
    }

    override fun clone(): Call<Result<T>> {
        return ResultCall(delegate.clone())
    }

    override fun request(): Request {
        return delegate.request()
    }

    override fun timeout(): Timeout {
        return delegate.timeout()
    }
}

internal class InterfaceAdapter<T>() : JsonSerializer<T>,
    JsonDeserializer<T> {
    override fun serialize(
        obj: T,
        interfaceType: Type,
        context: JsonSerializationContext
    ): JsonElement {
        val member = JsonObject()
        member.add("data", context.serialize(obj))
        return member
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        elem: JsonElement,
        interfaceType: Type?,
        context: JsonDeserializationContext
    ): T {
        val member: JsonObject = elem as JsonObject
        val type = if (member.has("response")) {
            val r = member.get("response")
            if (r.isJsonArray) {
                PaymentAeonResponse::class.java.typeName
            } else {
                AuthSuccessAeonResponse::class.java.typeName
            }
        } else {
            if (member.has("error")) {
                AuthFailAeonResponse::class.java.typeName
            } else {
                AeonResponse::class.java.typeName
            }
        }
        return context.deserialize(
            elem,
            Class.forName(type)
        )
    }

    private operator fun get(wrapper: JsonObject, memberName: String): JsonElement {
        return wrapper.get(memberName)
            ?: throw JsonParseException(
                "no '$memberName' member found in json file."
            )
    }
}

