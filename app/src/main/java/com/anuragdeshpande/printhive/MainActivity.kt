package com.anuragdeshpande.printhive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.anuragdeshpande.printhive.data.ServerPreferencesRepository
import com.anuragdeshpande.printhive.network.PrintHiveServerDiscovery
import com.anuragdeshpande.printhive.ui.OnboardingScreen
import com.anuragdeshpande.printhive.ui.ServerQrScanScreen
import com.anuragdeshpande.printhive.ui.ServerSetupScreen
import com.anuragdeshpande.printhive.ui.theme.PrintHiveTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PrintHiveTheme {
                val context = LocalContext.current
                val repository = remember { ServerPreferencesRepository(context) }
                val discovery = remember { PrintHiveServerDiscovery(context) }
                val scope = rememberCoroutineScope()

                val initialServerUrl by repository.serverUrlFlow.collectAsState(initial = ServerPreferencesRepository.DEFAULT_SERVER_URL)
                val isPaired by repository.isPairedFlow.collectAsState(initial = false)
                val discoveredServers by discovery.discoveredServers.collectAsState(initial = emptyList())

                DisposableEffect(discovery) {
                    discovery.startDiscovery()
                    onDispose { discovery.stopDiscovery() }
                }

                var hasCompletedOnboarding by rememberSaveable { mutableStateOf(false) }
                var showQrScanner by rememberSaveable { mutableStateOf(false) }
                var serverUrl by rememberSaveable { mutableStateOf(initialServerUrl) }
                var autoConnectUrl by rememberSaveable { mutableStateOf<String?>(null) }

                LaunchedEffect(initialServerUrl, isPaired) {
                    serverUrl = initialServerUrl
                    if (isPaired && initialServerUrl.isNotBlank()) {
                        hasCompletedOnboarding = true
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    when {
                        showQrScanner -> {
                            ServerQrScanScreen(
                                onQrScanned = { scannedUrl ->
                                    serverUrl = scannedUrl
                                    autoConnectUrl = scannedUrl
                                    showQrScanner = false
                                },
                                onClose = {
                                    showQrScanner = false
                                },
                            )
                        }

                        !hasCompletedOnboarding -> {
                            OnboardingScreen(
                                onNavigateToServerQr = {
                                    hasCompletedOnboarding = true
                                    showQrScanner = true
                                },
                                isServerConnected = isPaired && serverUrl.isNotBlank(),
                                serverUrl = serverUrl,
                                onFinish = {
                                    hasCompletedOnboarding = true
                                },
                            )
                        }

                        !isPaired -> {
                            ServerSetupScreen(
                                initialUrl = serverUrl,
                                discoveredServers = discoveredServers,
                                autoConnectUrl = autoConnectUrl,
                                onOpenQrScanner = {
                                    showQrScanner = true
                                },
                                onConnectSuccess = { connectedUrl ->
                                    serverUrl = connectedUrl
                                    autoConnectUrl = null
                                    scope.launch {
                                        repository.saveServerUrl(connectedUrl)
                                        repository.setPaired(true)
                                    }
                                },
                            )
                        }

                        else -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "Let's start developing",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
