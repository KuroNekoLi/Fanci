package com.cmoney.fanci.ui.screens.chat.state

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.ui.screens.chat.message.viewmodel.MessageViewModel
import com.cmoney.fanci.ui.screens.chat.viewmodel.ChatRoomViewModel
import org.koin.androidx.compose.koinViewModel

class ChatRoomState(
    val navController: NavHostController,
    val scaffoldState: ScaffoldState,
    val listState: LazyListState,
    val viewModel: ChatRoomViewModel,
    val messageViewModel: MessageViewModel
) {

}

@Composable
fun rememberChatRoomState(
    navController: NavHostController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    listState: LazyListState = rememberLazyListState(),
    viewModel: ChatRoomViewModel = koinViewModel(),
    messageViewModel: MessageViewModel = koinViewModel()
) = remember {
    ChatRoomState(navController, scaffoldState, listState, viewModel, messageViewModel)
}