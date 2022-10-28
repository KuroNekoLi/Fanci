package com.cmoney.fanci

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.Navigator
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.model.ChatMessageModel
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainStateHolder(
    val navController: NavHostController,
    val mainNavController: NavHostController,
    private val systemUiController: SystemUiController,
) {

    val route: (Route) -> Unit = {
        when (it) {
            is Route.Channel -> mainNavController.navigate("channel/${it.channelId}")
            is Route.Announce -> {
                mainNavController.currentBackStackEntry?.savedStateHandle?.apply {
                    set("message", it.message)
                }
                mainNavController.navigate("announce")
            }
        }
    }

    @Composable
    fun setStatusBarColor() {
        val statusBarColor = MaterialTheme.colors.primary
        SideEffect {
            systemUiController.setStatusBarColor(
                color = statusBarColor,
                darkIcons = false
            )
        }
    }

    sealed class Route {
        data class Channel(val channelId: String) : Route()

        data class Announce(val message: ChatMessageModel) : Route()
    }

}

@Composable
fun rememberMainState(
    navController: NavHostController = rememberNavController(),
    mainNavController: NavHostController = rememberNavController(),
    systemUiController: SystemUiController = rememberSystemUiController()
) = remember {
    MainStateHolder(navController, mainNavController, systemUiController)
}