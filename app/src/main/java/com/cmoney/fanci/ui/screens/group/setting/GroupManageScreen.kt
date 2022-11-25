package com.cmoney.fanci.ui.screens.group.setting

import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanci.R
import com.cmoney.fanci.ui.screens.shared.SettingItemScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor

/**
 * 社團管理
 */
@Composable
fun GroupManageScreen(modifier: Modifier = Modifier) {
    Column {
        Text(
            modifier = Modifier.padding(start = 25.dp, bottom = 9.dp),
            text = "社團管理", fontSize = 14.sp, color = LocalColor.current.text.default_100
        )

        SettingItemScreen(
            iconRes = R.drawable.info,
            text = "社團設定",
            onItemClick= {}
        )

        Spacer(modifier = Modifier.height(1.dp))

        SettingItemScreen(
            iconRes = R.drawable.channel_setting,
            text = "頻道管理",
            onItemClick= {}
        )
        Spacer(modifier = Modifier.height(1.dp))

        SettingItemScreen(
            iconRes = R.drawable.lock,
            text = "社團公開度",
            onItemClick= {}
        ) {
            Text(text = "私密", fontSize = 17.sp, color = Color.Red)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupManageScreenPreview() {
    FanciTheme {
        GroupManageScreen()
    }
}