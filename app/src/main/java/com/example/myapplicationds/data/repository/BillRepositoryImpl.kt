package com.example.myapplicationds.data.repository

import com.example.myapplicationds.data.local.dao.BillDao
import com.example.myapplicationds.data.local.dao.CategoryDao
import com.example.myapplicationds.data.local.dao.PaymentHistoryDao
import com.example.myapplicationds.data.local.entity.BillEntity
import com.example.myapplicationds.data.local.entity.CategoryEntity
import com.example.myapplicationds.data.local.entity.PaymentHistoryEntity
import com.example.myapplicationds.data.preferences.UserPreferencesManager
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

data class BackupData(
    val version: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val bills: List<BillEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val paymentHistory: List<PaymentHistoryEntity> = emptyList()
)

@Singleton
class BillRepositoryImpl @Inject constructor(
    private val billDao: BillDao,
    private val categoryDao: CategoryDao,
    private val paymentHistoryDao: PaymentHistoryDao,
    private val userPreferencesManager: UserPreferencesManager,
    private val gson: Gson
) : BillRepository {

    override fun getAllBills(): Flow<List<BillEntity>> = billDao.getAllBills()

    override fun getBillById(id: Long): Flow<BillEntity?> = billDao.getBillById(id)

    override suspend fun getBillByIdOneShot(id: Long): BillEntity? = billDao.getBillByIdOneShot(id)

    override fun getBillsByStatus(status: String): Flow<List<BillEntity>> = billDao.getBillsByStatus(status)

    override fun searchBills(query: String): Flow<List<BillEntity>> = billDao.searchBills(query)

    override fun getUpcomingCount(): Flow<Int> = billDao.getUpcomingCount()

    override fun getOverdueCount(): Flow<Int> = billDao.getOverdueCount()

    override fun getPaidCount(): Flow<Int> = billDao.getPaidCount()

    override suspend fun insertBill(bill: BillEntity): Long {
        val updatedStatus = calculateInitialStatus(bill)
        return billDao.insertBill(bill.copy(paymentStatus = updatedStatus, updatedAt = System.currentTimeMillis()))
    }

    override suspend fun updateBill(bill: BillEntity) {
        val updatedStatus = calculateInitialStatus(bill)
        billDao.updateBill(bill.copy(paymentStatus = updatedStatus, updatedAt = System.currentTimeMillis()))
    }

    private fun calculateInitialStatus(bill: BillEntity): String {
        if (bill.paymentStatus == "PAID") return "PAID"
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        return if (bill.dueDate < today) {
            "OVERDUE"
        } else {
            "UPCOMING"
        }
    }

    override suspend fun deleteBill(bill: BillEntity) {
        billDao.deleteBill(bill)
    }

    override suspend fun markBillAsPaid(bill: BillEntity) {
        // Record in payment history
        val payment = PaymentHistoryEntity(
            billId = bill.id,
            billName = bill.billName,
            category = bill.category,
            paidAmount = bill.amount,
            paidDate = System.currentTimeMillis()
        )
        paymentHistoryDao.insertPayment(payment)

        if (bill.recurringType == "None") {
            billDao.updateBill(bill.copy(paymentStatus = "PAID", updatedAt = System.currentTimeMillis()))
        } else {
            // Advance due date to next period
            val nextDueDate = getNextRecurringDate(bill.dueDate, bill.recurringType)
            val nextReminderDate = getNextRecurringDate(bill.reminderDate, bill.recurringType)
            billDao.updateBill(
                bill.copy(
                    dueDate = nextDueDate,
                    reminderDate = nextReminderDate,
                    paymentStatus = "UPCOMING",
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    private fun getNextRecurringDate(currentMillis: Long, recurringType: String): Long {
        val calendar = Calendar.getInstance().apply { timeInMillis = currentMillis }
        when (recurringType) {
            "Daily" -> calendar.add(Calendar.DAY_OF_YEAR, 1)
            "Weekly" -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            "Monthly" -> calendar.add(Calendar.MONTH, 1)
            "Yearly" -> calendar.add(Calendar.YEAR, 1)
        }
        return calendar.timeInMillis
    }

    override fun getAllCategories(): Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

    override suspend fun insertCategory(category: CategoryEntity): Long = categoryDao.insertCategory(category)

    override suspend fun deleteCategory(category: CategoryEntity) = categoryDao.deleteCategory(category)

    override fun getAllPaymentHistory(): Flow<List<PaymentHistoryEntity>> = paymentHistoryDao.getAllPaymentHistory()

    override suspend fun insertPayment(payment: PaymentHistoryEntity): Long = paymentHistoryDao.insertPayment(payment)

    override suspend fun exportBackupJson(): String {
        val bills = billDao.getAllBills().first()
        val categories = categoryDao.getAllCategories().first()
        val history = paymentHistoryDao.getAllPaymentHistory().first()

        val backupData = BackupData(
            bills = bills,
            categories = categories,
            paymentHistory = history
        )
        return gson.toJson(backupData)
    }

    override suspend fun importBackupJson(jsonString: String): Boolean {
        return try {
            val backupData = gson.fromJson(jsonString, BackupData::class.java) ?: return false
            if (backupData.bills.isNotEmpty()) {
                billDao.insertAllBills(backupData.bills)
            }
            if (backupData.categories.isNotEmpty()) {
                categoryDao.insertAllCategories(backupData.categories)
            }
            if (backupData.paymentHistory.isNotEmpty()) {
                paymentHistoryDao.insertAllPayments(backupData.paymentHistory)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun deleteAllData() {
        billDao.deleteAllBills()
        paymentHistoryDao.deleteAllPaymentHistory()
        userPreferencesManager.clearAll()
    }

    override val currencyFlow: Flow<String> = userPreferencesManager.currencyFlow
    override val themeFlow: Flow<String> = userPreferencesManager.themeFlow
    override val notificationEnabledFlow: Flow<Boolean> = userPreferencesManager.notificationEnabledFlow
    override val reminderDaysBeforeFlow: Flow<Int> = userPreferencesManager.reminderDaysBeforeFlow
    override val languageFlow: Flow<String> = userPreferencesManager.languageFlow

    override suspend fun setCurrency(currency: String) = userPreferencesManager.setCurrency(currency)
    override suspend fun setTheme(theme: String) = userPreferencesManager.setTheme(theme)
    override suspend fun setNotificationEnabled(enabled: Boolean) = userPreferencesManager.setNotificationEnabled(enabled)
    override suspend fun setReminderDaysBefore(days: Int) = userPreferencesManager.setReminderDaysBefore(days)
    override suspend fun setLanguage(language: String) = userPreferencesManager.setLanguage(language)
}
