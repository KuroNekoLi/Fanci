package com.cmoney.kolfanci.ui.screens.shared.dialog

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

/**
 * 彈窗內容 以及 確定,返回 二個按鈕
 */
@Composable
fun DialogDefaultContentScreen(
    modifier: Modifier = Modifier,
    content: String,
    confirmTitle: String,
    cancelTitle: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        if (content.isNotEmpty()) {
            Text(
                text = content, fontSize = 17.sp, color = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        BorderButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            text = confirmTitle,
            borderColor = LocalColor.current.component.other,
            textColor = LocalColor.current.specialColor.red
        ) {
            onConfirm.invoke()
        }

        Spacer(modifier = Modifier.height(20.dp))

        BorderButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            text = cancelTitle,
            borderColor = LocalColor.current.component.other,
            textColor = Color.White
        ) {
            onCancel.invoke()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DialogDefaultContentScreenPreview() {
    FanciTheme {
        DialogDefaultContentScreen(
            content = "一旦被禁言，將會無法對頻道做出\n" +
                    "任何社群行為：留言、按讚等等。",
            confirmTitle = "確定禁言",
            cancelTitle = "返回",
            onConfirm = {},
            onCancel = {}
        )
    }
}