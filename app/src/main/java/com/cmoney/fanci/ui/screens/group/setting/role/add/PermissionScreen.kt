package com.cmoney.fanci.ui.screens.group.setting.role.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanci.ui.theme.Color_29787880
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Permission
import com.cmoney.fanciapi.fanci.model.PermissionCategory

@Composable
fun PermissionScreen(
    modifier: Modifier = Modifier,
    permissionList: List<PermissionCategory>,
    permissionSelected: Map<String, Boolean>,
    onSwitch: (String, Boolean) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LocalColor.current.env_80)
    ) {
        items(permissionList) { permission ->
            PermissionItem(permission, permissionSelected) { key, selected ->
                onSwitch.invoke(key, selected)
            }
        }
    }
}

@Composable
private fun PermissionItem(
    permissionCategory: PermissionCategory,
    checkedMap: Map<String, Boolean>,
    onSwitch: (String, Boolean) -> Unit
) {
    Column {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            modifier = Modifier.padding(start = 24.dp, bottom = 10.dp),
            text = permissionCategory.displayCategoryName.orEmpty(),
            fontSize = 14.sp,
            color = LocalColor.current.text.default_100
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LocalColor.current.background)
                .padding(start = 24.dp, end = 24.dp)
        ) {

            permissionCategory.permissions?.forEach { permission ->
                Row(
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = permission.displayName.orEmpty(),
                            fontSize = 16.sp,
                            color = if (permission.highlight == true) {
                                LocalColor.current.specialColor.red
                            } else {
                                LocalColor.current.text.default_100
                            }
                        )
                        Text(
                            text = permission.description.orEmpty(),
                            fontSize = 12.sp,
                            color = LocalColor.current.text.default_50
                        )
                    }
                    Switch(
                        modifier = Modifier.size(51.dp, 31.dp),
                        checked = checkedMap[permission.id] ?: false,
                        onCheckedChange = {
                            onSwitch.invoke(permission.id.orEmpty(), it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = LocalColor.current.primary,
                            checkedTrackAlpha = 1f,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color_29787880,
                            uncheckedTrackAlpha = 1f
                        ),
                    )
                }
            }
        }

    }

}

@Preview(showBackground = true)
@Composable
fun PermissionScreenPreview() {
    FanciTheme {
        PermissionScreen(
            permissionList = listOf(
                PermissionCategory(
                    categoryName = "01-GroupEdit",
                    displayCategoryName = "社團編輯",
                    permissions = listOf(
                        Permission(
                            name = "編輯社團",
                            description = "編輯社團名稱、簡介、頭像與背景。",
                            displayName = "編輯社團",
                            highlight = false,
                            displayCategoryName = "社團編輯"
                        ),
                        Permission(
                            name = "編輯社團2",
                            description = "編輯社團名稱、簡介、頭像與背景。",
                            displayName = "編輯社團",
                            highlight = false,
                            displayCategoryName = "社團編輯"
                        ),
                        Permission(
                            name = "編輯社團3",
                            description = "編輯社團名稱、簡介、頭像與背景。",
                            displayName = "編輯社團",
                            highlight = true,
                            displayCategoryName = "社團編輯"
                        )
                    )
                )
            ),
            permissionSelected = emptyMap()
        ) { _, _ ->

        }
    }
}