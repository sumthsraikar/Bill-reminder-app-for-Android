package com.example.myapplicationds.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.myapplicationds.data.local.entity.BillEntity
import java.util.Calendar

object AlarmScheduler {

    fun scheduleBillReminders(context: Context, bill: BillEntity, currency: String = "₹") {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Parse reminder time (format HH:mm)
        val (hours, minutes) = try {
            val parts = bill.reminderTime.split(":")
            Pair(parts[0].toInt(), parts[1].toInt())
        } catch (e: Exception) {
            Pair(9, 0)
        }

        // Schedule notification for reminderDate
        val reminderCal = Calendar.getInstance().apply {
            timeInMillis = bill.reminderDate
            set(Calendar.HOUR_OF_DAY, hours)
            set(Calendar.MINUTE, minutes)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (reminderCal.timeInMillis > System.currentTimeMillis()) {
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                action = AlarmReceiver.ACTION_BILL_REMINDER
                putExtra("BILL_ID", bill.id)
                putExtra("BILL_NAME", bill.billName)
                putExtra("BILL_AMOUNT", bill.amount)
                putExtra("CURRENCY", currency)
                putExtra("MESSAGE", "Due on ${formatDate(bill.dueDate)}")
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                bill.id.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminderCal.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        reminderCal.timeInMillis,
                        pendingIntent
                    )
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    fun cancelBillReminder(context: Context, billId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_BILL_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            billId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    private fun formatDate(millis: Long): String {
        val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(millis))
    }
}
