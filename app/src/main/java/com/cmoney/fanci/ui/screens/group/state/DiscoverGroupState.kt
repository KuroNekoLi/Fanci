package com.cmoney.fanci.ui.screens.group.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class DiscoverGroupState(val navController: NavHostController) {
}

@Composable
fun rememberDiscoverGroupState(
    navController: NavHostController = rememberNavController()
) = remember {
    DiscoverGroupState(navController)
}