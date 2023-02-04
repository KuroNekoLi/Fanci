package com.cmoney.kolfanci.ui.screens.shared.member

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.extension.toColor
import com.cmoney.kolfanci.ui.screens.group.setting.group.channel.viewmodel.AddChannelRoleModel
import com.cmoney.kolfanci.ui.screens.group.setting.group.channel.viewmodel.ChannelSettingViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.setting.BottomButtonScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import org.koin.androidx.compose.koinViewModel
import com.cmoney.kolfanci.R
@Destination
@Composable
fun ShareAddRoleScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    viewModel: ChannelSettingViewModel = koinViewModel(),
    group: Group,
    buttonText: String,
    existsRole: Array<FanciRole>,
    resultNavigator: ResultBackNavigator<String>
) {

    val uiState = viewModel.uiState

    if (uiState.groupRoleList.isEmpty()) {
        viewModel.getGroupRoleList(group.id.orEmpty(), existsRole)
    }

    if (uiState.confirmRoleList.isNotEmpty()) {
        //confirm callback
        resultNavigator.navigateBack(uiState.confirmRoleList)
    }

    ShareAddRoleScreenView(
        modifier,
        navigator,
        uiState.groupRoleList,
        buttonText = buttonText,
        onRoleClick = {
            viewModel.onRoleClick(it)
        },
        onConfirm = {
            viewModel.onAddRoleConfirm()
        }
    )
}

@Composable
fun ShareAddRoleScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    roleList: List<AddChannelRoleModel>,
    buttonText: String,
    onRoleClick: (AddChannelRoleModel) -> Unit,
    onConfirm: () -> Unit
) {
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
    ) { innerPadding ->
        if (roleList.isNotEmpty()) {
            Column(modifier = Modifier.padding(innerPadding)) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(roleList) { roleModel ->
                        RoleItemScreen(roleModel) {
                            onRoleClick.invoke(it)
                        }
                    }
                }

                BottomButtonScreen(
                    text = buttonText
                ) {
                    onConfirm.invoke()
                }
            }

        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "尚未建立任何角色", fontSize = 14.sp, color = LocalColor.current.component.other
                )
            }
        }
    }
}


@Composable
private fun RoleItemScreen(
    addChannelRoleModel: AddChannelRoleModel,
    onRoleClick: (AddChannelRoleModel) -> Unit
) {
    val fanciRole = addChannelRoleModel.role
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalColor.current.background)
            .padding(start = 25.dp)
            .clickable {
                onRoleClick.invoke(addChannelRoleModel)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val roleColor = if (fanciRole.color?.isNotEmpty() == true) {
            var roleColor = LocalColor.current.specialColor.red

            val filterList = LocalColor.current.roleColor.colors.filter {
                it.name == fanciRole.color
            }

            if (filterList.isNotEmpty()) {
                filterList[0].hexColorCode?.let {
                    roleColor = it.toColor()
                }
            }
            roleColor
        } else {
            LocalColor.current.specialColor.red
        }

        Image(
            modifier = Modifier.size(25.dp),
            contentScale = ContentScale.FillBounds,
            painter = painterResource(id = R.drawable.rule_manage),
            colorFilter = ColorFilter.tint(color = roleColor),
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = fanciRole.name.orEmpty(), fontSize = 16.sp, color = Color.White)
            Text(
                text = "%d 位成員".format(fanciRole.userCount ?: 0),
                fontSize = 12.sp,
                color = LocalColor.current.component.other
            )
        }

        Box(
            modifier = Modifier.padding(end = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(17.dp)
                    .clip(CircleShape)
                    .background(
                        if (addChannelRoleModel.isChecked) {
                            LocalColor.current.primary
                        } else {
                            Color.Transparent
                        }
                    )
            )

            Canvas(modifier = Modifier.size(57.dp)) {
                drawCircle(
                    color = Color.White,
                    radius = 30f,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShareAddRoleScreenViewPreview() {
    FanciTheme {
        ShareAddRoleScreenView(
            navigator = EmptyDestinationsNavigator,
            roleList = listOf(
                AddChannelRoleModel(
                    role = FanciRole(
                        name = "Hello",
                        userCount = 3
                    )
                ),
                AddChannelRoleModel(
                    role = FanciRole(
                        name = "Hello2",
                        userCount = 3
                    )
                )
            ),
            onRoleClick = {},
            onConfirm = {},
            buttonText = "新增角色成為頻道管理員"
        )
    }
}