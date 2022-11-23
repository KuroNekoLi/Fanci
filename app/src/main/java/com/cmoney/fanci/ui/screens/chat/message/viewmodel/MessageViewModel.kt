package com.cmoney.fanci.ui.screens.chat.message.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.BuildConfig
import com.cmoney.fanci.model.ChatMessageWrapper
import com.cmoney.fanci.model.usecase.ChatRoomPollUseCase
import com.cmoney.fanci.model.usecase.ChatRoomUseCase
import com.cmoney.fanci.ui.screens.chat.viewmodel.ChatRoomUiState
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fanciapi.fanci.model.MediaIChatContent
import com.cmoney.imagelibrary.UploadImage
import com.cmoney.xlogin.XLoginHelper
import com.socks.library.KLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class MessageUiState(
    val message: List<ChatMessageWrapper> = emptyList(),
    val imageAttach: List<Uri> = emptyList(),
    val isSendComplete: Boolean = false
)

/**
 * 處理聊天室 相關訊息
 */
class MessageViewModel(
    val context: Context,
    val chatRoomUseCase: ChatRoomUseCase,
    private val chatRoomPollUseCase: ChatRoomPollUseCase
) : ViewModel() {
    private val TAG = MessageViewModel::class.java.simpleName

    var uiState by mutableStateOf(MessageUiState())
        private set

    private val preSendChatId = "Preview"       //發送前預覽的訊息id, 用來跟其他訊息區分

    /**
     * 圖片上傳
     */
    interface ImageUploadCallback {
        fun complete(images: List<String>)

        fun onFailure(e: Throwable)
    }

    /**
     * 獲取 聊天室 訊息
     * @param channelId 聊天室id
     */
    fun startPolling(channelId: String?) {
        KLog.i(TAG, "startPolling:$channelId")
        viewModelScope.launch {
            if (channelId?.isNotEmpty() == true) {
                chatRoomPollUseCase.poll(3000, channelId).collect {
                    KLog.i(TAG, it)
                    val newMessage = it.items?.map { chatMessage ->
                        ChatMessageWrapper(message = chatMessage)
                    }.orEmpty().reversed()
                    processMessageCombine(newMessage)
                }
            }
        }
    }

    /**
     * 停止 聊天室 訊息
     */
    fun stopPolling() {
        KLog.i(TAG, "stopPolling")
        chatRoomPollUseCase.close()
    }

    /**
     *  將新的訊息 跟舊的合併
     *  @param newChatMessage 新訊息
     */
    private fun processMessageCombine(newChatMessage: List<ChatMessageWrapper>) {
        //combine old message
        val oldMessage = uiState.message.toMutableList()
        oldMessage.addAll(0, newChatMessage)

        val distinctMessage = oldMessage.distinctBy { combineMessage ->
            combineMessage.message.id
        }
        uiState = uiState.copy(
            message = distinctMessage,
        )
    }

    /**
     * 讀取更多 訊息
     * @param channelId 頻道id
     */
    fun onLoadMore(channelId: String) {
        KLog.i(TAG, "onLoadMore.")
        viewModelScope.launch {
            //訊息不為空,才抓取分頁,因為預設會有Polling訊息, 超過才需讀取分頁
            if (uiState.message.isNotEmpty()) {
                val lastMessage = uiState.message.last()

                val serialNumber = lastMessage.message.serialNumber
                chatRoomUseCase.fetchMoreMessage(
                    chatRoomChannelId = channelId,
                    fromSerialNumber = serialNumber,
                ).fold({
                    it.items?.also { message ->
                        val newMessage = message.map {
                            ChatMessageWrapper(message = it)
                        }.reversed()
                        processMessageCombine(newMessage)
                    }
                }, {
                    KLog.e(TAG, it)
                })
            }
        }
    }

    /**
     * 附加 圖片
     * @param uri 圖片 uri.
     */
    fun attachImage(uri: Uri) {
        KLog.i(TAG, "attachImage:$uri")
        val imageList = uiState.imageAttach.toMutableList()
        imageList.add(uri)
        uiState = uiState.copy(
            imageAttach = imageList
        )
    }

    /**
     * 移除 附加 圖片
     * @param uri 圖片 uri.
     */
    fun removeAttach(uri: Uri) {
        KLog.i(TAG, "removeAttach:$uri")
        val imageList = uiState.imageAttach.toMutableList()

        uiState = uiState.copy(
            imageAttach = imageList.filter {
                it != uri
            }
        )
    }

    /**
     * 對外 發送訊息 接口
     */
    fun messageSend(channelId: String, text: String) {
        KLog.i(TAG, "messageSend:$text")
        viewModelScope.launch {
            generatePreviewBeforeSend(text)

            //附加圖片, 獲取圖片 Url
            if (uiState.imageAttach.isNotEmpty()) {
                uploadImages(uiState.imageAttach, object : ImageUploadCallback {
                    override fun complete(images: List<String>) {
                        KLog.i(TAG, "all image upload complete:" + images.size)
                        send(channelId, text, images)
                    }

                    override fun onFailure(e: Throwable) {
                        KLog.e(TAG, "onFailure:$e")
                    }
                })
                uiState = uiState.copy(imageAttach = emptyList())
            }
            //Only text
            else {
                send(channelId, text)
            }
        }
    }

    /**
     * 發送前 直接先貼上畫面 Preview
     * @param text 發送文字
     */
    private fun generatePreviewBeforeSend(text: String) {
        uiState = uiState.copy(
            message = uiState.message.toMutableList().apply {
                add(0,
                    ChatMessageWrapper(
                        message = ChatMessage(
                            id = preSendChatId,
                            author = GroupMember(
                                name = XLoginHelper.nickName,
                                thumbNail = XLoginHelper.headImagePath
                            ),
                            content = MediaIChatContent(
                                text = text
                            ),
                            createUnixTime = System.currentTimeMillis() / 1000
                        ),
                        uploadAttachPreview = uiState.imageAttach.map { uri ->
                            ChatRoomUiState.ImageAttachState(
                                uri = uri
                            )
                        }
                    )
                )
            }
        )
    }


    /**
     * 上傳所有 附加圖片
     * @param uriLis 圖片清單
     * @param imageUploadCallback 圖片上傳 callback
     */
    private suspend fun uploadImages(uriLis: List<Uri>, imageUploadCallback: ImageUploadCallback) {
        KLog.i(TAG, "uploadImages:" + uriLis.size)

        uiState = uiState.copy(
            imageAttach = emptyList()
        )

        val uploadImage = UploadImage(
            context,
            uriLis,
            XLoginHelper.accessToken,
            isStaging = BuildConfig.DEBUG
        )

        withContext(Dispatchers.IO) {
            uploadImage.upload().catch { e ->
                KLog.e(TAG, e)
                imageUploadCallback.onFailure(e)

            }.collect {
                KLog.i(TAG, "uploadImage:$it")
                val uri = it.first
                val imageUrl = it.second
                //將 上傳成功的圖片 message overwrite 更改狀態
                uiState = uiState.copy(
                    message = uiState.message.map { chatMessageWrapper ->
                        if (chatMessageWrapper.message.id == preSendChatId) {
                            chatMessageWrapper.copy(
                                uploadAttachPreview = chatMessageWrapper.uploadAttachPreview.map { attachImage ->
                                    if (attachImage.uri == uri) {
                                        ChatRoomUiState.ImageAttachState(
                                            uri = uri,
                                            serverUrl = imageUrl,
                                            isUploadComplete = true
                                        )
                                    } else {
                                        attachImage
                                    }
                                }
                            )
                        } else {
                            chatMessageWrapper
                        }
                    }
                )

                //check is all image upload complete
                val preSendAttach = uiState.message.find {
                    it.message.id == preSendChatId
                }?.uploadAttachPreview

                val isComplete = preSendAttach?.none { imageAttach ->
                    !imageAttach.isUploadComplete
                }

                if (isComplete == true) {
                    imageUploadCallback.complete(preSendAttach.map { attach ->
                        attach.serverUrl
                    })
                }
            }
        }
    }

    /**
     * 對後端 Server 發送訊息
     * @param channelId 頻道id
     * @param text 發送內文
     * @param images 圖片清單
     */
    private fun send(channelId: String, text: String, images: List<String> = emptyList()) {
        KLog.i(TAG, "send:" + text + " , media:" + images.size)
        viewModelScope.launch {
            chatRoomUseCase.sendMessage(
                chatRoomChannelId = channelId,
                text = text,
                images = images
            ).fold({ chatMessage ->
                //發送成功
                KLog.i(TAG, "send success:$chatMessage")
                uiState = uiState.copy(
                    message = uiState.message.map {
                        if (it.message.id == preSendChatId) {
                            ChatMessageWrapper(message = chatMessage)
                        } else {
                            it
                        }
                    },
                    isSendComplete = true
                )

                //恢復聊天室內訊息,不會自動捲到最下面
                delay(800)
                uiState = uiState.copy(isSendComplete = false)

            }, {
                KLog.e(TAG, it)
            })
        }
    }
}