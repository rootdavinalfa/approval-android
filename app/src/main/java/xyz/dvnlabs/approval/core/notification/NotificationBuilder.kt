/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import xyz.dvnlabs.approval.R

class NotificationBuilder {
    companion object {
        fun notify(
            notificationID: NotificationID,
            context: Context,
            title: String,
            body: String,
            pendingIntent: PendingIntent? = null,
            identifier: Int = -1,
        ) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.getString(R.string.notification_name)
                val descriptionText = context.getString(R.string.notification_name)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(notificationID.id.toString(), name, importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }

            val builder = NotificationCompat.Builder(context, notificationID.id.toString())
                .setSmallIcon(R.drawable.ic_bell)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(
                    NotificationCompat
                        .BigTextStyle()
                        .bigText(body)
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            pendingIntent?.let {
                builder.setContentIntent(pendingIntent)
            }

            with(NotificationManagerCompat.from(context)) {
                notify(if (identifier == -1) notificationID.id else identifier, builder.build())
            }

        }
    }

}