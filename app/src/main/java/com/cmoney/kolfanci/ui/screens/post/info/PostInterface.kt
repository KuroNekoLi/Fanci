package com.cmoney.kolfanci.ui.screens.post.info

import com.cmoney.fanciapi.fanci.model.BulletinboardMessage

/**
 * 貼文 詳細頁面 交互介面
 */
interface PostInfoListener {
    fun onEmojiClick(comment: BulletinboardMessage, resourceId: Int)

    /**
     * 送出留言
     */
    fun onCommentSend(text: String)

    /**
     * 點擊返回
     */
    fun onBackClick()

    /**
     * 點擊附加圖片
     */
    fun onAttachClick()

    /**
     * 點擊留言的 Emoji
     */
    fun onCommentEmojiClick(comment: BulletinboardMessage, resourceId: Int)

    /**
     * 關閉回覆UI
     */
    fun onCommentReplyClose()

    /**
     * 讀取更多留言
     */
    fun onCommentLoadMore()

    /**
     * 文章點擊更多
     */
    fun onPostMoreClick(post: BulletinboardMessage)
}

/**
 * 貼文 詳細頁面 - 留言 交互介面
 */
interface CommentBottomContentListener {
    /**
     * 點擊 回覆按鈕
     */
    fun onCommentReplyClick(comment: BulletinboardMessage)

    /**
     * 點擊 展開/隱藏
     */
    fun onExpandClick(comment: BulletinboardMessage)

    /**
     * 點擊 讀取更多回覆
     */
    fun onLoadMoreReply(comment: BulletinboardMessage)

    /**
     * 點擊 Emoji
     */
    fun onReplyEmojiClick(
        comment: BulletinboardMessage,
        reply: BulletinboardMessage,
        resourceId: Int
    )

    /**
     * 點擊 留言的更多
     */
    fun onCommentMoreActionClick(comment: BulletinboardMessage)

    /**
     * 點擊 回覆的更多
     */
    fun onReplyMoreActionClick(comment: BulletinboardMessage, reply: BulletinboardMessage)
}

object EmptyPostInfoListener : PostInfoListener {
    override fun onEmojiClick(comment: BulletinboardMessage, resourceId: Int) {
    }

    override fun onCommentSend(text: String) {
    }

    override fun onBackClick() {
    }

    override fun onAttachClick() {
    }

    override fun onCommentEmojiClick(comment: BulletinboardMessage, resourceId: Int) {
    }

    override fun onCommentReplyClose() {
    }

    override fun onCommentLoadMore() {
    }

    override fun onPostMoreClick(post: BulletinboardMessage) {
    }
}

object EmptyCommentBottomContentListener : CommentBottomContentListener {
    override fun onCommentReplyClick(comment: BulletinboardMessage) {
    }

    override fun onExpandClick(comment: BulletinboardMessage) {
    }

    override fun onLoadMoreReply(comment: BulletinboardMessage) {
    }

    override fun onReplyEmojiClick(
        comment: BulletinboardMessage,
        reply: BulletinboardMessage,
        resourceId: Int
    ) {
    }

    override fun onCommentMoreActionClick(comment: BulletinboardMessage) {
    }

    override fun onReplyMoreActionClick(
        comment: BulletinboardMessage,
        reply: BulletinboardMessage
    ) {
    }
}