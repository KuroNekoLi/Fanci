package com.cmoney.fanci.ui.screens.shared

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanci.R
import com.cmoney.fanci.ui.theme.Black_14171C
import com.cmoney.fanci.ui.theme.Black_1B2129
import com.cmoney.fanci.ui.theme.White_BBBCBF
import com.socks.library.KLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InteractDialogScreen(
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState,
    onReplyClick: () -> Unit
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
            .background(Black_1B2129),
    ) {
        Column(
            modifier = Modifier.padding(
                top = 20.dp,
                start = 17.5.dp,
                end = 17.5.dp,
                bottom = 10.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                emojiLit.forEach {
                    EmojiIcon(it)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            FeatureText(R.drawable.reply, "回覆") {
                onClose(coroutineScope, modalBottomSheetState)
                onReplyClick.invoke()
            }

            FeatureText(R.drawable.copy, "複製訊息") {
                onClose(coroutineScope, modalBottomSheetState)
            }

            FeatureText(R.drawable.top, "置頂訊息") {
                onClose(coroutineScope, modalBottomSheetState)
            }

            FeatureText(R.drawable.report, "檢舉") {
                onClose(coroutineScope, modalBottomSheetState)
            }

            FeatureText(R.drawable.delete, "刪除訊息") {
                onClose(coroutineScope, modalBottomSheetState)
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
private fun EmojiIcon(@DrawableRes resId: Int) {
    Box(
        modifier = Modifier
            .padding(end = 10.dp)
            .size(40.dp)
            .clip(CircleShape)
            .background(Black_14171C)
            .clickable {
                // TODO:  
                KLog.i("EmojiIcon", "EmojiIcon click")
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
        .padding(start = 5.dp)
        .fillMaxWidth()
        .height(IntrinsicSize.Min)
        .clickable {
            // TODO:
            KLog.i(TAG, "FeatureText click:$text")
            onClick.invoke()
        }
    ) {
        Row(
            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painter = painterResource(id = resId), contentDescription = null)
            Spacer(modifier = Modifier.width(17.dp))
            Text(text = text, fontSize = 17.sp, color = White_BBBCBF)
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
        )
    ){

    }
}