package com.books.app.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager

    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

    return (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
}

private object RegisteredCallbacks{
    val callbacks = mutableListOf<ConnectivityManager.NetworkCallback>()
}

fun registerNetworkCallback(context: Context, networkCallback: ConnectivityManager.NetworkCallback) {
    //avoid callback re-registration
    if (RegisteredCallbacks.callbacks.contains(networkCallback)) return

    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkRequest = NetworkRequest.Builder().build()
    connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    RegisteredCallbacks.callbacks.add(networkCallback)
}