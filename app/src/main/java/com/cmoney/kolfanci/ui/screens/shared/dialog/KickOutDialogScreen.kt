package com.cmoney.kolfanci.ui.screens.shared.dialog

import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.screens.shared.dialog.item.DialogDefaultContentScreen
import com.cmoney.kolfanci.ui.screens.shared.vip.getVipDiamondInlineContent
import com.cmoney.kolfanci.ui.theme.FanciTheme

/**
 * 踢出社團 彈窗
 */
@Composable
fun KickOutDialogScreen(
    name: String,
    isVip: Boolean,
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
            title = stringResource(id = R.string.kick_x_out_from_group, name),
        ) {
            if (isVip) {
                val vipId = "vip"
                val kickContent = buildAnnotatedString {
                    append(stringResource(id = R.string.kick_vip_x_content, name))
                    append(' ')
                    appendInlineContent(vipId)
                }
                DialogDefaultContentScreen(
                    content = kickContent,
                    inlineContent = mapOf(
                        vipId to getVipDiamondInlineContent()
                    ),
                    confirmTitle = stringResource(id = R.string.confirm),
                    cancelTitle = stringResource(id = R.string.cancel),
                    onConfirm = {
                        showFirstDialog = false
                        showKickOutDoubleConfirmDialog.value = true
                    },
                    onCancel = {
                        onDismiss.invoke()
                    }
                )
            } else {
                val kickContent = stringResource(id = R.string.kick_x_content, name)
                DialogDefaultContentScreen(
                    content = kickContent,
                    confirmTitle = stringResource(id = R.string.confirm),
                    cancelTitle = stringResource(id = R.string.cancel),
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
    }

    //再次確認 踢出社團 彈窗
    if (showKickOutDoubleConfirmDialog.value) {
        AlertDialogScreen(
            onDismiss = {
                showKickOutDoubleConfirmDialog.value = false
                onDismiss.invoke()
            },
            title = stringResource(id = R.string.kick_x_out_from_group, name),
        ) {
            if (isVip) {
                val vipId = "vip"
                val kickContent = buildAnnotatedString {
                    append(stringResource(id = R.string.kick_vip_x_content, name))
                    append(' ')
                    appendInlineContent(vipId)
                }
                DialogDefaultContentScreen(
                    content = kickContent,
                    inlineContent = mapOf(
                        vipId to getVipDiamondInlineContent()
                    ),
                    confirmTitle = stringResource(id = R.string.confirm_kick_out_from_group),
                    cancelTitle = stringResource(id = R.string.cancel),
                    onConfirm = {
                        showKickOutDoubleConfirmDialog.value = false
                        onConfirm.invoke()
                    },
                    onCancel = {
                        showKickOutDoubleConfirmDialog.value = false
                        onDismiss.invoke()
                    }
                )
            } else {
                val kickContent = stringResource(id = R.string.kick_x_content, name)
                DialogDefaultContentScreen(
                    content = kickContent,
                    confirmTitle = stringResource(id = R.string.confirm_kick_out_from_group),
                    cancelTitle = stringResource(id = R.string.cancel),
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
}

@Preview(showBackground = true)
@Composable
fun KickOutDialogScreenPreview() {
    FanciTheme {
        KickOutDialogScreen(
            name = "Hello",
            isVip = false,
            onDismiss = {},
            onConfirm = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VipKickOutDialogScreenPreview() {
    FanciTheme {
        KickOutDialogScreen(
            name = "Hello",
            isVip = true,
            onDismiss = {},
            onConfirm = {}
        )
    }
}
