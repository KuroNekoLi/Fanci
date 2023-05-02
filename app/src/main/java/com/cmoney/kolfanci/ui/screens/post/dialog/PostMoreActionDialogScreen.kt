package com.cmoney.kolfanci.ui.screens.post.dialog

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
import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.isMyPost
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.socks.library.KLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class PostInteract(postMessage: BulletinboardMessage) {
    //編輯
    data class Edit(val post: BulletinboardMessage) : PostInteract(post)

    //置頂
    data class Announcement(val post: BulletinboardMessage) : PostInteract(post)

    //檢舉
    data class Report(val post: BulletinboardMessage) : PostInteract(post)

    //刪除
    data class Delete(val post: BulletinboardMessage) : PostInteract(post)
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostMoreActionDialogScreen(
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState,
    postMessage: BulletinboardMessage,
    onInteractClick: (PostInteract) -> Unit
) {
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
            if (postMessage.isMyPost(Constant.MyInfo)) {
                FeatureText(R.drawable.edit_post, "編輯貼文") {
                    onClose(coroutineScope, modalBottomSheetState)
                    onInteractClick.invoke(PostInteract.Edit(postMessage))
                }
            }

            FeatureText(R.drawable.top, "置頂貼文") {
                onClose(coroutineScope, modalBottomSheetState)
                onInteractClick.invoke(PostInteract.Announcement(postMessage))
            }

            if (!postMessage.isMyPost(Constant.MyInfo)) {
                FeatureText(R.drawable.report, "向管理員檢舉這則貼文") {
                    onClose(coroutineScope, modalBottomSheetState)
                    onInteractClick.invoke(PostInteract.Report(postMessage))
                }
            }

            FeatureText(R.drawable.delete, "刪除貼文") {
                onClose(coroutineScope, modalBottomSheetState)
                onInteractClick.invoke(PostInteract.Delete(postMessage))
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
fun MoreActionDialogScreenPreview() {
    FanciTheme {
        PostMoreActionDialogScreen(
            rememberCoroutineScope(),
            rememberModalBottomSheetState(
                ModalBottomSheetValue.Hidden,
                confirmStateChange = {
                    it != ModalBottomSheetValue.HalfExpanded
                }
            ),
            postMessage = BulletinboardMessage()
        ) {

        }
    }
}