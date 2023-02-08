package com.example.ecommerce_rifqi.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.ecommerce_rifqi.R
import com.example.ecommerce_rifqi.data.local.Notification
import com.example.ecommerce_rifqi.data.local.NotificationDAO
import com.example.ecommerce_rifqi.data.local.ProductDatabase
import com.example.ecommerce_rifqi.helper.Constant
import com.example.ecommerce_rifqi.helper.PreferencesHelper
import com.example.ecommerce_rifqi.ui.LoginActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MyFirebaseMessagingService: FirebaseMessagingService() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${remoteMessage.from}")
        Log.d(TAG, "Message data payload: " + remoteMessage.data)
        Log.d(TAG, "Message Notification Body: ${remoteMessage.notification?.body}")

        sendNotification(remoteMessage.notification?.title, remoteMessage.notification?.body)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendNotification(title: String?, messageBody: String?) {

        val contentIntent = Intent(applicationContext, LoginActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(applicationContext,
            NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())

        val current = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = current.format(formatter)

        val productDatabase = ProductDatabase.getDatabase(applicationContext)
        val notification = Notification(0, title = title.toString(), message = messageBody.toString(), date = date)
        productDatabase?.notificationDAO()?.addToNotification(notification)

        Log.e("Title", title.toString())
        Log.e("Message", messageBody.toString())
    }

    companion object {
        private val TAG = MyFirebaseMessagingService::class.java.simpleName
        private const val NOTIFICATION_ID = 1
        private const val NOTIFICATION_CHANNEL_ID = "Firebase Channel"
        private const val NOTIFICATION_CHANNEL_NAME = "Firebase Notification"
    }

}