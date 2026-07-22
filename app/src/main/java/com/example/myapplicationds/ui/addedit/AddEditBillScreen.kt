package com.example.myapplicationds.ui.addedit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplicationds.ui.components.GlassCard
import com.example.myapplicationds.ui.components.getCategoryIconByName
import com.example.myapplicationds.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditBillScreen(
    viewModel: AddEditBillViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val currency by viewModel.currency.collectAsState()
    val context = LocalContext.current

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    var categoryExpanded by remember { mutableStateOf(false) }
    var repeatExpanded by remember { mutableStateOf(false) }
    var showNewCategoryDialog by remember { mutableStateOf(false) }

    val repeatOptions = listOf("None", "Daily", "Weekly", "Monthly", "Yearly")
    val availableIcons = listOf("DirectionsCar", "ElectricBolt", "Smartphone", "Wifi", "AccountBalance", "Subscriptions", "CreditCard", "Home", "WaterDrop", "Tv", "Shield", "School", "ShoppingCart")

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimaryDark)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (uiState.isEditing) "Edit Bill" else "Add New Bill",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
            }
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            if (uiState.errorMessage != null) {
                Surface(
                    color = StatusOverdue.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, StatusOverdue),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = StatusOverdue,
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Transaction Type Selector (Debited vs Credited)
                    Text(
                        text = "Transaction Type",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Debited (Expense)
                        val isDebit = uiState.transactionType == "DEBIT"
                        FilterChip(
                            selected = isDebit,
                            onClick = { viewModel.onTransactionTypeChange("DEBIT") },
                            label = { Text("📤 Debited (Expense)", fontWeight = FontWeight.Bold, color = if (isDebit) Color.White else TextSecondaryDark) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StatusOverdue,
                                containerColor = Color(0x1F22222E)
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        // Credited (Income)
                        val isCredit = uiState.transactionType == "CREDIT"
                        FilterChip(
                            selected = isCredit,
                            onClick = { viewModel.onTransactionTypeChange("CREDIT") },
                            label = { Text("📥 Credited (Income)", fontWeight = FontWeight.Bold, color = if (isCredit) Color.White else TextSecondaryDark) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StatusPaid,
                                containerColor = Color(0x1F22222E)
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Bill Name
                    OutlinedTextField(
                        value = uiState.billName,
                        onValueChange = viewModel::onNameChange,
                        label = { Text(if (uiState.transactionType == "CREDIT") "Title / Source *" else "Bill Name *", color = TextSecondaryDark) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        ),
                        leadingIcon = { Icon(Icons.Default.Receipt, contentDescription = null, tint = PrimaryBlueLight) }
                    )


                    // Amount
                    OutlinedTextField(
                        value = uiState.amount,
                        onValueChange = viewModel::onAmountChange,
                        label = { Text("Amount ($currency) *", color = TextSecondaryDark) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        ),
                        leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null, tint = PrimaryBlueLight) }
                    )

                    // Category Dropdown
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded }
                    ) {
                        OutlinedTextField(
                            value = uiState.category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category", color = TextSecondaryDark) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark
                            ),
                            leadingIcon = { Icon(Icons.Default.Category, contentDescription = null, tint = PrimaryBlueLight) }
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false },
                            modifier = Modifier.background(DarkSurface)
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.categoryName, color = TextPrimaryDark) },
                                    onClick = {
                                        viewModel.onCategoryChange(cat.categoryName)
                                        viewModel.onIconChange(cat.icon)
                                        viewModel.onColorChange(cat.color)
                                        categoryExpanded = false
                                    }
                                )
                            }
                            HorizontalDivider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 4.dp))
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = PrimaryBlueLight, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Add New Category...", color = PrimaryBlueLight, fontWeight = FontWeight.Bold)
                                    }
                                },
                                onClick = {
                                    categoryExpanded = false
                                    showNewCategoryDialog = true
                                }
                            )
                        }
                    }

                    // Due Date Picker
                    OutlinedTextField(
                        value = dateFormat.format(Date(uiState.dueDate)),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Due Date", color = TextSecondaryDark) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        ),
                        trailingIcon = {
                            IconButton(onClick = {
                                val cal = Calendar.getInstance().apply { timeInMillis = uiState.dueDate }
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        val selectedCal = Calendar.getInstance().apply {
                                            set(year, month, dayOfMonth)
                                        }
                                        viewModel.onDueDateChange(selectedCal.timeInMillis)
                                    },
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Pick Due Date", tint = PrimaryBlueLight)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val cal = Calendar.getInstance().apply { timeInMillis = uiState.dueDate }
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        val selectedCal = Calendar.getInstance().apply {
                                            set(year, month, dayOfMonth)
                                        }
                                        viewModel.onDueDateChange(selectedCal.timeInMillis)
                                    },
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            },
                        shape = RoundedCornerShape(16.dp)
                    )

                    // Reminder Date & Time Pickers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = dateFormat.format(Date(uiState.reminderDate)),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Reminder Date", color = TextSecondaryDark) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark
                            ),
                            trailingIcon = {
                                IconButton(onClick = {
                                    val cal = Calendar.getInstance().apply { timeInMillis = uiState.reminderDate }
                                    DatePickerDialog(
                                        context,
                                        { _, year, month, dayOfMonth ->
                                            val selectedCal = Calendar.getInstance().apply {
                                                set(year, month, dayOfMonth)
                                            }
                                            viewModel.onReminderDateChange(selectedCal.timeInMillis)
                                        },
                                        cal.get(Calendar.YEAR),
                                        cal.get(Calendar.MONTH),
                                        cal.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                }) {
                                    Icon(Icons.Default.Notifications, contentDescription = null, tint = PrimaryBlueLight)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp)
                        )

                        OutlinedTextField(
                            value = uiState.reminderTime,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Time", color = TextSecondaryDark) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark
                            ),
                            trailingIcon = {
                                IconButton(onClick = {
                                    val parts = uiState.reminderTime.split(":")
                                    val hour = parts.getOrNull(0)?.toIntOrNull() ?: 9
                                    val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

                                    TimePickerDialog(
                                        context,
                                        { _, selectedHour, selectedMinute ->
                                            val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
                                            viewModel.onReminderTimeChange(formattedTime)
                                        },
                                        hour,
                                        minute,
                                        true
                                    ).show()
                                }) {
                                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = PrimaryBlueLight)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }

                    // Repeat Dropdown
                    ExposedDropdownMenuBox(
                        expanded = repeatExpanded,
                        onExpandedChange = { repeatExpanded = !repeatExpanded }
                    ) {
                        OutlinedTextField(
                            value = uiState.recurringType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Repeat Frequency", color = TextSecondaryDark) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = repeatExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark
                            ),
                            leadingIcon = { Icon(Icons.Default.Repeat, contentDescription = null, tint = PrimaryBlueLight) }
                        )
                        ExposedDropdownMenu(
                            expanded = repeatExpanded,
                            onDismissRequest = { repeatExpanded = false },
                            modifier = Modifier.background(DarkSurface)
                        ) {
                            repeatOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, color = TextPrimaryDark) },
                                    onClick = {
                                        viewModel.onRecurringTypeChange(option)
                                        repeatExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Notes
                    OutlinedTextField(
                        value = uiState.notes,
                        onValueChange = viewModel::onNotesChange,
                        label = { Text("Notes (Optional)", color = TextSecondaryDark) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        minLines = 3,
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        ),
                        leadingIcon = { Icon(Icons.Default.Note, contentDescription = null, tint = PrimaryBlueLight) }
                    )
                }
            }

            // Category Icons & Color Selector Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "Category Icon",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(availableIcons) { iconName ->
                            val isSelected = uiState.icon == iconName
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (isSelected) PrimaryBlue else Color(0x1F22222E)
                                    )
                                    .border(
                                        BorderStroke(1.dp, if (isSelected) PrimaryBlueLight else Color.White.copy(alpha = 0.08f)),
                                        RoundedCornerShape(14.dp)
                                    )
                                    .clickable { viewModel.onIconChange(iconName) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getCategoryIconByName(iconName, iconName),
                                    contentDescription = iconName,
                                    tint = if (isSelected) Color.White else TextSecondaryDark,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Color Theme",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(CategoryColors) { colorHex ->
                            val isSelected = uiState.color == colorHex
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(colorHex))
                                    .border(
                                        width = if (isSelected) 3.dp else 0.dp,
                                        color = if (isSelected) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { viewModel.onColorChange(colorHex) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                ) {
                    Text("Cancel", fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                }

                Button(
                    onClick = viewModel::saveBill,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue
                    )
                ) {
                    Text("Save Bill", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }

        if (showNewCategoryDialog) {
            NewCategoryDialog(
                availableIcons = availableIcons,
                onDismiss = { showNewCategoryDialog = false },
                onConfirm = { name, icon, color ->
                    viewModel.addNewCategory(name, icon, color)
                    showNewCategoryDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCategoryDialog(
    availableIcons: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Long) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf(availableIcons.first()) }
    var selectedColor by remember { mutableStateOf(CategoryColors.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(24.dp),
        title = { Text("New Category", color = TextPrimaryDark, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Category Name", color = TextSecondaryDark) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    )
                )

                Text("Select Icon", style = MaterialTheme.typography.titleSmall, color = TextPrimaryDark)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(availableIcons) { icon ->
                        val isSelected = selectedIcon == icon
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PrimaryBlue else Color(0x1F22222E))
                                .clickable { selectedIcon = icon },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getCategoryIconByName(icon, icon),
                                contentDescription = null,
                                tint = if (isSelected) Color.White else TextSecondaryDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Text("Select Color", style = MaterialTheme.typography.titleSmall, color = TextPrimaryDark)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(CategoryColors) { color ->
                        val isSelected = selectedColor == color
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(color))
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = if (isSelected) Color.White else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = color },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name, selectedIcon, selectedColor) },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Add", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondaryDark)
            }
        }
    )
}

