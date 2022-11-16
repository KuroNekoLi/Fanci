package com.cmoney.fanci.ui.screens.my

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanci.R
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor

@Composable
fun AboutScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier
                .padding(
                    start = 25.dp,
                ),
            text = "關於我們", fontSize = 14.sp, color = LocalColor.current.text.default_100
        )
        Spacer(modifier = Modifier.height(10.dp))
        Column(modifier = Modifier.background(LocalColor.current.background)) {
            Row(
                modifier = Modifier
                    .padding(
                        top = 10.dp,
                        bottom = 10.dp,
                        start = 25.dp,
                        end = 20.dp
                    )
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = R.drawable.system),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = LocalColor.current.component.other)
                )
                Spacer(modifier = Modifier.width(17.dp))
                Text(
                    modifier = Modifier.weight(1f),
                    text = "系統版本", fontSize = 17.sp, color = LocalColor.current.text.default_100
                )
                Text(
                    text = "1.0.0", fontSize = 14.sp, color = LocalColor.current.text.default_80
                )
            }

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
                    painter = painterResource(id = R.drawable.service),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = LocalColor.current.component.other)
                )
                Spacer(modifier = Modifier.width(17.dp))
                Text(
                    modifier = Modifier.weight(1f),
                    text = "客服中心", fontSize = 17.sp, color = LocalColor.current.text.default_100
                )
                Image(
                    painter = painterResource(id = R.drawable.next),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = LocalColor.current.text.default_80)
                )
            }

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
                    painter = painterResource(id = R.drawable.rating),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = LocalColor.current.component.other)
                )
                Spacer(modifier = Modifier.width(17.dp))
                Text(
                    modifier = Modifier.weight(1f),
                    text = "幫我們評分", fontSize = 17.sp, color = LocalColor.current.text.default_100
                )
                Image(
                    painter = painterResource(id = R.drawable.next),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = LocalColor.current.text.default_80)
                )
            }

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
                    text = "隱私權政策", fontSize = 17.sp, color = LocalColor.current.text.default_100
                )
                Image(
                    painter = painterResource(id = R.drawable.next),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = LocalColor.current.text.default_80)
                )
            }

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
                    painter = painterResource(id = R.drawable.copyright),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = LocalColor.current.component.other)
                )
                Spacer(modifier = Modifier.width(17.dp))
                Text(
                    modifier = Modifier.weight(1f),
                    text = "著作權政策", fontSize = 17.sp, color = LocalColor.current.text.default_100
                )
                Image(
                    painter = painterResource(id = R.drawable.next),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = LocalColor.current.text.default_80)
                )
            }

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
                    painter = painterResource(id = R.drawable.guideline),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = LocalColor.current.component.other)
                )
                Spacer(modifier = Modifier.width(17.dp))
                Text(
                    modifier = Modifier.weight(1f),
                    text = "服務條款", fontSize = 17.sp, color = LocalColor.current.text.default_100
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
fun AboutScreenPreview() {
    FanciTheme {
        AboutScreen()
    }
}