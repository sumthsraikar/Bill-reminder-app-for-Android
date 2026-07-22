package com.example.myapplicationds.ui.settings

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplicationds.R
import com.example.myapplicationds.ui.components.GlassCard
import com.example.myapplicationds.ui.theme.*
import kotlinx.coroutines.launch

import com.example.myapplicationds.ui.components.GlassIconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val currency by viewModel.currency.collectAsState()
    val theme by viewModel.theme.collectAsState()
    val notificationEnabled by viewModel.notificationEnabled.collectAsState()
    val reminderDaysBefore by viewModel.reminderDaysBefore.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    // Launcher to Export Backup File (Save File)
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                try {
                    val json = viewModel.getExportJson()
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(json.toByteArray())
                    }
                    Toast.makeText(context, "Backup exported successfully!", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Failed to export backup", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Launcher to Import Backup File (Open File)
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                try {
                    val json = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        inputStream.bufferedReader().readText()
                    }
                    if (json != null) {
                        val success = viewModel.importBackupJson(json)
                        if (success) {
                            Toast.makeText(context, "Backup restored successfully!", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Invalid backup file format", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Failed to import backup", Toast.LENGTH_SHORT).show()
                }
            }
        }
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
                    text = "Settings",
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
            // Theme Setting Glass Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(20.dp)
            ) {
                Text(
                    text = "App Theme",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("DARK" to "Dark Glass", "LIGHT" to "Light", "SYSTEM" to "System").forEach { (key, label) ->
                        FilterChip(
                            selected = theme == key,
                            onClick = { viewModel.setTheme(key) },
                            label = { Text(label, color = if (theme == key) Color.White else TextSecondaryDark) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryBlue,
                                containerColor = Color(0x1F22222E)
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Currency Setting Glass Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(20.dp)
            ) {
                Text(
                    text = "Currency Symbol",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("₹", "$", "€", "£").forEach { symbol ->
                        FilterChip(
                            selected = currency == symbol,
                            onClick = { viewModel.setCurrency(symbol) },
                            label = { Text(symbol, fontWeight = FontWeight.Bold, color = if (currency == symbol) Color.White else TextSecondaryDark) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryBlue,
                                containerColor = Color(0x1F22222E)
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Push Notifications Setting Glass Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Push Notifications",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Receive timely bill payment reminders",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondaryDark
                        )
                    }
                    Switch(
                        checked = notificationEnabled,
                        onCheckedChange = viewModel::setNotificationEnabled,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PrimaryBlue
                        )
                    )
                }

                if (notificationEnabled) {
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Reminder Lead Time",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1 to "1 Day", 3 to "3 Days", 7 to "7 Days").forEach { (days, label) ->
                            FilterChip(
                                selected = reminderDaysBefore == days,
                                onClick = { viewModel.setReminderDaysBefore(days) },
                                label = { Text(label, color = if (reminderDaysBefore == days) Color.White else TextSecondaryDark) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryBlue,
                                    containerColor = Color(0x1F22222E)
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Backup & Restore Glass Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(20.dp)
            ) {
                Text(
                    text = "Backup & Restore",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Export or restore your bill data locally as JSON file",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryDark
                )
                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { exportLauncher.launch("billbuddy_backup.json") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null, tint = TextPrimaryDark, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export JSON", color = TextPrimaryDark, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { importLauncher.launch(arrayOf("application/json", "*/*")) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Import JSON", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Danger Zone Glass Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = StatusOverdue.copy(alpha = 0.1f),
                borderColor = StatusOverdue.copy(alpha = 0.3f),
                contentPadding = PaddingValues(20.dp)
            ) {
                Text(
                    text = "Reset Application",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = StatusOverdue
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Permanently delete all stored bills, payment history, and preferences",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryDark
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusOverdue),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete All Data", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            // Developer & Contact Info Glass Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(20.dp)
            ) {
                Text(
                    text = "Contact Information",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = PrimaryBlueLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Developed by",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMutedDark
                        )
                        Text(
                            text = "Sumith S. Raikar",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = PrimaryBlueLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Email",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMutedDark
                        )
                        Text(
                            text = "sumithsraikar10@gmail.com",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    }
                }
            }

            // About & Privacy Policy Glass Card
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAboutDialog = true },
                contentPadding = PaddingValues(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = PrimaryBlueLight)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "About & Privacy Policy",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondaryDark)
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Delete All Data?", color = TextPrimaryDark, fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently clear all your stored bills and settings from your device. Are you sure?", color = TextSecondaryDark) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAllData()
                        showDeleteDialog = false
                        Toast.makeText(context, "All data deleted", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Delete Everything", color = StatusOverdue, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = TextSecondaryDark)
                }
            }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "BillBuddy Logo",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("BillBuddy v1.0", color = TextPrimaryDark, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        text = "BillBuddy is a 100% offline, local-first bill management and reminder application.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Privacy Guarantee:\nAll data, settings, and schedules are stored strictly on your local device. No internet connection or tracking required.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Developed by",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedDark
                    )
                    Text(
                        text = "Sumith S. Raikar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimaryDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close", color = PrimaryBlueLight, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

