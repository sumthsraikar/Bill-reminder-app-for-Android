package com.example.myapplicationds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.myapplicationds.data.repository.BillRepository
import com.example.myapplicationds.ui.navigation.NavGraph
import com.example.myapplicationds.ui.theme.BillBuddyTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var billRepository: BillRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by billRepository.themeFlow.collectAsState(initial = "DARK")

            BillBuddyTheme(themeMode = themeMode) {
                NavGraph()
            }
        }
    }
}