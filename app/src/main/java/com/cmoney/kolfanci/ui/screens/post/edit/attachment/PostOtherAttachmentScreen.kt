package com.cmoney.kolfanci.ui.screens.post.edit.attachment

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.kolfanci.model.attachment.AttachmentType
import com.cmoney.kolfanci.model.attachment.ReSendFile
import com.cmoney.kolfanci.model.attachment.AttachmentInfoItem
import com.cmoney.kolfanci.ui.screens.shared.attachment.AttachmentAudioItem
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
    onClick: (Uri) -> Unit,
    onDelete: (Uri) -> Unit,
    onResend: ((ReSendFile) -> Unit)? = null
) {
    val listState = rememberLazyListState()

    LazyRow(
        modifier = modifier.padding(start = 10.dp, end = 10.dp),
        state = listState, horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        attachment.forEach { (attachmentType, item) ->
            when (attachmentType) {
                AttachmentType.Audio -> {
                    item {
                        AttachmentAudioItem(
                            modifier = itemModifier,
                            file = item.uri,
                            duration = item.duration ?: 0,
                            isItemClickable = item.isAttachmentItemClickable(),
                            isItemCanDelete = (item.status !is AttachmentInfoItem.Status.Failed),
                            isShowResend = (item.status is AttachmentInfoItem.Status.Failed),
                            displayName = item.filename,
                            onClick = onClick,
                            onDelete = onDelete,
                            onResend = onResend
                        )
                    }
                }

                AttachmentType.Pdf, AttachmentType.Txt -> {
                    item {
                        AttachmentFileItem(
                            modifier = itemModifier,
                            file = item.uri,
                            fileSize = item.fileSize,
                            isItemClickable = item.isAttachmentItemClickable(),
                            isItemCanDelete = (item.status !is AttachmentInfoItem.Status.Failed),
                            isShowResend = (item.status is AttachmentInfoItem.Status.Failed),
                            displayName = item.filename,
                            onClick = onClick,
                            onDelete = onDelete,
                            onResend = onResend
                        )
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