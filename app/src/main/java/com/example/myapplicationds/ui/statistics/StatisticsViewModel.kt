package com.example.myapplicationds.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplicationds.data.local.entity.BillEntity
import com.example.myapplicationds.data.local.entity.PaymentHistoryEntity
import com.example.myapplicationds.data.repository.BillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.util.Calendar
import javax.inject.Inject

data class CategoryExpense(
    val categoryName: String,
    val totalAmount: Double,
    val color: Long
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: BillRepository
) : ViewModel() {

    val currency: StateFlow<String> = repository.currencyFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "₹")

    val allBills: StateFlow<List<BillEntity>> = repository.getAllBills()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val paymentHistory: StateFlow<List<PaymentHistoryEntity>> = repository.getAllPaymentHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalBillsCount: StateFlow<Int> = allBills.map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val paidBillsCount: StateFlow<Int> = allBills.map { list -> list.count { it.paymentStatus == "PAID" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val upcomingBillsCount: StateFlow<Int> = allBills.map { list -> list.count { it.paymentStatus == "UPCOMING" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val overdueBillsCount: StateFlow<Int> = allBills.map { list -> list.count { it.paymentStatus == "OVERDUE" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCredited: StateFlow<Double> = allBills.map { list ->
        list.filter { it.transactionType == "CREDIT" }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalDebited: StateFlow<Double> = allBills.map { list ->
        list.filter { it.transactionType == "DEBIT" }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val monthlySpending: StateFlow<Double> = paymentHistory.map { history ->
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        history.filter { payment ->
            val cal = Calendar.getInstance().apply { timeInMillis = payment.paidDate }
            cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear
        }.sumOf { it.paidAmount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val categoryExpenses: StateFlow<List<CategoryExpense>> = combine(allBills, paymentHistory) { bills, history ->
        val map = mutableMapOf<String, Double>()
        val colorMap = mutableMapOf<String, Long>()

        bills.forEach { bill ->
            colorMap[bill.category] = bill.color
        }

        history.forEach { item ->
            val cat = if (item.category.isNotBlank()) item.category else "Other"
            map[cat] = (map[cat] ?: 0.0) + item.paidAmount
        }

        // Include upcoming bills in breakdown if history is light
        if (map.isEmpty()) {
            bills.forEach { bill ->
                map[bill.category] = (map[bill.category] ?: 0.0) + bill.amount
            }
        }

        map.map { (cat, amount) ->
            CategoryExpense(
                categoryName = cat,
                totalAmount = amount,
                color = colorMap[cat] ?: 0xFF2563EBL
            )
        }.sortedByDescending { it.totalAmount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
