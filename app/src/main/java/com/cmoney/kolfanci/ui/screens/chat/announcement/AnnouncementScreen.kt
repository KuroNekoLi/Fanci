package com.cmoney.kolfanci.ui.screens.chat.announcement

import android.net.Uri
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import com.cmoney.kolfanci.ui.screens.media.audio.AudioViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.audio.AudioMiniPlayIconScreen
import com.cmoney.kolfanci.ui.screens.shared.bottomSheet.audio.AudioBottomPlayerScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.EmptyResultBackNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Parcelize
data class AnnouncementResult(
    val message: ChatMessage,
    val isSetting: Boolean
) : Parcelable

/**
 * 公告 訊息
 * 設定, 取消,  觀看
 *
 * @param message 訊息文本
 * @param isPinMessage 是否為置頂貼文
 * @param resultBackNavigator 設定結果 callback, boolean:是否為設定or取消 公告 true:設定 false:取消
 */
@OptIn(ExperimentalMaterialApi::class)
@Destination
@Composable
fun AnnouncementScreen(
    navigator: DestinationsNavigator,
    message: ChatMessage,
    isPinMessage: Boolean,
    audioViewModel: AudioViewModel = koinViewModel(
        parameters = {
            parametersOf(Uri.EMPTY)
        }
    ),
    resultBackNavigator: ResultBackNavigator<AnnouncementResult>
) {

    val isShowAudioMiniIcon by audioViewModel.isShowMiniIcon.collectAsState()

    val isAudioPlaying by audioViewModel.isPlaying.collectAsState()

    val isOpenBottomAudioPlayer by audioViewModel.isShowBottomPlayer.collectAsState()

    //控制 audio BottomSheet
    val audioPlayerState = rememberModalBottomSheetState(
        if (isOpenBottomAudioPlayer) {
            ModalBottomSheetValue.Expanded
        } else {
            ModalBottomSheetValue.Hidden
        }
    )

    val coroutineScope = rememberCoroutineScope()

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

            if (isShowAudioMiniIcon) {
                AudioMiniPlayIconScreen(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 120.dp),
                    isPlaying = isAudioPlaying,
                    onClick = {
                        coroutineScope.launch {
                            audioPlayerState.show()
                        }
                    }
                )

                //mini player
                AudioBottomPlayerScreen(
                    state = audioPlayerState
                )
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
            EmptyDestinationsNavigator,
            MockData.mockMessage,
            resultBackNavigator = EmptyResultBackNavigator(),
            isPinMessage = true
        )
    }
}