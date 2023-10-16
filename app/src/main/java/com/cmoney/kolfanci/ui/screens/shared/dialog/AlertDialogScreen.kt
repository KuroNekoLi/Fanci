package com.cmoney.kolfanci.ui.screens.shared.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cmoney.kolfanci.ui.screens.shared.dialog.item.BanDayItemScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

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
                        color = LocalColor.current.specialColor.hintRed,
                        textAlign = TextAlign.Center
                    )

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
        ) {
            BanDayItemScreen(
                name = "Hello",
                isVip = false,
                onClick = {},
                onDismiss = {}
            )
        }
    }
}