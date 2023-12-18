package com.cmoney.kolfanci.ui.screens.chat.attachment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.attachment.AttachmentInfoItem
import com.cmoney.kolfanci.model.attachment.AttachmentType
import com.cmoney.kolfanci.model.attachment.ReSendFile
import com.cmoney.kolfanci.model.vote.VoteModel
import com.cmoney.kolfanci.ui.screens.shared.attachment.AttachmentChoiceItem
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 * 聊天室, 附加檔案 preview 呈現畫面, 目前規則只會呈一種 type.
 *
 * @param attachment 附加檔案
 * @param isShowLoading 是否show loading
 * @param onDelete 刪除檔案 callback
 * @param onAddImage 增加圖檔
 * @param onClick 點擊檔案
 * @param onResend 重新發送 callback
 */
@Composable
fun ChatRoomAttachmentScreen(
    modifier: Modifier = Modifier,
    attachment: Map<AttachmentType, List<AttachmentInfoItem>>,
    isShowLoading: Boolean,
    onDelete: (AttachmentInfoItem) -> Unit,
    onAddImage: () -> Unit,
    onClick: (AttachmentInfoItem) -> Unit,
    onResend: ((ReSendFile) -> Unit)
) {
    Box(
        modifier = modifier
    ) {
        attachment.forEach { (attachmentType, attachmentInfoItems) ->
            when (attachmentType) {
                AttachmentType.Image -> ChatRoomAttachImageScreen(
                    modifier = modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.primary),
                    imageAttach = attachmentInfoItems,
                    onDelete = onDelete,
                    onAdd = onAddImage,
                    onClick = onClick,
                    onResend = onResend
                )

                AttachmentType.Audio -> {
                    AttachmentAudioScreen(
                        audioList = attachmentInfoItems,
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                            .background(MaterialTheme.colors.primary),
                        itemModifier = Modifier
                            .width(270.dp)
                            .height(75.dp),
                        onClick = onClick,
                        onDelete = onDelete,
                        onResend = onResend
                    )
                }

                AttachmentType.Pdf, AttachmentType.Txt -> {
                    AttachmentFileScreen(
                        fileList = attachmentInfoItems,
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                            .background(MaterialTheme.colors.primary),
                        itemModifier = Modifier
                            .width(270.dp)
                            .height(75.dp),
                        onClick = onClick,
                        onDelete = onDelete,
                        onResend = onResend
                    )
                }

                //其他
                AttachmentType.Unknown -> {
                }

                //選擇題
                AttachmentType.Choice -> {
                    val filterAttachment = attachmentInfoItems.filter { attachmentInfoItem ->
                        attachmentInfoItem.other is VoteModel
                    }

                    LazyRow(
                        modifier = modifier.padding(start = 10.dp, end = 10.dp),
                        state = rememberLazyListState(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filterAttachment) { attachment ->
                            if (attachment.other is VoteModel) {
                                AttachmentChoiceItem(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .width(270.dp)
                                        .height(75.dp),
                                    voteModel = attachment.other,
                                    isItemClickable = true,
                                    isItemCanDelete = true,
                                    onClick = {
                                        onClick.invoke(attachment)
                                    },
                                    onDelete = {
                                        onDelete.invoke(attachment)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (isShowLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clickable { }
                    .background(colorResource(id = R.color.color_4620262F)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(45.dp)
                        .align(Alignment.Center),
                    color = LocalColor.current.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatRoomAttachmentScreenPreview() {
    ChatRoomAttachmentScreen(
        attachment = emptyMap(),
        isShowLoading = false,
        onDelete = {},
        onAddImage = {},
        onClick = {},
        onResend = {}
    )
}