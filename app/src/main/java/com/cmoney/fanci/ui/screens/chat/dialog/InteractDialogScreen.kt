package com.cmoney.fanci.ui.screens.chat.dialog

import androidx.annotation.DrawableRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanci.R
import com.cmoney.fanci.model.ChatMessageModel
import com.cmoney.fanci.model.usecase.ChatRoomUseCase
import com.cmoney.fanci.ui.screens.shared.bottomSheet.MessageInteract
import com.cmoney.fanci.ui.theme.Black_14171C
import com.cmoney.fanci.ui.theme.Black_1B2129
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanci.ui.theme.White_BBBCBF
import com.socks.library.KLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InteractDialogScreen(
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState,
    message: ChatMessageModel,
    onInteractClick: (MessageInteract) -> Unit
) {
    val emojiLit = listOf(
        R.drawable.emoji_angry,
        R.drawable.emoji_cry,
        R.drawable.emoji_like,
        R.drawable.emoji_haha,
        R.drawable.emoji_happiness,
        R.drawable.emoji_love,
        R.drawable.emoji_happy,
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(LocalColor.current.env_80),
    ) {
        Column(
            modifier = Modifier.padding(
                top = 20.dp,
                bottom = 10.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp)
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                emojiLit.forEach {
                    EmojiIcon(it) { resId ->
                        onClose(coroutineScope, modalBottomSheetState)
                        onInteractClick.invoke(MessageInteract.EmojiClick(message, resId))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            FeatureText(R.drawable.reply, "回覆") {
                onClose(coroutineScope, modalBottomSheetState)
                onInteractClick.invoke(MessageInteract.Reply(message))
            }

            FeatureText(R.drawable.recycle, "收回訊息") {
                onClose(coroutineScope, modalBottomSheetState)
                onInteractClick.invoke(MessageInteract.Recycle(message))
            }

            FeatureText(R.drawable.copy, "複製訊息") {
                onClose(coroutineScope, modalBottomSheetState)
                onInteractClick.invoke(MessageInteract.Copy(message))
            }

            FeatureText(R.drawable.top, "置頂訊息") {
                onClose(coroutineScope, modalBottomSheetState)
                onInteractClick.invoke(MessageInteract.Announcement(message))
            }

            FeatureText(R.drawable.hide, "隱藏此用戶的所有內容") {
                onClose(coroutineScope, modalBottomSheetState)
                onInteractClick.invoke(MessageInteract.HideUser(message))
            }

            FeatureText(R.drawable.report, "向管理員檢舉此用戶") {
                onClose(coroutineScope, modalBottomSheetState)
                onInteractClick.invoke(MessageInteract.Report(message))
            }

            FeatureText(R.drawable.delete, "刪除訊息") {
                onClose(coroutineScope, modalBottomSheetState)
                onInteractClick.invoke(MessageInteract.Delete(message))
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
fun onClose(coroutineScope: CoroutineScope, modalBottomSheetState: ModalBottomSheetState) {
    coroutineScope.launch {
        modalBottomSheetState.hide()
    }
}

@Composable
private fun EmojiIcon(@DrawableRes resId: Int, onClick: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .padding(end = 10.dp)
            .size(40.dp)
            .clip(CircleShape)
            .background(LocalColor.current.background)
            .clickable {
                KLog.i("EmojiIcon", "EmojiIcon click")
                onClick.invoke(resId)
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = resId),
            contentDescription = null
        )
    }
}

@Composable
private fun FeatureText(@DrawableRes resId: Int, text: String, onClick: () -> Unit) {
    val TAG = "FeatureText"
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(IntrinsicSize.Min)
        .clickable {
            KLog.i(TAG, "FeatureText click:$text")
            onClick.invoke()
        }
    ) {
        Row(
            modifier = Modifier.padding(start = 20.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = resId), contentDescription = null,
                colorFilter = ColorFilter.tint(color = LocalColor.current.component.other)
            )
            Spacer(modifier = Modifier.width(17.dp))
            Text(text = text, fontSize = 17.sp, color = LocalColor.current.text.default_100)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun InteractDialogScreenPreview() {
    InteractDialogScreen(
        rememberCoroutineScope(),
        rememberModalBottomSheetState(
            ModalBottomSheetValue.Hidden,
            confirmStateChange = {
                it != ModalBottomSheetValue.HalfExpanded
            }
        ),
        message = ChatRoomUseCase.textType
    ) {

    }
}