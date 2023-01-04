package com.cmoney.fanci.ui.screens.shared.dialog.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanci.ui.common.BorderButton
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.BanPeriodOption

@Composable
fun BanDayItemScreen(modifier: Modifier = Modifier,
                     name: String,
                     onClick: (BanPeriodOption) -> Unit,
                     onDismiss: () -> Unit) {

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "你確定要將 %s 禁言嗎？\n日後可以從禁言列表中取消懲處".format(name), fontSize = 17.sp, color = Color.White
        )

        Spacer(modifier = Modifier.height(20.dp))

        BorderButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            text = "1日",
            borderColor = LocalColor.current.component.other,
            textColor = Color.White
        ) {
            onClick.invoke(BanPeriodOption.oneDay)
        }

        Spacer(modifier = Modifier.height(20.dp))

        BorderButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            text = "3日",
            borderColor = LocalColor.current.component.other,
            textColor = Color.White
        ) {
            onClick.invoke(BanPeriodOption.threeDay)
        }

        Spacer(modifier = Modifier.height(20.dp))

        BorderButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            text = "7日",
            borderColor = LocalColor.current.component.other,
            textColor = Color.White
        ) {
            onClick.invoke(BanPeriodOption.oneWeek)
        }

        Spacer(modifier = Modifier.height(20.dp))

        BorderButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            text = "30日",
            borderColor = LocalColor.current.component.other,
            textColor = Color.White
        ) {
            onClick.invoke(BanPeriodOption.oneMonth)
        }

        Spacer(modifier = Modifier.height(20.dp))

        BorderButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            text = "永久",
            borderColor = LocalColor.current.component.other,
            textColor = Color.White
        ) {
            onClick.invoke(BanPeriodOption.forever)
        }

        Spacer(modifier = Modifier.height(20.dp))

        BorderButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            text = "取消",
            borderColor = LocalColor.current.component.other,
            textColor = Color.White
        ) {
            onDismiss.invoke()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BanDayItemScreenPreview() {
    FanciTheme {
        BanDayItemScreen(
            name = "王力宏 ",
            onClick = {}
        ) {

        }
    }
}