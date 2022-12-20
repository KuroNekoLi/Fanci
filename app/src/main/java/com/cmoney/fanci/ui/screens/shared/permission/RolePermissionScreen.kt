package com.cmoney.fanci.ui.screens.shared.permission

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.PermissionCategory

/**
 * 角色 權限
 */
@Composable
fun RolePermissionScreen(
    modifier: Modifier = Modifier,
    permissionCategory: PermissionCategory
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(start = 24.dp, bottom = 10.dp),
            text = "社團編輯", fontSize = 14.sp, color = LocalColor.current.text.default_100
        )

        //todo
        Row(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 8.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "編輯社團", fontSize = 16.sp, color = LocalColor.current.text.default_100)
                Text(
                    text = "編輯社團名稱、簡介、頭像與背景。",
                    fontSize = 12.sp,
                    color = LocalColor.current.text.default_50
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RolePermissionScreenPreview() {
    FanciTheme {
        RolePermissionScreen(
            permissionCategory = PermissionCategory()
        )
    }
}