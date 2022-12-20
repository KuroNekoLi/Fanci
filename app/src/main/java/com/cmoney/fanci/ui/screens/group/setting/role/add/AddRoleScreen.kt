package com.cmoney.fanci.ui.screens.group.setting.role.add

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanci.LocalDependencyContainer
import com.cmoney.fanci.MainActivity
import com.cmoney.fanci.R
import com.cmoney.fanci.extension.showColorPickerDialogBottomSheet
import com.cmoney.fanci.ui.screens.group.setting.role.viewmodel.RoleManageViewModel
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.screens.shared.setting.BottomButtonScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.PermissionCategory
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel

@Destination
@Composable
fun AddRoleScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    group: Group,
    viewModel: RoleManageViewModel = koinViewModel()
) {
    val mainActivity = LocalDependencyContainer.current

    AddRoleScreenView(
        modifier,
        navigator,
        group,
        mainActivity,
        viewModel.uiState.permissionList.orEmpty()
    )

    viewModel.fetchPermissionList()
}

@Composable
private fun AddRoleScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    group: Group,
    mainActivity: MainActivity,
    permissionList: List<PermissionCategory>
) {
    val tabList = listOf("樣式", "權限", "成員")
    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "新增角色",
                leadingEnable = true,
                moreEnable = false,
                backClick = {
                    navigator.popBackStack()
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LocalColor.current.env_80)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            TabRow(
                selectedTabIndex = selectedIndex,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .height(40.dp)
                    .padding(1.dp)
                    .clip(RoundedCornerShape(35)),
                indicator = { tabPositions: List<TabPosition> ->
                    Box {}
                },
                backgroundColor = LocalColor.current.env_100
            ) {
                tabList.forEachIndexed { index, text ->
                    val selected = selectedIndex == index
                    Tab(
                        modifier = if (selected) Modifier
                            .padding(2.dp)
                            .clip(RoundedCornerShape(25))
                            .background(
                                LocalColor.current.env_60
                            )
                        else Modifier
                            .padding(10.dp)
                            .clip(RoundedCornerShape(15))
                            .background(
                                Color.Transparent
                            ),
                        selected = selected,
                        onClick = { selectedIndex = index },
                        text = {
                            Text(
                                text = text, color = Color.White, fontSize = 14.sp
                            )
                        }
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                when (selectedIndex) {
                    //樣式
                    0 -> {
                        StyleView(mainActivity = mainActivity)
                    }
                    //權限
                    1 -> {

                    }
                    //成員
                    else -> {

                    }
                }
            }

            //========== 儲存 ==========
            BottomButtonScreen(
                text = "確定新增"
            ) {

            }
        }
    }
}

@Composable
fun StyleView(modifier: Modifier = Modifier, mainActivity: MainActivity) {
    val TAG = "StyleView"
    val maxLength = 10
    var textState by remember { mutableStateOf("") }
    val defaultColor = LocalColor.current.specialColor.red
    var selectRoleColor = remember {
        mutableStateOf(
            defaultColor
        )
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.padding(
                top = 20.dp,
                start = 24.dp,
                end = 24.dp,
                bottom = 20.dp
            )
        ) {
            Text(
                text = "角色組名稱",
                fontSize = 14.sp,
                color = LocalColor.current.text.default_100
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "%d/%d".format(textState.length, maxLength),
                fontSize = 14.sp,
                color = LocalColor.current.text.default_50
            )
        }

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = textState,
            colors = TextFieldDefaults.textFieldColors(
                textColor = LocalColor.current.text.default_100,
                backgroundColor = LocalColor.current.background,
                cursorColor = LocalColor.current.primary,
                disabledLabelColor = LocalColor.current.text.default_30,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            onValueChange = {
                if (it.length <= maxLength) {
                    textState = it
                }
            },
            maxLines = 1,
            textStyle = TextStyle.Default.copy(fontSize = 16.sp),
            placeholder = {
                Text(
                    text = "輸入角色名稱",
                    fontSize = 16.sp,
                    color = LocalColor.current.text.default_30
                )
            }
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            modifier = Modifier.padding(start = 24.dp, bottom = 10.dp),
            text = "角色顯示顏色", fontSize = 14.sp, color = LocalColor.current.text.default_100
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(LocalColor.current.background)
                .clickable {
                    mainActivity.showColorPickerDialogBottomSheet(
                        selectedColor = selectRoleColor.value
                    ) {
                        KLog.i(TAG, "color pick:$it")
                        selectRoleColor.value = it
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                modifier = Modifier.padding(start = 25.dp),
                painter = painterResource(id = R.drawable.rule_manage),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = selectRoleColor.value)
            )

            Text(
                modifier = Modifier.padding(start = 17.dp),
                text = "紅色 ",
                fontSize = 17.sp,
                color = LocalColor.current.text.default_100
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddRoleScreenPreview() {
    FanciTheme {
        AddRoleScreenView(
            navigator = EmptyDestinationsNavigator,
            group = Group(),
            mainActivity = MainActivity(),
            permissionList = emptyList()
        )
    }
}