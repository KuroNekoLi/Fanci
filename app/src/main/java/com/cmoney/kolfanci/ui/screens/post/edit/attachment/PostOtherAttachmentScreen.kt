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
import com.cmoney.kolfanci.extension.getFileName
import com.cmoney.kolfanci.model.attachment.AttachmentType
import com.cmoney.kolfanci.model.attachment.ReSendFile
import com.cmoney.kolfanci.model.attachment.UploadFileItem
import com.cmoney.kolfanci.ui.screens.chat.attachment.AttachmentAudioItem
import com.cmoney.kolfanci.ui.screens.chat.attachment.AttachmentFileItem
import com.cmoney.kolfanci.ui.theme.FanciTheme

@Composable
fun PostOtherAttachmentScreen(
    modifier: Modifier = Modifier,
    itemModifier: Modifier = Modifier,
    attachment: List<Pair<AttachmentType, UploadFileItem>>,
    onClick: (Uri) -> Unit,
    onDelete: (Uri) -> Unit,
    onResend: ((ReSendFile) -> Unit)? = null
) {

    val listState = rememberLazyListState()
    val context = LocalContext.current

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
                            audio = item,
                            displayName = item.uri.getFileName(context).orEmpty(),
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
                            file = item,
                            displayName = item.uri.getFileName(context).orEmpty(),
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