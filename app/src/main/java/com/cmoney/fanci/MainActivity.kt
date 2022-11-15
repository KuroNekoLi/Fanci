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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.ui.MainNavHost
import com.cmoney.fanci.ui.MyAppNavHost
import com.cmoney.fanci.ui.screens.BottomBarScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    val viewModel by inject<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO:  
        viewModel.test()

        setContent {
            val theme = viewModel.theme.observeAsState()

            FanciTheme(themeSetting = theme.value ?: ThemeSetting.Default) {
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
                    ) {
                        viewModel.settingTheme(it)
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    mainNavController: NavHostController,
    route: (MainStateHolder.Route) -> Unit,
    theme: (ThemeSetting) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomBarScreen(mainNavController)
        }
    ) { innerPadding ->
        MainNavHost(
            navController = mainNavController,
            modifier = Modifier.padding(innerPadding),
            route = route,
            theme = theme
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MainScreen(rememberNavController(), route = {}) {
    }
}