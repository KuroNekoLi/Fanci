package com.cmoney.kolfanci.ui.screens.group.setting.member.all

import FlowRow
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.toColor
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.ui.common.CircleImage
import com.cmoney.kolfanci.ui.destinations.MemberManageScreenDestination
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.member.viewmodel.MemberViewModel
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
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
    if (uiState.groupMember == null && uiState.loading) {
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
        isLoading = uiState.loading,
        group = group,
        groupMemberList = uiState.groupMember.orEmpty().map {
            it.groupMember
        },
        onSearch = {
            viewModel.onSearchMember(
                groupId = group.id.orEmpty(),
                keyword = it
            )
        }
    )
}

@Composable
fun AllMemberScreenView(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    group: Group,
    groupMemberList: List<GroupMember>,
    isLoading: Boolean,
    onSearch: (String) -> Unit
) {
    val TAG = "AllMemberScreenView"
    var textState by remember { mutableStateOf("") }
    val maxLength = 20

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
        Column {
            //Search bar
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                value = textState,
                colors = TextFieldDefaults.textFieldColors(
                    textColor = LocalColor.current.text.default_100,
                    backgroundColor = LocalColor.current.background,
                    cursorColor = LocalColor.current.primary,
                    disabledLabelColor = LocalColor.current.text.default_30,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                onValueChange = {
                    if (it.length <= maxLength) {
                        textState = it
                        onSearch.invoke(it)
                    }
                },
                shape = RoundedCornerShape(4.dp),
                maxLines = 1,
                textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                placeholder = {
                    Text(
                        text = "輸入名稱搜尋成員",
                        fontSize = 16.sp,
                        color = LocalColor.current.text.default_30
                    )
                },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.member_search),
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    if (textState.isNotEmpty()) {
                        Image(
                            modifier = Modifier.clickable {
                                textState = ""
                                onSearch.invoke("")
                            },
                            painter = painterResource(id = R.drawable.clear),
                            contentDescription = null
                        )
                    }
                }
            )

            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                items(groupMemberList) { groupMember ->
                    MemberItem(groupMember = groupMember) {
                        KLog.i(TAG, "member click:$it")
                        if (Constant.MyGroupPermission.createOrEditRole == true) {
                            navController.navigate(MemberManageScreenDestination(
                                group = group,
                                groupMember =  groupMember
                            ))
                        }
                    }
                    Spacer(modifier = Modifier.height(1.dp))
                }

                if (isLoading) {
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(size = 32.dp),
                                color = LocalColor.current.primary
                            )
                        }
                    }
                }
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

        if (Constant.MyGroupPermission.createOrEditRole == true) {
            Text(text = "管理", fontSize = 14.sp, color = LocalColor.current.primary)
        }
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
            isLoading = false,
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
            group = Group(),
            onSearch = {}
        )
    }
}