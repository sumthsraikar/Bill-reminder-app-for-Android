package com.example.myapplicationds.ui.statistics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplicationds.ui.components.GlassCard
import com.example.myapplicationds.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel
) {
    val currency by viewModel.currency.collectAsState()
    val totalBills by viewModel.totalBillsCount.collectAsState()
    val paidBills by viewModel.paidBillsCount.collectAsState()
    val upcomingBills by viewModel.upcomingBillsCount.collectAsState()
    val overdueBills by viewModel.overdueBillsCount.collectAsState()
    val monthlySpending by viewModel.monthlySpending.collectAsState()
    val categoryExpenses by viewModel.categoryExpenses.collectAsState()
    val paymentHistory by viewModel.paymentHistory.collectAsState()

    val dateFormat = remember { SimpleDateFormat("dd MMM, yyyy • HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Analytics & Reports",
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Spending Summary Card with Gradient Glass
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0x1F3B82F6),
                borderColor = PrimaryBlue.copy(alpha = 0.3f),
                contentPadding = PaddingValues(24.dp)
            ) {
                Text(
                    text = "THIS MONTH'S SPENDING",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryBlueLight,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "$currency${String.format(Locale.getDefault(), "%.2f", monthlySpending)}",
                    style = BillAmountStyle,
                    color = TextPrimaryDark
                )
            }

            // Stats Metric Grid Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlassMetricCard(
                    title = "Total",
                    count = totalBills.toString(),
                    color = PrimaryBlueLight,
                    modifier = Modifier.weight(1f)
                )
                GlassMetricCard(
                    title = "Paid",
                    count = paidBills.toString(),
                    color = StatusPaid,
                    modifier = Modifier.weight(1f)
                )
                GlassMetricCard(
                    title = "Upcoming",
                    count = upcomingBills.toString(),
                    color = StatusUpcoming,
                    modifier = Modifier.weight(1f)
                )
                GlassMetricCard(
                    title = "Overdue",
                    count = overdueBills.toString(),
                    color = StatusOverdue,
                    modifier = Modifier.weight(1f)
                )
            }

            // Chart View Selector (Bar Graph, Pie Chart, Line Graph)
            var selectedChartType by remember { mutableStateOf("BAR") } // "BAR", "PIE", "LINE"

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("BAR" to "📊 Bar Graph", "PIE" to "🍕 Pie Chart", "LINE" to "📈 Line Graph").forEach { (typeKey, label) ->
                    val isSelected = selectedChartType == typeKey
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedChartType = typeKey },
                        label = { Text(label, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else TextSecondaryDark) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryBlue,
                            containerColor = Color(0x1F22222E)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Dynamic Analytics Chart Glass Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when (selectedChartType) {
                                "PIE" -> Icons.Default.PieChart
                                "LINE" -> Icons.Default.ShowChart
                                else -> Icons.Default.BarChart
                            },
                            contentDescription = null,
                            tint = PrimaryBlueLight,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = when (selectedChartType) {
                                "PIE" -> "Category Pie Breakdown"
                                "LINE" -> "Cash Flow Trend Line"
                                else -> "Category Spending Bars"
                            },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (categoryExpenses.isEmpty()) {
                    Text(
                        text = "No analytics data available yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryDark
                    )
                } else {
                    when (selectedChartType) {
                        "PIE" -> {
                            // Pie Chart Renderer
                            val totalSum = categoryExpenses.sumOf { it.totalAmount }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(130.dp)
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        var startAngle = -90f
                                        val strokeWidth = 32f

                                        categoryExpenses.forEach { exp ->
                                            val sweepAngle = if (totalSum > 0) {
                                                ((exp.totalAmount / totalSum) * 360f).toFloat()
                                            } else 0f

                                            drawArc(
                                                color = Color(exp.color),
                                                startAngle = startAngle,
                                                sweepAngle = sweepAngle,
                                                useCenter = false,
                                                style = Stroke(width = strokeWidth)
                                            )
                                            startAngle += sweepAngle
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(20.dp))

                                // Pie Chart Legends
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    categoryExpenses.take(5).forEach { exp ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(exp.color))
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = exp.categoryName,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextSecondaryDark,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = "$currency${String.format(Locale.getDefault(), "%.0f", exp.totalAmount)}",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimaryDark
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        "LINE" -> {
                            // Smooth Canvas Line Graph Renderer
                            val maxAmount = categoryExpenses.maxOf { it.totalAmount }.coerceAtLeast(1.0)
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .padding(vertical = 8.dp)
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val width = size.width
                                        val height = size.height
                                        val pointsCount = categoryExpenses.size
                                        val spacing = width / (pointsCount.coerceAtLeast(2) - 1).coerceAtLeast(1)

                                        val path = androidx.compose.ui.graphics.Path()
                                        val fillPath = androidx.compose.ui.graphics.Path()

                                        categoryExpenses.forEachIndexed { index, exp ->
                                            val x = index * spacing
                                            val y = height - ((exp.totalAmount / maxAmount) * (height - 30f)).toFloat() - 15f

                                            if (index == 0) {
                                                path.moveTo(x, y)
                                                fillPath.moveTo(x, height)
                                                fillPath.lineTo(x, y)
                                            } else {
                                                val prevX = (index - 1) * spacing
                                                val prevY = height - ((categoryExpenses[index - 1].totalAmount / maxAmount) * (height - 30f)).toFloat() - 15f
                                                val controlX1 = prevX + (spacing / 2f)
                                                val controlY1 = prevY
                                                val controlX2 = prevX + (spacing / 2f)
                                                val controlY2 = y

                                                path.cubicTo(controlX1, controlY1, controlX2, controlY2, x, y)
                                                fillPath.cubicTo(controlX1, controlY1, controlX2, controlY2, x, y)
                                            }

                                            // Draw data point circles
                                            drawCircle(
                                                color = Color(exp.color),
                                                radius = 6f,
                                                center = androidx.compose.ui.geometry.Offset(x, y)
                                            )
                                        }

                                        fillPath.lineTo(width, height)
                                        fillPath.close()

                                        // Draw filled area below line
                                        drawPath(
                                            path = fillPath,
                                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                                colors = listOf(PrimaryBlue.copy(alpha = 0.35f), Color.Transparent)
                                            )
                                        )

                                        // Draw smooth line
                                        drawPath(
                                            path = path,
                                            color = PrimaryBlue,
                                            style = Stroke(width = 3.dp.toPx())
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Category Node Summary List
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    categoryExpenses.take(4).forEach { exp ->
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = exp.categoryName.take(6),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextSecondaryDark,
                                                fontSize = 10.sp
                                            )
                                            Text(
                                                text = "$currency${String.format(Locale.getDefault(), "%.0f", exp.totalAmount)}",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryBlueLight,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        else -> { // "BAR"
                            // Category Bar Chart Renderer
                            val maxAmount = categoryExpenses.maxOf { it.totalAmount }.coerceAtLeast(1.0)
                            Column(
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                categoryExpenses.take(6).forEach { exp ->
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = exp.categoryName,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextSecondaryDark
                                            )
                                            Text(
                                                text = "$currency${String.format(Locale.getDefault(), "%.2f", exp.totalAmount)}",
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimaryDark
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(10.dp)
                                                .clip(RoundedCornerShape(5.dp))
                                                .background(Color(0x1F22222E))
                                        ) {
                                            val fraction = (exp.totalAmount / maxAmount).toFloat()
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .fillMaxWidth(fraction)
                                                    .clip(RoundedCornerShape(5.dp))
                                                    .background(Color(exp.color))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }


            // Recent Payment History Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = StatusPaid,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Recent Payment History",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (paymentHistory.isEmpty()) {
                    Text(
                        text = "No paid history recorded yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryDark
                    )
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        paymentHistory.take(8).forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = StatusPaid,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = item.billName.ifEmpty { "Bill Payment" },
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimaryDark
                                        )
                                        Text(
                                            text = dateFormat.format(Date(item.paidDate)),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextMutedDark
                                        )
                                    }
                                }

                                Text(
                                    text = "+$currency${String.format(Locale.getDefault(), "%.2f", item.paidAmount)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusPaid
                                )
                            }
                            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun GlassMetricCard(
    title: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        contentPadding = PaddingValues(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryDark,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

