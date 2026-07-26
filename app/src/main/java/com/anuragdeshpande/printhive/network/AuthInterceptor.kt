package com.anuragdeshpande.printhive.network

import com.anuragdeshpande.printhive.data.ServerPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val repository: ServerPreferencesRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val apiKey = runBlocking { repository.apiKeyFlow.first() }

        val requestBuilder = originalRequest.newBuilder()

        if (apiKey.isNotBlank()) {
            requestBuilder.header("X-API-Key", apiKey)
            if (!originalRequest.headers.names().contains("Authorization")) {
                requestBuilder.header("Authorization", "******")
            }
        }

        val response = chain.proceed(requestBuilder.build())

        if (response.code == 401 && apiKey.isNotBlank()) {
            runBlocking {
                repository.setPaired(false)
            }
        }

        return response
    }
}
