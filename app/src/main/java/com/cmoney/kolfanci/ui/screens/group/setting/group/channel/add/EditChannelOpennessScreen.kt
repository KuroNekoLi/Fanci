package com.cmoney.kolfanci.ui.screens.group.setting.group.channel.add

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.fancylog.model.data.From
import com.cmoney.fancylog.model.data.Page
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.ui.screens.shared.toolbar.EditToolbarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.EmptyResultBackNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.socks.library.KLog

/**
 * 設定 頻道公開度
 * @param isNeedApproval 預設是否公開
 * @param from 事件參數
 */
@Destination
@Composable
fun EditChannelOpennessScreen(
    modifier: Modifier = Modifier,
    from: From = From.Edit,
    isNeedApproval: Boolean = true,
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<Boolean>
) {
    val TAG = "EditChannelOpennessScreen"

    var isNeedApprovalCurrent by remember {
        mutableStateOf(isNeedApproval)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            EditToolbarScreen(
                title = stringResource(id = R.string.channel_openness),
                backClick = {
                    navigator.popBackStack()
                },
                saveClick = {
                    KLog.i(TAG, "saveClick click.")
                    resultNavigator.navigateBack(result = isNeedApprovalCurrent)
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .background(LocalColor.current.env_80)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LocalColor.current.background)
                        .clickable {
                            AppUserLogger.getInstance()
                                .log(Clicked.PermissionsPublic, from)
                            isNeedApprovalCurrent = false
                        }
                        .padding(top = 10.dp, bottom = 10.dp, start = 16.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(id = R.string.full_public),
                            fontSize = 17.sp,
                            color = if (!isNeedApprovalCurrent) {
                                LocalColor.current.primary
                            } else {
                                LocalColor.current.text.default_100
                            }
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = stringResource(id = R.string.full_public_desc),
                            fontSize = 14.sp,
                            color = LocalColor.current.text.default_50
                        )
                    }

                    if (!isNeedApprovalCurrent) {
                        Image(
                            painter = painterResource(id = R.drawable.checked),
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(25.dp))
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LocalColor.current.background)
                        .clickable {
                            AppUserLogger.getInstance()
                                .log(Clicked.PermissionsNonPublic, from)
                            isNeedApprovalCurrent = true
                        }
                        .padding(top = 10.dp, bottom = 10.dp, start = 16.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(id = R.string.not_public),
                            fontSize = 17.sp,
                            color = if (isNeedApprovalCurrent) {
                                LocalColor.current.primary
                            } else {
                                LocalColor.current.text.default_100
                            }
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = stringResource(id = R.string.not_public_desc),
                            fontSize = 14.sp,
                            color = LocalColor.current.text.default_50
                        )
                    }

                    if (isNeedApprovalCurrent) {
                        Image(
                            painter = painterResource(id = R.drawable.checked),
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(25.dp))
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        AppUserLogger.getInstance().log(Page.GroupSettingsChannelManagementPermissionsOpenness)
    }
}

@Preview(showBackground = true)
@Composable
fun EditChannelOpennessScreenPreview() {
    FanciTheme {
        EditChannelOpennessScreen(
            navigator = EmptyDestinationsNavigator,
            resultNavigator = EmptyResultBackNavigator()
        )
    }
}