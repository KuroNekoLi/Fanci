package com.cmoney.kolfanci.ui.screens.group.setting.group.notification

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.mock.MockData
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.DialogScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import org.koin.androidx.compose.koinViewModel

@Destination
@Composable
fun NotificationSettingScreen(
    navController: DestinationsNavigator,
    modifier: Modifier = Modifier,
    viewModel: NotificationSettingViewModel = koinViewModel(),
    notificationSettingItem: NotificationSettingItem,
    resultNavigator: ResultBackNavigator<NotificationSettingItem>
) {
    val notificationItems by viewModel.notificationSetting.collectAsState()

    val isShowNotificationOpenAlert by viewModel.showOpenNotificationSettingAlert.collectAsState()

    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current
        ?.onBackPressedDispatcher

    val saveSettingComplete by viewModel.saveSettingComplete.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.onSettingItemClick(notificationSettingItem)
    }

    NotificationSettingView(
        modifier = modifier,
        notificationSettingItems = notificationItems,
        onBackClick = {
            onBackPressedDispatcher?.onBackPressed()
        }
    ) {
        viewModel.onSettingItemClick(it)
    }

    BackHandler {
        viewModel.saveNotificationSetting()
    }

    saveSettingComplete?.let {
        resultNavigator.navigateBack(it)
    }

    if (isShowNotificationOpenAlert) {
        DialogScreen(
            title = stringResource(id = R.string.open_notification_setting),
            subTitle = stringResource(id = R.string.open_notification_setting_desc),
            onDismiss = {
                viewModel.dismissNotificationOpenAlert()
            }) {
            Column {
                BlueButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    text = stringResource(id = R.string.forward_system_setting)
                ) {
                    viewModel.dismissNotificationOpenAlert()
                    viewModel.openSystemNotificationSetting()
                }

                Spacer(modifier = Modifier.height(20.dp))

                BorderButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    text = stringResource(id = R.string.back),
                    borderColor = LocalColor.current.text.default_100
                ) {
                    viewModel.dismissNotificationOpenAlert()
                }
            }
        }
    }

}

@Composable
private fun NotificationSettingView(
    modifier: Modifier = Modifier,
    notificationSettingItems: List<NotificationSettingItem>,
    onBackClick: () -> Unit,
    onClick: (NotificationSettingItem) -> Unit
) {
    Scaffold(modifier = modifier.fillMaxSize(),
        topBar = {
            TopBarScreen(
                title = stringResource(id = R.string.notification_center),
                backClick = {
                    onBackClick.invoke()
                }
            )
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .background(LocalColor.current.env_80)
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }

                items(notificationSettingItems) { notificationSettingItem ->
                    NotificationSettingItemView(
                        notificationSettingItem = notificationSettingItem,
                        onClick = onClick
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationSettingItemView(
    modifier: Modifier = Modifier,
    notificationSettingItem: NotificationSettingItem,
    onClick: (NotificationSettingItem) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(LocalColor.current.background)
            .clickable(enabled = !notificationSettingItem.isChecked) {
                onClick.invoke(notificationSettingItem)
            }
            .padding(top = 10.dp, bottom = 10.dp, start = 24.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = notificationSettingItem.title,
                fontSize = 16.sp,
                color = if (notificationSettingItem.isChecked) {
                    LocalColor.current.primary
                } else {
                    LocalColor.current.text.default_100
                }
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = notificationSettingItem.description,
                fontSize = 12.sp,
                color = LocalColor.current.text.default_50
            )
        }

        if (notificationSettingItem.isChecked) {
            Image(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = R.drawable.checked),
                colorFilter = ColorFilter.tint(
                    LocalColor.current.primary
                ),
                contentDescription = null
            )
        }

    }

}

@Preview
@Composable
fun NotificationSettingScreenPreview() {
    FanciTheme {
        NotificationSettingView(
            notificationSettingItems = listOf(
                NotificationSettingItem(
                    title = "title",
                    description = "description",
                    isChecked = true,
                    shortTitle = ""
                ),
                NotificationSettingItem(
                    title = "title1",
                    description = "description2",
                    isChecked = false,
                    shortTitle = ""
                ),
                NotificationSettingItem(
                    title = "title2",
                    description = "description2",
                    isChecked = false,
                    shortTitle = ""
                )
            ),
            onBackClick = {}
        ) {}
    }
}

@Preview
@Composable
fun NotificationSettingItemViewPreview() {
    FanciTheme {
        NotificationSettingItemView(
            notificationSettingItem = MockData.mockNotificationSettingItem,
            onClick = {}
        )
    }
}
