package com.cmoney.kolfanci.ui.screens.group.create

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.fancylog.model.data.Page
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.screens.shared.theme.ThemeColorCardScreen
import com.cmoney.kolfanci.ui.theme.FanciColor
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

@Composable
fun Step3Screen(
    modifier: Modifier = Modifier,
    groupLogo: String,
    groupIcon: String,
    groupBackground: String,
    fanciColor: FanciColor?,
    onChangeLogo: () -> Unit,
    onChangeIcon: () -> Unit,
    onChangeBackground: () -> Unit,
    onThemeChange: () -> Unit,
    onNext: () -> Unit,
    onPre: () -> Unit
) {
    LaunchedEffect(Unit) {
        AppUserLogger.getInstance()
            .log(Page.CreateGroupGroupArrangement)
    }

    Spacer(modifier = Modifier.height(20.dp))
    Column(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
        ) {
            Spacer(modifier = Modifier.height(1.dp))
            DescWithLogoImage(desc = stringResource(id = R.string.group_logo), groupLogo) {
                onChangeLogo.invoke()
            }
            Spacer(modifier = Modifier.height(1.dp))
            DescWithImage(desc = "社團圖示", groupIcon) {
                AppUserLogger.getInstance().log(Clicked.CreateGroupGroupIcon)

                AppUserLogger.getInstance()
                    .log(Page.CreateGroupGroupArrangementGroupIcon)

                onChangeIcon.invoke()
            }
            Spacer(modifier = Modifier.height(1.dp))
            DescWithImage(desc = "首頁背景", groupBackground) {
                AppUserLogger.getInstance().log(Clicked.CreateGroupHomeBackground)

                AppUserLogger.getInstance()
                    .log(Page.CreateGroupGroupArrangementHomeBackground)

                onChangeBackground.invoke()
            }
            Spacer(modifier = Modifier.height(1.dp))
            Row(
                modifier = Modifier
                    .background(LocalColor.current.background)
                    .clickable {
                        AppUserLogger
                            .getInstance()
                            .log(Clicked.CreateGroupThemeColor)

                        AppUserLogger
                            .getInstance()
                            .log(Page.CreateGroupGroupArrangementThemeColor)
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

                if (fanciColor != null) {
                    ThemeColorCardScreen(
                        modifier = Modifier
                            .size(55.dp),
                        primary = fanciColor.primary,
                        env_100 = fanciColor.env_100,
                        env_80 = fanciColor.env_80,
                        env_60 = fanciColor.env_60
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

        }

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
                    .size(55.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.placeholder)
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

@Composable
private fun DescWithLogoImage(desc: String, thumbnail: String, onClick: () -> Unit) {
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
        val imageModel = thumbnail.ifEmpty {
            R.drawable.group_logo_default
        }

        Text(
            modifier = Modifier.weight(1f),
            text = desc,
            fontSize = 17.sp,
            color = LocalColor.current.text.default_100,
            fontWeight = FontWeight.Bold
        )

        AsyncImage(
            model = imageModel,
            modifier = Modifier
                .sizeIn(maxWidth = 125.dp, maxHeight = 40.dp),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.placeholder)
        )
    }
}

@Preview
@Composable
fun Step3ScreenPreview() {
    FanciTheme {
        Step3Screen(
            groupIcon = "",
            groupLogo = "",
            groupBackground = "",
            fanciColor = null,
            onChangeLogo = {},
            onChangeIcon = {},
            onChangeBackground = {},
            onThemeChange = {},
            onNext = {},
            onPre = {}
        )
    }
}