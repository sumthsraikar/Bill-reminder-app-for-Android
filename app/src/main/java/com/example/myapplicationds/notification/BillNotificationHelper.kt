package com.example.myapplicationds.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.myapplicationds.MainActivity
import com.example.myapplicationds.R

object BillNotificationHelper {

    private const val CHANNEL_ID = "bill_reminders_channel"
    private const val CHANNEL_NAME = "Bill Reminders"
    private const val CHANNEL_DESC = "Notifications for upcoming and overdue bill payments"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showBillNotification(
        context: Context,
        billId: Long,
        billName: String,
        amount: Double,
        currency: String,
        message: String
    ) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("BILL_ID", billId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            billId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val markPaidIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_MARK_PAID
            putExtra("BILL_ID", billId)
        }
        val markPaidPendingIntent = PendingIntent.getBroadcast(
            context,
            (billId + 10000).toInt(),
            markPaidIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Bill Reminder: $billName")
            .setContentText("$message - Amount: $currency$amount")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.checkbox_on_background, "Mark as Paid", markPaidPendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(billId.toInt(), notification)
    }
}
