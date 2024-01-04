package com.cmoney.kolfanci.utils

import com.cmoney.fanciapi.fanci.model.DeleteStatus
import com.cmoney.kolfanci.extension.startOfDayFromTimestamp
import com.cmoney.kolfanci.model.ChatMessageWrapper

class MessageUtils {
    companion object {
        /**
         *  檢查內容,是否有跨日期,並在跨日中間插入 time bar,
         *  或是 內容不滿分頁大小(訊息太少), 也插入
         */
        fun insertTimeBar(newMessage: List<ChatMessageWrapper>): List<ChatMessageWrapper> {
            if (newMessage.isEmpty()) {
                return newMessage
            }

            //依照日期 分群
            val groupList = newMessage.groupBy {
                val createTime = it.message.createUnixTime?.times(1000) ?: 0L
                Utils.getTimeGroupByKey(createTime)
            }

            //大於2群, 找出跨日交界並插入time bar
            return if (groupList.size > 1) {
                val groupMessage = groupList.map {
                    val newList = it.value.toMutableList() //此list的時間是由新到舊
                    newList.add(generateTimeBar(it.value.last())) //將當天最早的訊息資料作為參數
                    newList
                }.flatten().toMutableList()
                groupMessage
            } else {
                val newList = newMessage.toMutableList()
                val timeBarMessage = generateTimeBar(newMessage.last())
                newList.add(timeBarMessage)
                return newList
            }
        }

        /**
         * 產生 Time bar item
         */
        private fun generateTimeBar(chatMessageWrapper: ChatMessageWrapper): ChatMessageWrapper {
            return chatMessageWrapper.copy(
                message = chatMessageWrapper.message.copy(
                    id = chatMessageWrapper.message.createUnixTime?.startOfDayFromTimestamp().toString(),
                    createUnixTime = chatMessageWrapper.message.createUnixTime?.startOfDayFromTimestamp()
                ),
                messageType = ChatMessageWrapper.MessageType.TimeBar
            )
        }

        /**
         * 定義 訊息類型
         */
        fun defineMessageType(chatMessageWrapper: ChatMessageWrapper): ChatMessageWrapper {
            val messageType =
                if (chatMessageWrapper.messageType == ChatMessageWrapper.MessageType.TimeBar) {
                    ChatMessageWrapper.MessageType.TimeBar
                } else if (chatMessageWrapper.isBlocking) {
                    ChatMessageWrapper.MessageType.Blocking
                } else if (chatMessageWrapper.isBlocker) {
                    ChatMessageWrapper.MessageType.Blocker
                } else if (chatMessageWrapper.message.isDeleted == true && chatMessageWrapper.message.deleteStatus == DeleteStatus.deleted) {
                    ChatMessageWrapper.MessageType.Delete
                } else if (chatMessageWrapper.message.isDeleted == true) {
                    ChatMessageWrapper.MessageType.RecycleMessage
                } else {
                    chatMessageWrapper.messageType
                }

            return chatMessageWrapper.copy(
                messageType = messageType
            )
        }
    }
}