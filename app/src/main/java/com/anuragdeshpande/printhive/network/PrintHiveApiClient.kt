package com.anuragdeshpande.printhive.network

import com.anuragdeshpande.printhive.data.ServerPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class PrintHiveApiClient(
    private val repository: ServerPreferencesRepository
) {
    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(repository))
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    suspend fun mintWebSocketToken(serverUrl: String): String? = withContext(Dispatchers.IO) {
        val apiKey = repository.apiKeyFlow.first()
        val normalizedUrl = serverUrl.trim().removeSuffix("/")

        val requestBuilder = Request.Builder()
            .url("$normalizedUrl/api/v1/auth/mint-websocket-token")
            .post("{}".toRequestBody())

        if (apiKey.isNotBlank()) {
            requestBuilder.header("X-API-Key", apiKey)
            requestBuilder.header("Authorization", "******")
        }

        try {
            val response = okHttpClient.newCall(requestBuilder.build()).execute()
            if (response.isSuccessful) {
                val bodyStr = response.body?.string() ?: return@withContext apiKey.ifBlank { null }
                val json = JSONObject(bodyStr)
                val wsToken = json.optString("token", apiKey)
                response.close()
                return@withContext wsToken
            }
            response.close()
            return@withContext apiKey.ifBlank { null }
        } catch (e: Exception) {
            return@withContext apiKey.ifBlank { null }
        }
    }
}
