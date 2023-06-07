package com.cmoney.kolfanci.ui.screens.group.setting.vip.info

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.common.ChannelText
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanPermissionModel
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 * VIP方案權限管理頁
 *
 * @param modifier 元件外框 Modifier
 * @param permissionModels 頻道權限物件集合
 * @param onEditPermission 當要改變個別頻道權限設定時
 * @receiver
 */
@Composable
fun VipPlanInfoPermissionPage(
    modifier: Modifier = Modifier,
    permissionModels: List<VipPlanPermissionModel>,
    onEditPermission: (Int) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(id = R.string.vip_manage_channel_permission_description),
            fontSize = 14.sp,
            color = LocalColor.current.text.default_50
        )
        Spacer(modifier = Modifier.height(20.dp))
        val listState = rememberLazyListState()
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            itemsIndexed(permissionModels) { index, permissionModel ->
                ChannelPermissionBarItem(
                    permissionModel = permissionModel,
                    onClick = {
                        onEditPermission(index)
                    }
                )
            }
        }
    }
}

@Composable
private fun ChannelPermissionBarItem(
    permissionModel: VipPlanPermissionModel,
    onClick: () -> Unit
) {
    val clickModifier = if (permissionModel.canEdit) {
        Modifier.clickable {
            onClick()
        }
    } else {
        Modifier
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color = LocalColor.current.background)
            .then(clickModifier)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.message),
            contentDescription = null,
            tint = LocalColor.current.component.other
        )
        Spacer(modifier = Modifier.width(14.dp))
        ChannelText(
            modifier = Modifier.weight(weight = 1f),
            text = permissionModel.name
        )
        Text(
            text = permissionModel.permissionTitle,
            color = if (permissionModel.canEdit) {
                LocalColor.current.primary
            } else {
                LocalColor.current.text.default_30
            },
            fontSize = 14.sp
        )
    }
}

@Preview(showBackground = true, heightDp = 667, widthDp = 375)
@Composable
fun VipPlanInfoPermissionPagePreview() {
    FanciTheme {
        Box(modifier = Modifier.background(color = MaterialTheme.colors.background)) {
            VipPlanInfoPermissionPage(
                permissionModels = listOf(
                    VipPlanPermissionModel(
                        name = "歡迎新朋友",
                        canEdit = false,
                        permissionTitle = "公開頻道",
                        authType = "basic"
                    ),
                    VipPlanPermissionModel(
                        name = "健身肌肉男",
                        canEdit = true,
                        permissionTitle = "進階權限",
                        authType = "basic"
                    )
                ),
                onEditPermission = { _ ->
                }
            )
        }
    }
}

@Preview
@Composable
fun ChannelPermissionBarItemPreview() {
    FanciTheme {
        Column {
            ChannelPermissionBarItem(
                permissionModel = VipPlanPermissionModel(
                    name = "歡迎新朋友",
                    canEdit = false,
                    permissionTitle = "公開頻道",
                    authType = "basic"
                ),
                onClick = {
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            ChannelPermissionBarItem(
                permissionModel = VipPlanPermissionModel(
                    name = "健身肌肉男",
                    canEdit = true,
                    permissionTitle = "進階權限",
                    authType = "advanced"
                ),
                onClick = {
                }
            )
        }
    }
}
