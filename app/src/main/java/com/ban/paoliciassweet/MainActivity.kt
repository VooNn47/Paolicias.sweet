package com.ban.paoliciassweet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ban.paoliciassweet.ui.navigation.AppNavigation
import com.ban.paoliciassweet.ui.theme.BanqueteriaTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            BanqueteriaTheme {
                AppNavigation()
            }
        }
    }
}

