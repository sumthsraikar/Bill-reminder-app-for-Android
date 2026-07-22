package com.example.myapplicationds.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.myapplicationds.data.repository.BillRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var billRepository: BillRepository

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_BILL_REMINDER -> {
                val billId = intent.getLongExtra("BILL_ID", -1L)
                val billName = intent.getStringExtra("BILL_NAME") ?: "Bill"
                val amount = intent.getDoubleExtra("BILL_AMOUNT", 0.0)
                val currency = intent.getStringExtra("CURRENCY") ?: "₹"
                val message = intent.getStringExtra("MESSAGE") ?: "Reminder"

                if (billId != -1L) {
                    BillNotificationHelper.showBillNotification(
                        context,
                        billId,
                        billName,
                        amount,
                        currency,
                        message
                    )
                }
            }
            ACTION_MARK_PAID -> {
                val billId = intent.getLongExtra("BILL_ID", -1L)
                if (billId != -1L) {
                    val pendingResult = goAsync()
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val bill = billRepository.getBillByIdOneShot(billId)
                            if (bill != null) {
                                billRepository.markBillAsPaid(bill)
                            }
                        } finally {
                            pendingResult.finish()
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val ACTION_BILL_REMINDER = "com.example.myapplicationds.ACTION_BILL_REMINDER"
        const val ACTION_MARK_PAID = "com.example.myapplicationds.ACTION_MARK_PAID"
    }
}
