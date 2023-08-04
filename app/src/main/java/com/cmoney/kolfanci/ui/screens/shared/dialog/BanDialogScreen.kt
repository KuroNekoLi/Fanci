package com.cmoney.kolfanci.ui.screens.shared.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.cmoney.fanciapi.fanci.model.BanPeriodOption
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.toDisplayDay
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.ui.screens.shared.dialog.item.BanDayItemScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.item.DialogDefaultContentScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme

@Composable
fun BanDialogScreen(
    name: String,
    isVip: Boolean,
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
            title = stringResource(id = R.string.ban_x, name),
        ) {
            BanDayItemScreen(
                name = name,
                isVip = isVip,
                onClick = {
                    AppUserLogger.getInstance().log(Clicked.PunishMuteDate)
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
            title = stringResource(R.string.confirm_ban_x_y_days, name, it.toDisplayDay())
        ) {
            DialogDefaultContentScreen(
                content = stringResource(id = R.string.confirm_ban_x_content),
                confirmTitle = stringResource(id = R.string.confirm_ban),
                cancelTitle = stringResource(id = R.string.cancel),
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
            isVip = false,
            onConfirm = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VipBanDialogScreenPreview() {
    FanciTheme {
        BanDialogScreen(
            name = "Hello",
            isVip = true,
            onConfirm = {},
            onDismiss = {}
        )
    }
}