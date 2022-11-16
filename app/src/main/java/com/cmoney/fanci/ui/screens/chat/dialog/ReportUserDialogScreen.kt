package com.cmoney.fanci.ui.screens.chat.dialog

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.cmoney.fanci.R
import com.cmoney.fanci.model.ChatMessageModel
import com.cmoney.fanci.ui.screens.shared.ChatUsrAvatarScreen
import com.cmoney.fanci.ui.theme.*

/**
 * 隱藏用戶 彈窗
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ReportUserDialogScreen(
    user: ChatMessageModel.User,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val openDialog = remember { mutableStateOf(true) }

    val showReason = remember {
        mutableStateOf(false)
    }

    val dialogHeight = remember {
        mutableStateOf(IntrinsicSize.Min)
    }

    val reportReason = listOf("濫發廣告訊息", "傳送色情訊息", "騷擾行為", "內容與主題無關", "其他", "取消檢舉")

    if (openDialog.value) {
        Dialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = {
                openDialog.value = false
                onDismiss.invoke()
            }) {
            Box(
                modifier = Modifier
                    .padding(23.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(LocalColor.current.env_80)
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.report),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(9.dp))
                        Text(text = "向管理員檢舉此用戶", fontSize = 19.sp, color = LocalColor.current.text.default_100)
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    Text(
                        text = "送出檢舉要求給管理員，會由社團管理員決定是否該對此用戶進行限制。",
                        fontSize = 17.sp,
                        color = LocalColor.current.text.default_100
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(LocalColor.current.background)
                            .padding(start = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        ChatUsrAvatarScreen(user)
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    //檢舉原因
                    if (showReason.value) {
                        reportReason.forEachIndexed { index, reason ->
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                border = BorderStroke(1.dp, LocalColor.current.text.default_100),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = LocalColor.current.env_80
                                ),
                                onClick = {
                                    if (index == reportReason.size - 1) {
                                        onDismiss.invoke()
                                    } else {
                                        onConfirm.invoke(reason)
                                    }
                                }) {
                                Text(text = reason, fontSize = 16.sp, color = LocalColor.current.text.default_100)
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    } else {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            border = BorderStroke(1.dp, LocalColor.current.text.default_100),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = LocalColor.current.env_80
                            ),
                            onClick = {
                                dialogHeight.value = IntrinsicSize.Max
                                showReason.value = true
                            }) {
                            Text(text = "確定檢舉", fontSize = 16.sp, color = Color_CB4A4A)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            border = BorderStroke(1.dp, LocalColor.current.text.default_100),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = LocalColor.current.env_80
                            ),
                            onClick = {
                                openDialog.value = false
                                onDismiss.invoke()
                            }) {
                            Text(text = "取消", fontSize = 16.sp, color = LocalColor.current.text.default_100)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportUserDialogScreenPreview() {
    FanciTheme {
        ReportUserDialogScreen(
            ChatMessageModel.User(
                avatar = "https://pickaface.net/gallery/avatar/unr_sample_161118_2054_ynlrg.png",
                nickname = "Hello"
            ),
            {

            }
        ) {

        }
    }
}