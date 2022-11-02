package com.cmoney.fanci.ui.screens.my.state

import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope

class MyScreenState(
    val navController: NavHostController,
    val scope: CoroutineScope,
    val scaffoldState: ScaffoldState
) {

}

@Composable
fun rememberMyScreenState(
    navController: NavHostController = rememberNavController(),
    scope: CoroutineScope = rememberCoroutineScope(),
    scaffoldState: ScaffoldState = rememberScaffoldState()
) = remember {
    MyScreenState(navController, scope, scaffoldState)
}