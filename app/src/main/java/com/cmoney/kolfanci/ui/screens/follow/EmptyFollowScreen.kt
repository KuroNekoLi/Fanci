package com.cmoney.kolfanci.ui.screens.follow

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.common.CircleImage
import com.cmoney.kolfanci.ui.screens.follow.viewmodel.FollowViewModel
import com.cmoney.kolfanci.ui.screens.shared.dialog.DialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.item.BanDayItemScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.item.InputInviteCodeScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import org.koin.androidx.compose.koinViewModel

@Composable
fun EmptyFollowScreen(
    modifier: Modifier = Modifier,
    viewModel: FollowViewModel = koinViewModel()
) {
    val allMyApplyGroup by viewModel.allMyApplyGroup.collectAsState()

    val showInviteCodeDialog by viewModel.isShowInviteCodeDialog.collectAsState()

    EmptyFollowScreenView(
        modifier = modifier,
        allMyApplyGroup = allMyApplyGroup,
        onCreateClick = {
            viewModel.onCreateGroupClick()
        },
        onApplyGroupClick = {
            AppUserLogger.getInstance()
                .log(Clicked.HomeWaitToJoinGroup)
            viewModel.openGroupItemDialog(it)
        },
        onInputInviteCodeClick = {
            viewModel.onInputInviteCodeClick()
        }
    )

    LaunchedEffect(key1 = Unit) {
        viewModel.fetchAllMyGroupApplyUnConfirmed()
    }

    if (showInviteCodeDialog) {
        DialogScreen(
            title = "輸入邀請碼",
            subTitle = "透過邀請碼加入社團吧！",
            onDismiss = {
                viewModel.closeInviteCodeDialog()
            }
        ) {
            InputInviteCodeScreen(
                onConfirm = { inviteCode ->
                    viewModel.onInputInviteCode(inviteCode)
                },
                onCancel = {
                    viewModel.closeInviteCodeDialog()
                }
            )
        }
    }
}

@Composable
private fun EmptyFollowScreenView(
    modifier: Modifier = Modifier,
    onCreateClick: () -> Unit,
    allMyApplyGroup: List<Group>,
    onApplyGroupClick: (Group) -> Unit,
    onInputInviteCodeClick: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(64.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.sizeIn(minWidth = 300.dp, minHeight = 300.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier
                        .width(113.dp)
                        .aspectRatio(1f),
                    contentScale = ContentScale.Fit,
                    painter = painterResource(id = R.drawable.planetary),
                    contentDescription = null,
                )

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.empty_follow_tip),
                    fontSize = 16.sp,
                    color = LocalColor.current.text.default_30,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        BlueButton(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .height(50.dp),
            text = stringResource(id = R.string.input_invite_code)
        ) {
            onInputInviteCodeClick.invoke()
        }

        Spacer(modifier = Modifier.height(20.dp))

        BlueButton(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .height(50.dp),
            text = stringResource(id = R.string.create_group)
        ) {
            onCreateClick.invoke()
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {

            items(allMyApplyGroup) {
                ApplyGroupItem(it, onClick = onApplyGroupClick)
            }
        }
    }
}

@Composable
private fun ApplyGroupItem(group: Group, onClick: (Group) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(93.dp)
            .background(LocalColor.current.background)
            .clickable {
                onClick.invoke(group)
            }
            .padding(start = 15.dp, end = 15.dp, top = 22.dp, bottom = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleImage(
            modifier = Modifier
                .size(45.dp),
            imageUrl = group.thumbnailImageUrl.orEmpty()
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(verticalArrangement = Arrangement.Center) {

            Text(
                text = stringResource(id = R.string.wait_for_apply), style = TextStyle(
                    fontSize = 16.sp,
                    color = LocalColor.current.text.default_100
                )
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = stringResource(id = R.string.wait_for_group_apply).format(group.name.orEmpty()),
                style = TextStyle(
                    fontSize = 14.sp,
                    color = LocalColor.current.text.default_80
                )
            )
        }

    }
}


@Preview
@Composable
fun EmptyFollowScreenPreview() {
    FanciTheme {
        EmptyFollowScreenView(
            onCreateClick = {},
            allMyApplyGroup = listOf(
                Group(),
                Group(),
                Group(),
                Group()
            ),
            onApplyGroupClick = {},
            onInputInviteCodeClick = {}
        )
    }
}