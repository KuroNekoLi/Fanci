package com.cmoney.fanci.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat

/**
 * 聊天室 訊息 model
 */
@Parcelize
data class ChatMessageModel(
    val poster: User,
    val publishTime: Long,
    val message: Message
): Parcelable {

    val displayTime: String
        get() {
            val sdf = SimpleDateFormat("yyyy/MM/dd")
            return sdf.format(publishTime)
        }

    @Parcelize
    data class User(
        val avatar: String,
        val nickname: String
    ): Parcelable

    @Parcelize
    data class Message(
        val reply: Reply? = null,
        val text: String,               //內文
        val media: List<Media> = emptyList(),        //多媒體
        val emoji: List<Emoji> = emptyList(),        //Emoji List
        val isRecycle: Boolean = false, //是否被回收
        val isHideUser: Boolean = false //該用戶是否隱藏
    ): Parcelable

    /**
     * 回覆
     */
    @Parcelize
    data class Reply(
        val replyUser: User,
        val text: String
    ): Parcelable

    @Parcelize
    data class Emoji(@DrawableRes val resource: Int, val count: Int): Parcelable

    sealed class Media: Parcelable {
        @Parcelize
        data class Image(val image: List<String>) : Media()
    }

}
