package com.cmoney.kolfanci.ui.screens.chat.attachment

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.cmoney.kolfanci.ui.screens.chat.message.viewmodel.AttachmentType

//TODO
/**
 * 附加檔案 preview 呈現畫面
 */
@Composable
fun ChatRoomAttachmentScreen(
    modifier: Modifier = Modifier,
    attachment: Map<AttachmentType, List<Uri>>,
    onDelete: (Uri) -> Unit,
    onAdd: () -> Unit
) {
    attachment.forEach { (attachmentType, uris) ->
        when (attachmentType) {
            AttachmentType.Picture -> ChatRoomAttachImageScreen(
                modifier = modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.primary),
                imageAttach = uris,
                onDelete = onDelete,
                onAdd = onAdd
            )

            AttachmentType.Music -> {
                //TODO

            }
            AttachmentType.Pdf -> {
                //TODO
            }
            AttachmentType.Txt -> {
                //TODO
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
        onAdd = {}
    )
}