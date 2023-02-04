package com.cmoney.kolfanci.ui.screens.group.setting.report

import FlowRow
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.cmoney.kolfanci.ui.screens.group.setting.member.all.RoleItem
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.cmoney.kolfanci.R
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
                leadingEnable = true,
                moreEnable = false,
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
                ReporterItem(reportUser = reporter)
            }
        }
    }
}

@Composable
private fun ReporterItem(reportUser: GroupMember) {
    Row(
        modifier = Modifier
            .background(LocalColor.current.env_80)
            .padding(top = 20.dp, bottom = 20.dp, start = 24.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //大頭貼
        AsyncImage(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape),
            model = reportUser.thumbNail,
            contentScale = ContentScale.Crop,
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.resource_default)
        )

        Spacer(modifier = Modifier.width(15.dp))

        Column {
            Row {
                //使用者名稱
                Text(
                    text = reportUser.name.orEmpty(),
                    fontSize = 16.sp,
                    color = LocalColor.current.text.default_100
                )

                Spacer(modifier = Modifier.width(5.dp))

                //使用者編號
                Text(
                    modifier = Modifier.weight(1f),
                    text = reportUser.serialNumber.toString(),
                    fontSize = 12.sp,
                    color = LocalColor.current.text.default_50
                )
            }

            Spacer(modifier = Modifier.width(7.dp))

            // TODO:
            //使用者擁有權限
            FlowRow(
                horizontalGap = 5.dp,
                verticalGap = 5.dp,
            ) {
                reportUser.roleInfos?.let {
                    repeat(it.size) { index ->
                        RoleItem(
                            it[index]
                        )
                    }
                }
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
                    serialNumber = 12345
                )
            )
        )
    }
}