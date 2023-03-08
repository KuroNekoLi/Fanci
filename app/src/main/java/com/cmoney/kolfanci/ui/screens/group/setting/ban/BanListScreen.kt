package com.cmoney.kolfanci.ui.screens.group.setting.ban

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.kolfanci.ui.screens.group.setting.ban.viewmodel.BanListViewModel
import com.cmoney.kolfanci.ui.screens.group.setting.ban.viewmodel.BanUiModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.AlertDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.item.DisBanItemScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.User
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel
import com.cmoney.kolfanci.R

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
        }
    )

    //解除禁言 彈窗
    if (showDisBanDialog.value.first) {
        AlertDialogScreen(
            onDismiss = {
                showDisBanDialog.value = Pair(false, null)
            },
            title = showDisBanDialog.value.second?.user?.name + " 禁言中",
        ) {
            DisBanItemScreen(
                onConfirm = {
                    showDisBanDialog.value.second?.let {
                        viewModel.liftBanUser(
                            groupId = group.id.orEmpty(),
                            userId = it.user?.id.orEmpty()
                        )
                    }
                    showDisBanDialog.value = Pair(false, null)
                },
                onDismiss = {
                    showDisBanDialog.value = Pair(false, null)
                }
            )
        }
    }
}

@Composable
private fun BanListScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    banUserList: List<BanUiModel>,
    onClick: (BanUiModel) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "禁言列表",
                leadingEnable = true,
                moreEnable = false,
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
            AsyncImage(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape),
                model = user?.thumbNail,
                contentScale = ContentScale.Crop,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.placeholder)
            )

            Spacer(modifier = Modifier.width(15.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user?.name.orEmpty(),
                    fontSize = 16.sp,
                    color = LocalColor.current.text.default_100
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = user?.serialNumber.toString(),
                    fontSize = 12.sp,
                    color = LocalColor.current.text.default_50
                )
            }

            Text(
                text = "調整", fontSize = 14.sp, color = LocalColor.current.primary
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "被禁言日：%s".format(banUiModel.startDay),
            fontSize = 16.sp,
            color = LocalColor.current.text.default_100
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "禁言時長：%s".format(banUiModel.duration),
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
                    user = User(
                        name = "Hi",
                        serialNumber = 123456
                    ),
                    startDay = "2022/12/12",
                    duration = "3日"
                ),
                BanUiModel(
                    user = User(
                        name = "Hi2",
                        serialNumber = 123456
                    ),
                    startDay = "2022/12/14",
                    duration = "1日"
                )
            ),
            onClick = {}
        )
    }
}