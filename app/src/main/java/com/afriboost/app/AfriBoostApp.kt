package com.afriboost.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class AfriBoostApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            val ordersChannel = NotificationChannel(
                CHANNEL_ORDERS,
                getString(R.string.notif_channel_orders_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.notif_channel_orders_desc)
            }
            manager.createNotificationChannel(ordersChannel)
        }
    }

    companion object {
        const val CHANNEL_ORDERS = "orders"
    }
}
