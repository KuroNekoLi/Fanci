package com.cmoney.kolfanci.ui.screens.group.setting.report

import FlowRow
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.screens.group.setting.member.all.RoleItem
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.member.HorizontalMemberItemScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

/**
 * 檢舉人列表
 */
@Destination
@Composable
fun GroupReporterScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    reporterList: Array<GroupMember>
) {
    GroupReporterScreenView(
        modifier = modifier,
        navigator = navigator,
        reporterList = reporterList.toList()
    )
}

@Composable
private fun GroupReporterScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    reporterList: List<GroupMember>
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "檢舉人列表",
                backClick = {
                    navigator.popBackStack()
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(reporterList) { reporter ->
                HorizontalMemberItemScreen(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LocalColor.current.env_80)
                        .padding(top = 20.dp, bottom = 20.dp, start = 24.dp, end = 24.dp),
                    groupMember = reporter,
                    isShowRoleInfo = true
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupReporterScreenPreview() {
    FanciTheme {
        GroupReporterScreenView(
            navigator = EmptyDestinationsNavigator,
            reporterList = listOf(
                GroupMember(
                    name = "Name",
                    serialNumber = 12345,
                    roleInfos = listOf(
                        FanciRole(
                            name = "Role",
                            color = ""
                        )
                    )
                )
            )
        )
    }
}