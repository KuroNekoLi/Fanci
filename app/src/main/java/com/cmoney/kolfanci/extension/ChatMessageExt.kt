package com.cmoney.kolfanci.extension

import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fanciapi.fanci.model.User

/**
 * 是否為自己的訊息
 */
fun ChatMessage.isMyPostMessage(myInfo: User?): Boolean = author?.id == myInfo?.id

fun ChatMessage.toBulletinboardMessage(): BulletinboardMessage = BulletinboardMessage(
    author = author,
    content = content,
    emojiCount = emojiCount,
    id = id,
    isDeleted = isDeleted,
    createUnixTime = createUnixTime,
    updateUnixTime = updateUnixTime,
    serialNumber = serialNumber,
    messageReaction = messageReaction,
    deleteStatus = deleteStatus,
    deleteFrom = deleteFrom,
    commentCount = commentCount,
    votings = votings
)

/**
 * 取得 置頂訊息 顯示內容
 */
fun ChatMessage.getPinMessage(): String {
    val content = this.content
    val text = content?.text.orEmpty()

    val mediaContent = mutableListOf<String>()

    content?.medias?.forEach { media ->
        val content = media.getDisplayType()
        mediaContent.add(content)
    }

    val mediaString = mediaContent.joinToString(separator = " ")
    return text + mediaString
}