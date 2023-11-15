package com.cmoney.kolfanci.ui.screens.post.info.data

import android.os.Parcelable
import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostInfoScreenResult(
    val post: BulletinboardMessage,
    val action: PostInfoAction = PostInfoAction.Default
) : Parcelable {
    @Parcelize
    sealed class PostInfoAction : Parcelable {
        object Default : PostInfoAction()
        object Pin : PostInfoAction()
        object Delete : PostInfoAction()
    }
}