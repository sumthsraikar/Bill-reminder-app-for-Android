package com.example.myapplicationds.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myapplicationds.data.repository.BillRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.Calendar

@HiltWorker
class BillReminderWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: BillRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val notificationsEnabled = repository.notificationEnabledFlow.first()
            if (!notificationsEnabled) return Result.success()

            val currency = repository.currencyFlow.first()
            val bills = repository.getAllBills().first()

            val todayCal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val todayMillis = todayCal.timeInMillis

            for (bill in bills) {
                if (bill.paymentStatus == "PAID") continue

                // Check overdue state
                val dueCal = Calendar.getInstance().apply {
                    timeInMillis = bill.dueDate
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val dueMillis = dueCal.timeInMillis

                val diffDays = ((dueMillis - todayMillis) / (1000 * 60 * 60 * 24)).toInt()

                if (diffDays < 0 && bill.paymentStatus != "OVERDUE") {
                    repository.updateBill(bill.copy(paymentStatus = "OVERDUE"))
                    BillNotificationHelper.showBillNotification(
                        appContext,
                        bill.id,
                        bill.billName,
                        bill.amount,
                        currency,
                        "OVERDUE by ${-diffDays} day(s)!"
                    )
                } else if (diffDays == 0) {
                    BillNotificationHelper.showBillNotification(
                        appContext,
                        bill.id,
                        bill.billName,
                        bill.amount,
                        currency,
                        "DUE TODAY!"
                    )
                } else if (diffDays in listOf(1, 3, 7)) {
                    BillNotificationHelper.showBillNotification(
                        appContext,
                        bill.id,
                        bill.billName,
                        bill.amount,
                        currency,
                        "Due in $diffDays day(s)"
                    )
                }

                // Reschedule exact alarm for bill
                AlarmScheduler.scheduleBillReminders(appContext, bill, currency)
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
