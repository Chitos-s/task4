package com.example.ramapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.ramapp.ui.navigation.RamAppNavHost
import com.example.ramapp.ui.theme.RamAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RamAppTheme {
                RamAppNavHost()
            }
        }
    }
}
