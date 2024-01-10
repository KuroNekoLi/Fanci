package com.cmoney.kolfanci.ui.screens.chat.message

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RichTooltipBox
import androidx.compose.material3.RichTooltipState
import androidx.compose.material3.TooltipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fanciapi.fanci.model.IVotingOptionStatistic
import com.cmoney.fanciapi.fanci.model.Media
import com.cmoney.fanciapi.fanci.model.Voting
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.fancylog.model.data.From
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.goAppStore
import com.cmoney.kolfanci.extension.toColor
import com.cmoney.kolfanci.extension.toUploadFileItemMap
import com.cmoney.kolfanci.model.ChatMessageWrapper
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.model.attachment.AttachmentType
import com.cmoney.kolfanci.model.mock.MockData
import com.cmoney.kolfanci.model.usecase.AttachmentController
import com.cmoney.kolfanci.ui.common.AutoLinkText
import com.cmoney.kolfanci.ui.common.ChatTimeText
import com.cmoney.kolfanci.ui.screens.chat.MessageHideUserScreen
import com.cmoney.kolfanci.ui.screens.chat.MessageRecycleScreen
import com.cmoney.kolfanci.ui.screens.chat.MessageRemoveScreen
import com.cmoney.kolfanci.ui.screens.chat.message.viewmodel.ImageAttachState
import com.cmoney.kolfanci.ui.screens.media.audio.AudioViewModel
import com.cmoney.kolfanci.ui.screens.post.EmojiFeedback
import com.cmoney.kolfanci.ui.screens.shared.ChatUsrAvatarScreen
import com.cmoney.kolfanci.ui.screens.shared.EmojiCountScreen
import com.cmoney.kolfanci.ui.screens.shared.attachment.AttachmentAudioItem
import com.cmoney.kolfanci.ui.screens.shared.attachment.AttachmentFileItem
import com.cmoney.kolfanci.ui.screens.shared.attachment.UnknownFileItem
import com.cmoney.kolfanci.ui.screens.shared.choice.ChoiceScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.kolfanci.ui.theme.White_767A7F
import com.cmoney.kolfanci.utils.Utils
import com.google.accompanist.flowlayout.FlowRow
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * 訊息互動
 */
sealed class MessageContentCallback {
    data class LongClick(val message: ChatMessage) : MessageContentCallback()
    data class MsgDismissHideClick(val message: ChatMessage) : MessageContentCallback()
    data class EmojiClick(val message: ChatMessage, val resourceId: Int) :
        MessageContentCallback()

    /**
     * 點擊投票
     *
     * @param message 訊息
     * @param voting 投票 model
     * @param choices 所選擇的項目
     */
    data class VotingClick(
        val message: ChatMessage,
        val voting: Voting,
        val choices: List<IVotingOptionStatistic>
    ) : MessageContentCallback()
}

/**
 * 聊天內容 item
 * @param collapsed 此訊息是否應該被折疊
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageContentScreen(
    navController: DestinationsNavigator,
    modifier: Modifier = Modifier,
    channelId: String,
    chatMessageWrapper: ChatMessageWrapper,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    onMessageContentCallback: (MessageContentCallback) -> Unit,
    onReSendClick: (ChatMessageWrapper) -> Unit,
    collapsed: Boolean = false
) {
    val contentPaddingModifier = Modifier.padding(top = 10.dp, start = 40.dp, end = 10.dp)
    val defaultColor = LocalColor.current.env_80
    var longTap by remember { mutableStateOf(false) }
    var backgroundColor by remember { mutableStateOf(defaultColor) }
    val longPressColor = LocalColor.current.component.other
    val messageModel = chatMessageWrapper.message
    val scope = rememberCoroutineScope()
    //Popup emoji selector
    val tooltipStateRich = remember { RichTooltipState() }

    //長案訊息
    val onLongPress = {
        longTap = true
        backgroundColor = longPressColor
        coroutineScope.launch {
            delay(300)
            //不是刪除訊息  以及 不是未送出訊息
            if (longTap && messageModel.isDeleted != true && !chatMessageWrapper.isPendingSendMessage) {
                AppUserLogger.getInstance().log(Clicked.MessageLongPressMessage)
                onMessageContentCallback.invoke(
                    MessageContentCallback.LongClick(messageModel)
                )
            }
            longTap = false
            backgroundColor = defaultColor
        }
    }

    Box(modifier = modifier
        .fillMaxWidth()
        .background(backgroundColor)
        .pointerInput(messageModel) {
            detectTapGestures(
                onLongPress = {
                    onLongPress.invoke()
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .alpha(
                    //發送失敗 淡化訊息
                    if (chatMessageWrapper.isPendingSendMessage) {
                        0.5f
                    } else {
                        1f
                    }
                )
                .padding(
                    start = 20.dp,
                    end = 20.dp
                )
                .fillMaxWidth()
        ) {
            when (chatMessageWrapper.messageType) {
                //時間標記
                ChatMessageWrapper.MessageType.TimeBar -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(35.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(LocalColor.current.background),
                        contentAlignment = Alignment.Center
                    ) {
                        val createTime = chatMessageWrapper.message.createUnixTime?.times(1000) ?: 0
                        Text(
                            text = Utils.getTimeGroupByKey(
                                createTime
                            ),
                            fontSize = 14.sp,
                            color = White_767A7F
                        )
                    }
                }
                //被封鎖
                ChatMessageWrapper.MessageType.Blocker -> {
                    MessageHideUserScreen(
                        chatMessageModel = messageModel,
                        isBlocking = chatMessageWrapper.isBlocking
                    ) {
                        onMessageContentCallback.invoke(
                            MessageContentCallback.MsgDismissHideClick(
                                it
                            )
                        )
                    }
                }
                //封鎖對方
                ChatMessageWrapper.MessageType.Blocking -> {
                    MessageHideUserScreen(
                        chatMessageModel = messageModel,
                        isBlocking = chatMessageWrapper.isBlocking
                    ) {
                        onMessageContentCallback.invoke(
                            MessageContentCallback.MsgDismissHideClick(
                                it
                            )
                        )
                    }
                }
                //刪除
                ChatMessageWrapper.MessageType.Delete -> {
                    MessageRemoveScreen()
                }
                //收回
                ChatMessageWrapper.MessageType.RecycleMessage -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        //大頭貼
                        messageModel.author?.let {
                            ChatUsrAvatarScreen(user = it)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        //發文時間
                        ChatTimeText(
                            Utils.getDisplayTime(
                                messageModel.createUnixTime?.times(1000) ?: 0
                            )
                        )
                    }
                    MessageRecycleScreen(modifier = contentPaddingModifier)
                }
                //普通內文
                ChatMessageWrapper.MessageType.Default -> {
                    if (!collapsed) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            //大頭貼
                            messageModel.author?.let {
                                val firstRoleColorName = it.roleInfos?.firstOrNull()?.color

                                val roleColor =
                                    LocalColor.current.roleColor.colors.filter { color ->
                                        color.name == firstRoleColorName
                                    }.map { fanciColor ->
                                        fanciColor.hexColorCode?.toColor()
                                            ?: LocalColor.current.text.default_100
                                    }.firstOrNull()

                                ChatUsrAvatarScreen(
                                    user = it,
                                    nickNameColor = roleColor ?: LocalColor.current.text.default_100
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            //發文時間
                            ChatTimeText(
                                Utils.getDisplayTime(
                                    messageModel.createUnixTime?.times(1000) ?: 0
                                )
                            )
                        }
                    }

                    //Reply
                    messageModel.replyMessage?.apply {
                        MessageReplayScreen(
                            reply = this,
                            modifier = contentPaddingModifier
                                .clip(RoundedCornerShape(9.dp))
                                .background(LocalColor.current.background)
                        )
                    }

                    //內文
                    messageModel.content?.text?.apply {
                        if (this.isNotEmpty()) {
                            AutoLinkText(
                                modifier = contentPaddingModifier,
                                text = this,
                                fontSize = 17.sp,
                                color = LocalColor.current.text.default_100
                            ) {
                                onLongPress.invoke()
                            }

                            //OG
                            Utils.extractLinks(this).forEach { url ->
                                MessageOGScreen(modifier = contentPaddingModifier, url = url)
                            }
                        }
                    }

                    //附加檔案
                    messageModel.content?.medias?.let {
                        MediaContent(
                            navController = navController,
                            medias = it
                        )
                    }

                    //上傳圖片前預覽
                    if (chatMessageWrapper.uploadAttachPreview.isNotEmpty()) {
                        MessageImageScreen(
                            images = chatMessageWrapper.uploadAttachPreview.map {
                                if (it.uri == Uri.EMPTY) {
                                    it.serverUrl
                                } else {
                                    it.uri
                                }
                            },
                            modifier = contentPaddingModifier,
                            isShowLoading = chatMessageWrapper.uploadAttachPreview.map {
                                it.isUploadComplete
                            }.any {
                                !it
                            }
                        )
                    }

                    //投票
                    messageModel.votings?.let { votes ->
                        ChoiceScreen(
                            channelId = channelId,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 40.dp),
                            navController = navController,
                            votings = votes,
                            isMyPost = (messageModel.author?.id == Constant.MyInfo?.id),
                            onVotingClick = { voting, choices ->
                                onMessageContentCallback.invoke(
                                    MessageContentCallback.VotingClick(
                                        message = messageModel,
                                        voting = voting,
                                        choices = choices
                                    )
                                )
                            }
                        )
                    }

                    //Emoji
                    messageModel.emojiCount?.apply {
                        FlowRow(
                            modifier = contentPaddingModifier.fillMaxWidth(),
                            crossAxisSpacing = 10.dp
                        ) {
                            Utils.emojiMapping(this).forEach { emoji ->
                                if (emoji.second != 0) {
                                    EmojiCountScreen(
                                        modifier = Modifier
                                            .padding(end = 10.dp),
                                        emojiResource = emoji.first,
                                        countText = emoji.second.toString()
                                    ) {
                                        AppUserLogger.getInstance()
                                            .log(Clicked.ExistingEmoji, From.Message)

                                        onMessageContentCallback.invoke(
                                            MessageContentCallback.EmojiClick(
                                                messageModel,
                                                it
                                            )
                                        )
                                    }
                                }
                            }

                            if (Utils.emojiMapping(this).sumOf {
                                    it.second
                                } > 0) {
                                RichTooltipBox(
                                    modifier = Modifier
                                        .padding(20.dp, bottom = 25.dp)
                                        .fillMaxWidth()
                                        .height(60.dp),
                                    action = {
                                        EmojiFeedback(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .offset(y = (-15).dp)
                                        ) {
                                            AppUserLogger.getInstance()
                                                .log(Clicked.AddEmoji, From.Message)

                                            onMessageContentCallback.invoke(
                                                MessageContentCallback.EmojiClick(
                                                    messageModel,
                                                    it
                                                )
                                            )
                                            scope.launch { tooltipStateRich.dismiss() }
                                        }
                                    },
                                    text = { },
                                    shape = RoundedCornerShape(69.dp),
                                    tooltipState = tooltipStateRich,
                                    colors = TooltipDefaults.richTooltipColors(
                                        containerColor = LocalColor.current.env_80
                                    )
                                ) {
                                    //Add Emoji
                                    EmojiCountScreen(
                                        modifier = Modifier
                                            .height(30.dp),
                                        emojiResource = R.drawable.empty_emoji,
                                        emojiIconSize = 23.dp,
                                        countText = ""
                                    ) {
                                        scope.launch { tooltipStateRich.show() }
                                    }
                                }
                            }
                        }
                    }

                    //Re-Send
                    if (chatMessageWrapper.isPendingSendMessage) {
                        Box(modifier = contentPaddingModifier
                            .clickable {
                                onReSendClick.invoke(chatMessageWrapper)
                            }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.resend),
                                contentDescription = ""
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 多媒體 型態
 *
 * @param navController 跳轉用 Controller
 * @param medias 附加檔案
 * @param isClickable 是否可以點擊圖片,放大瀏覽
 * @param onAttachClick 自定義點擊處理
 */
@Composable
fun MediaContent(
    modifier: Modifier = Modifier.padding(start = 40.dp),
    navController: DestinationsNavigator,
    medias: List<Media>,
    isClickable: Boolean = true,
    audioViewModel: AudioViewModel = koinViewModel(
        parameters = {
            parametersOf(Uri.EMPTY)
        }
    ),
    onAttachClick: ((Media) -> Unit)? = null
) {
    val context = LocalContext.current
    val mapList = medias.toUploadFileItemMap()

    mapList.forEach { entry ->
        val key = entry.key
        val attachmentInfoItems = entry.value

        when (key) {
            AttachmentType.VoiceMessage,
            AttachmentType.Audio -> {
                LazyRow(
                    modifier = modifier.then(
                        Modifier.padding(top = 10.dp, end = 10.dp)
                    ),
                    state = rememberLazyListState(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(attachmentInfoItems) { attachmentInfoItem ->
                        val fileUrl = attachmentInfoItem.serverUrl
                        AttachmentAudioItem(
                            modifier = Modifier
                                .width(270.dp)
                                .height(75.dp),
                            file = Uri.parse(fileUrl),
                            duration = attachmentInfoItem.duration ?: 0L,
                            isItemClickable = true,
                            isItemCanDelete = false,
                            isShowResend = false,
                            displayName = attachmentInfoItem.filename,
                            onClick = {
                                AttachmentController.onAttachmentClick(
                                    navController = navController,
                                    attachmentInfoItem = attachmentInfoItem,
                                    context = context,
                                    fileName = attachmentInfoItem.filename,
                                    duration = attachmentInfoItem.duration ?: 0L,
                                    audioViewModel = audioViewModel
                                )
                            }
                        )
                    }
                }
            }

            AttachmentType.Image -> {
                MessageImageScreenV2(
                    images = attachmentInfoItems.map {
                        it.serverUrl
                    },
                    modifier = modifier.then(
                        Modifier.padding(top = 10.dp, end = 10.dp)
                    ),
                    otherItemModifier = Modifier.padding(top = 10.dp),
                    isClickable = isClickable,
                    onImageClick = {
                        AppUserLogger.getInstance().log(Clicked.Image, From.Message)
                    }
                )
            }

            AttachmentType.Pdf, AttachmentType.Txt -> {
                LazyRow(
                    modifier = modifier.then(
                        Modifier.padding(top = 10.dp, end = 10.dp)
                    ),
                    state = rememberLazyListState(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(attachmentInfoItems) { attachmentInfoItem ->
                        val fileUrl = attachmentInfoItem.serverUrl

                        AttachmentFileItem(
                            modifier = Modifier
                                .width(270.dp)
                                .height(75.dp),
                            file = Uri.parse(fileUrl),
                            fileSize = attachmentInfoItem.fileSize,
                            isItemClickable = true,
                            isItemCanDelete = false,
                            isShowResend = false,
                            displayName = attachmentInfoItem.filename,
                            onClick = {
                                AttachmentController.onAttachmentClick(
                                    navController = navController,
                                    attachmentInfoItem = attachmentInfoItem,
                                    context = context,
                                    fileName = attachmentInfoItem.filename
                                )
                            }
                        )
                    }
                }
            }

            //未知檔案
            AttachmentType.Unknown -> {
                LazyRow(
                    modifier = modifier.then(
                        Modifier.padding(top = 10.dp, end = 10.dp)
                    ),
                    state = rememberLazyListState(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(attachmentInfoItems) { attachmentInfoItem ->
                        val fileUrl = attachmentInfoItem.serverUrl
                        UnknownFileItem(
                            file = Uri.parse(fileUrl),
                            onClick = {
                                context.goAppStore()
                            }
                        )
                    }
                }
            }

            AttachmentType.Choice -> {
                //TODO: 附加檔案-選擇題
            }

            AttachmentType.Unknown -> {}
        }
    }
}

@Preview(showBackground = false)
@Composable
fun MessageContentScreenPreview() {
    FanciTheme {
        MessageContentScreen(
            channelId = "",
            chatMessageWrapper = ChatMessageWrapper(
                message = MockData.mockMessage,
                uploadAttachPreview = listOf(
                    ImageAttachState(
                        uri = Uri.parse("")
                    )
                ),
                isPendingSendMessage = true
            ),
            coroutineScope = rememberCoroutineScope(),
            onMessageContentCallback = {},
            onReSendClick = {},
            navController = EmptyDestinationsNavigator
        )
    }
}