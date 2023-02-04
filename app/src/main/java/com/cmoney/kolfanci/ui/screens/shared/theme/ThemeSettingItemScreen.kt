package com.cmoney.kolfanci.ui.screens.shared.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.kolfanci.R
/**
 * 主題
 */
@Composable
fun ThemeSettingItemScreen(
    modifier: Modifier = Modifier,
    primary: Color,
    env_100: Color,
    env_80: Color,
    env_60: Color,
    name: String,
    isSelected: Boolean,
    isShowArrow: Boolean = true,
    onItemClick: () -> Unit,
    onConfirm: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onItemClick.invoke()
            }
            .padding(top = 20.dp, bottom = 20.dp, start = 35.dp, end = 35.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ThemeColorCardScreen(
            modifier = Modifier
                .width(56.dp)
                .height(73.dp),
            primary = primary,
            env_100 = env_100,
            env_80 = env_80,
            env_60 = env_60
        )

        Spacer(modifier = Modifier.width(30.dp))

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {

            Text(text = name, fontSize = 16.sp, color = LocalColor.current.text.default_100)

            Spacer(modifier = Modifier.height(10.dp))

            if (isSelected) {
                BorderButton(
                    modifier = Modifier
                        .width(80.dp)
                        .height(35.dp),
                    text = "套用中", borderColor = LocalColor.current.text.default_30
                ) {
                }
            }
            else {
                BorderButton(
                    modifier = Modifier
                        .width(75.dp)
                        .height(35.dp),
                    text = "套用", borderColor = LocalColor.current.text.default_100
                ) {
                    onConfirm.invoke()
                }
            }
        }
        if (isShowArrow) {
            Image(
                painter = painterResource(id = R.drawable.next),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = LocalColor.current.text.default_80)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ThemeSettingItemScreenPreview() {
    FanciTheme {
        ThemeSettingItemScreen(
            primary = Color.Gray,
            env_100 = Color.Red,
            env_80 = Color.Blue,
            env_60 = Color.Cyan,
            name = "Blue",
            isSelected = false,
            onItemClick = {},
            onConfirm = {}
        )
    }
}