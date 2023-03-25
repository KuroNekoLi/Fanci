package com.cmoney.kolfanci.ui.screens.shared.member

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.toColor
import com.cmoney.kolfanci.ui.screens.shared.CircleCheckedScreen
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.member.viewmodel.AddChannelRoleModel
import com.cmoney.kolfanci.ui.screens.shared.member.viewmodel.RoleViewModel
import com.cmoney.kolfanci.ui.screens.shared.setting.BottomButtonScreen
import com.cmoney.kolfanci.ui.screens.shared.snackbar.CustomMessage
import com.cmoney.kolfanci.ui.screens.shared.snackbar.FanciSnackBarScreen
import com.cmoney.kolfanci.ui.theme.Color_80FFFFFF
import com.cmoney.kolfanci.ui.theme.Color_99FFFFFF
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel

/**
 * 選擇挑選角色
 */
@Destination
@Composable
fun ShareAddRoleScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    viewModel: RoleViewModel = koinViewModel(),
    group: Group,
    title: String = "新增角色",
    subTitle: String = "",
    buttonText: String,
    existsRole: Array<FanciRole>,
    resultNavigator: ResultBackNavigator<String>
) {
    val TAG = "ShareAddRoleScreen"
    val uiState = viewModel.uiState
    val loadingState = viewModel.loadingState

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
        title,
        subTitle,
        uiState.groupRoleList,
        buttonText = buttonText,
        isLoading = loadingState.isLoading,
        onRoleClick = {
            viewModel.onRoleClick(it)
        },
        onConfirm = {
            viewModel.onAddRoleConfirm()
        },
        onBack = {
            resultNavigator.navigateBack(
                result = viewModel.fetchSelectedRole()
            )
        }
    )

    if (uiState.showAddSuccessTip) {
        FanciSnackBarScreen(
            modifier = Modifier.padding(bottom = 70.dp),
            message = CustomMessage(
                textString = "角色新增成功！",
                iconRes = R.drawable.all_member,
                iconColor = Color_99FFFFFF,
                textColor = Color.White
            )
        ) {
            viewModel.dismissAddSuccessTip()
        }
    }

    BackHandler {
        KLog.i(TAG, "BackHandler")
        resultNavigator.navigateBack(
            result = viewModel.fetchSelectedRole()
        )
    }

}

@Composable
fun ShareAddRoleScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    title: String,
    subTitle: String,
    roleList: List<AddChannelRoleModel>,
    buttonText: String,
    isLoading: Boolean,
    onRoleClick: (AddChannelRoleModel) -> Unit,
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = title,
                leadingEnable = true,
                moreEnable = false,
                backClick = {
                    onBack.invoke()
                }
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(size = 32.dp),
                    color = LocalColor.current.primary
                )
            }
        } else {
            if (roleList.isNotEmpty()) {
                Column(modifier = Modifier.padding(innerPadding)) {

                    if (subTitle.isNotEmpty()) {
                        Text(
                            modifier = Modifier.padding(20.dp),
                            text = subTitle, fontSize = 14.sp, color = Color_80FFFFFF
                        )
                    }

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
                        text = "尚未建立任何角色",
                        fontSize = 14.sp,
                        color = LocalColor.current.component.other
                    )
                }
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
            .clickable {
                onRoleClick.invoke(addChannelRoleModel)
            }
            .padding(start = 25.dp),
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

        CircleCheckedScreen(
            isChecked = addChannelRoleModel.isChecked
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ShareAddRoleScreenViewPreview() {
    FanciTheme {
        ShareAddRoleScreenView(
            navigator = EmptyDestinationsNavigator,
            title = "新增角色",
            subTitle = "直接指定角色，讓一批成員進入私密頻道。",
            isLoading = false,
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
            buttonText = "新增角色成為頻道管理員",
            onBack = {}
        )
    }
}