package com.cmoney.kolfanci.model.notification

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

sealed class TargetType : Parcelable {

    @Parcelize
    object MainPage : TargetType()

    @Parcelize
    data class InviteGroup(
        @SerializedName("groupId", alternate = ["GroupId"])
        val groupId: String = ""
    ) : TargetType()

    @Parcelize
    data class ReceiveMessage(
        @SerializedName("groupId", alternate = ["GroupId"])
        val groupId: String = "",
        @SerializedName("serialNumber", alternate = ["SerialNumber"])
        val serialNumber: String = "",
        @SerializedName("channelId", alternate = ["ChannelId"])
        val channelId: String = "",
    ) : TargetType()

}
