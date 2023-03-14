package com.cmoney.kolfanci.ui.screens.shared.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.cmoney.kolfanci.ui.common.GrayButton
import com.cmoney.kolfanci.ui.theme.FanciTheme

/**
 * 重新發送 Dialog
 */
@Composable
fun MessageReSendDialogScreen(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onReSend: () -> Unit,
    onDelete: () -> Unit
) {
    val TAG = "MessageReSendDialogScreen"

    Dialog(onDismissRequest = {
        onDismiss()
    }) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier.padding(bottom = 25.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GrayButton(
                    text = "重新傳送",
                    shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
                ) {
                    onReSend.invoke()
                }

                GrayButton(
                    text = "刪除訊息",
                    shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
                ) {
                    onDelete.invoke()
                }

                Spacer(modifier = Modifier.height(20.dp))

                GrayButton(
                    text = "返回"
                ) {
                    onDismiss()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MessageReSendDialogScreenPreview() {
    FanciTheme {
        MessageReSendDialogScreen(
            onDismiss = {},
            onReSend = {}
        ) {}
    }
}