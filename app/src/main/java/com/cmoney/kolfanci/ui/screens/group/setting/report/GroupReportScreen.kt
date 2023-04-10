package com.cmoney.kolfanci.ui.screens.group.setting.report

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.cmoney.fanciapi.fanci.model.*
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.destinations.GroupReportMessageScreenDestination
import com.cmoney.kolfanci.destinations.GroupReporterScreenDestination
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.common.GrayButton
import com.cmoney.kolfanci.ui.screens.group.setting.report.viewmodel.GroupReportViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.BanDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.KickOutDialogScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.kolfanci.utils.Utils
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * 檢舉審核
 */
@Destination
@Composable
fun GroupReportScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    group: Group,
    reportList: Array<ReportInformation>,
    viewModel: GroupReportViewModel = koinViewModel(
        parameters = {
            parametersOf(reportList.toList(), group)
        }
    ),
    resultBackNavigator: ResultBackNavigator<Boolean>
) {
    val uiState = viewModel.uiState

    GroupReportScreenView(
        modifier = modifier,
        navigator = navigator,
        reportList = uiState.reportList,
        onIgnore = {
            viewModel.ignoreReport(it)
        },
        onBack = {
            resultBackNavigator.navigateBack(
                result = (reportList.size != uiState.reportList.size)
            )
        },
        onReport = {
            viewModel.showReportDialog(it)
        }
    )

    //懲處 dialog
    if (uiState.showReportDialog != null) {
        ReportDialog(
            reportInformation = uiState.showReportDialog,
            onDismiss = {
                viewModel.dismissReportDialog()
            },
            onSilence = {
                viewModel.dismissReportDialog()
                viewModel.showSilenceDialog(it)
            },
            onKick = {
                viewModel.dismissReportDialog()
                viewModel.showKickDialog(it)
            }
        )
    }

    //禁言 dialog
    if (uiState.showSilenceDialog != null) {
        val name = uiState.showSilenceDialog.reportee?.name.orEmpty()

        BanDialogScreen(
            name = name,
            onDismiss = {
                viewModel.dismissSilenceDialog()
            },
            onConfirm = {
                viewModel.dismissSilenceDialog()
                uiState.showSilenceDialog.let { reportInfo ->
                    viewModel.silenceUser(reportInfo, it)
                }
            }
        )
    }

    //踢出 dialog
    if (uiState.kickDialog != null) {
        val name = uiState.kickDialog.reportee?.name.orEmpty()

        KickOutDialogScreen(
            name = name,
            onDismiss = {
                viewModel.dismissKickDialog()
            },
            onConfirm = {
                viewModel.dismissKickDialog()
                uiState.kickDialog.let {
                    viewModel.kickOutMember(it)
                }
            }
        )
    }

    //返回
    BackHandler {
        resultBackNavigator.navigateBack(
            result = (reportList.size != uiState.reportList.size)
        )
    }
}

@Composable
private fun ReportDialog(
    modifier: Modifier = Modifier,
    reportInformation: ReportInformation,
    onDismiss: () -> Unit,
    onSilence: (ReportInformation) -> Unit,
    onKick: (ReportInformation) -> Unit
) {
    Dialog(onDismissRequest = {
        onDismiss()
    }) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier.padding(bottom = 25.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GrayButton(
                    text = "禁言",
                    shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
                ) {
                    onSilence.invoke(reportInformation)
                }

                GrayButton(
                    text = "踢出社團",
                    shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
                ) {
                    onKick.invoke(reportInformation)
                }

                Spacer(modifier = Modifier.height(20.dp))

                GrayButton(
                    text = "返回"
                ) {
                    onDismiss()
                }
            }
        }
    }
}

@Composable
private fun GroupReportScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    reportList: List<ReportInformation>,
    onIgnore: (ReportInformation) -> Unit,
    onBack: () -> Unit,
    onReport: (ReportInformation) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "檢舉審核",
                leadingEnable = true,
                moreEnable = false,
                backClick = {
                    onBack.invoke()
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            items(reportList) { report ->
                ReportItem(
                    reportInformation = report,
                    onFullMessageClick = {
                        navigator.navigate(
                            GroupReportMessageScreenDestination(
                                reportInformation = it
                            )
                        )
                    },
                    onReporterClick = {
                        navigator.navigate(
                            GroupReporterScreenDestination(
                                it.reporters?.toTypedArray() ?: arrayOf()
                            )
                        )
                    },
                    onIgnore = {
                        onIgnore.invoke(it)
                    },
                    onReportClick = {
                        onReport.invoke(it)
                    }
                )
            }
        }
    }
}

@Composable
private fun ReportItem(
    reportInformation: ReportInformation,
    onFullMessageClick: (ReportInformation) -> Unit,
    onReporterClick: (ReportInformation) -> Unit,
    onIgnore: (ReportInformation) -> Unit,
    onReportClick: (ReportInformation) -> Unit
) {
    val reportUser = reportInformation.reportee
    val channel = reportInformation.channel

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalColor.current.env_80)
            .padding(top = 20.dp, bottom = 20.dp, start = 24.dp, end = 24.dp)
    ) {
        //被檢舉人info
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape),
                model = reportUser?.thumbNail,
                contentScale = ContentScale.Crop,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.placeholder)
            )

            Spacer(modifier = Modifier.width(15.dp))

            Text(
                text = reportUser?.name.orEmpty(),
                fontSize = 16.sp,
                color = LocalColor.current.text.default_100
            )

            Spacer(modifier = Modifier.width(5.dp))

            Text(
                modifier = Modifier.weight(1f),
                text = reportUser?.serialNumber.toString(),
                fontSize = 12.sp,
                color = LocalColor.current.text.default_50
            )
        }

        Spacer(modifier = Modifier.height(13.dp))

        //檢舉原因
        reportInformation.mostReason?.let {
            Text(
                text = Utils.getReportReasonShowText(it),
                fontSize = 16.sp,
                color = LocalColor.current.specialColor.red
            )
        }

        Spacer(modifier = Modifier.height(13.dp))

        //Chat room title
        Text(
            text = "於聊天室「%s」發布：".format(channel?.name.orEmpty()),
            fontSize = 16.sp,
            color = LocalColor.current.text.default_100
        )

        Spacer(modifier = Modifier.height(13.dp))

        //被檢舉內文
        Box(
            modifier = Modifier
                .width(200.dp)
                .clip(
                    RoundedCornerShape(10.dp)
                )
                .background(LocalColor.current.background)
                .padding(15.dp)
        ) {
            Column {
                Text(
                    text = reportInformation.contentSnapshot.orEmpty(),
                    fontSize = 14.sp,
                    color = LocalColor.current.text.default_100,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    modifier = Modifier
                        .clickable {
                            onFullMessageClick.invoke(reportInformation)
                        },
                    text = "完整訊息",
                    style = TextStyle(textDecoration = TextDecoration.Underline),
                    fontSize = 14.sp,
                    color = LocalColor.current.text.default_100,
                )
            }
        }

        Spacer(modifier = Modifier.height(13.dp))

        //檢舉人數
        Text(
            text = "檢舉人數",
            fontSize = 12.sp,
            color = LocalColor.current.text.default_50
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Text(
                text = "%d人・".format(reportInformation.reporters?.size ?: 0),
                fontSize = 16.sp,
                color = LocalColor.current.text.default_100
            )
            Text(
                modifier = Modifier.clickable {
                    onReporterClick.invoke(reportInformation)
                },
                text = "詳情",
                fontSize = 16.sp,
                color = LocalColor.current.text.default_100,
                style = TextStyle(textDecoration = TextDecoration.Underline)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        //按鈕
        Row {
            BorderButton(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                borderColor = LocalColor.current.text.default_50,
                textColor = LocalColor.current.text.default_100,
                text = "不懲處"
            ) {
                onIgnore.invoke(reportInformation)
            }

            Spacer(modifier = Modifier.width(20.dp))

            BlueButton(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                text = "懲處"
            ) {
                onReportClick.invoke(reportInformation)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupReportScreenPreview() {
    FanciTheme {
        GroupReportScreenView(
            navigator = EmptyDestinationsNavigator,
            reportList = listOf(
                ReportInformation(
                    reportee = GroupMember(
                        name = "Name",
                        thumbNail = "",
                        serialNumber = 7788
                    ),
                    mostReason = ReportReason.harass,
                    channel = Channel(name = "❣️｜聊聊大廳")
                ),
                ReportInformation(
                    reportee = GroupMember(
                        name = "Name",
                        thumbNail = "",
                        serialNumber = 7788
                    ),
                    mostReason = ReportReason.harass,
                    channel = Channel(name = "❣️｜聊聊大廳")
                )
            ),
            onIgnore = {},
            onBack = {},
            onReport = {}
        )
    }
}