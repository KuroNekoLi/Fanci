package com.cmoney.kolfanci.utils

import com.cmoney.fanciapi.fanci.model.DeleteStatus
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
                val createTime = it.message.createUnixTime?.div(1000) ?: 0L
                Utils.getTimeGroupByKey(createTime)
            }

            //大於2群, 找出跨日交界並插入time bar
            return if (groupList.size > 1) {
                val groupMessage = groupList.map {
                    val newList = it.value.toMutableList()
                    newList.add(generateTimeBar(it.value.first()))
                    newList
                }.flatten().toMutableList()
                groupMessage
            } else {
                val newList = newMessage.toMutableList()
                val timeBarMessage = generateTimeBar(newMessage.first())
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
                    id = chatMessageWrapper.message.createUnixTime.toString()
                ),
                messageType = ChatMessageWrapper.MessageType.TimeBar
            )
        }

        /**
         * 定義 訊息類型
         */
        fun defineMessageType(chatMessageWrapper: ChatMessageWrapper): ChatMessageWrapper {
            val messageType = if (chatMessageWrapper.isBlocking) {
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