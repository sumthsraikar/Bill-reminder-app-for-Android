package com.example.myapplicationds.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplicationds.data.local.entity.BillEntity
import com.example.myapplicationds.data.repository.BillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: BillRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedTab = MutableStateFlow(0) // 0: Upcoming, 1: Overdue, 2: Paid
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    val currency: StateFlow<String> = repository.currencyFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "₹")

    val upcomingCount: StateFlow<Int> = repository.getUpcomingCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val overdueCount: StateFlow<Int> = repository.getOverdueCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val paidCount: StateFlow<Int> = repository.getPaidCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val bills: StateFlow<List<BillEntity>> = combine(
        _selectedTab,
        _searchQuery
    ) { tabIndex, query ->
        val status = when (tabIndex) {
            0 -> "UPCOMING"
            1 -> "OVERDUE"
            2 -> "PAID"
            else -> "UPCOMING"
        }
        Pair(status, query)
    }.flatMapLatest { (status, query) ->
        if (query.isBlank()) {
            repository.getBillsByStatus(status)
        } else {
            repository.searchBills(query).map { list ->
                list.filter { it.paymentStatus == status }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onTabSelected(index: Int) {
        _selectedTab.value = index
    }

    fun markAsPaid(bill: BillEntity) {
        viewModelScope.launch {
            repository.markBillAsPaid(bill)
        }
    }

    fun deleteBill(bill: BillEntity) {
        viewModelScope.launch {
            repository.deleteBill(bill)
        }
    }
}
