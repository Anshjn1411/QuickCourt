package com.project.odoo_235.data.api



import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.project.odoo_235.data.datastore.UserSessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private fun gson(): Gson {
        // Backend returns ISO-like timestamps; support common patterns.
        return GsonBuilder()
            .setLenient()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create()
    }

    private fun getClient(
        userSessionLayer: UserSessionManager,
        timeoutSeconds: Long = 30,
        useCookies: Boolean = false
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("API_BODY", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val builder = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(userSessionLayer))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)

        if (useCookies) {
            builder.cookieJar(object : CookieJar {
                private val store = mutableMapOf<String, List<Cookie>>()
                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                    store[url.host] = cookies
                }
                override fun loadForRequest(url: HttpUrl): List<Cookie> {
                    return store[url.host] ?: emptyList()
                }
            })
        }

        return builder.build()
    }

    fun getRetrofit(
        baseUrl: String,
        userSessionLayer: UserSessionManager,
        timeoutSeconds: Long = 30,
        useCookies: Boolean = false
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl) // e.g. "http://10.0.2.2:5001/"
            .client(getClient(userSessionLayer, timeoutSeconds, useCookies))
            .addConverterFactory(GsonConverterFactory.create(gson()))
            .build()
    }

    fun getApi(userSessionLayer: UserSessionManager): Repo {
        // Backend listens on 5001 and mounts under /api/...
        return getRetrofit("http://192.168.43.29:5001/", userSessionLayer)
            .create(Repo::class.java)
    }
}

class AuthInterceptor(
    private val userSessionLayer: UserSessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = runBlocking { userSessionLayer.getToken() }

        val reqBuilder = originalRequest.newBuilder()
            .addHeader("Accept", "application/json")

        if (!token.isNullOrEmpty()) {
            reqBuilder.addHeader("Authorization", "Bearer $token")
        }

        val newRequest = reqBuilder.build()

        Log.d("API_LOGS", "👉 Final Endpoint: ${newRequest.url}")
        Log.d("API_LOGS", "👉 Method: ${newRequest.method}")
        Log.d("API_LOGS", "👉 Headers:\n${newRequest.headers}")

        return chain.proceed(newRequest)
    }
}
