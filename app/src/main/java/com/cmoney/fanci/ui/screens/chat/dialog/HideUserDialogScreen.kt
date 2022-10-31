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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cmoney.fanci.R
import com.cmoney.fanci.model.ChatMessageModel
import com.cmoney.fanci.ui.screens.shared.ChatUsrAvatarScreen
import com.cmoney.fanci.ui.theme.Black_181C23
import com.cmoney.fanci.ui.theme.Color_CB4A4A
import com.cmoney.fanci.ui.theme.White_262C34
import com.cmoney.fanci.ui.theme.White_BBBCBF

/**
 * 隱藏用戶 彈窗
 */
@Composable
fun HideUserDialogScreen(user: ChatMessageModel.User, onDismiss: () -> Unit) {
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
                    .background(White_262C34)
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
                        Text(text = "隱藏此用戶的所有內容", fontSize = 19.sp, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    Text(text = "該用戶在社團內發布的所有內容\n將會自動為你隱藏", fontSize = 17.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Black_181C23)
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
                        border = BorderStroke(1.dp, White_BBBCBF),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Transparent
                        ),
                        onClick = { /*TODO*/ }) {
                        Text(text = "確定隱藏", fontSize = 16.sp, color = Color_CB4A4A)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        border = BorderStroke(1.dp, White_BBBCBF),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Transparent
                        ),
                        onClick = {
                            openDialog.value = false
                            onDismiss.invoke()
                        }) {
                        Text(text = "取消", fontSize = 16.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HideUserDialogScreenPreview() {
    HideUserDialogScreen(
        ChatMessageModel.User(
            avatar = "https://pickaface.net/gallery/avatar/unr_sample_161118_2054_ynlrg.png",
            nickname = "Hello"
        )
    ) {

    }
}