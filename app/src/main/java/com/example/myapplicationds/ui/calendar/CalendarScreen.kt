package com.example.myapplicationds.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplicationds.ui.components.GlassCard
import com.example.myapplicationds.ui.components.GlassIconButton
import com.example.myapplicationds.ui.home.BillCardItem
import com.example.myapplicationds.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    onNavigateToEditBill: (Long) -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    val currentMonthCal by viewModel.currentMonthCal.collectAsState()
    val selectedDateMillis by viewModel.selectedDateMillis.collectAsState()
    val allBills by viewModel.allBills.collectAsState()
    val billsForSelectedDate by viewModel.billsForSelectedDate.collectAsState()
    val currency by viewModel.currency.collectAsState()

    val monthYearFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }
    val dayFormat = remember { SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()) }

    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    // Calculate calendar grid days
    val calendarDays = remember(currentMonthCal) {
        val cal = currentMonthCal.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val daysList = mutableListOf<CalendarDay?>()
        repeat(firstDayOfWeek) {
            daysList.add(null)
        }
        for (day in 1..maxDays) {
            val dayCal = cal.clone() as Calendar
            dayCal.set(Calendar.DAY_OF_MONTH, day)
            daysList.add(CalendarDay(day, dayCal.timeInMillis))
        }
        daysList
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassIconButton(
                    icon = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    onClick = onNavigateBack,
                    tint = TextPrimaryDark
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Bill Calendar",
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
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            // Month Header Controls inside Glass Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlassIconButton(
                        icon = Icons.Default.ChevronLeft,
                        contentDescription = "Previous Month",
                        onClick = viewModel::onPreviousMonth,
                        tint = TextPrimaryDark
                    )

                    Text(
                        text = monthYearFormat.format(currentMonthCal.time),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )

                    GlassIconButton(
                        icon = Icons.Default.ChevronRight,
                        contentDescription = "Next Month",
                        onClick = viewModel::onNextMonth,
                        tint = TextPrimaryDark
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Day Header Labels (Sun - Sat)
                Row(modifier = Modifier.fillMaxWidth()) {
                    daysOfWeek.forEach { dayLabel ->
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondaryDark,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Days Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.height(260.dp),
                    userScrollEnabled = false
                ) {
                    items(calendarDays) { day ->
                        if (day == null) {
                            Box(modifier = Modifier.size(36.dp))
                        } else {
                            val isSelected = isSameDay(day.millis, selectedDateMillis)
                            val dayBills = allBills.filter { isSameDay(it.dueDate, day.millis) }

                            val hasPaid = dayBills.any { it.paymentStatus == "PAID" }
                            val hasOverdue = dayBills.any { it.paymentStatus == "OVERDUE" }
                            val hasUpcoming = dayBills.any { it.paymentStatus == "UPCOMING" }

                            Column(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) PrimaryBlue else Color.Transparent
                                    )
                                    .clickable { viewModel.onDateSelected(day.millis) },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = day.dayNumber.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else TextPrimaryDark
                                )

                                // Indicator dots
                                if (dayBills.isNotEmpty()) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        if (hasOverdue) Dot(StatusOverdue)
                                        if (hasUpcoming) Dot(StatusUpcoming)
                                        if (hasPaid) Dot(StatusPaid)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Selected Date Bills Header & List
            Text(
                text = dayFormat.format(Date(selectedDateMillis)),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (billsForSelectedDate.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No bills due on this date",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryDark
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 120.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(billsForSelectedDate, key = { it.id }) { bill ->
                        BillCardItem(
                            bill = bill,
                            currency = currency,
                            onMarkAsPaid = { viewModel.markAsPaid(bill) },
                            onEdit = { onNavigateToEditBill(bill.id) },
                            onDelete = {}
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Dot(color: Color) {
    Box(
        modifier = Modifier
            .size(5.dp)
            .clip(CircleShape)
            .background(color)
    )
}

data class CalendarDay(val dayNumber: Int, val millis: Long)

fun isSameDay(millis1: Long, millis2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = millis1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = millis2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

