package com.anuragdeshpande.printhive.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anuragdeshpande.printhive.R
import com.anuragdeshpande.printhive.network.DiscoveredServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

@Composable
fun ServerSetupScreen(
    initialUrl: String,
    discoveredServers: List<DiscoveredServer>,
    autoConnectUrl: String? = null,
    onOpenQrScanner: () -> Unit,
    onConnectSuccess: (String) -> Unit,
) {
    var urlText by remember { mutableStateOf(initialUrl) }
    var isTesting by remember { mutableStateOf(false) }
    var testError by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val okHttpClient = remember {
        OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build()
    }

    fun testAndConnect(targetUrl: String) {
        val normalized = targetUrl.trim().removeSuffix("/")
        if (normalized.isBlank()) return

        isTesting = true
        testError = null

        scope.launch(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$normalized/api/v1/health")
                    .get()
                    .build()

                val response = okHttpClient.newCall(request).execute()
                val isOk = response.isSuccessful
                response.close()

                withContext(Dispatchers.Main) {
                    isTesting = false
                    if (isOk) {
                        onConnectSuccess(normalized)
                    } else {
                        testError = "Server responded with status ${response.code}"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isTesting = false
                    onConnectSuccess(normalized)
                }
            }
        }
    }

    LaunchedEffect(initialUrl) {
        urlText = initialUrl
    }

    LaunchedEffect(autoConnectUrl) {
        if (!autoConnectUrl.isNullOrBlank()) {
            urlText = autoConnectUrl
            testAndConnect(autoConnectUrl)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.printhive_header_logo),
                contentDescription = "PrintHive Logo",
                modifier = Modifier
                    .width(220.dp)
                    .height(70.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Connect to PrintHive Server",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (discoveredServers.isNotEmpty()) {
                Text(
                    text = "⚡ Discovered Local Servers",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(8.dp))

                discoveredServers.forEach { server ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { testAndConnect(server.url) },
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Default.Dns, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(server.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text(server.url, style = MaterialTheme.typography.bodySmall)
                            }
                            Text("Connect", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OutlinedTextField(
                        value = urlText,
                        onValueChange = { urlText = it },
                        label = { Text("Server Address") },
                        placeholder = { Text("http://192.168.1.102:8000") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Server URL is already filled in. Tap the button below to scan your PrintHive API QR code.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onOpenQrScanner,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Scan PrintHive QR Code", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { testAndConnect(urlText) },
                        enabled = !isTesting && urlText.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                    ) {
                        if (isTesting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text("Connect to Server", fontWeight = FontWeight.Bold)
                        }
                    }

                    AnimatedVisibility(visible = testError != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 12.dp),
                        ) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = testError ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
            }
        }
    }
}
