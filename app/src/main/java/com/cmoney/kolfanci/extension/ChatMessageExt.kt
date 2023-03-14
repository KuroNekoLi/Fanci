package com.cmoney.kolfanci.extension

import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fanciapi.fanci.model.User

/**
 * 是否為自己的訊息
 */
fun ChatMessage.isMyPostMessage(myInfo: User?): Boolean = author?.id == myInfo?.id