package com.cmoney.kolfanci.ui.screens.shared.dialog.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.BanPeriodOption
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.screens.shared.vip.getVipDiamondInlineContent
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

@Composable
fun BanDayItemScreen(
    modifier: Modifier = Modifier,
    name: String,
    isVip: Boolean,
    onClick: (BanPeriodOption) -> Unit,
    onDismiss: () -> Unit
) {
    val options = stringArrayResource(id = R.array.ban_day_options)
    val context = LocalContext.current
    val innerClickFunc = rememberUpdatedState { option: String ->
        when (option) {
            context.getString(R.string.one_day) -> {
                onClick.invoke(BanPeriodOption.oneDay)
            }

            context.getString(R.string.three_day) -> {
                onClick.invoke(BanPeriodOption.threeDay)
            }

            context.getString(R.string.seven_day) -> {
                onClick.invoke(BanPeriodOption.oneWeek)
            }

            context.getString(R.string.thirty_day) -> {
                onClick.invoke(BanPeriodOption.oneMonth)
            }

            context.getString(R.string.forever) -> {
                onClick.invoke(BanPeriodOption.forever)
            }
        }
    }
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isVip) {
                    val vipId = "vip"
                    val banContent = buildAnnotatedString {
                        append(stringResource(id = R.string.ban_vip_x_content, name))
                        append(' ')
                        appendInlineContent(vipId)
                    }

                    Text(
                        text = banContent,
                        textAlign = TextAlign.Center,
                        inlineContent = mapOf(
                            vipId to getVipDiamondInlineContent()
                        ),
                        fontSize = 17.sp,
                        color = LocalColor.current.text.default_100
                    )
                } else {
                    val banContent = stringResource(id = R.string.ban_x_content, name)
                    Text(
                        text = banContent,
                        textAlign = TextAlign.Center,
                        fontSize = 17.sp,
                        color = LocalColor.current.text.default_100
                    )
                }
            }
        }
        items(options) { option ->
            BorderButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                text = option,
                borderColor = LocalColor.current.component.other,
                textColor = LocalColor.current.text.default_100
            ) {
                innerClickFunc.value.invoke(option)
            }
        }
        item {
            BorderButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                text = "取消",
                borderColor = LocalColor.current.component.other,
                textColor = LocalColor.current.text.default_100
            ) {
                onDismiss.invoke()
            }
        }
    }
}

@Preview
@Composable
fun BanDayItemScreenPreview() {
    FanciTheme {
        BanDayItemScreen(
            name = "王力宏 ",
            isVip = false,
            onClick = {},
            onDismiss = {}
        )
    }
}

@Preview
@Composable
fun VipBanDayItemScreenPreview() {
    FanciTheme {
        BanDayItemScreen(
            name = "王力宏 ",
            isVip = true,
            onClick = {},
            onDismiss = {}
        )
    }
}