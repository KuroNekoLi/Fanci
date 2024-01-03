package com.cmoney.kolfanci.ui.screens.shared.bottomSheet.mediaPicker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.attachment.AttachmentInfoItem
import com.cmoney.kolfanci.model.attachment.AttachmentType
import com.cmoney.kolfanci.ui.screens.chat.attachment.AttachImageDefault

class MediaPickerBottomSheetViewModel(
    val context: Application
) : AndroidViewModel(context) {

    /**
     * 檢查 上傳圖片 資格
     * 聊天室: 最多上傳10張, 只能上傳一種類型附加檔案
     * 貼文: 最多上傳10張
     *
     * @param onOpen 檢查通過
     * @param onError 檢查失敗, 並回傳錯誤訊息 (title, description)
     */
    fun photoPickCheck(
        selectedAttachment: Map<AttachmentType, List<AttachmentInfoItem>>,
        attachmentEnv: AttachmentEnv,
        onOpen: () -> Unit,
        onError: (String, String) -> Unit
    ) {
        val attachmentTypes = selectedAttachment.keys
        when (attachmentEnv) {
            AttachmentEnv.Chat -> {
                if (!attachmentTypes.contains(AttachmentType.Image) && attachmentTypes.isNotEmpty()) {
                    onError.invoke(
                        context.getString(R.string.chat_attachment_limit_title),
                        context.getString(R.string.chat_attachment_limit_desc)
                    )
                } else if (attachmentTypes.contains(AttachmentType.Image) && (selectedAttachment[AttachmentType.Image]?.size
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
                if (attachmentTypes.contains(AttachmentType.Image) && (selectedAttachment[AttachmentType.Image]?.size
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
                    onOpen.invoke()
                }
            }
        }
    }

    /**
     * 檢查 上傳檔案 資格
     * 聊天室: 最多上傳一個檔案, 只能上傳一種類型附加檔案
     * 貼文: (待補)
     */
    fun filePickCheck(
        selectedAttachment: Map<AttachmentType, List<AttachmentInfoItem>>,
        attachmentEnv: AttachmentEnv,
        onOpen: () -> Unit,
        onError: (String, String) -> Unit
    ) {
        val attachmentTypes = selectedAttachment.keys
        when (attachmentEnv) {
            AttachmentEnv.Chat -> {
                if (attachmentTypes.contains(AttachmentType.Image)) {
                    onError.invoke(
                        context.getString(R.string.chat_attachment_limit_title),
                        context.getString(R.string.chat_attachment_limit_desc)
                    )
                } else if (selectedAttachment.isNotEmpty()) {
                    onError.invoke(
                        context.getString(R.string.chat_attachment_file_limit_title),
                        context.getString(R.string.chat_attachment_file_limit_desc)
                    )

                } else {
                    onOpen.invoke()
                }
            }

            AttachmentEnv.Post -> {
                val otherFilesCount = selectedAttachment.filter {
                    it.key != AttachmentType.Image
                }.size

                if (otherFilesCount >= AttachImageDefault.DEFAULT_QUANTITY_LIMIT
                ) {
                    onError.invoke(
                        context.getString(R.string.post_attachment_file_limit_title)
                            .format(AttachImageDefault.DEFAULT_QUANTITY_LIMIT),
                        context.getString(R.string.post_attachment_file_limit_desc).format(
                            AttachImageDefault.DEFAULT_QUANTITY_LIMIT,
                            AttachImageDefault.DEFAULT_QUANTITY_LIMIT
                        )
                    )
                } else {
                    onOpen.invoke()
                }
            }
        }
    }

    fun choiceVoteCheck(
        selectedAttachment: Map<AttachmentType, List<AttachmentInfoItem>>,
        attachmentEnv: AttachmentEnv,
        onOpen: () -> Unit,
        onError: (String, String) -> Unit
    ) {
        when (attachmentEnv) {
            AttachmentEnv.Chat -> {
                if (selectedAttachment.isNotEmpty()) {
                    onError.invoke(
                        context.getString(R.string.chat_attachment_limit),
                        context.getString(R.string.chat_attachment_limit_text)
                    )
                } else {
                    onOpen.invoke()
                }
            }

            AttachmentEnv.Post -> {
                val otherFilesCount = selectedAttachment.filter {
                    it.key != AttachmentType.Image
                }.size

                if (otherFilesCount >= AttachImageDefault.DEFAULT_QUANTITY_LIMIT
                ) {
                    onError.invoke(
                        context.getString(R.string.post_attachment_file_limit_title)
                            .format(AttachImageDefault.DEFAULT_QUANTITY_LIMIT),
                        context.getString(R.string.post_attachment_file_limit_desc).format(
                            AttachImageDefault.DEFAULT_QUANTITY_LIMIT,
                            AttachImageDefault.DEFAULT_QUANTITY_LIMIT
                        )
                    )
                } else {
                    onOpen.invoke()
                }
            }
        }
    }

    fun RecordCheck(
        selectedAttachment: Map<AttachmentType, List<AttachmentInfoItem>>,
        attachmentEnv: AttachmentEnv,
        onOpen: () -> Unit,
        onError: (String, String) -> Unit
    ) {
        when (attachmentEnv) {
            AttachmentEnv.Chat -> {
                if (selectedAttachment.isNotEmpty()) {
                    onError.invoke(
                        context.getString(R.string.chat_attachment_limit),
                        context.getString(R.string.chat_attachment_limit_text)
                    )
                } else {
                    onOpen.invoke()
                }
            }

            AttachmentEnv.Post -> {
                val otherFilesCount = selectedAttachment.filter {
                    it.key != AttachmentType.Image
                }.size

                if (otherFilesCount >= AttachImageDefault.DEFAULT_QUANTITY_LIMIT
                ) {
                    onError.invoke(
                        context.getString(R.string.post_attachment_file_limit_title)
                            .format(AttachImageDefault.DEFAULT_QUANTITY_LIMIT),
                        context.getString(R.string.post_attachment_file_limit_desc).format(
                            AttachImageDefault.DEFAULT_QUANTITY_LIMIT,
                            AttachImageDefault.DEFAULT_QUANTITY_LIMIT
                        )
                    )
                } else {
                    onOpen.invoke()
                }
            }
        }
    }
}