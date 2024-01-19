package com.cmoney.kolfanci.ui.screens.post.edit.attachment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.attachment.AttachmentInfoItem
import com.cmoney.kolfanci.model.attachment.AttachmentType
import com.cmoney.kolfanci.model.attachment.ReSendFile
import com.cmoney.kolfanci.model.vote.VoteModel
import com.cmoney.kolfanci.ui.screens.shared.attachment.AttachmentAudioItem
import com.cmoney.kolfanci.ui.screens.shared.attachment.AttachmentChoiceItem
import com.cmoney.kolfanci.ui.screens.shared.attachment.AttachmentFileItem
import com.cmoney.kolfanci.ui.theme.FanciTheme

/**
 * 發佈貼文 附加檔案 UI.
 */
@Composable
fun PostOtherAttachmentScreen(
    modifier: Modifier = Modifier,
    itemModifier: Modifier = Modifier,
    attachment: List<Pair<AttachmentType, AttachmentInfoItem>>,
    onClick: (AttachmentInfoItem) -> Unit,
    onDelete: (AttachmentInfoItem) -> Unit,
    onResend: ((ReSendFile) -> Unit)? = null
) {
    val listState = rememberLazyListState()
    val context = LocalContext.current

    LazyRow(
        modifier = modifier.padding(start = 10.dp, end = 10.dp),
        state = listState, horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        attachment.forEach { (attachmentType, attachmentInfoItem) ->
            when (attachmentType) {
                AttachmentType.VoiceMessage,
                AttachmentType.Audio -> {
                    item {
                        AttachmentAudioItem(
                            modifier = itemModifier,
                            file = attachmentInfoItem.uri,
                            duration = attachmentInfoItem.duration ?: 0,
                            isRecordFile = attachmentType == AttachmentType.VoiceMessage,
                            isItemClickable = attachmentInfoItem.isAttachmentItemClickable(),
                            isItemCanDelete = (attachmentInfoItem.status !is AttachmentInfoItem.Status.Failed),
                            isShowResend = (attachmentInfoItem.status is AttachmentInfoItem.Status.Failed),
                            displayName = attachmentInfoItem.filename,
                            onClick = {
                                onClick.invoke(attachmentInfoItem)
                            },
                            onDelete = {
                                onDelete.invoke(attachmentInfoItem)
                            },
                            onResend = {
                                val file = ReSendFile(
                                    type = AttachmentType.Audio,
                                    attachmentInfoItem = attachmentInfoItem,
                                    title = context.getString(R.string.file_upload_fail_title),
                                    description = context.getString(R.string.file_upload_fail_desc)
                                )
                                onResend?.invoke(file)
                            }
                        )
                    }
                }

                AttachmentType.Pdf, AttachmentType.Txt -> {
                    item {
                        AttachmentFileItem(
                            modifier = itemModifier,
                            file = attachmentInfoItem.uri,
                            fileSize = attachmentInfoItem.fileSize,
                            isItemClickable = attachmentInfoItem.isAttachmentItemClickable(),
                            isItemCanDelete = (attachmentInfoItem.status !is AttachmentInfoItem.Status.Failed),
                            isShowResend = (attachmentInfoItem.status is AttachmentInfoItem.Status.Failed),
                            displayName = attachmentInfoItem.filename,
                            onClick = {
                                onClick.invoke(attachmentInfoItem)
                            },
                            onDelete = {
                                onDelete.invoke(attachmentInfoItem)
                            },
                            onResend = {
                                val file = ReSendFile(
                                    type = AttachmentType.Audio,
                                    attachmentInfoItem = attachmentInfoItem,
                                    title = context.getString(R.string.file_upload_fail_title),
                                    description = context.getString(R.string.file_upload_fail_desc)
                                )
                                onResend?.invoke(file)
                            }
                        )
                    }
                }

                AttachmentType.Choice -> {
                    val voteModel = attachmentInfoItem.other
                    if (voteModel is VoteModel) {
                        item {
                            AttachmentChoiceItem(
                                modifier = Modifier
                                    .width(270.dp)
                                    .height(75.dp),
                                voteModel = voteModel,
                                isItemClickable = voteModel.id.isEmpty(),
                                isItemCanDelete = true,
                                onClick = {
                                    onClick.invoke(attachmentInfoItem)
                                },
                                onDelete = {
                                    onDelete.invoke(attachmentInfoItem)
                                },
                            )
                        }
                    }
                }

                else -> {

                }
            }
        }
    }

}

@Preview
@Composable
fun PostOtherAttachmentScreenPreview() {
    FanciTheme {
        PostOtherAttachmentScreen(
            attachment = emptyList(),
            onClick = {},
            onDelete = {},
            onResend = {}
        )
    }
}