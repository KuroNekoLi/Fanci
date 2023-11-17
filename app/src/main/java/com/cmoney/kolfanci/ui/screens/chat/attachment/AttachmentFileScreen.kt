package com.cmoney.kolfanci.ui.screens.chat.attachment

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.cmoney.kolfanci.ui.screens.shared.attachment.AttachmentFileItem
import com.cmoney.kolfanci.ui.theme.FanciTheme

/**
 * 附加檔案 item
 */
@Composable
fun AttachmentFileScreen(
    modifier: Modifier = Modifier,
    itemModifier: Modifier = Modifier,
    fileList: List<AttachmentInfoItem>,
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
        items(fileList) { file ->
            AttachmentFileItem(
                modifier = itemModifier,
                file = file.uri,
                fileSize = file.fileSize,
                isItemClickable = true,
                isItemCanDelete = (file.status == AttachmentInfoItem.Status.Undefined),
                isShowResend = (file.status is AttachmentInfoItem.Status.Failed),
                displayName = file.filename,
                onClick = {
                    onClick.invoke(file)
                },
                onDelete = { onDelete.invoke(file) },
                onResend = {
                    val file = ReSendFile(
                        type = AttachmentType.Audio,
                        attachmentInfoItem = file,
                        title = context.getString(R.string.file_upload_fail_title),
                        description = context.getString(R.string.file_upload_fail_desc)
                    )
                    onResend?.invoke(file)
                }
            )
        }
    }
}

@Preview
@Composable
fun AttachmentFileScreenPreview() {
    FanciTheme {
        AttachmentFileScreen(
            itemModifier = Modifier
                .width(270.dp)
                .height(75.dp),
            fileList = listOf(
                AttachmentInfoItem(uri = Uri.EMPTY),
                AttachmentInfoItem(uri = Uri.EMPTY),
                AttachmentInfoItem(uri = Uri.EMPTY)
            ),
            onClick = {},
            onDelete = {}
        )
    }
}