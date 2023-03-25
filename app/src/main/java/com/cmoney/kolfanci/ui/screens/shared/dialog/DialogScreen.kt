package com.cmoney.kolfanci.ui.screens.shared.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cmoney.kolfanci.ui.screens.shared.dialog.item.BanDayItemScreen
import com.cmoney.kolfanci.ui.theme.Color_2B313C
import com.cmoney.kolfanci.ui.theme.Color_CCFFFFFF
import com.cmoney.kolfanci.ui.theme.FanciTheme

@Composable
fun DialogScreen(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    title: String,
    subTitle: String,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = {
        onDismiss.invoke()
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
                    Text(text = title, fontSize = 19.sp, color = Color_CCFFFFFF)

                    Spacer(modifier = Modifier.height(20.dp))

                    Column(
                        modifier = modifier
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = subTitle,
                            fontSize = 17.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    //Content
                    content.invoke()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DialogScreenPreview() {
    FanciTheme {
        DialogScreen(
            onDismiss = {},
            title = "Hello",
            subTitle = "社團名稱不可以是空白的唷！"
        ) {
            BanDayItemScreen(
                name = "Hello",
                onClick = {}
            ) {

            }
        }
    }
}