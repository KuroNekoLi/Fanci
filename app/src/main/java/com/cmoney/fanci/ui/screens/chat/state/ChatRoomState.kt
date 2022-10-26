package com.cmoney.fanci.ui.screens.chat.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class ChatRoomState(
    val navController: NavHostController
) {
}

@Composable
fun rememberChatRoomState(
    navController: NavHostController = rememberNavController()
) = remember {
    ChatRoomState(navController)
}