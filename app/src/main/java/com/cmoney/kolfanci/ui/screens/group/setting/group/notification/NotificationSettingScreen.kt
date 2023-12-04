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
import com.cmoney.fanciapi.fanci.model.PushNotificationSetting
import com.cmoney.fanciapi.fanci.model.PushNotificationSettingType
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.fancylog.model.data.Page
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.model.mock.MockData
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
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
    groupId: String,
    viewModel: NotificationSettingViewModel = koinViewModel(),
    pushNotificationSetting: PushNotificationSetting,
    resultNavigator: ResultBackNavigator<PushNotificationSetting>
) {
    val notificationItems by viewModel.notificationSetting.collectAsState()

    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current
        ?.onBackPressedDispatcher

    val saveSettingComplete by viewModel.saveSettingComplete.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.fetchAllNotificationSetting(pushNotificationSetting)
        AppUserLogger.getInstance()
            .log(Page.GroupSettingsNotificationSetting)
    }

    NotificationSettingView(
        modifier = modifier,
        notificationSettingItems = notificationItems,
        onBackClick = {
            onBackPressedDispatcher?.onBackPressed()
        }
    ) { pushNotificationSettingWrap ->
        val clickedEvent = when (pushNotificationSettingWrap.pushNotificationSetting.settingType) {
            PushNotificationSettingType.silent -> {
                Clicked.NotificationSettingMute
            }
            PushNotificationSettingType.newPost -> {
                Clicked.NotificationSettingOnlyNewPost
            }
            PushNotificationSettingType.newStory -> {
                Clicked.NotificationSettingAnyNews
            }
            else -> {
                null
            }
        }
        if (clickedEvent != null) {
            AppUserLogger.getInstance()
                .log(clickedEvent)
        }
        viewModel.onNotificationSettingItemClick(pushNotificationSettingWrap)
    }

    BackHandler {
        viewModel.saveNotificationSetting(groupId)
    }

    saveSettingComplete?.let {
        resultNavigator.navigateBack(it)
    }
}

@Composable
private fun NotificationSettingView(
    modifier: Modifier = Modifier,
    notificationSettingItems: List<PushNotificationSettingWrap>,
    onBackClick: () -> Unit,
    onClick: (PushNotificationSettingWrap) -> Unit
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
                        pushNotificationSettingWrap = notificationSettingItem,
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
    pushNotificationSettingWrap: PushNotificationSettingWrap,
    onClick: (PushNotificationSettingWrap) -> Unit
) {

    val pushNotificationSetting = pushNotificationSettingWrap.pushNotificationSetting

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(LocalColor.current.background)
            .clickable(enabled = !pushNotificationSettingWrap.isChecked) {
                onClick.invoke(pushNotificationSettingWrap)
            }
            .padding(top = 10.dp, bottom = 10.dp, start = 24.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = pushNotificationSetting.title.orEmpty(),
                fontSize = 16.sp,
                color = if (pushNotificationSettingWrap.isChecked) {
                    LocalColor.current.primary
                } else {
                    LocalColor.current.text.default_100
                }
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = pushNotificationSetting.description.orEmpty(),
                fontSize = 12.sp,
                color = LocalColor.current.text.default_50
            )
        }

        if (pushNotificationSettingWrap.isChecked) {
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
            notificationSettingItems = MockData.mockNotificationSettingItemWrapList,
            onBackClick = {}
        ) {}
    }
}

@Preview
@Composable
fun NotificationSettingItemViewPreview() {
    FanciTheme {
        NotificationSettingItemView(
            pushNotificationSettingWrap = MockData.mockNotificationSettingItemWrapList.first(),
            onClick = {}
        )
    }
}
