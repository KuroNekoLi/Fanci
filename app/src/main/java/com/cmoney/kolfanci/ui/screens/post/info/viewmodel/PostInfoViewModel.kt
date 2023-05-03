package com.cmoney.kolfanci.ui.screens.post.info.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import com.cmoney.fanciapi.fanci.model.IUserMessageReaction
import com.cmoney.kolfanci.extension.clickCount
import com.cmoney.kolfanci.model.usecase.ChatRoomUseCase
import com.cmoney.kolfanci.utils.Utils
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostInfoViewModel(
    private val chatRoomUseCase: ChatRoomUseCase,
    private val bulletinboardMessage: BulletinboardMessage
): ViewModel() {
    private val TAG = PostInfoViewModel::class.java.simpleName

    private val _post = MutableStateFlow<BulletinboardMessage>(bulletinboardMessage)
    val post = _post.asStateFlow()

    fun onEmojiClick(postMessage: BulletinboardMessage, resourceId: Int) {
        KLog.i(TAG, "onEmojiClick:$resourceId")
        viewModelScope.launch {
            val clickEmoji = Utils.emojiResourceToServerKey(resourceId)
            //判斷是否為收回Emoji
            var emojiCount = 1
            postMessage.messageReaction?.let {
                emojiCount = if (it.emoji.orEmpty().lowercase() == clickEmoji.value.lowercase()) {
                    //收回
                    -1
                } else {
                    //增加
                    1
                }
            }

            val orgEmoji = postMessage.emojiCount
            val newEmoji = clickEmoji.clickCount(emojiCount, orgEmoji)

            //回填資料
            val postMessage = postMessage.copy(
                emojiCount = newEmoji,
                messageReaction = if (emojiCount == -1) null else {
                    IUserMessageReaction(
                        emoji = clickEmoji.value
                    )
                }
            )

            //UI show
            _post.value = postMessage

            //Call Emoji api
            chatRoomUseCase.clickEmoji(
                messageId = postMessage.id.orEmpty(),
                emojiCount = emojiCount,
                clickEmoji = clickEmoji
            )

        }
    }
}