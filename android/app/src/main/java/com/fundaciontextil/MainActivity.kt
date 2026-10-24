package com.fundaciontextil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.fundaciontextil.ui.ejemplo.EjemploScreen
import com.fundaciontextil.ui.theme.FundacionTextilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FundacionTextilTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EjemploScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
