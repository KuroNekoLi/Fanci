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
    messageReaction =  messageReaction,
    deleteStatus = deleteStatus,
    deleteFrom = deleteFrom,
    commentCount = commentCount,
    votings = votings
)