package com.grandiamuhammad3096.assessment02

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grandiamuhammad3096.assessment02.navigasi.NavGraph
import com.grandiamuhammad3096.assessment02.ui.screen.ThemeViewModel
import com.grandiamuhammad3096.assessment02.ui.theme.Assessment02Theme
import com.grandiamuhammad3096.assessment02.util.ThemeViewModelFactory

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModelFactory(context))
            val isDark by themeViewModel.isDark.collectAsState()

            Assessment02Theme(darkTheme = isDark) {
                NavGraph(themeViewModel = themeViewModel)
            }
        }
    }
}