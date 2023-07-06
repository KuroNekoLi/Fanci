package com.cmoney.kolfanci.ui.screens.post.info.model

import com.cmoney.fanciapi.fanci.model.BulletinboardMessage

/**
 * 回覆資料
 *
 * @param replyList 回覆資料
 * @param haveMore 是否還有分頁
 */
data class ReplyData(
    val replyList: List<BulletinboardMessage>,
    val haveMore: Boolean
)
