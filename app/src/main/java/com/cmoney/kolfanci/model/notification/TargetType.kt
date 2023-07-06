package com.cmoney.kolfanci.model.notification

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

sealed class TargetType : Parcelable {

    @Parcelize
    object MainPage : TargetType()

    @Parcelize
    data class InviteGroup(
        @SerializedName("groupId", alternate = ["GroupId"])
        val groupId: String = ""
    ) : TargetType()
}
