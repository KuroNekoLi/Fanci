package com.cmoney.kolfanci.ui.screens.post.edit.attachment

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.attachment.AttachmentType
import com.cmoney.kolfanci.model.attachment.ReSendFile
import com.cmoney.kolfanci.model.attachment.AttachmentInfoItem
import com.cmoney.kolfanci.ui.screens.chat.attachment.ChatRoomAttachImageScreen
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 * 附加檔案 preview 呈現畫面
 */
@Composable
fun PostAttachmentScreen(
    modifier: Modifier = Modifier,
    attachment: List<Pair<AttachmentType, AttachmentInfoItem>>,
    isShowLoading: Boolean,
    onDelete: (Uri) -> Unit,
    onClick: (Uri) -> Unit,
    onAddImage: () -> Unit,
    onResend: (ReSendFile) -> Unit
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
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column {
            if (imageAttachment.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))

                ChatRoomAttachImageScreen(
                    modifier = modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.primary),
                    imageAttach = imageAttachment,
                    onDelete = onDelete,
                    onAdd = onAddImage,
                    onClick = onClick,
                    onResend = onResend
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
                    onClick = onClick,
                    onResend = onResend
                )
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
fun PostAttachmentScreenPreview() {
    PostAttachmentScreen(
        attachment = emptyList(),
        isShowLoading = false,
        onDelete = {},
        onClick = {},
        onAddImage = {},
        onResend = {}
    )
}