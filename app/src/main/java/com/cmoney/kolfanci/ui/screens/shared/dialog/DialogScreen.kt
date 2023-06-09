package com.cmoney.kolfanci.ui.screens.shared.dialog

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.screens.shared.dialog.item.BanDayItemScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

@Composable
fun DialogScreen(
    modifier: Modifier = Modifier,
    titleIconRes: Int? = null,
    iconFilter: Color? = LocalColor.current.primary,
    title: String,
    subTitle: String,
    onDismiss: () -> Unit,
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
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (titleIconRes != null) {
                            Image(
                                painter = painterResource(id = titleIconRes),
                                colorFilter = iconFilter?.let {
                                    ColorFilter.tint(iconFilter)
                                },
                                contentDescription = null
                            )

                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Text(
                            text = title,
                            fontSize = 19.sp,
                            color = LocalColor.current.text.default_100
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Column(
                        modifier = modifier
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = subTitle,
                            fontSize = 17.sp,
                            color = LocalColor.current.text.default_100
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
            titleIconRes = R.drawable.dialog_ban,
            title = "Hello",
            subTitle = "社團名稱不可以是空白的唷！"
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