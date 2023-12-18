package com.cmoney.kolfanci.ui.screens.group.setting.report

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.ChannelTabType
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fanciapi.fanci.model.ReportInformation
import com.cmoney.fanciapi.fanci.model.ReportReason
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.fancylog.model.data.Page
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.getDisplayType
import com.cmoney.kolfanci.extension.isVip
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.common.GrayButton
import com.cmoney.kolfanci.ui.destinations.GroupReportMessageScreenDestination
import com.cmoney.kolfanci.ui.destinations.GroupReporterScreenDestination
import com.cmoney.kolfanci.ui.screens.group.setting.report.viewmodel.GroupReportViewModel
import com.cmoney.kolfanci.ui.screens.shared.toolbar.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.BanDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.KickOutDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.member.HorizontalMemberItemScreen
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

    LaunchedEffect(key1 = group) {
        AppUserLogger.getInstance().log(Page.GroupSettingsReportReview)
    }

    GroupReportScreenView(
        modifier = modifier,
        navigator = navigator,
        reportList = uiState.reportList,
        onIgnore = {
            viewModel.ignoreReport(it)
        },
        onBack = {
            AppUserLogger.getInstance().log(Clicked.PunishBack)
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
                AppUserLogger.getInstance().log(Clicked.PunishMute)
                viewModel.dismissReportDialog()
                viewModel.showSilenceDialog(it)
            },
            onKick = {
                AppUserLogger.getInstance().log(Clicked.PunishKickOut)
                viewModel.dismissReportDialog()
                viewModel.showKickDialog(it)
            }
        )
    }

    //禁言 dialog
    if (uiState.showSilenceDialog != null) {
        val name = uiState.showSilenceDialog.reportee?.name.orEmpty()
        val isVip = uiState.showSilenceDialog.reportee?.isVip() ?: false

        BanDialogScreen(
            name = name,
            isVip = isVip,
            onDismiss = {
                AppUserLogger.getInstance().log(Clicked.PunishMuteBack)
                viewModel.dismissSilenceDialog()
            },
            onConfirm = {
                viewModel.dismissSilenceDialog()
                uiState.showSilenceDialog.let { reportInfo ->
                    viewModel.silenceUser(reportInfo, it)
                }
            }
        )

        AppUserLogger.getInstance().log(Page.GroupSettingsReportReviewMute)
    }

    //踢出 dialog
    if (uiState.kickDialog != null) {
        val name = uiState.kickDialog.reportee?.name.orEmpty()
        val isVip = uiState.kickDialog.reportee?.isVip() ?: false

        KickOutDialogScreen(
            name = name,
            isVip = isVip,
            onDismiss = {
                AppUserLogger.getInstance().log(Clicked.PunishKickOutCancel)
                viewModel.dismissKickDialog()
            },
            onConfirm = {
                AppUserLogger.getInstance().log(Clicked.PunishKickOutConfirmKickOut)
                viewModel.dismissKickDialog()
                uiState.kickDialog.let {
                    viewModel.kickOutMember(it)
                }
            }
        )

        AppUserLogger.getInstance().log(Page.GroupSettingsReportReviewKickOut)
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
                    text = stringResource(id = R.string.ban),
                    shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
                ) {
                    onSilence.invoke(reportInformation)
                }

                GrayButton(
                    text = stringResource(id = R.string.kick_out_from_group),
                    shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
                ) {
                    onKick.invoke(reportInformation)
                }

                Spacer(modifier = Modifier.height(20.dp))

                GrayButton(
                    text = stringResource(id = R.string.back)
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
                title = stringResource(id = R.string.report_review),
                backClick = {
                    onBack.invoke()
                }
            )
        },
        backgroundColor = LocalColor.current.env_80
    ) { innerPadding ->
        if (reportList.isEmpty()) {
            ReportEmptyScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            ReportReviewLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                reportList = reportList,
                navigator = navigator,
                onIgnore = onIgnore,
                onReport = onReport
            )
        }
    }
}

@Composable
private fun ReportReviewLazyColumn(
    modifier: Modifier = Modifier,
    reportList: List<ReportInformation>,
    navigator: DestinationsNavigator,
    onIgnore: (ReportInformation) -> Unit,
    onReport: (ReportInformation) -> Unit
) {
    LazyColumn(
        modifier = modifier,
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

@Composable
private fun ReportEmptyScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(110.dp),
            painter = painterResource(id = R.drawable.flower_box),
            contentDescription = null,
            colorFilter = ColorFilter.tint(LocalColor.current.text.default_30)
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = stringResource(id = R.string.report_is_empty),
            fontSize = 16.sp,
            color = LocalColor.current.text.default_30
        )
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
        reportUser?.let {
            HorizontalMemberItemScreen(
                groupMember = it
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

        val reportTitle = when (reportInformation.tabType) {
            ChannelTabType.bulletinboard -> {
                val messageId = reportInformation.contentId.orEmpty()

                when (messageId.count { it == '-' }) {  //區分 貼文,留言,回覆
                    //留言
                    1 -> {
                        "於「%s」發布一則留言：".format(channel?.name.orEmpty())
                    }
                    //回覆
                    2 -> {
                        "於「%s」發布一則回覆：".format(channel?.name.orEmpty())
                    }

                    else -> {
                        "於「%s」發布一則貼文：".format(channel?.name.orEmpty())
                    }
                }
            }

            else -> "於「%s」發布一則聊天聊天訊息：".format(channel?.name.orEmpty())
        }

        //title
        Text(
            text = reportTitle,
            fontSize = 16.sp,
            color = LocalColor.current.text.default_100
        )

        Spacer(modifier = Modifier.height(13.dp))

        //被檢舉內文
        Box(
            modifier = Modifier
                .fillMaxWidth()
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

                //如果有圖片附件,顯示
                reportInformation.mediasSnapshot?.let { medias ->
                    medias.forEach { media ->
                        Spacer(modifier = Modifier.height(5.dp))
                        val content = media.getDisplayType()
                        Text(
                            text = content,
                            fontSize = 14.sp,
                            color = LocalColor.current.text.default_100,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

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
                text = stringResource(id = R.string.not_punish)
            ) {
                AppUserLogger.getInstance().log(Clicked.ReportReviewNoPunish)
                onIgnore.invoke(reportInformation)
            }

            Spacer(modifier = Modifier.width(20.dp))

            BlueButton(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                text = stringResource(id = R.string.punish)
            ) {
                AppUserLogger.getInstance().log(Clicked.ReportReviewPunish)
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