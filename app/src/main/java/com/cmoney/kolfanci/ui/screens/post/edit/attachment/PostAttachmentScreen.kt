package com.cmoney.kolfanci.ui.screens.post.edit.attachment

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.kolfanci.model.usecase.UploadFileItem
import com.cmoney.kolfanci.ui.screens.chat.attachment.ChatRoomAttachImageScreen
import com.cmoney.kolfanci.ui.screens.chat.message.viewmodel.AttachmentType

/**
 * 附加檔案 preview 呈現畫面
 */
@Composable
fun PostAttachmentScreen(
    modifier: Modifier = Modifier,
    attachment: List<Pair<AttachmentType, UploadFileItem>>,
    onDelete: (Uri) -> Unit,
    onClick: (Uri) -> Unit,
    onAddImage: () -> Unit
) {
    //圖片檔
    val imageAttachment = attachment.filter { item ->
        item.first == AttachmentType.Image
    }.map { filterItem ->
        filterItem.second
    }

    //其餘檔案
    val otherAttachment = attachment.filter { item ->
        item.first != AttachmentType.Image
    }

    //ui
    Column {
        if (imageAttachment.isNotEmpty()) {
            Spacer(modifier = Modifier.height(2.dp))

            ChatRoomAttachImageScreen(
                modifier = modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.primary),
                imageAttach = imageAttachment.map { it.uri },
                onDelete = onDelete,
                onAdd = onAddImage,
                onClick = onClick
            )
        }

        if (otherAttachment.isNotEmpty()) {
            Spacer(modifier = Modifier.height(2.dp))

            PostOtherAttachmentScreen(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(15.dp)
                    .background(MaterialTheme.colors.primary),
                itemModifier = Modifier
                    .width(270.dp)
                    .height(75.dp),
                attachment = otherAttachment,
                onDelete = onDelete,
                onClick = onClick
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PostAttachmentScreenPreview() {
    PostAttachmentScreen(
        attachment = emptyList(),
        onDelete = {},
        onClick = {},
        onAddImage = {}
    )
}