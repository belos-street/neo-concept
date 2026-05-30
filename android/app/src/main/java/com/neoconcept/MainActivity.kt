package com.neoconcept

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.neoconcept.navigation.AppNavigation
import com.neoconcept.ui.theme.NeoConceptTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeoConceptTheme {
                AppNavigation()
            }
        }
    }
}
