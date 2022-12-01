package com.cmoney.fanci.ui.screens.group.setting.groupsetting

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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.cmoney.fanci.MainStateHolder
import com.cmoney.fanci.MainViewModel
import com.cmoney.fanci.R
import com.cmoney.fanci.ui.screens.group.setting.viewmodel.GroupSettingViewModel
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Group
import com.socks.library.KLog

const val GroupSettingBundleKey = "GroupSettingBundleKey"

/**
 * 社團設定 - 社團設定
 */
@Composable
fun GroupSettingSettingScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    group: Group,
    route: (MainStateHolder.Route) -> Unit,
    viewModel: GroupSettingViewModel,
    globalViewModel: MainViewModel
) {
    var groupParam = group
    viewModel.uiState.settingGroup?.let {
        groupParam = it
        globalViewModel.setCurrentGroup(it)
    }

    GroupSettingSettingView(
        modifier,
        navController,
        groupParam,
        route,
    )
}

@Composable
fun GroupSettingSettingView(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    group: Group,
    route: (MainStateHolder.Route) -> Unit
) {
    val TAG = "GroupSettingSettingScreen"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                navController,
                title = "社團設定",
                leadingEnable = true,
                moreEnable = false
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
                        route.invoke(
                            MainStateHolder.GroupRoute.GroupSettingSettingName(
                                group = group
                            )
                        )
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
                        route.invoke(
                            MainStateHolder.GroupRoute.GroupSettingSettingDesc(group = group)
                        )
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
                        route.invoke(
                            MainStateHolder.GroupRoute.GroupSettingSettingAvatar(group = group)
                        )
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
                    placeholder = painterResource(id = R.drawable.resource_default)
                )
            }

            Spacer(modifier = Modifier.height(1.dp))

            //========== 首頁背景 ==========
            Row(
                modifier = Modifier
                    .background(LocalColor.current.background)
                    .clickable {
                        KLog.i(TAG, "background image click")
                        route.invoke(
                            MainStateHolder.GroupRoute.GroupSettingSettingBackground(group = group)
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
                    placeholder = painterResource(id = R.drawable.resource_default)
                )
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
            navController = rememberNavController()
        ) {}
    }
}