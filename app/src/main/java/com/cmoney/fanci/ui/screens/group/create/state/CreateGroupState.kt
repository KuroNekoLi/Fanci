package com.cmoney.fanci.ui.screens.group.create.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class CreateGroupState(val navController: NavHostController) {
}

@Composable
fun rememberCreateGroupState(
    navController: NavHostController = rememberNavController()
) = remember {
    CreateGroupState(navController)
}