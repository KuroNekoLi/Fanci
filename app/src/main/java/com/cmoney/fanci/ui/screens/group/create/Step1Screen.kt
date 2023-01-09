package com.cmoney.fanci.ui.screens.group.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanci.ui.screens.shared.setting.BottomButtonScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor

/**
 * 設定名稱
 */
@Composable
fun Step1Screen(
    defaultName: String,
    onNext: (String) -> Unit
) {
    var textState by remember { mutableStateOf(defaultName) }
    val maxLength = 20

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Bottom
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 20.dp, start = 25.dp, end = 25.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "社團名稱：", fontSize = 14.sp, color = Color.White
                )
                Text(
                    text = "%d/20".format(textState.length),
                    fontSize = 14.sp,
                    color = LocalColor.current.text.default_50
                )
            }

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
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
                    if (it.length <= maxLength) {
                        textState = it
                    }
                },
                shape = RoundedCornerShape(4.dp),
                maxLines = 1,
                textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                placeholder = {
                    Text(
                        text = "填寫專屬於社團的名稱吧!",
                        fontSize = 16.sp,
                        color = LocalColor.current.text.default_30
                    )
                }
            )

        }

        BottomButtonScreen(text = "下一步") {
            onNext.invoke(textState)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Step1ScreenPreview() {
    FanciTheme {
        Step1Screen(
            defaultName = "Hello"
        ){}
    }
}