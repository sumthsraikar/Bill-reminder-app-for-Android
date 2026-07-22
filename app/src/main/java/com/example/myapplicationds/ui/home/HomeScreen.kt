package com.example.myapplicationds.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplicationds.R
import com.example.myapplicationds.data.local.entity.BillEntity
import com.example.myapplicationds.ui.components.*
import com.example.myapplicationds.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToAddBill: () -> Unit,
    onNavigateToEditBill: (Long) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val currency by viewModel.currency.collectAsState()
    val bills by viewModel.bills.collectAsState()
    val upcomingCount by viewModel.upcomingCount.collectAsState()
    val overdueCount by viewModel.overdueCount.collectAsState()
    val paidCount by viewModel.paidCount.collectAsState()

    var billToDelete by remember { mutableStateOf<BillEntity?>(null) }

    Scaffold(
        topBar = {
            // Header with proper padding (24dp horizontal) & vertical alignment
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x26FFFFFF))
                            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.app_logo),
                            contentDescription = "BillBuddy Logo",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "BillBuddy",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                        Text(
                            text = "Smart Bill Manager",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryDark
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GlassIconButton(
                        icon = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        onClick = { },
                        tint = TextPrimaryDark
                    )

                    GlassIconButton(
                        icon = Icons.Default.Settings,
                        contentDescription = "Settings",
                        onClick = onNavigateToSettings,
                        tint = TextPrimaryDark
                    )
                }
            }
        },
        floatingActionButton = {
            // Raised FAB with Blue Glow (64dp circular) above bottom navigation
            Surface(
                onClick = onNavigateToAddBill,
                modifier = Modifier
                    .padding(bottom = 80.dp, end = 8.dp)
                    .size(64.dp)
                    .shadow(16.dp, CircleShape, spotColor = PrimaryBlue)
                    .clip(CircleShape)
                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)), CircleShape),
                color = PrimaryBlue
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Bill",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Glass Search Bar
            Box(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                GlassSearchTextField(
                    value = searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    placeholderText = "Search bills..."
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Redesigned Pill-style Tabs with Circular Count Badges
            val tabs = listOf(
                "Upcoming" to upcomingCount,
                "Overdue" to overdueCount,
                "Paid" to paidCount
            )

            Box(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
            ) {
                GlassPillTabs(
                    tabs = tabs,
                    selectedTabIndex = selectedTab,
                    onTabSelected = viewModel::onTabSelected
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bills List / Empty State
            if (bills.isEmpty()) {
                val (emptyTitle, emptySubtitle) = when {
                    searchQuery.isNotBlank() -> "No matching bills" to "Try searching with a different bill name or category"
                    selectedTab == 0 -> "No Upcoming Bills" to "You have zero pending bills due soon. Tap + to add one!"
                    selectedTab == 1 -> "No Overdue Bills" to "Awesome! You have paid all your bills on time."
                    else -> "No Paid Bills" to "Mark bills as paid to build your payment history record."
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(28.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x1F3B82F6))
                                    .border(BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.3f)), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (selectedTab == 1 && bills.isEmpty()) Icons.Default.CheckCircle else Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    modifier = Modifier.size(36.dp),
                                    tint = PrimaryBlue
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = emptyTitle,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = emptySubtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondaryDark,
                                modifier = Modifier.padding(horizontal = 12.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            GlassMarkPaidButton(
                                onClick = onNavigateToAddBill,
                                text = "+ Add Your First Bill"
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 4.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(bills, key = { it.id }) { bill ->
                        BillCardItem(
                            bill = bill,
                            currency = currency,
                            onMarkAsPaid = { viewModel.markAsPaid(bill) },
                            onEdit = { onNavigateToEditBill(bill.id) },
                            onDelete = { billToDelete = bill }
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog (Glass theme)
    billToDelete?.let { bill ->
        AlertDialog(
            onDismissRequest = { billToDelete = null },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "Delete Bill",
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete '${bill.billName}'? This action cannot be undone.",
                    color = TextSecondaryDark
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteBill(bill)
                        billToDelete = null
                    }
                ) {
                    Text("Delete", color = StatusOverdue, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { billToDelete = null }) {
                    Text("Cancel", color = TextSecondaryDark)
                }
            }
        )
    }
}

/**
 * Premium Black Glass Bill Card Container
 */
@Composable
fun BillCardItem(
    bill: BillEntity,
    currency: String,
    onMarkAsPaid: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val formattedDueDate = dateFormat.format(Date(bill.dueDate))
    val formattedReminderDate = dateFormat.format(Date(bill.reminderDate))

    // Days Left calculation
    val todayCal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val dueCal = Calendar.getInstance().apply {
        timeInMillis = bill.dueDate
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val diffDays = ((dueCal - todayCal) / (1000 * 60 * 60 * 24)).toInt()

    val (remainingBadgeText, remainingBadgeColor) = when {
        bill.paymentStatus == "PAID" -> "Paid" to TextSecondaryDark
        diffDays < 0 -> "Overdue by ${-diffDays} Day(s)" to StatusOverdue
        diffDays == 0 -> "Due Today" to StatusOverdue
        diffDays <= 7 -> "$diffDays Days Left" to StatusOverdue
        diffDays <= 14 -> "$diffDays Days Left" to StatusUpcoming
        else -> "$diffDays Days Left" to StatusPaid
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(20.dp)
    ) {
        // Header Section: Category Icon, Name (up to 2 lines wrapping), Category, Days Left Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Icon Container
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(bill.color).copy(alpha = 0.15f))
                        .border(
                            BorderStroke(1.dp, Color(bill.color).copy(alpha = 0.3f)),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getCategoryIconByName(bill.category, bill.icon),
                        contentDescription = bill.category,
                        tint = Color(bill.color),
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = bill.billName,
                        style = BillTitleStyle,
                        color = TextPrimaryDark,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${getCategoryEmoji(bill.category)} ${bill.category}",
                        style = BillCategoryStyle,
                        color = TextSecondaryDark
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Days Left Badge
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = remainingBadgeColor.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, remainingBadgeColor.copy(alpha = 0.3f))
            ) {
                Text(
                    text = remainingBadgeText,
                    style = MaterialTheme.typography.labelSmall,
                    color = remainingBadgeColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Prominent Amount Section (34sp Extra Bold White)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "BILL AMOUNT",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMutedDark,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "$currency${String.format(Locale.getDefault(), "%.2f", bill.amount)}",
                    style = BillAmountStyle,
                    color = TextPrimaryDark
                )
            }

            // Recurring Frequency Badge
            if (bill.recurringType != "None") {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x1F3B82F6),
                    border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.3f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Repeat,
                            contentDescription = null,
                            tint = PrimaryBlueLight,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = bill.recurringType,
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryBlueLight,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
        Spacer(modifier = Modifier.height(14.dp))

        // Metadata Info Section: Due Date & Reminder Details
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = TextSecondaryDark,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Due: $formattedDueDate",
                    style = BillDueDateStyle,
                    color = TextSecondaryDark
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = PrimaryBlueLight,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Reminder: $formattedReminderDate • ${bill.reminderTime}",
                    style = BillReminderStyle,
                    color = TextMutedDark
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (bill.paymentStatus != "PAID") {
                GlassMarkPaidButton(
                    onClick = onMarkAsPaid,
                    text = "✓ Mark as Paid",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GlassIconButton(
                    icon = Icons.Default.Edit,
                    contentDescription = "Edit Bill",
                    onClick = onEdit,
                    tint = TextPrimaryDark
                )

                GlassIconButton(
                    icon = Icons.Default.Delete,
                    contentDescription = "Delete Bill",
                    onClick = onDelete,
                    tint = StatusOverdue
                )
            }
        }
    }
}

