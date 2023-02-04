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
 * 編輯,移除 彈窗
 */
@Composable
fun EditDialogScreen(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onRemove: () -> Unit
) {
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
                    text = "編輯",
                    shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
                ) {
                    onEdit.invoke()
                }

                GrayButton(
                    text = "移除",
                    shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
                ) {
                    onRemove.invoke()
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
fun EditDialogScreenPreview() {
    FanciTheme {
        EditDialogScreen(
            onDismiss = {},
            onEdit = {},
            onRemove = {}
        )
    }
}