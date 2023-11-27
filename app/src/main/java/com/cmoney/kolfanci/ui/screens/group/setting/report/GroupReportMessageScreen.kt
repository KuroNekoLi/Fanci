package com.cmoney.kolfanci.ui.screens.group.setting.report

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.ChannelTabType
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.MessageServiceType
import com.cmoney.fanciapi.fanci.model.ReportInformation
import com.cmoney.kolfanci.extension.getDuration
import com.cmoney.kolfanci.extension.getFileName
import com.cmoney.kolfanci.model.mock.MockData
import com.cmoney.kolfanci.ui.common.AutoLinkPostText
import com.cmoney.kolfanci.ui.destinations.AudioPreviewScreenDestination
import com.cmoney.kolfanci.ui.screens.chat.message.MediaContent
import com.cmoney.kolfanci.ui.screens.chat.message.MessageOGScreen
import com.cmoney.kolfanci.ui.screens.group.setting.report.viewmodel.GroupReportViewModel
import com.cmoney.kolfanci.ui.screens.shared.ChatUsrAvatarScreen
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.kolfanci.utils.Utils
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Destination
@Composable
fun GroupReportMessageScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    reportInformation: ReportInformation,
    groupReportViewModel: GroupReportViewModel = koinViewModel(
        parameters = {
            parametersOf(emptyList<ReportInformation>(), Group())
        }
    ),
) {

    val reportMessage by groupReportViewModel.singleMessage.collectAsState()

    LaunchedEffect(key1 = Unit) {
        groupReportViewModel.getReportMessageInfo(
            messageId = reportInformation.id.orEmpty(),
            messageServiceType = if (reportInformation.tabType == ChannelTabType.chatRoom) {
                MessageServiceType.chatroom
            } else {
                MessageServiceType.bulletinboard
            }
        )
    }

    GroupReportMessageScreenView(
        modifier = modifier,
        navigator = navigator,
        chatMessage = reportMessage ?: ChatMessage()
    )
}

@Composable
private fun GroupReportMessageScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    chatMessage: ChatMessage
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "遭檢舉資訊",
                backClick = {
                    navigator.popBackStack()
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LocalColor.current.env_80)
                .padding(innerPadding)
                .padding(top = 20.dp, bottom = 20.dp, start = 24.dp, end = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            //Avatar
            Row(verticalAlignment = Alignment.CenterVertically) {
                //大頭貼
                chatMessage.author?.let {
                    ChatUsrAvatarScreen(user = it)
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Column {
                //Message text
                chatMessage.content?.text?.apply {
                    if (this.isNotEmpty()) {
                        AutoLinkPostText(
                            text = this,
                            fontSize = 17.sp,
                            color = LocalColor.current.text.default_100,
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        //OG
                        Utils.extractLinks(this).forEach { url ->
                            MessageOGScreen(url = url)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                //attach
                if (chatMessage.content?.medias?.isNotEmpty() == true) {
                    //附加檔案
                    MediaContent(
                        modifier = Modifier,
                        navController = navigator,
                        medias = chatMessage.content?.medias.orEmpty(),
                        onAttachClick = { media ->
                            navigator.navigate(
                                AudioPreviewScreenDestination(
                                    uri = Uri.parse(media.resourceLink),
                                    duration = media.getDuration(),
                                    title = media.getFileName()
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun GroupReportMessageScreenPreview() {
    FanciTheme {
        GroupReportMessageScreenView(
            navigator = EmptyDestinationsNavigator,
            chatMessage = MockData.mockMessage
        )
    }
}