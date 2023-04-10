package com.cmoney.kolfanci.ui.screens.shared

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.kolfanci.R
/**
 * 設定 條列項目
 */
@Composable
fun SettingItemScreen(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    text: String,
    onItemClick: () -> Unit,
    otherContent: (@Composable RowScope.() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .background(LocalColor.current.background)
            .clickable {
                onItemClick.invoke()
            }
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
            painter = painterResource(id = iconRes),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = LocalColor.current.component.other)
        )
        Spacer(modifier = Modifier.width(17.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = text, fontSize = 17.sp, color = LocalColor.current.text.default_100
        )

        otherContent?.let { it() }

        Spacer(modifier = Modifier.width(5.dp))

        Image(
            painter = painterResource(id = R.drawable.next),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = LocalColor.current.text.default_80)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingItemScreenPreview() {
    FanciTheme {
        SettingItemScreen(
            iconRes = R.drawable.guideline,
            text = "服務條款",
            onItemClick= {}
        ) {
            Text(
                modifier = Modifier.padding(end = 15.dp),
                text = "1234", fontSize = 17.sp, color = LocalColor.current.text.default_100
            )
        }
    }
}