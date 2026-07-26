package com.anuragdeshpande.printhive.network

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DiscoveredServer(
    val name: String,
    val host: String,
    val port: Int,
    val url: String,
)

class PrintHiveServerDiscovery(context: Context) {
    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    private val _discoveredServers = MutableStateFlow<List<DiscoveredServer>>(emptyList())
    val discoveredServers: StateFlow<List<DiscoveredServer>> = _discoveredServers.asStateFlow()

    private var discoveryListener: NsdManager.DiscoveryListener? = null

    fun startDiscovery() {
        stopDiscovery()
        _discoveredServers.value = emptyList()

        discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {
                Log.e("ServerDiscovery", "Discovery start failed: $errorCode")
                stopDiscovery()
            }

            override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {
                Log.e("ServerDiscovery", "Discovery stop failed: $errorCode")
            }

            override fun onDiscoveryStarted(serviceType: String?) {
                Log.d("ServerDiscovery", "NSD Discovery Started: $serviceType")
            }

            override fun onDiscoveryStopped(serviceType: String?) {
                Log.d("ServerDiscovery", "NSD Discovery Stopped: $serviceType")
            }

            override fun onServiceFound(serviceInfo: NsdServiceInfo?) {
                if (serviceInfo != null && serviceInfo.serviceType.contains("_printhive")) {
                    nsdManager.resolveService(serviceInfo, object : NsdManager.ResolveListener {
                        override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                            Log.e("ServerDiscovery", "Resolve failed: $errorCode")
                        }

                        override fun onServiceResolved(resolvedInfo: NsdServiceInfo?) {
                            if (resolvedInfo != null) {
                                val host = resolvedInfo.host?.hostAddress ?: return
                                val port = resolvedInfo.port
                                val name = resolvedInfo.serviceName ?: "PrintHive Server"
                                val url = "http://$host:$port"

                                val current = _discoveredServers.value.toMutableList()
                                if (current.none { it.url == url }) {
                                    current.add(DiscoveredServer(name, host, port, url))
                                    _discoveredServers.value = current
                                }
                            }
                        }
                    })
                }
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo?) {
                Log.d("ServerDiscovery", "Service lost: ${serviceInfo?.serviceName}")
            }
        }

        try {
            nsdManager.discoverServices("_printhive._tcp.", NsdManager.PROTOCOL_DNS_SD, discoveryListener)
        } catch (e: Exception) {
            Log.e("ServerDiscovery", "Failed to discover services", e)
        }
    }

    fun stopDiscovery() {
        discoveryListener?.let {
            try {
                nsdManager.stopServiceDiscovery(it)
            } catch (e: Exception) {
                Log.e("ServerDiscovery", "Error stopping discovery", e)
            }
            discoveryListener = null
        }
    }
}
