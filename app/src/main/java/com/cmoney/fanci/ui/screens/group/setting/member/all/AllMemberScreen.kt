package com.cmoney.fanci.ui.screens.group.setting.member.all

import FlowRow
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanci.destinations.MemberManageScreenDestination
import com.cmoney.fanci.destinations.MemberRoleManageScreenDestination
import com.cmoney.fanci.extension.fromJsonTypeToken
import com.cmoney.fanci.extension.toColor
import com.cmoney.fanci.ui.common.CircleImage
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.screens.shared.member.viewmodel.MemberViewModel
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.google.gson.Gson
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel

@Destination
@Composable
fun AllMemberScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    group: Group,
    viewModel: MemberViewModel = koinViewModel(),
    setMemberResult: ResultRecipient<MemberManageScreenDestination, MemberManageResult>
) {
    val uiState = viewModel.uiState
    if (uiState.groupMember == null) {
        viewModel.fetchGroupMember(groupId = group.id.orEmpty())
    }

    //Edit callback
    setMemberResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }
            is NavResult.Value -> {
                val memberResult = result.value
                when (memberResult.type) {
                    MemberManageResult.Type.Update -> {
                        viewModel.editGroupMember(memberResult.groupMember)
                    }
                    MemberManageResult.Type.Delete -> {
                        viewModel.removeMember(memberResult.groupMember)
                    }
                }
            }
        }
    }

    AllMemberScreenView(
        modifier = modifier,
        navController = navController,
        group = group,
        groupMemberList = uiState.groupMember.orEmpty().map {
            it.groupMember
        }
    )
}

@Composable
fun AllMemberScreenView(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    group: Group,
    groupMemberList: List<GroupMember>
) {
    val TAG = "AllMemberScreenView"
    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "所有成員",
                leadingEnable = true,
                moreEnable = false,
                backClick = {
                    navController.popBackStack()
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(groupMemberList) { groupMember ->
                MemberItem(groupMember = groupMember) {
                    KLog.i(TAG, "member click:$it")
                    navController.navigate(MemberManageScreenDestination(
                        group = group,
                        groupMember =  groupMember
                    ))
                }
                Spacer(modifier = Modifier.height(1.dp))
            }
        }

    }
}

@Composable
private fun MemberItem(
    modifier: Modifier = Modifier,
    groupMember: GroupMember,
    onMemberClick: (GroupMember) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(LocalColor.current.background)
            .clickable {
                onMemberClick.invoke(groupMember)
            }
            .padding(start = 30.dp, end = 24.dp, top = 9.dp, bottom = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleImage(
            modifier = Modifier
                .size(34.dp),
            imageUrl = groupMember.thumbNail.orEmpty()
        )

        Spacer(modifier = Modifier.width(15.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                //名字
                Text(
                    text = groupMember.name.orEmpty(),
                    fontSize = 16.sp,
                    color = LocalColor.current.text.default_100
                )

                Spacer(modifier = Modifier.width(5.dp))

                //代號
                Text(
                    text = groupMember.serialNumber.toString(),
                    fontSize = 12.sp,
                    color = LocalColor.current.text.default_50
                )
            }

            Spacer(modifier = Modifier.height(7.dp))

            FlowRow(
                horizontalGap = 5.dp,
                verticalGap = 5.dp,
            ) {
                groupMember.roleInfos?.let {
                    repeat(it.size) { index ->
                        RoleItem(
                            it[index]
                        )
                    }
                }
            }
        }

        Text(text = "管理", fontSize = 14.sp, color = LocalColor.current.primary)
    }
}

@Composable
fun RoleItem(roleInfo: FanciRole) {
    val roleColor = LocalColor.current.roleColor.colors.firstOrNull {
        it.name == roleInfo.color
    } ?: LocalColor.current.roleColor.colors.first()

    val color = roleColor.hexColorCode?.toColor() ?: LocalColor.current.primary

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(40.dp))
            .background(LocalColor.current.env_80)
            .padding(start = 7.dp, end = 7.dp, top = 2.dp, bottom = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(color)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = roleInfo.name.orEmpty(),
                fontSize = 12.sp,
                color = LocalColor.current.text.default_100
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoleItemPreview() {
    FanciTheme {
        RoleItem(
            roleInfo = FanciRole(
                name = "Hello",
                color = ""
            )
        )
    }
}


@Preview(showBackground = true)
@Composable
fun AllMemberScreenPreview() {
    FanciTheme {
        AllMemberScreenView(
            navController = EmptyDestinationsNavigator,
            groupMemberList = listOf(
                GroupMember(
                    thumbNail = "",
                    name = "王力宏",
                    serialNumber = 123456,
                    roleInfos = listOf(
                        FanciRole(
                            name = "Role",
                            color = ""
                        )
                    )
                )
            ),
            group = Group()
        )
    }
}