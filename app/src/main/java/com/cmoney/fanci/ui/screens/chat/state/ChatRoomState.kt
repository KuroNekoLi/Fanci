package com.cmoney.fanci.ui.screens.chat.state

import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.model.viewmodel.ChatRoomViewModelFactory
import com.cmoney.fanci.ui.screens.chat.viewmodel.ChatRoomViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.androidx.compose.koinViewModel

class ChatRoomState(
    val navController: NavHostController,
    val scaffoldState: ScaffoldState,
    val viewModel: ChatRoomViewModel
) {

}

@Composable
fun rememberChatRoomState(
    navController: NavHostController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: ChatRoomViewModel = koinViewModel()
) = remember {
    ChatRoomState(navController, scaffoldState, viewModel)
}