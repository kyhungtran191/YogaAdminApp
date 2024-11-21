package com.example.yogaadminapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AlertDialog

object NetworkChecker {
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities != null && (
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                )
    }
    fun showAlertNoNetwork(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Connection Error!")
            .setMessage("No internet connection ! Please connect to use app")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}