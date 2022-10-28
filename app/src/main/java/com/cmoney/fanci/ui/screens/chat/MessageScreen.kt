package com.cmoney.fanci.ui.screens.chat

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.cmoney.fanci.R
import com.cmoney.fanci.extension.findActivity
import com.cmoney.fanci.extension.showInteractDialogBottomSheet
import com.cmoney.fanci.model.ChatMessageModel
import com.cmoney.fanci.ui.screens.shared.bottomSheet.MessageInteract
import com.cmoney.fanci.ui.theme.FanciTheme
import com.socks.library.KLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 聊天室 區塊
 */
@Composable
fun MessageScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    listState: LazyListState = rememberLazyListState(),
    message: List<ChatMessageModel>,
    coroutineScope: CoroutineScope,
    onInteractClick: (MessageInteract) -> Unit
) {
    Surface(
        color = MaterialTheme.colors.surface,
        modifier = modifier,
    ) {
        val context = LocalContext.current
        LazyColumn(state = listState) {
            if (message.isNotEmpty()) {
                items(message) { message ->
                    MessageContentScreen(
                        messageModel = message,
                        coroutineScope = coroutineScope) {
                        showInteractDialog(context.findActivity(), message, onInteractClick)
                    }
                }
            }
        }

        LaunchedEffect(message.size) {
            CoroutineScope(Dispatchers.Main).launch {
                listState.scrollToItem(index = message.size)
            }
        }
    }
}

/**
 * 互動式 彈窗
 */
fun showInteractDialog(
    activity: Activity,
    message: ChatMessageModel,
    onInteractClick: (MessageInteract) -> Unit
) {
    val TAG = "MessageScreen"
    KLog.i(TAG, "showInteractDialog:$message")
    activity.showInteractDialogBottomSheet(message, onInteractClick)
}

@Preview(showBackground = true)
@Composable
fun MessageScreenPreview() {
    FanciTheme {
        MessageScreen(
            coroutineScope = rememberCoroutineScope(),
            message = listOf(
                ChatMessageModel(
                    poster = ChatMessageModel.User(
                        avatar = "https://picsum.photos/100/100",
                        nickname = "Warren"
                    ),
                    publishTime = 1666334733000,
                    message = ChatMessageModel.Message(
                        reply = ChatMessageModel.Reply(
                            replyUser = ChatMessageModel.User(avatar = "", nickname = "阿修羅"),
                            text = "Literally the rookies this year have been killing it"
                        ),
                        text = "房市利空齊發，不動產大老林正雄提出建言，認為現階段打房政策已奏效，政府不應在此時通過平均地權條例的修法，而內政部長徐國勇則也語帶保留的說，修法時將會考慮各時期的情景轉變。",
                        media = listOf(
                            ChatMessageModel.Media.Article(
                                from = "Medium",
                                title = "12 BEST Apps for Productivity You Need To Use in 2022",
                                thumbnail = "https://miro.medium.com/max/1100/0*VLSb2Oc_WSxtqN6K"
                            ),
                            ChatMessageModel.Media.Youtube(
                                channel = "蔡阿嘎Life",
                                title = "【蔡阿嘎地獄廚房#16】廚佛Fred大戰阿煨師，幹話最多的兩個男人正面對決！",
                                thumbnail = "https://img.youtube.com/vi/1p_GLULMNbw/0.jpg"
                            ),
                            ChatMessageModel.Media.Instagram(
                                channel = "阿滴英文",
                                title = "有妹妹然後教英文的那個\uD83D\uDE4B\uD83C\uDFFB\u200D♂️@raydudaily 比較多日常廢文",
                                thumbnail = "https://okapi.books.com.tw/uploads/image/2018/03/source/22908-1520239183.jpg"
                            ),
                            ChatMessageModel.Media.Image(
                                image = listOf(
                                    "https://picsum.photos/500/500",
                                    "https://picsum.photos/400/400",
                                    "https://picsum.photos/300/300"
                                )
                            )
                        ),
                        emoji = listOf(
                            ChatMessageModel.Emoji(
                                resource = R.drawable.emoji_haha,
                                count = 777
                            ),
                            ChatMessageModel.Emoji(
                                resource = R.drawable.emoji_cry,
                                count = 123
                            ),
                            ChatMessageModel.Emoji(
                                resource = R.drawable.emoji_like,
                                count = 12355
                            ),
                            ChatMessageModel.Emoji(
                                resource = R.drawable.emoji_happiness,
                                count = 9
                            ),
                            ChatMessageModel.Emoji(
                                resource = R.drawable.emoji_angry,
                                count = 12
                            ),
                            ChatMessageModel.Emoji(
                                resource = R.drawable.emoji_happy,
                                count = 12
                            ),
                            ChatMessageModel.Emoji(
                                resource = R.drawable.emoji_love,
                                count = 55
                            ),
                        )
                    )
                )
            )
        ) {

        }
    }
}