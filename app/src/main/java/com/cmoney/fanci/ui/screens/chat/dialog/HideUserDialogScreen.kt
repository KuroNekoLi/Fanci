package com.cmoney.fanci.ui.screens.chat.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cmoney.fanci.R
import com.cmoney.fanci.model.ChatMessageModel
import com.cmoney.fanci.ui.screens.shared.ChatUsrAvatarScreen
import com.cmoney.fanci.ui.theme.Color_CB4A4A
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.GroupMember

/**
 * 隱藏用戶 彈窗
 */
@Composable
fun HideUserDialogScreen(
    user: GroupMember,
    onConfirm: (GroupMember) -> Unit,
    onDismiss: () -> Unit
) {
    val openDialog = remember { mutableStateOf(true) }
    if (openDialog.value) {
        Dialog(onDismissRequest = {
            openDialog.value = false
            onDismiss.invoke()
        }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .clip(RoundedCornerShape(8.dp))
                    .background(LocalColor.current.env_80)
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.hide),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(9.dp))
                        Text(
                            text = "封鎖此用戶",
                            fontSize = 19.sp,
                            color = LocalColor.current.text.default_100
                        )
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    Text(
                        text = "注意：封鎖是雙向的。\n" +
                                "\n" +
                                "你封鎖對方後，對方的所有動態會\n" +
                                "為你隱藏，你的所有動態也不會被\n" +
                                "對方看見。\n" +
                                "\n" +
                                "你可以在個人頁面中，解除封鎖。",
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
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        elevation = ButtonDefaults.elevation(0.dp),
                        border = BorderStroke(1.dp, LocalColor.current.text.default_100),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = LocalColor.current.env_80,
                        ),
                        onClick = {
                            onConfirm.invoke(user)
                        }) {
                        Text(text = "確定封鎖", fontSize = 16.sp, color = Color_CB4A4A)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        elevation = ButtonDefaults.elevation(0.dp),
                        border = BorderStroke(1.dp, LocalColor.current.text.default_100),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = LocalColor.current.env_80,
                        ),
                        onClick = {
                            openDialog.value = false
                            onDismiss.invoke()
                        }) {
                        Text(
                            text = "取消",
                            fontSize = 16.sp,
                            color = LocalColor.current.text.default_100
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HideUserDialogScreenPreview() {
    FanciTheme {
        HideUserDialogScreen(
            GroupMember(
                thumbNail = "https://pickaface.net/gallery/avatar/unr_sample_161118_2054_ynlrg.png",
                name = "Hello"
            ),
            {}
        ) {
        }
    }
}