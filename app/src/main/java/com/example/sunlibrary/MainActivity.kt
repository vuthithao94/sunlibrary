package com.example.sunlibrary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sunlibrary.ui.BorrowActivity
import com.example.sunlibrary.ui.LoginActivity
import com.example.sunlibrary.ui.theme.SunLibraryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val path = applicationContext.filesDir

        setContent {
            SunLibraryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "LoginActivity") {
                        composable("LoginActivity") { LoginActivity(navController, path.path) }
                        composable("BorrowActivity") { BorrowActivity() }
                    }
                }
            }
        }
    }
}
