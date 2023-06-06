package com.cmoney.kolfanci.ui.screens.shared.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

@Composable
fun DeleteAlertDialogScreen(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialogScreen(
        modifier = modifier,
        onDismiss = {},
        title = title,
    ) {
        Column {
            Text(
                text = subTitle, color = LocalColor.current.text.default_100
            )

            Spacer(modifier = Modifier.height(20.dp))

            BorderButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                text = "確定刪除",
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
                text = "返回",
                borderColor = LocalColor.current.component.other,
                textColor = Color.White
            ) {
                onCancel.invoke()
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DeleteAlertDialogScreenPreview() {
    FanciTheme {
        DeleteAlertDialogScreen(
            title = "title",
            subTitle = "subTitle",
            onConfirm = {},
            onCancel = {}
        )
    }
}