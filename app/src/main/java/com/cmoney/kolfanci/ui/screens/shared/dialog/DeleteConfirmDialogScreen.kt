package com.cmoney.kolfanci.ui.screens.shared.dialog

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 * 刪除前再次確認
 */
@Composable
fun DeleteConfirmDialogScreen(
    modifier: Modifier = Modifier,
    date: Any?,
    isShow: Boolean = false,
    title: String = "確定刪除",
    content: String = "刪除後，內容將會完全消失。",
    onCancel: () -> Unit,
    onConfirm: (Any?) -> Unit
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
            onCancel.invoke()
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
                            color = LocalColor.current.specialColor.red,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = content,
                            fontSize = 17.sp,
                            color = LocalColor.current.text.default_100,
                            textAlign = TextAlign.Center
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
                            showDialog.value = false
                            onConfirm.invoke(date)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        BorderButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            text = "取消",
                            borderColor = LocalColor.current.component.other,
                            textColor = LocalColor.current.text.default_100
                        ) {
                            showDialog.value = false
                            onCancel.invoke()
                        }
                    }
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DeleteConfirmDialogScreenPreview() {
    FanciTheme {
        DeleteConfirmDialogScreen(
            isShow = true,
            date = "",
            onCancel = {},
            onConfirm = {}
        )
    }
}