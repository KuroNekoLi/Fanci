package com.cmoney.kolfanci.extension

import android.content.Context
import com.cmoney.kolfanci.model.attachment.AttachmentInfoItem
import com.cmoney.kolfanci.model.attachment.AttachmentType
import com.cmoney.kolfanci.model.vote.VoteModel

fun AttachmentInfoItem.getAttachmentType(context: Context): AttachmentType {
    var attachmentType = this.uri.getAttachmentType(context)
    val otherModel = this.other
    if (attachmentType is AttachmentType.Unknown) {
        otherModel?.let {
            if (otherModel is VoteModel) {
                attachmentType = AttachmentType.Choice
            }
        }
    }
    return  attachmentType
}