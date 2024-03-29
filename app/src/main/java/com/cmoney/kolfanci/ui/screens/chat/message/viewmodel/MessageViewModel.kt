package com.cmoney.kolfanci.ui.screens.chat.message.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fanciapi.fanci.model.Emojis
import com.cmoney.fanciapi.fanci.model.IReplyMessage
import com.cmoney.fanciapi.fanci.model.IUserMessageReaction
import com.cmoney.fanciapi.fanci.model.MessageServiceType
import com.cmoney.fanciapi.fanci.model.OrderType
import com.cmoney.fanciapi.fanci.model.ReportReason
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.copyToClipboard
import com.cmoney.kolfanci.extension.toIReplyVotingList
import com.cmoney.kolfanci.model.ChatMessageWrapper
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.model.attachment.AttachmentInfoItem
import com.cmoney.kolfanci.model.attachment.AttachmentType
import com.cmoney.kolfanci.model.remoteconfig.PollingFrequencyKey
import com.cmoney.kolfanci.model.usecase.ChatRoomPollUseCase
import com.cmoney.kolfanci.model.usecase.ChatRoomUseCase
import com.cmoney.kolfanci.model.usecase.PermissionUseCase
import com.cmoney.kolfanci.ui.screens.shared.bottomSheet.MessageInteract
import com.cmoney.kolfanci.ui.screens.shared.snackbar.CustomMessage
import com.cmoney.kolfanci.ui.theme.White_494D54
import com.cmoney.kolfanci.ui.theme.White_767A7F
import com.cmoney.kolfanci.utils.MessageUtils
import com.cmoney.kolfanci.utils.Utils
import com.cmoney.remoteconfig_library.extension.getKeyValue
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.socks.library.KLog
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ImageAttachState(
    val uri: Uri,
    val isUploadComplete: Boolean = false,
    val serverUrl: String = ""
)

/**
 * 處理聊天室 相關訊息
 */
class MessageViewModel(
    val context: Application,
    private val chatRoomUseCase: ChatRoomUseCase,
    private val chatRoomPollUseCase: ChatRoomPollUseCase,
    private val permissionUseCase: PermissionUseCase
) : AndroidViewModel(context) {

    private val TAG = MessageViewModel::class.java.simpleName

    //通知訊息
    private val _snackBarMessage = MutableSharedFlow<CustomMessage>()
    val snackBarMessage = _snackBarMessage.asSharedFlow()

    //要回覆的訊息
    private val _replyMessage = MutableStateFlow<IReplyMessage?>(null)
    val replyMessage = _replyMessage.asStateFlow()

    //是否要show re-send dialog
    private val _showReSendDialog = MutableStateFlow<ChatMessageWrapper?>(null)
    val showReSendDialog = _showReSendDialog.asStateFlow()

    //刪除訊息
    private val _deleteMessage = MutableStateFlow<ChatMessage?>(null)
    val deleteMessage = _deleteMessage.asStateFlow()

    //檢舉訊息
    private val _reportMessage = MutableStateFlow<ChatMessage?>(null)
    val reportMessage = _reportMessage.asStateFlow()

    //封鎖用戶的訊息
    private val _hideUserMessage = MutableStateFlow<ChatMessage?>(null)
    val hideUserMessage = _hideUserMessage.asStateFlow()

    //複製訊息
    private val _copyMessage = MutableStateFlow<ChatMessage?>(null)
    val copyMessage = _copyMessage.asStateFlow()

    //設定公告訊息,跳轉設定頁面
    private val _routeAnnounceMessage = MutableStateFlow<ChatMessage?>(null)
    val routeAnnounceMessage = _routeAnnounceMessage.asStateFlow()

    //訊息是否發送完成
    private val _isSendComplete = MutableStateFlow<Boolean>(false)
    val isSendComplete = _isSendComplete.asStateFlow()

    //聊天訊息
    private val _message = MutableStateFlow<List<ChatMessageWrapper>>(emptyList())
    val message = _message.asStateFlow()

    //聊天室卷動至指定位置
    private val _scrollToPosition = MutableStateFlow<Int?>(null)
    val scrollToPosition = _scrollToPosition.asStateFlow()

    private val preSendChatId = "Preview"       //發送前預覽的訊息id, 用來跟其他訊息區分

    private val pollingInterval: Long
        get() {
            return FirebaseRemoteConfig.getInstance().getKeyValue(PollingFrequencyKey).times(1000)
        }

    //區塊刷新 間隔
    private val scopePollingInterval: Long = 5000

    /**
     * 圖片上傳 callback
     */
    interface ImageUploadCallback {
        fun complete(images: List<String>)

        fun onFailure(e: Throwable)
    }

    /**
     * 聊天室一進入時, 先抓取舊資料
     */
    fun chatRoomFirstFetch(channelId: String?) {
        KLog.i(TAG, "chatRoomFirstFetch:$channelId")
        viewModelScope.launch {
            if (channelId?.isNotEmpty() == true) {
                chatRoomUseCase.fetchMoreMessage(
                    chatRoomChannelId = channelId,
                    fromSerialNumber = null,
                ).onSuccess {
                    val newMessage = it.items?.map { chatMessage ->
                        ChatMessageWrapper(message = chatMessage)
                    }?.reversed().orEmpty()

                    //檢查插入時間 bar
                    val timeBarMessage = MessageUtils.insertTimeBar(newMessage)

                    processMessageCombine(timeBarMessage.map { chatMessageWrapper ->
                        MessageUtils.defineMessageType(chatMessageWrapper)
                    })

                    if (!Constant.isOpenMock) {
                        startPolling(channelId = channelId)
                    }

                }.onFailure { e ->
                    KLog.e(TAG, e)
                }
            }
        }
    }

    /**
     * 開始 Polling 聊天室 訊息
     * @param channelId 聊天室id
     */
    private fun startPolling(channelId: String?, fromIndex: Long? = null) {
        KLog.i(TAG, "startPolling:$channelId, fromIndex: $fromIndex")
        viewModelScope.launch {
            if (channelId?.isNotEmpty() == true) {
                stopPolling()
                chatRoomPollUseCase.poll(pollingInterval, channelId, fromIndex).collect {
//                    KLog.i(TAG, it)
                    if (it.items?.isEmpty() == true) {
                        return@collect
                    }

                    val newMessage = it.items?.map { chatMessage ->
                        ChatMessageWrapper(message = chatMessage)
                    }?.reversed().orEmpty()

                    //檢查插入時間 bar
                    val timeBarMessage = MessageUtils.insertTimeBar(newMessage)
                    processMessageCombine(timeBarMessage.map { chatMessageWrapper ->
                        MessageUtils.defineMessageType(chatMessageWrapper)
                    })
                }
            }
        }
    }


    /**
     * 停止 Polling 聊天室 訊息
     */
    fun stopPolling() {
        KLog.i(TAG, "stopPolling")
        chatRoomPollUseCase.close()
    }

    /**
     *  將新的訊息 跟舊的合併
     *  @param newChatMessage 新訊息
     */
    private fun processMessageCombine(
        newChatMessage: List<ChatMessageWrapper>,
    ) {
        val oldMessage = _message.value.filter {
            !it.isPendingSendMessage
        }.toMutableList()

        val pendingSendMessage = _message.value.filter {
            it.isPendingSendMessage
        }
        //判斷 要合併的訊息是新訊息 or 歷史訊息, 決定要放在 List 的前面 or 後面
        if ((newChatMessage.firstOrNull()?.message?.serialNumber
                ?: 0) < (oldMessage.firstOrNull()?.message?.serialNumber ?: 0)
        ) {
            oldMessage.addAll(newChatMessage)
        } else {
            oldMessage.addAll(0, newChatMessage)
        }

        oldMessage.addAll(0, pendingSendMessage)

        val distinctMessage = oldMessage.distinctBy { combineMessage ->
            if (combineMessage.messageType == ChatMessageWrapper.MessageType.TimeBar) {
                val createTime = combineMessage.message.createUnixTime?.times(1000) ?: 0L
                Utils.getTimeGroupByKey(createTime)
            } else {
                combineMessage.message.id
            }
        }
        val sortedDistinctMessage = distinctMessage.sortedByDescending {
            it.message.createUnixTime
        }
        _message.value = sortedDistinctMessage
    }

    /**
     * 讀取更多 訊息
     * @param channelId 頻道id
     */
    fun onLoadMore(channelId: String) {
        KLog.i(TAG, "onLoadMore.")
        viewModelScope.launch {
            //訊息不為空,才抓取分頁,因為預設會有Polling訊息, 超過才需讀取分頁
            if (_message.value.isNotEmpty()) {
                val lastMessage =
                    _message.value.last { it.messageType != ChatMessageWrapper.MessageType.TimeBar }

                val serialNumber = lastMessage.message.serialNumber
                chatRoomUseCase.fetchMoreMessage(
                    chatRoomChannelId = channelId,
                    fromSerialNumber = serialNumber,
                ).fold({
                    it.items?.also { message ->
                        if (message.isEmpty()) {
                            return@launch
                        }

                        val newMessage = message.map {
                            ChatMessageWrapper(message = it)
                        }.reversed()
                        //檢查插入時間 bar
                        val timeBarMessage = MessageUtils.insertTimeBar(newMessage)

                        processMessageCombine(timeBarMessage.map { chatMessageWrapper ->
                            MessageUtils.defineMessageType(chatMessageWrapper)
                        })
                    }
                }, {
                    KLog.e(TAG, it)
                })
            }
        }
    }

    /**
     * 對外 發送訊息 接口
     * @param channelId 頻道id
     * @param text 內文
     * @param attachment 附加檔案
     */
    fun messageSend(
        channelId: String,
        text: String,
        attachment: Map<AttachmentType, List<AttachmentInfoItem>>
    ) {
        KLog.i(TAG, "send:" + text + " , media:" + attachment.size)

        viewModelScope.launch {
            val listItem = attachment.flatMap { item ->
                val key = item.key
                val value = item.value
                value.map {
                    key to it
                }
            }

            chatRoomUseCase.sendMessage(
                chatRoomChannelId = channelId,
                text = text,
                attachment = listItem,
                replyMessageId = _replyMessage.value?.id.orEmpty()
            ).onSuccess { chatMessage ->
                //發送成功
                KLog.i(TAG, "send success:$chatMessage")
                _message.value = _message.value.map {
                    if (it.message.id == preSendChatId) {
                        ChatMessageWrapper(message = chatMessage)
                    } else {
                        it
                    }
                }

                _isSendComplete.value = true
                _replyMessage.value = null
                //恢復聊天室內訊息,不會自動捲到最下面
                delay(800)
                _isSendComplete.value = false
            }.onFailure {
                //發送失敗
                KLog.e(TAG, it)

                if (it.message?.contains("403") == true) {
                    //檢查身上狀態
                    checkChannelPermission(channelId)
                }
            }
        }
    }

    /**
     * 檢查 目前在此 channel permission 狀態, 並show tip
     */
    private fun checkChannelPermission(channelId: String) {
        KLog.i(TAG, "checkChannelPermission:$channelId")
        viewModelScope.launch {
            permissionUseCase.updateChannelPermissionAndBuff(channelId = channelId).fold({
                showPermissionTip()
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 點擊 Emoji, 判斷是否增加or收回
     *
     * @param chatMessage 訊息 model
     * @param resourceId 點擊的Emoji resourceId
     */
    private fun onEmojiClick(chatMessage: ChatMessage, resourceId: Int) {
        KLog.i(TAG, "onEmojiClick:$chatMessage")
        viewModelScope.launch {
            val clickEmoji = Utils.emojiResourceToServerKey(resourceId)

            //判斷是否為收回Emoji
            var emojiCount = 1
            chatMessage.messageReaction?.let {
                emojiCount = if (it.emoji.orEmpty().lowercase() == clickEmoji.value.lowercase()) {
                    //收回
                    -1
                } else {
                    //增加
                    1
                }
            }

            //先將 Emoji append to ui.
            _message.value = _message.value.map { chatMessageWrapper ->
                if (chatMessageWrapper.message.id == chatMessage.id) {
                    val orgEmoji = chatMessageWrapper.message.emojiCount
                    val newEmoji = when (clickEmoji) {
                        Emojis.like -> orgEmoji?.copy(like = orgEmoji.like?.plus(emojiCount))
                        Emojis.dislike -> orgEmoji?.copy(
                            dislike = orgEmoji.dislike?.plus(
                                emojiCount
                            )
                        )

                        Emojis.laugh -> orgEmoji?.copy(laugh = orgEmoji.laugh?.plus(emojiCount))
                        Emojis.money -> orgEmoji?.copy(money = orgEmoji.money?.plus(emojiCount))
                        Emojis.shock -> orgEmoji?.copy(shock = orgEmoji.shock?.plus(emojiCount))
                        Emojis.cry -> orgEmoji?.copy(cry = orgEmoji.cry?.plus(emojiCount))
                        Emojis.think -> orgEmoji?.copy(think = orgEmoji.think?.plus(emojiCount))
                        Emojis.angry -> orgEmoji?.copy(angry = orgEmoji.angry?.plus(emojiCount))
                    }

                    //回填資料
                    chatMessageWrapper.copy(
                        message = chatMessageWrapper.message.copy(
                            emojiCount = newEmoji,
                            messageReaction = if (emojiCount == -1) null else {
                                IUserMessageReaction(
                                    emoji = clickEmoji.value
                                )
                            }
                        )
                    )
                } else {
                    chatMessageWrapper
                }
            }

            //Call Emoji api
            if (emojiCount == -1) {
                //收回
                chatRoomUseCase.deleteEmoji(
                    messageServiceType = MessageServiceType.chatroom,
                    chatMessage.id.orEmpty()
                ).fold({
                    KLog.e(TAG, "delete emoji success.")
                }, {
                    KLog.e(TAG, it)
                })
            } else {
                //增加
                chatRoomUseCase.sendEmoji(
                    messageServiceType = MessageServiceType.chatroom,
                    chatMessage.id.orEmpty(), clickEmoji
                ).fold({
                    KLog.i(TAG, "sendEmoji success.")
                }, {
                    KLog.e(TAG, it)
                })
            }
        }
    }

    /**
     * 互動彈窗
     * @param messageInteract 互動 model
     */
    fun onInteractClick(messageInteract: MessageInteract) {
        KLog.i(TAG, "onInteractClick:$messageInteract")
        when (messageInteract) {
            is MessageInteract.Announcement -> {
                AppUserLogger.getInstance().log(Clicked.MessageLongPressMessagePinMessage)
                announceMessage(messageInteract.message)
            }

            is MessageInteract.Copy -> {
                AppUserLogger.getInstance().log(Clicked.MessageLongPressMessageCopyMessage)
                _copyMessage.value = messageInteract.message
            }

            is MessageInteract.Delete -> {
                AppUserLogger.getInstance().log(Clicked.MessageLongPressMessageDeleteMessage)
                deleteMessage(messageInteract.message)
            }

            is MessageInteract.HideUser -> {
                _hideUserMessage.value = messageInteract.message
            }

            is MessageInteract.Recycle -> {
                AppUserLogger.getInstance().log(Clicked.MessageLongPressMessageUnsendMessage)
                recycleMessage(messageInteract.message)
            }

            is MessageInteract.Reply -> {
                AppUserLogger.getInstance().log(Clicked.MessageLongPressMessageReply)
                replyMessage(
                    IReplyMessage(
                        id = messageInteract.message.id,
                        author = messageInteract.message.author,
                        content = messageInteract.message.content,
                        isDeleted = messageInteract.message.isDeleted,
                        replyVotings = messageInteract.message.votings?.toIReplyVotingList()
                    )
                )
            }

            is MessageInteract.Report -> {
                AppUserLogger.getInstance().log(Clicked.MessageLongPressMessageReport)
                _reportMessage.value = messageInteract.message
            }

            is MessageInteract.EmojiClick -> {
                onEmojiClick(
                    messageInteract.message,
                    messageInteract.emojiResId
                )
            }

            else -> {}
        }
    }

    /**
     * 刪除 訊息
     */
    private fun deleteMessage(message: ChatMessage) {
        KLog.i(TAG, "deleteMessage:$message")
        _deleteMessage.value = message
    }

    /**
     * 關閉 刪除訊息 彈窗
     */
    fun onDeleteMessageDialogDismiss() {
        _deleteMessage.value = null
    }

    /**
     * 確定 刪除 訊息
     * 要先判斷 刪除自己的貼文 or 刪除他人(需要有該權限)
     */
    fun onDeleteClick(chatMessageModel: ChatMessage) {
        KLog.i(TAG, "onDeleteClick:$chatMessageModel")
        viewModelScope.launch {
            if (chatMessageModel.author?.id == Constant.MyInfo?.id) {
                //自己發的文章
                KLog.i(TAG, "onDelete my post")
                chatRoomUseCase.takeBackMyMessage(
                    messageServiceType = MessageServiceType.chatroom,
                    chatMessageModel.id.orEmpty()
                ).fold({
                    KLog.i(TAG, "onDelete my post success")
                    _message.value = _message.value.filter {
                        it.message != chatMessageModel
                    }

                    _deleteMessage.value = null

                    snackBarMessage(
                        CustomMessage(
                            textString = "成功刪除訊息！",
                            textColor = Color.White,
                            iconRes = R.drawable.delete,
                            iconColor = White_767A7F,
                            backgroundColor = White_494D54
                        )
                    )
                }, {
                    KLog.e(TAG, it)
                })
            } else {
                //別人的文章
                KLog.i(TAG, "onDelete other post")
                chatRoomUseCase.deleteOtherMessage(
                    messageServiceType = MessageServiceType.chatroom,
                    chatMessageModel.id.orEmpty()
                ).fold({
                    KLog.i(TAG, "onDelete other post success")
                    _message.value = _message.value.filter {
                        it.message != chatMessageModel
                    }

                    _deleteMessage.value = null

                    snackBarMessage(
                        CustomMessage(
                            textString = "成功刪除訊息！",
                            textColor = Color.White,
                            iconRes = R.drawable.delete,
                            iconColor = White_767A7F,
                            backgroundColor = White_494D54
                        )
                    )
                }, {
                    KLog.e(TAG, it)
                })
            }
        }
    }

    /**
     * 收回 訊息
     */
    private fun recycleMessage(message: ChatMessage) {
        KLog.i(TAG, "recycleMessage:$message")
        viewModelScope.launch {
            chatRoomUseCase.recycleMessage(
                messageServiceType = MessageServiceType.chatroom,
                messageId = message.id.orEmpty()
            ).fold({
                _message.value = _message.value.map { chatMessageWrapper ->
                    if (chatMessageWrapper.message.id == message.id) {
                        chatMessageWrapper.copy(
                            message = chatMessageWrapper.message.copy(
                                isDeleted = true
                            )
                        )
                    } else {
                        chatMessageWrapper
                    }
                }
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 點擊 回覆訊息
     * @param message
     */
    private fun replyMessage(message: IReplyMessage) {
        KLog.i(TAG, "replyMessage click:$message")
        _replyMessage.value = message
    }

    /**
     * 取消 回覆訊息
     * @param reply
     */
    fun removeReply(reply: IReplyMessage) {
        KLog.i(TAG, "removeReply:$reply")
        _replyMessage.value = null
    }

    /**
     * 訊息 設定 公告
     * @param messageModel 訊息
     */
    private fun announceMessage(messageModel: ChatMessage) {
        KLog.i(TAG, "announceMessage:$messageModel")
        val noEmojiMessage = messageModel.copy(
            emojiCount = null
        )
        _routeAnnounceMessage.value = noEmojiMessage
    }

    /**
     * 設置公告 跳轉完畢
     */
    fun announceRouteDone() {
        _routeAnnounceMessage.value = null
    }

    /**
     * 執行完複製
     */
    fun copyDone() {
        _copyMessage.value = null
    }

    /**
     * 關閉 隱藏用戶彈窗
     */
    fun onHideUserDialogDismiss() {
        _hideUserMessage.value = null
    }


    /**
     * 檢舉 用戶
     */
    fun onReportUser(reason: ReportReason, channelId: String, contentId: String) {
        KLog.i(TAG, "onReportUser:$reason")
        viewModelScope.launch {
            chatRoomUseCase.reportContent(
                channelId = channelId,
                contentId = contentId,
                reason = reason
            ).fold({
                KLog.i(TAG, "onReportUser success:$it")
                _reportMessage.value = null

                snackBarMessage(
                    CustomMessage(
                        textString = "檢舉成立！",
                        textColor = Color.White,
                        iconRes = R.drawable.report,
                        iconColor = White_767A7F,
                        backgroundColor = White_494D54
                    )
                )
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 關閉 檢舉用戶 彈窗
     */
    fun onReportUserDialogDismiss() {
        _reportMessage.value = null
    }

    /**
     * 點擊 再次發送
     */
    fun onReSendClick(it: ChatMessageWrapper) {
        KLog.i(TAG, "onReSendClick:$it")
        _showReSendDialog.value = it
    }

    /**
     * 關閉 重新發送 Dialog
     */
    fun onReSendDialogDismiss() {
        _showReSendDialog.value = null
    }

    /**
     * 刪除 未發送訊息
     */
    fun onDeleteReSend(message: ChatMessageWrapper) {
        KLog.i(TAG, "onDeleteReSend:$message")
        _message.value = _message.value.filter {
            !it.isPendingSendMessage && it != message
        }
        _showReSendDialog.value = null
    }

    /**
     *  重新發送訊息
     */
    fun onResendMessage(
        channelId: String,
        message: ChatMessageWrapper,
        attachment: Map<AttachmentType, List<AttachmentInfoItem>>
    ) {
        KLog.i(TAG, "onResendMessage:$message")
        onDeleteReSend(message)
        messageSend(
            channelId = channelId,
            text = message.message.content?.text.orEmpty(),
            attachment = attachment
        )
        onReSendDialogDismiss()
    }

    /**
     * show 無法與頻道成員互動 tip
     */
    fun showPermissionTip() {
        KLog.i(TAG, "showBasicPermissionTip")
        snackBarMessage(
            CustomMessage(
                textString = Constant.getChannelSilenceDesc(
                    context = context
                ),
                iconRes = R.drawable.minus_people,
                iconColor = White_767A7F,
                textColor = Color.White
            )
        )
    }

    /**
     * 複製訊息
     */
    fun copyMessage(message: ChatMessage) {
        context.copyToClipboard(message.content?.text.orEmpty())
        snackBarMessage(
            CustomMessage(
                textString = "訊息複製成功！",
                textColor = Color.White,
                iconRes = R.drawable.copy,
                iconColor = White_767A7F,
                backgroundColor = White_494D54
            )
        )
    }

    /**
     * 發送 snackBar 訊息
     */
    private fun snackBarMessage(message: CustomMessage) {
        KLog.i(TAG, "snackMessage:$message")
        viewModelScope.launch {
            _snackBarMessage.emit(message)
        }
    }

    /**
     * 前往指定訊息, 並從該訊息index 開始往下 polling
     *
     * @param channelId 頻道id
     * @param jumpChatMessage 指定跳往的訊息
     */
    fun forwardToMessage(channelId: String, jumpChatMessage: ChatMessage) {
        KLog.i(TAG, "forwardToMessage:$jumpChatMessage")
        viewModelScope.launch {
            val messageId = jumpChatMessage.id
            messageId?.let {
                chatRoomUseCase.getSingleMessage(
                    messageId = messageId,
                    messageServiceType = MessageServiceType.chatroom
                ).onSuccess { chatMessage ->
                    KLog.i(TAG, "get single message:$chatMessage")

                    val allMessage = mutableListOf<ChatMessage>()

                    //前 20 筆
                    chatRoomUseCase.fetchMoreMessage(
                        chatRoomChannelId = channelId,
                        fromSerialNumber = chatMessage.serialNumber,
                        order = OrderType.latest
                    ).getOrNull()?.apply {
                        allMessage.addAll(this.items.orEmpty())
                    }

                    //當下訊息
                    allMessage.add(chatMessage)

                    //後 20 筆
                    chatRoomUseCase.fetchMoreMessage(
                        chatRoomChannelId = channelId,
                        fromSerialNumber = chatMessage.serialNumber,
                        order = OrderType.oldest
                    ).getOrNull()?.apply {
                        allMessage.addAll(this.items.orEmpty())
                    }

                    val newMessage = allMessage.map { chatMessage ->
                        ChatMessageWrapper(message = chatMessage)
                    }.reversed()

                    //檢查插入時間 bar
                    val timeBarMessage = MessageUtils.insertTimeBar(newMessage)

                    processMessageCombine(timeBarMessage.map { chatMessageWrapper ->
                        MessageUtils.defineMessageType(chatMessageWrapper)
                    })

                    //滑動至指定訊息
                    val scrollPosition =
                        _message.value.indexOfFirst { it.message == chatMessage }.coerceAtLeast(0)
                    _scrollToPosition.value = scrollPosition

                    startPolling(channelId, allMessage.last().serialNumber)
                }.onFailure { e ->
                    KLog.e(TAG, e)
                }
            }
        }
    }

    private var pollingJob: Job? = null

    /**
     * Polling 範圍內的訊息
     *
     * @param channelId 頻道 id
     * @param startItemIndex 畫面第一個 item position
     * @param lastIndex 畫面最後一個 item position
     */
    fun pollingScopeMessage(
        channelId: String,
        startItemIndex: Int,
        lastIndex: Int
    ) {
        KLog.i(
            TAG,
            "pollingScopeMessage:$channelId, startItemIndex:$startItemIndex, lastIndex:$lastIndex"
        )

        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            if (_message.value.size > startItemIndex) {
                val item = _message.value[startItemIndex]
                val message = item.message
                val scopeFetchCount = (lastIndex - startItemIndex).plus(1)

                //filter time bar
                if (item.messageType != ChatMessageWrapper.MessageType.TimeBar) {
                    chatRoomPollUseCase.pollScope(
                        delay = scopePollingInterval,
                        channelId = channelId,
                        fromSerialNumber = message.serialNumber,
                        fetchCount = scopeFetchCount
                    ).collect { emitMessagePaging ->
                        if (emitMessagePaging.items?.isEmpty() == true) {
                            return@collect
                        }

                        //Update data
                        val updateMessageList = _message.value.map { message ->
                            val filterMessage = emitMessagePaging.items?.firstOrNull {
                                it.id == message.message.id
                            }
                            if (filterMessage == null) {
                                message
                            } else {
                                message.copy(message = filterMessage)
                            }
                        }

                        _message.update {
                            updateMessageList
                        }
                    }
                }
            }
        }
    }
}