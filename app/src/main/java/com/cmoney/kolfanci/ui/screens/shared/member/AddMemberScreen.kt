package com.cmoney.kolfanci.ui.screens.shared.member

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.kolfanci.ui.screens.chat.message.OnBottomReached
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.member.viewmodel.MemberViewModel
import com.cmoney.kolfanci.ui.screens.shared.member.viewmodel.GroupMemberSelect
import com.cmoney.kolfanci.ui.screens.shared.setting.BottomButtonScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel
import com.cmoney.kolfanci.R
@Destination
@Composable
fun AddMemberScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    group: Group,
    viewModel: MemberViewModel = koinViewModel(),
    resultNavigator: ResultBackNavigator<String>
) {
    val TAG = "AddMemberScreen"
    val uiState = viewModel.uiState
    val listState: LazyListState = rememberLazyListState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "新增成員",
                leadingEnable = true,
                moreEnable = false,
                backClick = {
                    navigator.popBackStack()
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.background(LocalColor.current.env_80)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(uiState.groupMember ?: emptyList()) { member ->
                    MemberItem(member) {
                        viewModel.onMemberClick(it)
                    }
                }
            }

            BottomButtonScreen(
                text = "新增成員至角色中"
            ) {
                KLog.i(TAG, "on save click.")
                resultNavigator.navigateBack(
                    result = viewModel.fetchSelectedMember()
                )
            }
        }

        listState.OnBottomReached {
            viewModel.onLoadMoreGroupMember(group.id.orEmpty())
        }
    }

    if (uiState.groupMember == null) {
        viewModel.fetchGroupMember(groupId = group.id.orEmpty())
    }
}

@Composable
private fun MemberItem(
    groupMemberSelect: GroupMemberSelect,
    onMemberClick: (GroupMemberSelect) -> Unit
) {
    val groupMember = groupMemberSelect.groupMember
    Row(
        modifier = Modifier
            .background(LocalColor.current.background)
            .clickable {
                onMemberClick.invoke(groupMemberSelect)
            }
            .padding(start = 30.dp, top = 8.dp, bottom = 8.dp, end = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape),
            model = groupMember.thumbNail.orEmpty(),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.resource_default)
        )

        Spacer(modifier = Modifier.width(15.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = groupMember.name.orEmpty(),
                fontSize = 16.sp,
                color = LocalColor.current.text.default_100
            )
            Text(
                text = groupMember.serialNumber.toString(),
                fontSize = 12.sp,
                color = LocalColor.current.text.default_50
            )
        }

        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(17.dp)
                    .clip(CircleShape)
                    .background(
                        if (groupMemberSelect.isSelected) {
                            LocalColor.current.primary
                        } else {
                            Color.Transparent
                        }
                    )
            )

            Canvas(modifier = Modifier.size(57.dp)) {
                drawCircle(
                    color = Color.White,
                    radius = 30f,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MemberItemPreview() {
    FanciTheme {
        MemberItem(
            groupMemberSelect = GroupMemberSelect(
                groupMember = GroupMember(
                    name = "Hello",
                    serialNumber = 12345
                ),
                isSelected = true
            )
        ) {}
    }
}