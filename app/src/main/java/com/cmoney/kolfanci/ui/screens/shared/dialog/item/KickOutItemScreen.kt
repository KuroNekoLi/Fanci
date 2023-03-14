package com.cmoney.kolfanci.ui.screens.shared.dialog.item

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
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

@Composable
fun KickOutItemScreen(
    modifier: Modifier = Modifier,
    name: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "你確定要將 %s 踢出社團嗎？".format(name),
            fontSize = 17.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(20.dp))

        BorderButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            text = "確定",
            borderColor = LocalColor.current.component.other,
            textColor = Color.White
        ) {
            onConfirm.invoke()
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
fun KickOutItemScreenPreview() {
    FanciTheme {
        KickOutItemScreen(
            name = "王力宏 ",
            onConfirm = {},
            onDismiss = {}
        )
    }
}