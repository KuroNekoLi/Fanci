package com.cmoney.kolfanci.ui.screens.group.setting.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.ReportInformation
import com.cmoney.kolfanci.ui.common.AutoLinkPostText
import com.cmoney.kolfanci.ui.screens.chat.MessageImageScreen
import com.cmoney.kolfanci.ui.screens.shared.ChatUsrAvatarScreen
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

@Destination
@Composable
fun GroupReportMessageScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    reportInformation: ReportInformation
) {
    GroupReportMessageScreenView(
        modifier = modifier,
        navigator = navigator,
        reportInformation = reportInformation
    )
}

@Composable
private fun GroupReportMessageScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    reportInformation: ReportInformation
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "遭檢舉資訊",
                leadingEnable = true,
                moreEnable = false,
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
                reportInformation.reportee?.let {
                    ChatUsrAvatarScreen(user = it)
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Column(
            ) {
                //Message text
                reportInformation.contentSnapshot?.apply {
                    if (this.isNotEmpty()) {
                        AutoLinkPostText(
                            text = this,
                            fontSize = 17.sp,
                            color = LocalColor.current.text.default_100,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                //Image attach
                if (reportInformation.mediasSnapshot?.isNotEmpty() == true) {
                    MessageImageScreen(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        images = reportInformation.mediasSnapshot?.map {
                            it.resourceLink.orEmpty()
                        }.orEmpty()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupReportMessageScreenPreview() {
    FanciTheme {
        GroupReportMessageScreenView(
            navigator = EmptyDestinationsNavigator,
            reportInformation = ReportInformation(
                contentSnapshot = "Content..."
            )
        )
    }
}