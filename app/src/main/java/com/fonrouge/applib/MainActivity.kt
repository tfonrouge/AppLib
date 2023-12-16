package com.fonrouge.applib

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
import com.fonrouge.applib.ui.BarcodeReaderScreen1
import com.fonrouge.applib.ui.HomeScreen
import com.fonrouge.applib.ui.NavDrawerAsSheetScreen
import com.fonrouge.applib.ui.theme.AppLibTheme
import com.fonrouge.applib.ui.theme.Routes

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppLibTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navHostController = rememberNavController()
                    NavHost(
                        navController = navHostController,
                        startDestination = Routes.Home.destination
                    ) {
                        composable(Routes.Home.destination) {
                            HomeScreen(navHostController)
                        }
                        composable(Routes.NavDrawerAsSheetScreen.destination) {
                            NavDrawerAsSheetScreen(navHostController = navHostController)
                        }
                        composable(Routes.BarcodeReeaderScreen1.destination) {
                            BarcodeReaderScreen1()
                        }
                    }
                }
            }
        }
    }
}
