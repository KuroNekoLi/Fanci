package com.cmoney.kolfanci.ui.screens.shared.dialog

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.cmoney.kolfanci.ui.theme.FanciTheme

/**
 * 踢出社團 彈窗
 */
@Composable
fun KickOutDialogScreen(
    name: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var showFirstDialog by remember {
        mutableStateOf(true)
    }

    //再次確認 踢出社團 彈窗
    val showKickOutDoubleConfirmDialog = remember {
        mutableStateOf(false)
    }

    if (showFirstDialog) {
        AlertDialogScreen(
            onDismiss = {
                onDismiss.invoke()
            },
            title = "將 $name 踢出社團",
        ) {
            DialogDefaultContentScreen(
                content = "你確定要將 %s 踢出社團嗎？\n".format(name) +
                        "一旦踢出他下次要進入，需要重新申請",
                confirmTitle = "確定",
                cancelTitle = "取消",
                onConfirm = {
                    showFirstDialog = false
                    showKickOutDoubleConfirmDialog.value = true
                },
                onCancel = {
                    onDismiss.invoke()
                }
            )
        }
    }

    //再次確認 踢出社團 彈窗
    if (showKickOutDoubleConfirmDialog.value) {
        AlertDialogScreen(
            onDismiss = {
                showKickOutDoubleConfirmDialog.value = false
                onDismiss.invoke()
            },
            title = "確定要將 $name 踢出社團",
        ) {
            DialogDefaultContentScreen(
                content = "你確定要將 %s 踢出社團嗎？\n".format(name) +
                        "一旦踢出他下次要進入，需要重新申請",
                confirmTitle = "確定，踢出社團",
                cancelTitle = "取消",
                onConfirm = {
                    showKickOutDoubleConfirmDialog.value = false
                    onConfirm.invoke()
                },
                onCancel = {
                    showKickOutDoubleConfirmDialog.value = false
                    onDismiss.invoke()
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KickOutDialogScreenPreview() {
    FanciTheme {
        KickOutDialogScreen(
            name = "Hello",
            onDismiss = {},
            onConfirm = {}
        )
    }
}