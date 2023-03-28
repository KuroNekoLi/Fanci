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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.theme.Color_2B313C
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 * 儲存前再次確認
 */
@Composable
fun SaveConfirmDialogScreen(
    modifier: Modifier = Modifier,
    isShow: Boolean = false,
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
                color = Color_2B313C
            ) {
                Box(
                    modifier = Modifier.padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        Row {
                            Image(
                                painter = painterResource(id = R.drawable.save_icon),
                                contentDescription = null
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(text = "此次變更尚未儲存！", fontSize = 19.sp, color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        BorderButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            text = "繼續編輯",
                            borderColor = LocalColor.current.component.other,
                            textColor = Color.White
                        ) {
                            showDialog.value = false
                            onContinue.invoke()
                        }

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
            onContinue = {},
            onGiveUp = {}
        )
    }
}