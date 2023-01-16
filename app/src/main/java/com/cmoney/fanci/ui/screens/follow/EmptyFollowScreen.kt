package com.cmoney.fanci.ui.screens.follow

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanci.LocalDependencyContainer
import com.cmoney.fanci.R
import com.cmoney.fanci.destinations.ApplyForGroupScreenDestination
import com.cmoney.fanci.destinations.CreateGroupScreenDestination
import com.cmoney.fanci.ui.screens.follow.state.FollowScreenState
import com.cmoney.fanci.ui.screens.follow.state.rememberFollowScreenState
import com.cmoney.fanci.ui.screens.group.dialog.GroupItemDialogScreen
import com.cmoney.fanci.ui.screens.shared.GroupItemScreen
import com.cmoney.fanci.ui.screens.shared.dialog.LoginDialogScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Group
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun EmptyFollowScreen(
    followScreenState: FollowScreenState = rememberFollowScreenState(),
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator
) {
    val uiState = followScreenState.viewModel.uiState
    val viewModel = followScreenState.viewModel
    val groupList = viewModel.groupList.observeAsState()
    val mainActivity = LocalDependencyContainer.current

    EmptyFollowScreenView(
        modifier = modifier,
        groupList = groupList,
        onJoinClick = {
            followScreenState.openGroupItemDialog(it)
        },
        onCreateClick = {
            viewModel.onCreateGroupClick()
        }
    )

    followScreenState.openGroupDialog.value?.let { group ->
        GroupItemDialogScreen(
            groupModel = group,
            onDismiss = {
                followScreenState.closeGroupItemDialog()
            },
            onConfirm = {
                followScreenState.viewModel.joinGroup(it)
            }
        )
    }

    if (uiState.showLoginDialog) {
        LoginDialogScreen(
            onDismiss = {
                viewModel.dismissLoginDialog()
            },
            onLogin = {
                viewModel.dismissLoginDialog()
                mainActivity.startLogin()
            }
        )
    }

    //打開 建立社團
    if (uiState.navigateToCreateGroup) {
        navigator.navigate(CreateGroupScreenDestination)
        viewModel.navigateDone()
    }

    //前往社團認證
    uiState.navigateToApproveGroup?.let {
        navigator.navigate(
            ApplyForGroupScreenDestination(
                group = it
            )
        )
        viewModel.navigateDone()
    }

}

@Composable
private fun EmptyFollowScreenView(
    modifier: Modifier = Modifier,
    groupList: State<List<Group>?>,
    onJoinClick: (Group) -> Unit,
    onCreateClick: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(top = 20.dp, start = 20.dp, end = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Image(
            painter = painterResource(id = R.drawable.fanci),
            contentDescription = null,
            colorFilter = ColorFilter.tint(
                color = LocalColor.current.primary
            )
        )
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.3f),
            contentScale = ContentScale.Fit,
            painter = painterResource(id = R.drawable.follow_empty),
            contentDescription = null,
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "加入Fanci社團跟我們一起快快樂樂！\n立即建立、加入熱門社團",
            fontSize = 14.sp,
            color = LocalColor.current.text.default_100,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(LocalColor.current.primary)
                .clip(RoundedCornerShape(4.dp))
                .clickable {
                    onCreateClick.invoke()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "建立社團",
                fontSize = 16.sp,
                color = LocalColor.current.text.other,
                textAlign = TextAlign.Center
            )
        }

        groupList.value?.forEach {
            Spacer(modifier = Modifier.height(10.dp))
            GroupItemScreen(
                groupModel = it
            ) { groupModel ->
                onJoinClick.invoke(groupModel)
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyFollowScreenPreview() {
    FanciTheme {
        EmptyFollowScreenView(
            groupList = remember { mutableStateOf(emptyList()) },
            onJoinClick = {},
            onCreateClick = {})
    }
}