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

    private val _selectedTab = MutableStateFlow(0) // 0: Debited, 1: Credited, 2: Upcoming
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    val currency: StateFlow<String> = repository.currencyFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "₹")

    private val allBills = repository.getAllBills()

    val debitedCount: StateFlow<Int> = allBills.map { list ->
        list.count { it.transactionType != "CREDIT" }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val creditedCount: StateFlow<Int> = allBills.map { list ->
        list.count { it.transactionType == "CREDIT" }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val upcomingCount: StateFlow<Int> = allBills.map { list ->
        list.count { it.paymentStatus == "UPCOMING" }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val bills: StateFlow<List<BillEntity>> = combine(
        allBills,
        _selectedTab,
        _searchQuery
    ) { list, tabIndex, query ->
        val filteredByType = when (tabIndex) {
            0 -> list.filter { it.transactionType != "CREDIT" }
            1 -> list.filter { it.transactionType == "CREDIT" }
            2 -> list.filter { it.paymentStatus == "UPCOMING" }
            else -> list
        }

        if (query.isBlank()) {
            filteredByType
        } else {
            filteredByType.filter {
                it.billName.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true)
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
