package com.cmoney.kolfanci.ui.screens.shared.dialog

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.cmoney.fanciapi.fanci.model.BanPeriodOption
import com.cmoney.kolfanci.extension.toDisplayDay
import com.cmoney.kolfanci.ui.screens.shared.dialog.item.BanDayItemScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme

@Composable
fun BanDialogScreen(
    name: String,
    onDismiss: () -> Unit,
    onConfirm: (BanPeriodOption) -> Unit
) {
    var showFirstDialog by remember {
        mutableStateOf(true)
    }

    //再次確認禁言 彈窗
    val showBanDoubleConfirmDialog: MutableState<BanPeriodOption?> =
        remember { mutableStateOf(null) }

    if (showFirstDialog) {
        AlertDialogScreen(
            onDismiss = {
                onDismiss.invoke()
            },
            title = "禁言 $name",
        ) {
            BanDayItemScreen(
                name = name,
                onClick = {
                    showFirstDialog = false
                    showBanDoubleConfirmDialog.value = it
                },
                onDismiss = {
                    onDismiss.invoke()
                }
            )
        }
    }

    showBanDoubleConfirmDialog.value?.let {
        AlertDialogScreen(
            onDismiss = {
                showBanDoubleConfirmDialog.value = null
                onDismiss.invoke()
            },
            title = "確定禁言 %s %s".format(name, it.toDisplayDay())
        ) {
            DialogDefaultContentScreen(
                content = "一旦被禁言，將會無法對頻道做出任何社群行為：留言、按讚等等。",
                confirmTitle = "確定禁言",
                cancelTitle = "返回",
                onConfirm = {
                    showBanDoubleConfirmDialog.value = null
                    onConfirm.invoke(it)
                },
                onCancel = {
                    showBanDoubleConfirmDialog.value = null
                    onDismiss.invoke()
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BanDialogScreenPreview() {
    FanciTheme {
        BanDialogScreen(
            name = "Hello",
            onConfirm = {},
            onDismiss = {}
        )
    }
}