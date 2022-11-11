package com.cmoney.fanci.ui.screens.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.cmoney.fanci.R
import com.cmoney.fanci.model.ChatMessageModel
import com.cmoney.fanci.model.usecase.ChatRoomUseCase
import com.cmoney.fanci.ui.screens.chat.dialog.ShowDisplayHideMessageScreen
import com.cmoney.fanci.ui.theme.Black_181C23
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanci.ui.theme.White_BBBCBF

@Composable
fun MessageHideUserScreen(
    modifier: Modifier = Modifier,
    chatMessageModel: ChatMessageModel,
    onHideMessageClick: (ChatMessageModel) -> Unit
) {
    val openDialog = remember { mutableStateOf(false) }

    Row(
        modifier = modifier.clickable {
            openDialog.value = true
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(id = R.drawable.hide), contentDescription = null)
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(LocalColor.current.background)
                .padding(15.dp)
        ) {
            Text(text = "該用戶的內容已自動為你隱藏", fontSize = 17.sp, color = LocalColor.current.text.default_30)
        }
    }

    if (openDialog.value) {
        ShowDisplayHideMessageScreen(
            chatMessageModel = chatMessageModel,
            onConfirm = {
                openDialog.value = false
                onHideMessageClick.invoke(chatMessageModel)
            },
            onDismiss = {
                openDialog.value = false
            }
        )
    }

}

@Preview(showBackground = true)
@Composable
fun MessageHideUserScreenPreview() {
    FanciTheme {
        MessageHideUserScreen(
            chatMessageModel = ChatRoomUseCase.MockData.allMessageType
        ) {}
    }
}