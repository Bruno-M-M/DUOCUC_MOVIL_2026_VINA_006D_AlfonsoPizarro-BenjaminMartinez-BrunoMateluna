package com.fundaciontextil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fundaciontextil.ui.navigation.AppNav
import com.fundaciontextil.ui.theme.FundacionTextilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FundacionTextilTheme {
                AppNav()
            }
        }
    }
}
