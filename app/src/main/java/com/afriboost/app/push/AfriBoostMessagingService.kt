package com.afriboost.app.push

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.afriboost.app.AfriBoostApp
import com.afriboost.app.MainActivity
import com.afriboost.app.R
import com.afriboost.app.SiteRoutes
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Reçoit les notifications push (ex: "Votre commande #1234 est terminée").
 *
 * Ne fait rien tant que Firebase n'est pas pleinement configuré (voir README,
 * section Firebase) -- cette classe ne provoque aucune erreur de compilation
 * ou d'exécution en son absence, elle est juste inactive.
 *
 * Reste à faire côté backend afriboost (PHP) : à chaque changement de statut
 * de commande, enregistrer le token FCM de l'utilisateur (reçu via
 * onNewToken ci-dessous, à envoyer à une route du site à créer) puis
 * déclencher l'envoi via l'API Firebase Cloud Messaging.
 */
class AfriBoostMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO (phase Firebase) : envoyer ce token à afriboost.com pour l'associer
        // au compte connecté, afin que le serveur puisse cibler cet appareil.
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: message.data["title"] ?: getString(R.string.app_name)
        val body = message.notification?.body ?: message.data["body"] ?: return
        val targetPath = message.data["path"] ?: SiteRoutes.ORDERS

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_START_PATH, targetPath)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, AfriBoostApp.CHANNEL_ORDERS)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(getColor(R.color.afriboost_blue))
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val canNotify = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        if (canNotify) {
            NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), notification)
        }
    }
}
