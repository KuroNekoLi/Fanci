package com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting

import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.findActivity
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.destinations.*
import com.cmoney.kolfanci.ui.main.LocalDependencyContainer
import com.cmoney.kolfanci.ui.main.MainActivity
import com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.avatar.ImageChangeData
import com.cmoney.kolfanci.ui.screens.group.setting.viewmodel.GroupSettingViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.AlertDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.theme.ThemeColorCardScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel

/**
 * 社團設定 - 社團設定
 */
@Destination
@Composable
fun GroupSettingSettingScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    setNameResult: ResultRecipient<GroupSettingNameScreenDestination, String>,
    setDescResult: ResultRecipient<GroupSettingDescScreenDestination, String>,
    setAvatarResult: ResultRecipient<GroupSettingAvatarScreenDestination, ImageChangeData>,
    setBackgroundResult: ResultRecipient<GroupSettingBackgroundScreenDestination, ImageChangeData>,
    group: Group,
) {
    val globalViewModel = LocalDependencyContainer.current.globalViewModel
    val viewModel: GroupSettingViewModel = koinViewModel()

    setCallbackHandle(
        setNameResult = setNameResult,
        setDescResult = setDescResult,
        setAvatarResult = setAvatarResult,
        setBackgroundResult = setBackgroundResult,
        group = viewModel.uiState.settingGroup ?: group
    )

    var groupParam = group
    viewModel.uiState.settingGroup?.let {
        groupParam = it
        globalViewModel.setCurrentGroup(it)
    }

    GroupSettingSettingView(
        modifier,
        navController,
        groupParam,
        onDelectClick = {
            viewModel.onDelectClick()
        }
    )

    //第一次確認解散
    if (viewModel.uiState.showDelectDialog) {
        AlertDialogScreen(
            onDismiss = {
                viewModel.onDismissDelectDialog()
            },
            title = "你確定要把社團解散嗎？",
        ) {
            Column {
                Text(
                    text = "社團解散，所有內容、成員將會消失 平台「不會」有備份、復原功能。", fontSize = 17.sp, color = Color.White
                )

                Spacer(modifier = Modifier.height(20.dp))

                BorderButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    text = "返回",
                    borderColor = LocalColor.current.component.other,
                    textColor = Color.White
                ) {
                    viewModel.onDismissDelectDialog()
                }

                Spacer(modifier = Modifier.height(20.dp))

                BorderButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    text = "確定解散",
                    borderColor = LocalColor.current.specialColor.red,
                    textColor = LocalColor.current.specialColor.red
                ) {
                    viewModel.onDismissDelectDialog()
                    viewModel.onDelectReConfirm()
                }
            }
        }
    }

    //最終確認刪除
    if (viewModel.uiState.showFinalDelectDialog) {
        AlertDialogScreen(
            onDismiss = {
                viewModel.onDismissFinalDelectDialog()
            },
            title = "社團解散，最後確認通知！",
        ) {
            Column {
                Text(
                    text = "社團解散，所有內容、成員將會消失 平台「不會」有備份、復原功能。", fontSize = 17.sp, color = Color.White
                )

                Spacer(modifier = Modifier.height(20.dp))

                BorderButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    text = "返回",
                    borderColor = LocalColor.current.component.other,
                    textColor = Color.White
                ) {
                    viewModel.onDismissFinalDelectDialog()
                }

                Spacer(modifier = Modifier.height(20.dp))

                BorderButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    text = "確定解散",
                    borderColor = LocalColor.current.specialColor.red,
                    textColor = LocalColor.current.specialColor.red
                ) {
                    viewModel.onFinalConfirmDelete(group)
                }
            }
        }
    }

    //最終解散社團, 動作
    if (viewModel.uiState.popToMain) {
        val intent = Intent(LocalContext.current, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        LocalContext.current.findActivity().finish()
        LocalContext.current.startActivity(intent)
    }

}

/**
 * 處理 所有更改過的 callback
 */
@Composable
private fun setCallbackHandle(
    setNameResult: ResultRecipient<GroupSettingNameScreenDestination, String>,
    setDescResult: ResultRecipient<GroupSettingDescScreenDestination, String>,
    group: Group,
    viewModel: GroupSettingViewModel = koinViewModel(),
    setAvatarResult: ResultRecipient<GroupSettingAvatarScreenDestination, ImageChangeData>,
    setBackgroundResult: ResultRecipient<GroupSettingBackgroundScreenDestination, ImageChangeData>
) {
    //更改名字 callback
    setNameResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }
            is NavResult.Value -> {
                val changeName = result.value
                viewModel.changeGroupName(name = changeName, group)
            }
        }
    }

    //更改簡介 callback
    setDescResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }
            is NavResult.Value -> {
                val desc = result.value
                viewModel.changeGroupDesc(desc, group)
            }
        }
    }

    //更改頭貼
    setAvatarResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }
            is NavResult.Value -> {
                val uri = result.value
                viewModel.changeGroupAvatar(uri, group)
            }
        }
    }

    //更改背景
    setBackgroundResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }
            is NavResult.Value -> {
                val uri = result.value
                viewModel.changeGroupCover(uri, group)
            }
        }
    }
}

@Composable
fun GroupSettingSettingView(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    group: Group,
    onDelectClick: () -> Unit
) {
    val TAG = "GroupSettingSettingScreen"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "社團設定",
                leadingEnable = true,
                moreEnable = false,
                moreClick = {
                },
                backClick = {
                    navController.popBackStack()
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(LocalColor.current.env_80)
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            //========== 名稱 ==========
            Row(
                modifier = Modifier
                    .background(LocalColor.current.background)
                    .clickable {
                        KLog.i(TAG, "name click")
                        navController.navigate(GroupSettingNameScreenDestination(group = group))
                    }
                    .padding(start = 24.dp, end = 24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "社團名稱",
                        fontSize = 17.sp,
                        color = LocalColor.current.text.default_100,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = group.name.orEmpty(),
                        fontSize = 17.sp,
                        color = LocalColor.current.text.default_100
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Image(
                    painter = painterResource(id = R.drawable.next),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = LocalColor.current.text.default_80)
                )
            }
            Spacer(modifier = Modifier.height(1.dp))

            //========== 簡介 ==========
            Row(
                modifier = Modifier
                    .background(LocalColor.current.background)
                    .clickable {
                        KLog.i(TAG, "description click")
                        navController.navigate(GroupSettingDescScreenDestination(group = group))
                    }
                    .padding(start = 24.dp, end = 24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "社團簡介",
                        fontSize = 17.sp,
                        color = LocalColor.current.text.default_100,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (group.description?.isNotEmpty() == true) {
                        Text(
                            text = group.description.orEmpty(),
                            fontSize = 17.sp,
                            color = LocalColor.current.text.default_100
                        )
                    } else {
                        Text(
                            text = "填寫專屬於社團的簡介吧！",
                            fontSize = 17.sp,
                            color = LocalColor.current.text.default_30
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Image(
                    painter = painterResource(id = R.drawable.next),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = LocalColor.current.text.default_80)
                )
            }

            Spacer(modifier = Modifier.height(1.dp))

            //========== 社團圖示 ==========
            Row(
                modifier = Modifier
                    .background(LocalColor.current.background)
                    .clickable {
                        KLog.i(TAG, "avatar image click")
                        navController.navigate(GroupSettingAvatarScreenDestination(group = group))
                    }
                    .padding(start = 24.dp, end = 24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    modifier = Modifier.weight(1f),
                    text = "社團圖示",
                    fontSize = 17.sp,
                    color = LocalColor.current.text.default_100,
                    fontWeight = FontWeight.Bold
                )

                AsyncImage(
                    model = group.thumbnailImageUrl,
                    modifier = Modifier
                        .padding(top = 15.dp, bottom = 15.dp)
                        .size(55.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    placeholder = painterResource(id = R.drawable.placeholder)
                )
            }

            Spacer(modifier = Modifier.height(1.dp))

            //========== 首頁背景 ==========
            Row(
                modifier = Modifier
                    .background(LocalColor.current.background)
                    .clickable {
                        KLog.i(TAG, "background image click")
                        navController.navigate(
                            GroupSettingBackgroundScreenDestination(
                                group = group
                            )
                        )
                    }
                    .padding(start = 24.dp, end = 24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    modifier = Modifier.weight(1f),
                    text = "首頁背景",
                    fontSize = 17.sp,
                    color = LocalColor.current.text.default_100,
                    fontWeight = FontWeight.Bold
                )

                AsyncImage(
                    model = group.coverImageUrl,
                    modifier = Modifier
                        .padding(top = 15.dp, bottom = 15.dp)
                        .size(55.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    placeholder = painterResource(id = R.drawable.placeholder)
                )
            }

            Spacer(modifier = Modifier.height(1.dp))

            //========== 主題色彩 ==========
            Row(
                modifier = Modifier
                    .background(LocalColor.current.background)
                    .clickable {
                        KLog.i(TAG, "theme click")
                        navController.navigate(GroupSettingThemeScreenDestination())
                    }
                    .padding(start = 24.dp, end = 24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    modifier = Modifier.weight(1f),
                    text = "主題色彩",
                    fontSize = 17.sp,
                    color = LocalColor.current.text.default_100,
                    fontWeight = FontWeight.Bold
                )

                ThemeColorCardScreen(
                    modifier = Modifier
                        .padding(top = 15.dp, bottom = 15.dp)
                        .size(55.dp),
                    primary = LocalColor.current.primary,
                    env_100 = LocalColor.current.env_100,
                    env_80 = LocalColor.current.env_80,
                    env_60 = LocalColor.current.env_60
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            //========== 解散社團 ==========
            Box(
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
                    .background(LocalColor.current.background)
                    .clickable {
                        onDelectClick.invoke()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "解散社團", fontSize = 17.sp, color = LocalColor.current.specialColor.red)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupSettingSettingView() {
    FanciTheme {
        GroupSettingSettingView(
            group = Group(
                name = "韓勾ㄟ金針菇討論區",
                description = "我愛金針菇\uD83D\uDC97這裡是一群超愛金針菇的人類！喜歡的人就趕快來參加吧吧啊！"
            ),
            navController = EmptyDestinationsNavigator
        ) {}
    }
}