package com.cmoney.kolfanci.ui.screens.shared.dialog.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 * 輸入 邀請碼 item
 */
@Composable
fun InputInviteCodeScreen(
    modifier: Modifier = Modifier,
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit
) {
    var textState by remember { mutableStateOf("") }

    Column(
        modifier = modifier
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = textState,
            colors = TextFieldDefaults.textFieldColors(
                textColor = LocalColor.current.text.default_100,
                backgroundColor = LocalColor.current.background,
                cursorColor = LocalColor.current.primary,
                disabledLabelColor = LocalColor.current.text.default_30,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            onValueChange = {
                textState = it
            },
            shape = RoundedCornerShape(5.dp),
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle.Default.copy(fontSize = 16.sp, textAlign = TextAlign.Center),
            placeholder = {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "輸入邀請碼",
                    fontSize = 16.sp,
                    color = LocalColor.current.text.default_30
                )
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        BlueButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            text = stringResource(id = R.string.confirm)
        ) {
            onConfirm.invoke(textState)

        }

        Spacer(modifier = Modifier.height(20.dp))

        BorderButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            text = stringResource(id = R.string.back),
            borderColor = LocalColor.current.text.default_50,
            textColor = LocalColor.current.text.default_100
        ) {
            onCancel.invoke()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InputInviteCodeScreenPreview() {
    InputInviteCodeScreen(
        onConfirm = {},
        onCancel = {}
    )
}