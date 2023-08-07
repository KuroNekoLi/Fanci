package com.cmoney.kolfanci.ui.screens.group.setting.member.role.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.destinations.AddMemberScreenDestination
import com.cmoney.kolfanci.ui.screens.shared.member.MemberItemScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.socks.library.KLog

@Composable
fun MemberScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    group: Group,
    memberList: List<GroupMember>,
    onRemoveClick: (GroupMember) -> Unit
) {
    val TAG = "MemberScreen"

    Column(modifier = modifier.fillMaxSize()) {
        if (Constant.isCanEditRole()) {
            BorderButton(
                modifier = Modifier
                    .background(LocalColor.current.env_80)
                    .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 20.dp)
                    .fillMaxWidth()
                    .height(40.dp),
                text = "新增成員", borderColor = LocalColor.current.text.default_100
            ) {
                AppUserLogger.getInstance()
                    .log(Clicked.MembersAddMember)
                navigator.navigate(
                    AddMemberScreenDestination(
                        group = group,
                        excludeMember = memberList.toTypedArray()
                    )
                )
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(memberList) { member ->
                MemberItemScreen(
                    groupMember = member,
                    onMemberClick = {
                        KLog.i(TAG, "remove:$it")
                        AppUserLogger.getInstance()
                            .log(Clicked.MembersRemove)
                        onRemoveClick.invoke(it)
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun MemberScreenPreview() {
    FanciTheme {
        MemberScreen(
            navigator = EmptyDestinationsNavigator,
            group = Group(),
            memberList = emptyList()
        ) {}
    }
}