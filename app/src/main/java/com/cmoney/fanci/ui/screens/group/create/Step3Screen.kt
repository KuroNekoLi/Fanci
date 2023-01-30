package com.cmoney.fanci.ui.screens.group.create

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.fanci.R
import com.cmoney.fanci.ui.common.BlueButton
import com.cmoney.fanci.ui.common.BorderButton
import com.cmoney.fanci.ui.screens.group.setting.group.groupsetting.theme.model.GroupTheme
import com.cmoney.fanci.ui.screens.shared.theme.ThemeColorCardScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor

@Composable
fun Step3Screen(
    modifier: Modifier = Modifier,
    groupIcon: String,
    groupBackground: String,
    themeColor: GroupTheme?,
    onChangeIcon: () -> Unit,
    onChangeBackground: () -> Unit,
    onThemeChange: () -> Unit,
    onNext: () -> Unit,
    onPre: () -> Unit
) {
    Scaffold(
        modifier = modifier
    ) { padding ->
        Column {
            Spacer(modifier = Modifier.height(1.dp))
            DescWithImage(desc = "社團圖示", groupIcon) {
                onChangeIcon.invoke()
            }
            Spacer(modifier = Modifier.height(1.dp))
            DescWithImage(desc = "首頁背景", groupBackground) {
                onChangeBackground.invoke()
            }
            Spacer(modifier = Modifier.height(1.dp))
            Row(
                modifier = Modifier
                    .background(LocalColor.current.background)
                    .clickable {
                        onThemeChange.invoke()
                    }
                    .padding(top = 15.dp, bottom = 15.dp, start = 24.dp, end = 24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    modifier = Modifier.weight(1f),
                    text = "主題色彩",
                    fontSize = 17.sp,
                    color = LocalColor.current.text.default_100,
                    fontWeight = FontWeight.Bold
                )

                if (themeColor != null) {
                    ThemeColorCardScreen(
                        modifier = Modifier
                            .size(55.dp),
                        primary = themeColor.theme.primary,
                        env_100 = themeColor.theme.env_100,
                        env_80 = themeColor.theme.env_80,
                        env_60 = themeColor.theme.env_60
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(55.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(LocalColor.current.background)
                    ) {
                        Image(
                            modifier = Modifier
                                .size(22.dp)
                                .align(Alignment.Center),
                            painter = painterResource(id = R.drawable.plus_white),
                            contentDescription = null
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .background(LocalColor.current.env_100),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.padding(25.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    BorderButton(
                        modifier = Modifier.weight(1f),
                        text = "上一步",
                        borderColor = LocalColor.current.text.default_50,
                        textColor = LocalColor.current.text.default_100
                    ) {
                        onPre.invoke()
                    }

                    Spacer(modifier = Modifier.width(27.dp))

                    BlueButton(
                        modifier = Modifier.weight(1f),
                        text = "建立社團 Go!"
                    ) {
                        onNext.invoke()
                    }
                }
            }
        }
    }
}

@Composable
private fun DescWithImage(desc: String, thumbnail: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .background(LocalColor.current.background)
            .clickable {
                onClick.invoke()
            }
            .padding(top = 15.dp, bottom = 15.dp, start = 24.dp, end = 24.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            modifier = Modifier.weight(1f),
            text = desc,
            fontSize = 17.sp,
            color = LocalColor.current.text.default_100,
            fontWeight = FontWeight.Bold
        )

        if (thumbnail.isNotEmpty()) {
            AsyncImage(
                model = thumbnail,
                modifier = Modifier
                    .padding(top = 15.dp, bottom = 15.dp)
                    .size(55.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.resource_default)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(55.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(LocalColor.current.background)
            ) {
                Image(
                    modifier = Modifier
                        .size(22.dp)
                        .align(Alignment.Center),
                    painter = painterResource(id = R.drawable.plus_white), contentDescription = null
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Step3ScreenPreview() {
    FanciTheme {
        Step3Screen(
            groupIcon = "",
            groupBackground = "",
            themeColor = null,
            onChangeIcon = {},
            onChangeBackground = {},
            onThemeChange = {},
            onNext = {},
            onPre = {}
        )
    }
}