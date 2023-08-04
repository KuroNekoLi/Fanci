package com.cmoney.kolfanci.ui.screens.shared.dialog

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.ui.screens.shared.dialog.item.DialogDefaultContentScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme

/**
 * 解除 禁言 彈窗
 */
@Composable
fun DisBanDialogScreen(
    name: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var showFirstDialog by remember {
        mutableStateOf(true)
    }

    //再次確認 解除禁言 彈窗
    var showDisBanDoubleConfirmDialog by remember {
        mutableStateOf(false)
    }

    if (showFirstDialog) {
        AlertDialogScreen(
            onDismiss = {
                onDismiss.invoke()
            },
            title = "$name 禁言中",
        ) {
            DialogDefaultContentScreen(
                content = "",
                confirmTitle = "解除禁言",
                cancelTitle = "取消",
                onConfirm = {
                    AppUserLogger.getInstance().log(Clicked.ManageUnmute)
                    showFirstDialog = false
                    showDisBanDoubleConfirmDialog = true
                },
                onCancel = {
                    AppUserLogger.getInstance().log(Clicked.ManageCancel)
                    onDismiss.invoke()
                }
            )
        }
    }

    if (showDisBanDoubleConfirmDialog) {
        AlertDialogScreen(
            onDismiss = {
                showDisBanDoubleConfirmDialog = false
                onDismiss.invoke()
            },
            title = "解除 %s 禁言".format(name)
        ) {
            DialogDefaultContentScreen(
                content = "你確定要將 %s 解除禁言嗎？".format(name),
                confirmTitle = "確認，解除禁言",
                cancelTitle = "取消",
                onConfirm = {
                    AppUserLogger.getInstance().log(Clicked.ManageUnmuteConfirmUnmute)
                    showDisBanDoubleConfirmDialog = false
                    onConfirm.invoke()
                },
                onCancel = {
                    AppUserLogger.getInstance().log(Clicked.ManageUnmuteCancel)
                    showDisBanDoubleConfirmDialog = false
                    onDismiss.invoke()
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DisBanDialogScreenPreview() {
    FanciTheme {
        DisBanDialogScreen(
            name = "Hello",
            onDismiss = {},
            onConfirm = {}
        )
    }
}