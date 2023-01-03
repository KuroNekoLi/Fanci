package com.cmoney.fanci.ui.screens.shared.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cmoney.fanci.R
import com.cmoney.fanci.ui.screens.shared.dialog.item.BanDayItemScreen
import com.cmoney.fanci.ui.theme.Color_2B313C
import com.cmoney.fanci.ui.theme.Color_CA4848
import com.cmoney.fanci.ui.theme.FanciTheme

@Composable
fun AlertDialogScreen(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    title: String,
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
                    Row {
                        Image(
                            painter = painterResource(id = R.drawable.dialog_ban),
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(text = title, fontSize = 19.sp, color = Color_CA4848)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    //Content
                    content.invoke()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlertDialogScreenPreview() {
    FanciTheme {
        AlertDialogScreen(
            onDismiss = {},
            title = "Hello",
        ){
            BanDayItemScreen(
                name = "Hello",
                onClick = {}
            ) {

            }
        }
    }
}