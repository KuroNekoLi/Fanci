package com.cmoney.kolfanci.ui.screens.shared.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.theme.Blue_4F70E5
import com.cmoney.kolfanci.ui.theme.Color_2B313C
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 * 登入彈窗
 */
@Composable
fun LoginDialogScreen(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onLogin: () -> Unit,
    group: Group? = null
) {
    val modelResource: Any? = if (group != null) {
        group.coverImageUrl
    } else {
        R.drawable.login_top
    }

    val notificationTitle = group?.name?.orEmpty() ?: "網紅動態一手掌握"

    Dialog(onDismissRequest = {
        onDismiss.invoke()
    }) {
        Surface(
            modifier = modifier.height(520.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color_2B313C
        ) {
            Box {
                Column {
                    AsyncImage(
                        modifier = Modifier.height(166.dp),
                        contentScale = ContentScale.Crop,
                        model = modelResource,
                        contentDescription = null,
                        placeholder = painterResource(id = R.drawable.resource_default)
                    )

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        text = "登入 Fanci 享受完整功能",
                        fontSize = 21.sp,
                        textAlign = TextAlign.Center,
                        color = LocalColor.current.text.default_100
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.notification),
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        Column(
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = notificationTitle,
                                fontSize = 16.sp,
                                color = LocalColor.current.text.default_100
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "加入討論區，掌握所有消息！",
                                fontSize = 12.sp,
                                color = LocalColor.current.text.default_80
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(25.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.planet),
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        Column(
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "認識更多粉絲好朋友",
                                fontSize = 16.sp,
                                color = LocalColor.current.text.default_100
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "一起在聊天室開心聊天！",
                                fontSize = 12.sp,
                                color = LocalColor.current.text.default_80
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    //登入按鈕1
                    Button(
                        modifier = Modifier
                            .padding(start = 25.dp, end = 25.dp)
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Blue_4F70E5),
                        onClick = {
                            onLogin.invoke()
                        }) {
                        Row {
                            Text(
                                text = "信箱登入 / 註冊",
                                color = Color.White,
                                fontSize = 16.sp
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Image(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(id = R.drawable.mail),
                                contentDescription = null
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    //登入按鈕2
                    Button(
                        modifier = Modifier
                            .padding(start = 25.dp, end = 25.dp)
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Blue_4F70E5),
                        onClick = {
                            onLogin.invoke()
                        }) {
                        Row {
                            Text(
                                text = "社群帳號登入",
                                color = Color.White,
                                fontSize = 16.sp
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Image(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(id = R.drawable.fb),
                                contentDescription = null
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Image(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(id = R.drawable.google),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginDialogScreenPreview() {
    FanciTheme {
        LoginDialogScreen(
            onDismiss = {},
            onLogin = {}
        )
    }
}