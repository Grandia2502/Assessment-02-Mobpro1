package com.grandiamuhammad3096.assessment02

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.grandiamuhammad3096.assessment02.ui.screen.MainScreen
import com.grandiamuhammad3096.assessment02.ui.theme.Assessment02Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assessment02Theme {
                MainScreen()
            }
        }
    }
}