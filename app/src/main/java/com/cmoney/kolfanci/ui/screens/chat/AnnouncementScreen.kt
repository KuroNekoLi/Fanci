package com.cmoney.kolfanci.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.model.ChatMessageWrapper
import com.cmoney.kolfanci.model.usecase.ChatRoomUseCase
import com.cmoney.kolfanci.ui.screens.chat.message.MessageContentScreen
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.EmptyResultBackNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator

const val AnnounceBundleKey = "AnnounceBundleKey"

/**
 * 設定 公告 訊息
 */
@Destination
@Composable
fun AnnouncementScreen(
    navigator: DestinationsNavigator,
    message: ChatMessage,
    resultBackNavigator: ResultBackNavigator<ChatMessage>
) {
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBarScreen(
                title = "設定公告訊息",
                backClick = {
                    navigator.popBackStack()
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(MaterialTheme.colors.surface)
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Bottom
        ) {
            MessageContentScreen(
                chatMessageWrapper = ChatMessageWrapper(message),
                modifier = Modifier
                    .weight(1f),
                onMessageContentCallback = {

                },
                onReSendClick = {

                }
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LocalColor.current.env_100)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "這則訊息公告後，將會覆蓋上一則公告",
                        color = LocalColor.current.text.other,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(17.dp))

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = LocalColor.current.primary),
                        onClick = {
                            resultBackNavigator.navigateBack(message)
                        }) {
                        Text(
                            text = "將此訊息設為公告",
                            color = LocalColor.current.text.other,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnnouncementScreenPreview() {
    FanciTheme {
        AnnouncementScreen(
            EmptyDestinationsNavigator,
            ChatRoomUseCase.mockMessage,
            resultBackNavigator = EmptyResultBackNavigator())
    }
}