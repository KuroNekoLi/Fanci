package com.cmoney.kolfanci.ui.screens.shared.dialog

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 * 儲存前再次確認
 */
@Composable
fun SaveConfirmDialogScreen(
    modifier: Modifier = Modifier,
    isShow: Boolean = false,
    title: String = "此次變更尚未儲存",
    content: String = "你所設定的內容，尚未儲存喔！",
    onContinue: () -> Unit,
    onGiveUp: () -> Unit
) {
    val showDialog = remember {
        mutableStateOf(isShow)
    }

    BackHandler {
        showDialog.value = true
    }

    if (showDialog.value || isShow) {
        Dialog(onDismissRequest = {
            showDialog.value = false
            onContinue.invoke()
        }) {
            Surface(
                modifier = modifier,
                shape = RoundedCornerShape(16.dp),
                color = LocalColor.current.env_80
            ) {
                Box(
                    modifier = Modifier.padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = title,
                            fontSize = 19.sp,
                            color = LocalColor.current.specialColor.red
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = content,
                            fontSize = 17.sp,
                            color = LocalColor.current.text.default_100
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        BorderButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            text = "捨棄",
                            borderColor = LocalColor.current.component.other,
                            textColor = LocalColor.current.specialColor.red
                        ) {
                            showDialog.value = false
                            onGiveUp.invoke()
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        BorderButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            text = "繼續編輯",
                            borderColor = LocalColor.current.component.other,
                            textColor = LocalColor.current.text.default_100
                        ) {
                            showDialog.value = false
                            onContinue.invoke()
                        }
                    }
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun SaveConfirmDialogScreenPreview() {
    FanciTheme {
        SaveConfirmDialogScreen(
            isShow = true,
            onContinue = {},
            onGiveUp = {}
        )
    }
}