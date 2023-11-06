package com.cmoney.kolfanci.ui.screens.chat.attachment

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.kolfanci.model.attachment.AttachmentType
import com.cmoney.kolfanci.model.attachment.UploadFileItem

/**
 * 附加檔案 preview 呈現畫面
 */
@Composable
fun ChatRoomAttachmentScreen(
    modifier: Modifier = Modifier,
    attachment: Map<AttachmentType, List<UploadFileItem>>,
    onDelete: (Uri) -> Unit,
    onAdd: () -> Unit,
    onClick: (Uri) -> Unit
) {
    attachment.forEach { (attachmentType, uris) ->
        when (attachmentType) {
            AttachmentType.Image -> ChatRoomAttachImageScreen(
                modifier = modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.primary),
                imageAttach = uris,
                onDelete = onDelete,
                onAdd = onAdd,
                onClick = onClick
            )

            AttachmentType.Audio -> {
                AttachmentAudioScreen(
                    audioList = uris,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                        .background(MaterialTheme.colors.primary),
                    itemModifier = Modifier
                        .width(270.dp)
                        .height(75.dp),
                    onClick = onClick,
                    onDelete = onDelete
                )
            }

            AttachmentType.Pdf, AttachmentType.Txt -> {
                AttachmentFileScreen(
                    fileList = uris,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                        .background(MaterialTheme.colors.primary),
                    itemModifier = Modifier
                        .width(270.dp)
                        .height(75.dp),
                    onClick = onClick,
                    onDelete = onDelete
                )
            }

            AttachmentType.Unknown -> {
                //TODO
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatRoomAttachmentScreenPreview() {
    ChatRoomAttachmentScreen(
        attachment = emptyMap(),
        onDelete = {},
        onAdd = {},
        onClick = {}
    )
}