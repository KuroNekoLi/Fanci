package com.cmoney.kolfanci.model.notification

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

sealed class TargetType : Parcelable {

    /**
     * 打開首頁
     */
    @Parcelize
    object MainPage : TargetType()

    /**
     * 邀請 加入社團
     */
    @Parcelize
    data class InviteGroup(
        @SerializedName("groupId", alternate = ["GroupId"])
        val groupId: String = ""
    ) : TargetType()

    /**
     * 收到 社團訊息
     */
    @Parcelize
    data class ReceiveMessage(
        @SerializedName("groupId", alternate = ["GroupId"])
        val groupId: String = "",
        @SerializedName("messageId", alternate = ["MessageId"])
        val messageId: String = "",
        @SerializedName("channelId", alternate = ["ChannelId"])
        val channelId: String = "",
    ) : TargetType()

    /**
     * 收到 社團貼文
     */
    @Parcelize
    data class ReceivePostMessage(
        @SerializedName("groupId", alternate = ["GroupId"])
        val groupId: String = "",
        @SerializedName("channelId", alternate = ["ChannelId"])
        val channelId: String = "",
        @SerializedName("messageId", alternate = ["MessageId"])
        val messageId: String = "",
    ) : TargetType()

    /**
     * 解散 社團
     */
    @Parcelize
    data class DissolveGroup(
        @SerializedName("groupId", alternate = ["GroupId"])
        val groupId: String = ""
    ) : TargetType()

    /**
     * 管理者, 前往申請加入審核頁面
     */
    @Parcelize
    data class GroupApprove(
        @SerializedName("groupId", alternate = ["GroupId"])
        val groupId: String = ""
    ) : TargetType()

    /**
     * 打開指定社團
     */
    @Parcelize
    data class OpenGroup(
        @SerializedName("groupId", alternate = ["GroupId"])
        val groupId: String = ""
    ) : TargetType()
}
