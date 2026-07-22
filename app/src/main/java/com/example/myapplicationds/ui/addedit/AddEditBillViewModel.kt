package com.example.myapplicationds.ui.addedit

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplicationds.data.local.entity.BillEntity
import com.example.myapplicationds.data.local.entity.CategoryEntity
import com.example.myapplicationds.data.repository.BillRepository
import com.example.myapplicationds.notification.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class AddEditUiState(
    val billId: Long = -1L,
    val billName: String = "",
    val amount: String = "",
    val category: String = "Utilities",
    val dueDate: Long = System.currentTimeMillis() + 86400000L, // Tomorrow
    val reminderDate: Long = System.currentTimeMillis(),
    val reminderTime: String = "09:00",
    val recurringType: String = "None",
    val color: Long = 0xFF2563EBL,
    val icon: String = "Bolt",
    val notes: String = "",
    val isEditing: Boolean = false,
    val errorMessage: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class AddEditBillViewModel @Inject constructor(
    private val repository: BillRepository,
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val navBillId: Long = savedStateHandle.get<String>("billId")?.toLongOrNull() ?: -1L

    private val _uiState = MutableStateFlow(AddEditUiState())
    val uiState: StateFlow<AddEditUiState> = _uiState.asStateFlow()

    val categories: StateFlow<List<CategoryEntity>> = repository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currency: StateFlow<String> = repository.currencyFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "₹")

    init {
        if (navBillId != -1L) {
            viewModelScope.launch {
                val bill = repository.getBillByIdOneShot(navBillId)
                if (bill != null) {
                    _uiState.value = AddEditUiState(
                        billId = bill.id,
                        billName = bill.billName,
                        amount = bill.amount.toString(),
                        category = bill.category,
                        dueDate = bill.dueDate,
                        reminderDate = bill.reminderDate,
                        reminderTime = bill.reminderTime,
                        recurringType = bill.recurringType,
                        color = bill.color,
                        icon = bill.icon,
                        notes = bill.notes,
                        isEditing = true
                    )
                }
            }
        }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(billName = name, errorMessage = null) }
    }

    fun onAmountChange(amount: String) {
        _uiState.update { it.copy(amount = amount, errorMessage = null) }
    }

    fun onCategoryChange(category: String) {
        _uiState.update { it.copy(category = category) }
    }

    fun onDueDateChange(millis: Long) {
        _uiState.update { it.copy(dueDate = millis) }
    }

    fun onReminderDateChange(millis: Long) {
        _uiState.update { it.copy(reminderDate = millis) }
    }

    fun onReminderTimeChange(time: String) {
        _uiState.update { it.copy(reminderTime = time) }
    }

    fun onRecurringTypeChange(type: String) {
        _uiState.update { it.copy(recurringType = type) }
    }

    fun onColorChange(color: Long) {
        _uiState.update { it.copy(color = color) }
    }

    fun onIconChange(icon: String) {
        _uiState.update { it.copy(icon = icon) }
    }

    fun onNotesChange(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun addNewCategory(name: String, icon: String, color: Long) {
        viewModelScope.launch {
            val newCategory = CategoryEntity(
                categoryName = name,
                icon = icon,
                color = color
            )
            repository.insertCategory(newCategory)
            // Select the newly created category
            onCategoryChange(name)
            onIconChange(icon)
            onColorChange(color)
        }
    }

    fun saveBill() {
        val state = _uiState.value
        val name = state.billName.trim()
        val amountValue = state.amount.toDoubleOrNull()

        if (name.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Please enter a bill name") }
            return
        }

        if (amountValue == null || amountValue <= 0.0) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid amount") }
            return
        }

        viewModelScope.launch {
            val curr = currency.value
            val bill = BillEntity(
                id = if (state.isEditing) state.billId else 0,
                billName = name,
                category = state.category,
                amount = amountValue,
                dueDate = state.dueDate,
                reminderDate = state.reminderDate,
                reminderTime = state.reminderTime,
                recurringType = state.recurringType,
                color = state.color,
                icon = state.icon,
                notes = state.notes
            )

            val savedId = if (state.isEditing) {
                repository.updateBill(bill)
                bill.id
            } else {
                repository.insertBill(bill)
            }

            // Schedule notification
            val finalBill = bill.copy(id = savedId)
            AlarmScheduler.scheduleBillReminders(context, finalBill, curr)

            _uiState.update { it.copy(isSaved = true) }
        }
    }
}
