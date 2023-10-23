package com.cmoney.kolfanci.ui.screens.shared.bottomSheet.mediaPicker

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.screens.chat.attachment.AttachImageDefault
import com.cmoney.kolfanci.ui.screens.chat.message.viewmodel.AttachmentType

class MediaPickerBottomSheetViewModel(
    val context: Application
) : AndroidViewModel(context) {

    /**
     * 檢查 上傳圖片 資格
     * 聊天室, 最多上傳10張, 只能上傳一種類型附加檔案
     *
     * @param onOpen 檢查通過
     * @param onError 檢查失敗, 並回傳錯誤訊息 (title, description)
     */
    fun photoPickCheck(
        selectedAttachment: Map<AttachmentType, List<Uri>>,
        attachmentEnv: AttachmentEnv,
        onOpen: () -> Unit,
        onError: (String, String) -> Unit
    ) {
        val attachmentTypes = selectedAttachment.keys
        when (attachmentEnv) {
            AttachmentEnv.Chat -> {
                if (!attachmentTypes.contains(AttachmentType.Picture) && attachmentTypes.isNotEmpty()) {
                    onError.invoke(
                        context.getString(R.string.chat_attachment_limit_title),
                        context.getString(R.string.chat_attachment_limit_desc)
                    )
                } else if (attachmentTypes.contains(AttachmentType.Picture) && (selectedAttachment[AttachmentType.Picture]?.size
                        ?: 0) >= AttachImageDefault.DEFAULT_QUANTITY_LIMIT
                ) {
                    onError.invoke(
                        context.getString(R.string.chat_attachment_image_limit_title)
                            .format(AttachImageDefault.DEFAULT_QUANTITY_LIMIT),
                        context.getString(R.string.chat_attachment_image_limit_desc).format(
                            AttachImageDefault.DEFAULT_QUANTITY_LIMIT,
                            AttachImageDefault.DEFAULT_QUANTITY_LIMIT
                        )
                    )
                } else {
                    //pass
                    onOpen.invoke()
                }
            }

            AttachmentEnv.Post -> {
                //TODO
            }
        }
    }

}