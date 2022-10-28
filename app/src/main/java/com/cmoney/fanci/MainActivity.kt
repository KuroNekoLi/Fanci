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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.ui.MainNavHost
import com.cmoney.fanci.ui.MyAppNavHost
import com.cmoney.fanci.ui.screens.BottomBarScreen
import com.cmoney.fanci.ui.theme.FanciTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FanciTheme {
                val mainState = rememberMainState()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                ) {
                    mainState.setStatusBarColor()
                    MyAppNavHost(
                        mainState.navController,
                        mainState.mainNavController,
                        mainState.route
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    mainNavController: NavHostController,
    route: (MainStateHolder.Route) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomBarScreen(mainNavController)
        }
    ) { innerPadding ->
        MainNavHost(
            navController = mainNavController,
            modifier = Modifier.padding(innerPadding),
            route = route
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MainScreen(rememberNavController()) {
    }
}