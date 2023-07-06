package com.cmoney.kolfanci.ui.screens.follow

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.OnBottomReached
import com.cmoney.kolfanci.ui.destinations.ApplyForGroupScreenDestination
import com.cmoney.kolfanci.ui.destinations.CreateGroupScreenDestination
import com.cmoney.kolfanci.ui.main.MainActivity
import com.cmoney.kolfanci.ui.screens.follow.viewmodel.FollowViewModel
import com.cmoney.kolfanci.ui.screens.group.dialog.GroupItemDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.GroupItemScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.LoginDialogScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@Composable
fun EmptyFollowScreen(
    viewModel: FollowViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    groupList: List<Group>,
    onLoadMore: () -> Unit,
    isLoading: Boolean
) {
    val uiState = viewModel.uiState
    val openGroupDialog by viewModel.openGroupDialog.collectAsState()

    EmptyFollowScreenView(
        modifier = modifier,
        groupList = groupList,
        onJoinClick = {
            viewModel.openGroupItemDialog(it)
        },
        onCreateClick = {
            viewModel.onCreateGroupClick()
        },
        onLoadMore = {
            onLoadMore.invoke()
        },
        isLoading = isLoading
    )

    openGroupDialog?.let { group ->
        GroupItemDialogScreen(
            groupModel = group,
            onDismiss = {
                viewModel.closeGroupItemDialog()
            },
            onConfirm = {
                viewModel.joinGroup(it)
            }
        )
    }

    if (uiState.showLoginDialog) {
        val context = LocalContext.current
        LoginDialogScreen(
            onDismiss = {
                viewModel.dismissLoginDialog()
            },
            onLogin = {
                viewModel.dismissLoginDialog()
                (context as? MainActivity)?.startLogin()
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
    groupList: List<Group>,
    onJoinClick: (Group) -> Unit,
    onCreateClick: () -> Unit,
    onLoadMore: () -> Unit,
    isLoading: Boolean
) {
    val listState = rememberLazyListState()

    listState.OnBottomReached {
        onLoadMore.invoke()
    }

    LazyColumn(
        modifier = modifier
            .padding(top = 20.dp, start = 20.dp, end = 20.dp),
        state = listState
    ) {
        //Header
        item {
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
        }
        //List group
        items(groupList) {
            Spacer(modifier = Modifier.height(10.dp))
            GroupItemScreen(
                groupModel = it
            ) { groupModel ->
                onJoinClick.invoke(groupModel)
            }
            Spacer(modifier = Modifier.height(10.dp))
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

@Preview(showBackground = true)
@Composable
fun EmptyFollowScreenPreview() {
    FanciTheme {
        EmptyFollowScreenView(
            groupList = emptyList(),
            onJoinClick = {},
            onCreateClick = {},
            onLoadMore = {},
            isLoading = true
        )
    }
}