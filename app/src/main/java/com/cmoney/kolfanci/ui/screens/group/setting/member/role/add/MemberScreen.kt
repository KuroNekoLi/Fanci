package com.cmoney.kolfanci.ui.screens.group.setting.member.role.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
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
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.socks.library.KLog
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.destinations.AddMemberScreenDestination
import com.cmoney.kolfanci.ui.screens.shared.member.MemberItemScreen

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
                        onRemoveClick.invoke(it)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
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