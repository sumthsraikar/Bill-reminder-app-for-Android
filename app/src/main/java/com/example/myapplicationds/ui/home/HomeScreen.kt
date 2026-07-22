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
    onNavigateToCalendar: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
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
            // Top Header: "My Bills" + Analytics 📊 & Settings ⚙️
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "My Bills",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark,
                    fontSize = 26.sp
                )

                // Top Right Action Buttons (Analytics & Settings)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GlassIconButton(
                        icon = Icons.Default.BarChart,
                        contentDescription = "Analytics",
                        onClick = onNavigateToAnalytics,
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
            // Pill Floating Action Button (+ New Bill) matching reference screenshot
            Surface(
                onClick = onNavigateToAddBill,
                modifier = Modifier
                    .padding(bottom = 24.dp, end = 16.dp)
                    .height(48.dp)
                    .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = PrimaryBlue)
                    .clip(RoundedCornerShape(24.dp)),
                color = PrimaryBlue,
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Bill",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "New Bill",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 15.sp
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
            // Unified Dark Summary Container matching reference screenshot
            val currentMonthName = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date()) }
            
            Box(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF16161C),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Summary Container Header: Calendar Icon + Month String + "View >" Link
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = PrimaryBlueLight,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = currentMonthName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark,
                                    fontSize = 15.sp
                                )
                            }

                            Text(
                                text = "View ›",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlueLight,
                                fontSize = 13.sp,
                                modifier = Modifier.clickable(onClick = onNavigateToCalendar)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 3 Summary Grid Cards (Still to Pay, Upcoming, Overdue)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Still to Pay Card (Blue Container)
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF162033),
                                border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.2f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Still to Pay",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondaryDark,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "$currency${String.format(Locale.getDefault(), "%,.0f", upcomingTotal + overdueTotal)}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryBlueLight,
                                        fontSize = 17.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // Upcoming Card (Dark Container)
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF141922),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Upcoming ($upcomingCount)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondaryDark,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "$currency${String.format(Locale.getDefault(), "%,.0f", upcomingTotal)}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF60A5FA),
                                        fontSize = 17.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // Overdue Card (Reddish Container)
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF261619),
                                border = BorderStroke(1.dp, StatusOverdue.copy(alpha = 0.2f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Overdue ($overdueCount)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondaryDark,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "$currency${String.format(Locale.getDefault(), "%,.0f", overdueTotal)}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (overdueTotal > 0) StatusOverdue else Color(0xFFEF4444).copy(alpha = 0.7f),
                                        fontSize = 17.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Glass Search Bar
            Box(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp)
            ) {
                GlassSearchTextField(
                    value = searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    placeholderText = "Search bills..."
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Tabs with Circular Count Badges
            val tabs = listOf(
                "Upcoming" to upcomingCount,
                "Overdue" to overdueCount,
                "Paid" to paidCount
            )

            Box(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp)
            ) {
                GlassPillTabs(
                    tabs = tabs,
                    selectedTabIndex = selectedTab,
                    onTabSelected = viewModel::onTabSelected
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

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

    Surface(
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
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF16161C),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Row 1: Circular Category Icon + Title/Category + Translucent "X days left" Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Circular Category Vector Icon Container
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(bill.color).copy(alpha = 0.18f))
                            .border(BorderStroke(1.dp, Color(bill.color).copy(alpha = 0.3f)), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getCategoryIconByName(bill.category, bill.icon),
                            contentDescription = bill.category,
                            tint = Color(bill.color),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = bill.billName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark,
                            fontSize = 15.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = bill.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryDark,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Translucent "X days left" Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = remainingBadgeColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, remainingBadgeColor.copy(alpha = 0.25f))
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

            // Row 2: Large Bold Amount (e.g. ₹65 / ₹12,000 / +₹50,000)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val isCredit = bill.transactionType == "CREDIT"
                    Text(
                        text = "${if (isCredit) "+" else ""}$currency${String.format(Locale.getDefault(), "%,.0f", bill.amount)}",
                        style = MaterialTheme.typography.headlineMedium,
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
            }

            // Row 3: DUE DATE label & Date String on Left + Compact Dark Action Button on Right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "DUE DATE",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedDark,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formattedDueDate,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark,
                        fontSize = 14.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (bill.paymentStatus != "PAID") {
                        GlassMarkPaidButton(
                            onClick = onMarkAsPaid,
                            text = "Mark as Paid"
                        )
                    }

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

            HorizontalDivider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 2.dp))

            // Footer Row: 🔔 Reminder: 05 Aug 2026, 09:00 AM
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.NotificationsNone,
                    contentDescription = null,
                    tint = TextSecondaryDark,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Reminder: $formattedReminderDate, ${bill.reminderTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark,
                    fontSize = 12.sp
                )
            }
        }
    }
}


