package com.cmoney.kolfanci.ui.screens.group.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.destinations.BanListScreenDestination
import com.cmoney.kolfanci.destinations.GroupReportScreenDestination
import com.cmoney.kolfanci.ui.screens.shared.SettingItemScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.ReportInformation
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

/**
 * 秩序管理
 */
@Composable
fun GroupRuleManageScreen(
    modifier: Modifier = Modifier,
    group: Group,
    navController: DestinationsNavigator,
    reportList: List<ReportInformation>?
) {
    val reportCount = reportList?.size ?: 0

    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(start = 25.dp, bottom = 9.dp),
            text = "秩序管理", fontSize = 14.sp, color = LocalColor.current.text.default_100
        )

        SettingItemScreen(
            iconRes = R.drawable.report_apply,
            text = "檢舉審核",
            onItemClick = {
                if (reportCount != 0) {
                    reportList?.let {
                        navController.navigate(
                            GroupReportScreenDestination(
                                reportList = it.toTypedArray(),
                                group = group
                            )
                        )
                    }
                }
            }
        ) {
            if (reportCount != 0) {
                Text(
                    text = reportCount.toString(),
                    fontSize = 17.sp,
                    color = LocalColor.current.text.default_100
                )
            }
        }

        Spacer(modifier = Modifier.height(1.dp))

        SettingItemScreen(
            iconRes = R.drawable.ban,
            text = "禁言列表",
            onItemClick = {
                navController.navigate(
                    BanListScreenDestination(
                        group = group
                    )
                )
            }
        )
        Spacer(modifier = Modifier.height(1.dp))

//        SettingItemScreen(
//            iconRes = R.drawable.block_usr,
//            text = "黑名單列表",
//            onItemClick= {}
//        )
    }
}

@Preview(showBackground = true)
@Composable
fun GroupRuleManageScreenPreview() {
    FanciTheme {
        GroupRuleManageScreen(
            group = Group(),
            navController = EmptyDestinationsNavigator,
            reportList = emptyList()
        )
    }
}