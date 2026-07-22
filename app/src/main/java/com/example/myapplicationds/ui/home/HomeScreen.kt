package com.example.myapplicationds.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
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

    val todayMillis = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    // Summary calculations
    val upcomingTotal = remember(bills) {
        bills.filter { it.paymentStatus != "PAID" && it.dueDate >= todayMillis }.sumOf { it.amount }
    }
    val overdueTotal = remember(bills) {
        bills.filter { it.paymentStatus != "PAID" && it.dueDate < todayMillis }.sumOf { it.amount }
    }
    val paidTotal = remember(bills) {
        bills.filter { it.paymentStatus == "PAID" }.sumOf { it.amount }
    }

    Scaffold(
        topBar = {
            // Header with 48dp action buttons & 12dp spacing
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
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
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
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
            // Raised FAB positioned 24–32dp above bottom navigation bar (96dp bottom margin)
            Surface(
                onClick = onNavigateToAddBill,
                modifier = Modifier
                    .padding(bottom = 96.dp, end = 8.dp)
                    .size(64.dp)
                    .shadow(12.dp, CircleShape, spotColor = PrimaryBlue)
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
                        modifier = Modifier.size(30.dp)
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
            // Glass Search Bar (56dp height)
            Box(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
            ) {
                GlassSearchTextField(
                    value = searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    placeholderText = "Search bills..."
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Home Summary Cards Component (3 equal glass cards above tabs)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Upcoming Glass Card
                GlassCard(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = StatusUpcoming, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Upcoming", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$currency${String.format(Locale.getDefault(), "%.0f", upcomingTotal)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Overdue Glass Card
                GlassCard(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = StatusOverdue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Overdue", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$currency${String.format(Locale.getDefault(), "%.0f", overdueTotal)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (overdueTotal > 0) StatusOverdue else TextPrimaryDark,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Paid Glass Card
                GlassCard(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusPaid, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Paid", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$currency${String.format(Locale.getDefault(), "%.0f", paidTotal)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Redesigned Pill-style Tabs with Circular Count Badges
            val tabs = listOf(
                "Upcoming" to upcomingCount,
                "Overdue" to overdueCount,
                "Paid" to paidCount
            )

            Box(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 2.dp)
            ) {
                GlassPillTabs(
                    tabs = tabs,
                    selectedTabIndex = selectedTab,
                    onTabSelected = viewModel::onTabSelected
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bills List / Enhanced Empty State
            if (bills.isEmpty()) {
                val (emptyTitle, emptySubtitle, emptyIcon) = when {
                    searchQuery.isNotBlank() -> Triple("No matching bills", "Try searching with a different bill name or category", Icons.Default.Search)
                    selectedTab == 0 -> Triple("No Upcoming Bills", "You have zero pending bills due soon. Tap + to add one!", Icons.Default.ReceiptLong)
                    selectedTab == 1 -> Triple("No Overdue Bills", "Awesome! You have paid all your bills on time.", Icons.Default.CheckCircle)
                    else -> Triple("No Paid Bills", "Mark bills as paid to build your payment history record.", Icons.Default.Payments)
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(24.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x1F3B82F6))
                                    .border(BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.3f)), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = emptyIcon,
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp),
                                    tint = PrimaryBlue
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = emptyTitle,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = emptySubtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondaryDark,
                                modifier = Modifier.padding(horizontal = 12.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            GlassMarkPaidButton(
                                onClick = onNavigateToAddBill,
                                text = "+ Add Bill"
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 4.dp, bottom = 140.dp), // 140dp bottom padding ensures no card is hidden
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(bills, key = { it.id }) { bill ->
                        // Swipe to dismiss box (Swipe Right -> Mark Paid, Swipe Left -> Delete)
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                when (value) {
                                    SwipeToDismissBoxValue.StartToEnd -> {
                                        if (bill.paymentStatus != "PAID") {
                                            viewModel.markAsPaid(bill)
                                        }
                                        false
                                    }
                                    SwipeToDismissBoxValue.EndToStart -> {
                                        billToDelete = bill
                                        false
                                    }
                                    else -> false
                                }
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                val bg = when (dismissState.dismissDirection) {
                                    SwipeToDismissBoxValue.StartToEnd -> StatusPaid.copy(alpha = 0.25f)
                                    SwipeToDismissBoxValue.EndToStart -> StatusOverdue.copy(alpha = 0.25f)
                                    else -> Color.Transparent
                                }
                                val icon = when (dismissState.dismissDirection) {
                                    SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Check
                                    SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
                                    else -> null
                                }
                                val align = when (dismissState.dismissDirection) {
                                    SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                                    SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                                    else -> Alignment.Center
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(bg)
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = align
                                ) {
                                    icon?.let {
                                        Icon(imageVector = it, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                                    }
                                }
                            }
                        ) {
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
 * Optimized Black Glass Bill Card Container (10-15% reduced height, 28sp amount, 3-line title, glass reminder chip)
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

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) 0.98f else 1.0f, animationSpec = tween(150), label = "cardScale")

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
        diffDays < 0 -> "Overdue by ${-diffDays}d" to StatusOverdue
        diffDays == 0 -> "Due Today" to StatusOverdue
        diffDays <= 7 -> "$diffDays Days Left" to StatusOverdue
        diffDays <= 14 -> "$diffDays Days Left" to StatusUpcoming
        else -> "$diffDays Days Left" to StatusPaid
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        contentPadding = PaddingValues(16.dp) // Optimized 10-15% reduced padding
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Header Row: Category Icon, Title (up to 3 lines), Category text (no emoji), Days Left badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Vector Icon Container
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(bill.color).copy(alpha = 0.15f))
                            .border(
                                BorderStroke(1.dp, Color(bill.color).copy(alpha = 0.3f)),
                                RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getCategoryIconByName(bill.category, bill.icon),
                            contentDescription = bill.category,
                            tint = Color(bill.color),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = bill.billName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        // Display clean monochrome category name without emoji
                        Text(
                            text = bill.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryDark
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Days Left Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = remainingBadgeColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, remainingBadgeColor.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = remainingBadgeText,
                        style = MaterialTheme.typography.labelSmall,
                        color = remainingBadgeColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Amount Section with Credited (+) vs Debited (-) styling
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val isCredit = bill.transactionType == "CREDIT"
                    Text(
                        text = "${if (isCredit) "+" else "-"}$currency${String.format(Locale.getDefault(), "%.2f", bill.amount)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = if (isCredit) StatusPaid else TextPrimaryDark
                    )

                    if (isCredit) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = StatusPaid.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, StatusPaid.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "Credited",
                                style = MaterialTheme.typography.labelSmall,
                                color = StatusPaid,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Frequency Chip beside amount / due date
                if (bill.recurringType != "None") {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0x1F3B82F6),
                        border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.3f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Repeat,
                                contentDescription = null,
                                tint = PrimaryBlueLight,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = bill.recurringType,
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryBlueLight,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }


            // Due Date & Reminder Row in Premium Glass Chip
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0x18FFFFFF),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Due Date
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = TextSecondaryDark,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Due: $formattedDueDate",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryDark,
                            fontSize = 12.sp
                        )
                    }

                    // Glass Reminder Chip
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = PrimaryBlueLight,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Reminder • $formattedReminderDate • ${bill.reminderTime}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedDark,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.06f), modifier = Modifier.padding(vertical = 2.dp))

            // Action Buttons Row (Mark Paid + Edit/Delete circular buttons)
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
}


