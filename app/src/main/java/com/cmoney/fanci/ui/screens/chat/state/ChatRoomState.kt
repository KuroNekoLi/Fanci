package com.cmoney.fanci.ui.screens.chat.state

import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope

class ChatRoomState(
    val navController: NavHostController,
    val scope: CoroutineScope,
    val scaffoldState: ScaffoldState
) {

    suspend fun showRecycleMessageSnackBar() {
        scaffoldState.snackbarHostState.showSnackbar(message = "訊息收回成功！")
    }

}

@Composable
fun rememberChatRoomState(
    navController: NavHostController = rememberNavController(),
    scope: CoroutineScope = rememberCoroutineScope(),
    scaffoldState: ScaffoldState = rememberScaffoldState()
) = remember {
    ChatRoomState(navController, scope, scaffoldState)
}