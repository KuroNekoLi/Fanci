package com.cmoney.kolfanci.ui.screens.shared.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.common.GrayButton
import com.cmoney.kolfanci.ui.theme.FanciTheme

/**
 * 加入社團
 *
 * @param onInviteCodeClick 點擊 邀請碼加入
 * @param onCreateGroupClick 點擊 建立社團
 */
@Composable
fun JoinGroupDialogScreen(
    modifier: Modifier = Modifier,
    onInviteCodeClick: () -> Unit,
    onCreateGroupClick: () -> Unit,
    onDismiss: () -> Unit,
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
                    text = stringResource(id = R.string.join_group_by_code),
                    shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
                ) {
                    onInviteCodeClick.invoke()
                }

                GrayButton(
                    text = stringResource(id = R.string.create_group),
                    shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
                ) {
                    onCreateGroupClick.invoke()
                }

                Spacer(modifier = Modifier.height(20.dp))

                GrayButton(
                    text = stringResource(id = R.string.back)
                ) {
                    onDismiss()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JoinGroupDialogScreenPreview() {
    FanciTheme {
        JoinGroupDialogScreen(
            onDismiss = {},
            onInviteCodeClick = {},
            onCreateGroupClick = {}
        )
    }
}