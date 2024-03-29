package com.cmoney.kolfanci.ui.screens.chat.announcement

import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.kolfanci.model.ChatMessageWrapper
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.model.mock.MockData
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.screens.chat.message.MessageContentScreen
import com.cmoney.kolfanci.ui.screens.shared.toolbar.TopBarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.EmptyResultBackNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.parcelize.Parcelize

@Parcelize
data class AnnouncementResult(
    val message: ChatMessage,
    val isSetting: Boolean
) : Parcelable

/**
 * 公告 訊息
 * 設定, 取消,  觀看
 *
 * @param channelId
 * @param message 訊息文本
 * @param isPinMessage 是否為置頂貼文
 * @param resultBackNavigator 設定結果 callback, boolean:是否為設定or取消 公告 true:設定 false:取消
 */
@Destination
@Composable
fun AnnouncementScreen(
    navigator: DestinationsNavigator,
    channelId: String,
    message: ChatMessage,
    isPinMessage: Boolean,
    resultBackNavigator: ResultBackNavigator<AnnouncementResult>
) {
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current
        ?.onBackPressedDispatcher

    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBarScreen(
                title = "設定公告訊息",
                backClick = {
                    onBackPressedDispatcher?.onBackPressed()
                }
            )
        },
        backgroundColor = LocalColor.current.env_80
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            LazyColumn(
                modifier = Modifier
                    .background(LocalColor.current.env_80)
            ) {
                item {
                    MessageContentScreen(
                        channelId = channelId,
                        chatMessageWrapper = ChatMessageWrapper(message),
                        onMessageContentCallback = {

                        },
                        onReSendClick = {

                        },
                        navController = navigator
                    )
                }
            }


            if (Constant.isCanManage()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .fillMaxWidth()
                        .background(LocalColor.current.env_100)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        if (isPinMessage) {
                            Spacer(modifier = Modifier.height(17.dp))

                            BorderButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                text = "取消公告",
                                textColor = LocalColor.current.text.default_100,
                                borderColor = LocalColor.current.text.default_50
                            ) {
                                resultBackNavigator.navigateBack(
                                    AnnouncementResult(
                                        message = message,
                                        isSetting = false
                                    )
                                )
                            }
                        } else {
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
                                    resultBackNavigator.navigateBack(
                                        AnnouncementResult(
                                            message = message,
                                            isSetting = true
                                        )
                                    )
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
    }

    BackHandler {
        navigator.popBackStack()
    }
}

@Preview
@Composable
fun AnnouncementScreenPreview() {
    FanciTheme {
        AnnouncementScreen(
            channelId = "",
            navigator = EmptyDestinationsNavigator,
            message = MockData.mockMessage,
            isPinMessage = true,
            resultBackNavigator = EmptyResultBackNavigator()
        )
    }
}