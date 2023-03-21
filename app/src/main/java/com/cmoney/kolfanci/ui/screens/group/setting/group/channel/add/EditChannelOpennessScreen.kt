package com.cmoney.kolfanci.ui.screens.group.setting.group.channel.add

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.theme.Color_80FFFFFF
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
 */
@Destination
@Composable
fun EditChannelOpennessScreen(
    modifier: Modifier = Modifier,
    isNeedApproval: Boolean = true,
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<Boolean>
) {
    val TAG = "EditChannelOpennessScreen"

    var isNeedApproval by remember {
        mutableStateOf(isNeedApproval)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "頻公開度",
                leadingEnable = true,
                moreEnable = false,
                backClick = {
                    navigator.popBackStack()
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
                            isNeedApproval = false
                        }
                        .padding(top = 10.dp, bottom = 10.dp, start = 16.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "完全公開",
                            fontSize = 17.sp,
                            color = if (!isNeedApproval) {
                                LocalColor.current.primary
                            } else {
                                LocalColor.current.text.default_100
                            }
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(text = "任何人都能看到頻道，任何人都能進入。", fontSize = 14.sp, color = Color_80FFFFFF)
                    }

                    if (!isNeedApproval) {
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
                            isNeedApproval = true
                        }
                        .padding(top = 10.dp, bottom = 10.dp, start = 16.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "不公開",
                            fontSize = 17.sp,
                            color = if (isNeedApproval) {
                                LocalColor.current.primary
                            } else {
                                LocalColor.current.text.default_100
                            }
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = "任何人都能看到頻道，只有指定成員才能進入。",
                            fontSize = 14.sp,
                            color = Color_80FFFFFF
                        )
                    }

                    if (isNeedApproval) {
                        Image(
                            painter = painterResource(id = R.drawable.checked),
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(25.dp))
                    }
                }
            }

            //========== 儲存 ==========
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp)
                    .background(LocalColor.current.env_100),
                contentAlignment = Alignment.Center
            ) {
                BlueButton(
                    text = "儲存變更"
                ) {
                    KLog.i(TAG, "onSave click:$isNeedApproval")
                    resultNavigator.navigateBack(result = isNeedApproval)
                }
            }
        }
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