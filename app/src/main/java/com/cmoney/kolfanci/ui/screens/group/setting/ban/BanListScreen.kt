package com.cmoney.kolfanci.ui.screens.group.setting.ban

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fanciapi.fanci.model.User
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.screens.group.setting.ban.viewmodel.BanListViewModel
import com.cmoney.kolfanci.ui.screens.group.setting.ban.viewmodel.BanUiModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.DisBanDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.member.MemberInfoItemScreen
import com.cmoney.kolfanci.ui.screens.shared.member.MemberItemScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel

@Destination
@Composable
fun BanListScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    group: Group,
    viewModel: BanListViewModel = koinViewModel()
) {
    val TAG = "BanListScreen"
    val defaultClick = Pair<Boolean, BanUiModel?>(false, null)
    val showDisBanDialog = remember { mutableStateOf(defaultClick) }

    val uiState = viewModel.uiState
    if (uiState.banUserList == null) {
        viewModel.fetchBanList(group.id.orEmpty())
    }

    BanListScreenView(
        modifier = modifier,
        navigator = navigator,
        banUserList = uiState.banUserList.orEmpty(),
        onClick = {
            KLog.i(TAG, "on fix click:$it")
            showDisBanDialog.value = Pair(true, it)
        },
        loading = uiState.loading
    )

    //解除禁言 彈窗
    if (showDisBanDialog.value.first) {
        val name = showDisBanDialog.value.second?.user?.name.orEmpty()

        DisBanDialogScreen(
            name = name,
            onDismiss = {
                showDisBanDialog.value = Pair(false, null)
            },
            onConfirm = {
                showDisBanDialog.value.second?.let {
                    viewModel.liftBanUser(
                        groupId = group.id.orEmpty(),
                        userId = it.user?.id.orEmpty()
                    )
                }
            }
        )
    }
}

@Composable
private fun BanListScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    banUserList: List<BanUiModel>,
    onClick: (BanUiModel) -> Unit,
    loading: Boolean
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = stringResource(id = R.string.ban_list),
                backClick = {
                    navigator.popBackStack()
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(LocalColor.current.env_80),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(banUserList) { banUser ->
                BanUserItem(banUser) {
                    onClick.invoke(banUser)
                }
            }

            if (loading) {
                item {
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

@Composable
private fun BanUserItem(banUiModel: BanUiModel, onClick: () -> Unit) {
    val user = banUiModel.user
    Column(
        modifier = Modifier
            .background(LocalColor.current.background)
            .clickable {
                onClick.invoke()
            }
            .padding(top = 20.dp, bottom = 20.dp, start = 25.dp, end = 25.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            user?.apply {
                MemberInfoItemScreen(
                    modifier = Modifier.weight(1f),
                    groupMember = this
                )
            }

            Text(
                text = stringResource(id = R.string.adjust), fontSize = 14.sp, color = LocalColor.current.primary
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = stringResource(R.string.ban_start_at, banUiModel.startDay),
            fontSize = 16.sp,
            color = LocalColor.current.text.default_100
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.ban_duration, banUiModel.duration),
            fontSize = 16.sp,
            color = LocalColor.current.text.default_100
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BanListScreenPreview() {
    FanciTheme {
        BanListScreenView(
            navigator = EmptyDestinationsNavigator,
            banUserList = listOf(
                BanUiModel(
                    user = GroupMember(
                        name = "Hi",
                        serialNumber = 123456
                    ),
                    startDay = "2022/12/12",
                    duration = "3日"
                ),
                BanUiModel(
                    user = GroupMember(
                        name = "Hi2",
                        serialNumber = 123456
                    ),
                    startDay = "2022/12/14",
                    duration = "1日"
                )
            ),
            onClick = {},
            loading = true
        )
    }
}