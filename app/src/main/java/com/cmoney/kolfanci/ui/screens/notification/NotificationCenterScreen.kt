package com.cmoney.kolfanci.ui.screens.notification

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.model.mock.MockData
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@Destination
@Composable
fun NotificationCenterScreen(
    navController: DestinationsNavigator,
    viewModel: NotificationCenterViewModel = koinViewModel()
) {
    val notificationCenterDataList by viewModel.notificationCenter.collectAsState()

    NotificationCenterView(
        navController = navController,
        notificationCenterDataList = notificationCenterDataList,
        onClick = {
            //TODO
        }
    )
}

@Composable
fun NotificationCenterView(
    navController: DestinationsNavigator,
    modifier: Modifier = Modifier,
    notificationCenterDataList: List<NotificationCenterData>,
    onClick: (NotificationCenterData) -> Unit
) {
    Scaffold(modifier = modifier.fillMaxSize(),
        topBar = {
            TopBarScreen(
                title = stringResource(id = R.string.notification_center),
                backClick = {
                    AppUserLogger.getInstance().log(Clicked.MemberPageHome)
                    navController.popBackStack()
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

                items(notificationCenterDataList) { notificationCenterData ->
                    NotificationItem(
                        notificationCenterData = notificationCenterData,
                        onClick = onClick
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    modifier: Modifier = Modifier,
    notificationCenterData: NotificationCenterData,
    onClick: (NotificationCenterData) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                (if (notificationCenterData.isRead) {
                    Color.Transparent
                } else {
                    LocalColor.current.background
                })
            )
            .clickable {
                onClick.invoke(notificationCenterData)
            }
            .padding(top = 10.dp, bottom = 10.dp, start = 24.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        //Icon
        AsyncImage(
            modifier = Modifier
                .size(45.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            model = notificationCenterData.icon,
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            //Title
            Text(
                text = notificationCenterData.title,
                fontSize = 16.sp,
                color = LocalColor.current.text.default_100
            )

            Spacer(modifier = Modifier.width(5.dp))

            //Description
            Text(
                text = notificationCenterData.description,
                fontSize = 16.sp,
                color = LocalColor.current.text.default_50
            )
        }
    }
}

@Preview
@Composable
fun NotificationCenterScreenPreview() {
    FanciTheme {
        NotificationCenterView(
            navController = EmptyDestinationsNavigator,
            notificationCenterDataList = MockData.mockNotificationCenter,
            onClick = {}
        )
    }
}

@Preview
@Composable
fun NotificationCenterItemPreview() {
    FanciTheme {
        NotificationItem(
            notificationCenterData = MockData.mockNotificationCenter.first(),
            onClick = {}
        )
    }
}