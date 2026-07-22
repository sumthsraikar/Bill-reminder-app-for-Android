package com.example.myapplicationds.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplicationds.data.local.entity.BillEntity
import com.example.myapplicationds.data.repository.BillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: BillRepository
) : ViewModel() {

    private val _currentMonthCal = MutableStateFlow(Calendar.getInstance())
    val currentMonthCal: StateFlow<Calendar> = _currentMonthCal.asStateFlow()

    private val _selectedDateMillis = MutableStateFlow(System.currentTimeMillis())
    val selectedDateMillis: StateFlow<Long> = _selectedDateMillis.asStateFlow()

    val currency: StateFlow<String> = repository.currencyFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "₹")

    val allBills: StateFlow<List<BillEntity>> = repository.getAllBills()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val billsForSelectedDate: StateFlow<List<BillEntity>> = combine(allBills, _selectedDateMillis) { bills, selMillis ->
        val selCal = Calendar.getInstance().apply { timeInMillis = selMillis }
        bills.filter { bill ->
            val billCal = Calendar.getInstance().apply { timeInMillis = bill.dueDate }
            billCal.get(Calendar.YEAR) == selCal.get(Calendar.YEAR) &&
                    billCal.get(Calendar.DAY_OF_YEAR) == selCal.get(Calendar.DAY_OF_YEAR)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onPreviousMonth() {
        val newCal = (_currentMonthCal.value.clone() as Calendar).apply {
            add(Calendar.MONTH, -1)
        }
        _currentMonthCal.value = newCal
    }

    fun onNextMonth() {
        val newCal = (_currentMonthCal.value.clone() as Calendar).apply {
            add(Calendar.MONTH, 1)
        }
        _currentMonthCal.value = newCal
    }

    fun onDateSelected(millis: Long) {
        _selectedDateMillis.value = millis
    }

    fun markAsPaid(bill: BillEntity) {
        viewModelScope.launch {
            repository.markBillAsPaid(bill)
        }
    }
}
