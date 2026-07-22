package com.example.myapplicationds.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {

            BillNotificationHelper.createNotificationChannel(context)

            val workRequest = OneTimeWorkRequestBuilder<BillReminderWorker>()
                .setInitialDelay(5, TimeUnit.SECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "BootReminderWork",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )

            // Also schedule periodic work
            val periodicWork = PeriodicWorkRequestBuilder<BillReminderWorker>(12, TimeUnit.HOURS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "PeriodicReminderWork",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWork
            )
        }
    }
}
