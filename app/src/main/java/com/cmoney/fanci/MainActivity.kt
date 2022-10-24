package com.cmoney.fanci

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.ui.MainActions
import com.cmoney.fanci.ui.MyAppNavHost
import com.cmoney.fanci.ui.MainNavHost
import com.cmoney.fanci.ui.screens.BottomBarController
import com.cmoney.fanci.ui.theme.FanciTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FanciTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                ) {
                    MyAppNavHost(rememberNavController())
                }
            }
        }
    }
}

@Composable
fun MainScreen(mainAction: MainActions) {
    val navController = rememberNavController()
    val systemUiController = rememberSystemUiController()
    val statusBarColor = MaterialTheme.colors.primary
    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = false
        )
    }

    Scaffold(
        bottomBar = {
            BottomBarController(navController)
        }
    ) { innerPadding ->
        MainNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            actions = mainAction
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MainScreen(MainActions(rememberNavController()))
}