package com.cmoney.kolfanci.ui.screens.group.setting.vip.info

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.ChannelAuthType
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.usecase.VipManagerUseCase
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanPermissionModel
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanPermissionOptionModel
import com.cmoney.kolfanci.ui.screens.shared.toolbar.EditToolbarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.EmptyResultBackNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator

/**
 * 選擇此頻道VIP方案下擁有權限的頁面
 *
 * @param modifier 元件外框的Modifier
 * @param permissionModel 目前選擇的頻道權限
 * @param permissionOptionModels 可以選擇的權限選項
 * @param resultNavigator 回傳選擇結果的callback
 */
@Destination
@Composable
fun VipPlanInfoEditChannelPermissionScreen(
    modifier: Modifier = Modifier,
    permissionModel: VipPlanPermissionModel,
    permissionOptionModels: Array<VipPlanPermissionOptionModel>,
    resultNavigator: ResultBackNavigator<VipPlanPermissionModel>
) {
    VipPlanInfoEditChannelPermissionScreenView(
        modifier = modifier,
        permissionModel = permissionModel,
        permissionOptionModels = permissionOptionModels,
        onSave = { index ->
            val option = permissionOptionModels[index]
            resultNavigator.navigateBack(
                result = permissionModel.copy(
                    authType = option.authType,
                    permissionTitle = option.name
                )
            )
        },
        onBack = {
            resultNavigator.navigateBack()
        }
    )
}

@Composable
fun VipPlanInfoEditChannelPermissionScreenView(
    modifier: Modifier = Modifier,
    permissionModel: VipPlanPermissionModel,
    permissionOptionModels: Array<VipPlanPermissionOptionModel>,
    onSave: (Int) -> Unit,
    onBack: () -> Unit
) {
    var selectedIndex by remember {
        mutableStateOf(
            permissionOptionModels.indexOfFirst { option ->
                option.authType == permissionModel.authType
            }
        )
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            EditToolbarScreen(
                title = stringResource(id = R.string.choose_channel),
                saveClick = {
                    onSave(selectedIndex)
                },
                backClick = onBack
            )
        },
        backgroundColor = LocalColor.current.env_80
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(permissionOptionModels) { index, option ->
                    PermissionOptionItem(
                        vipPlanPermissionOptionModel = option,
                        selected = index == selectedIndex,
                        onClick = {
                            selectedIndex = index
                        }
                    )
                }
            }
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(id = R.drawable.permission_table),
                contentScale = ContentScale.FillWidth,
                contentDescription = "permission_table"
            )
        }
    }
}

@Preview
@Composable
fun VipPlanInfoEditChannelPermissionScreenViewPreview() {
    FanciTheme {
        VipPlanInfoEditChannelPermissionScreen(
            permissionModel = VipPlanPermissionModel(
                id = "102",
                name = "健身肌肉男",
                canEdit = true,
                permissionTitle = "基本權限",
                authType = ChannelAuthType.basic
            ),
            permissionOptionModels = VipManagerUseCase.getVipPlanPermissionOptionsMockData()
                .toTypedArray(),
            resultNavigator = EmptyResultBackNavigator()
        )
    }
}

@Composable
private fun PermissionOptionItem(
    modifier: Modifier = Modifier,
    vipPlanPermissionOptionModel: VipPlanPermissionOptionModel,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(67.dp)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(24.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = vipPlanPermissionOptionModel.name,
                color = if (selected) {
                    LocalColor.current.primary
                } else {
                    LocalColor.current.text.default_100
                },
                fontSize = 17.sp
            )
            Text(
                text = vipPlanPermissionOptionModel.description,
                color = LocalColor.current.text.default_50,
                fontSize = 14.sp
            )
        }
        if (selected) {
            Icon(
                painter = painterResource(id = R.drawable.checked),
                contentDescription = "selected",
                tint = LocalColor.current.primary
            )
            Spacer(modifier = Modifier.width(28.dp))
        } else {
            Spacer(modifier = Modifier.width(24.dp))
        }
    }
}

@Preview
@Composable
fun PermissionOptionItemPreview() {
    FanciTheme {
        Column(
            modifier = Modifier.background(LocalColor.current.background)
        ) {
            PermissionOptionItem(
                vipPlanPermissionOptionModel = VipPlanPermissionOptionModel(
                    name = "名稱",
                    description = "描述",
                    authType = ChannelAuthType.basic
                ),
                selected = true,
                onClick = {
                }
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .background(color = LocalColor.current.env_80)
            )
            PermissionOptionItem(
                vipPlanPermissionOptionModel = VipPlanPermissionOptionModel(
                    name = "名稱",
                    description = "描述",
                    authType = ChannelAuthType.noPermission
                ),
                selected = false,
                onClick = {
                }
            )
        }
    }
}
