package com.cmoney.fanci.ui.screens.my

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanci.R
import com.cmoney.fanci.ui.theme.Black_181C23
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanci.ui.theme.White_BBBCBF

@Composable
fun GroupSettingScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier
                .padding(
                    start = 25.dp,
                ),
            text = "社團設定", fontSize = 14.sp, color = LocalColor.current.text.default_100
        )
        Spacer(modifier = Modifier.height(10.dp))
        Column(modifier = Modifier.background(LocalColor.current.background)) {
            Row(
                modifier = Modifier
                    .padding(
                        top = 10.dp,
                        bottom = 10.dp,
                        start = 25.dp,
                        end = 10.dp
                    )
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = R.drawable.hide_user),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = LocalColor.current.component.other)
                )
                Spacer(modifier = Modifier.width(17.dp))
                Text(
                    modifier = Modifier.weight(1f),
                    text = "隱藏用戶名單", fontSize = 17.sp, color = LocalColor.current.text.default_100
                )
                Image(
                    painter = painterResource(id = R.drawable.next),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = LocalColor.current.text.default_80)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupSettingScreenPreview() {
    FanciTheme {
        GroupSettingScreen()
    }
}