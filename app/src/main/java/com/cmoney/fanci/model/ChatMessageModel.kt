package com.cmoney.fanci.model

import androidx.annotation.DrawableRes
import java.text.SimpleDateFormat

/**
 * 聊天室 訊息 model
 */
data class ChatMessageModel(
    val poster: User,
    val publishTime: Long,
    val message: Message
) {

    val displayTime: String
        get() {
            val sdf = SimpleDateFormat("yyyy/MM/dd")
            return sdf.format(publishTime)
        }


    data class User(
        val avatar: String,
        val nickname: String
    )

    data class Message(
        val reply: Reply?,
        val text: String,               //內文
        val media: List<Media>?,        //多媒體
        val emoji: List<Emoji>?,        //Emoji List
        val isRecycle: Boolean = false  //是否被回收
    )

    /**
     * 回覆
     */
    data class Reply(
        val replyUser: User,
        val text: String
    )

    data class Emoji(@DrawableRes val resource: Int, val count: Int)

    sealed class Media {
        data class Article(val from: String, val title: String, val thumbnail: String) : Media()
        data class Youtube(val channel: String, val title: String, val thumbnail: String) : Media()
        data class Instagram(val channel: String, val title: String, val thumbnail: String) :
            Media()

        data class Image(val image: List<String>) : Media()
    }

}
