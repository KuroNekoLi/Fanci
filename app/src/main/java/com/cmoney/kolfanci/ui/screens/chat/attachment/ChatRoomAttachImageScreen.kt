package com.cmoney.kolfanci.ui.screens.chat.attachment

import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.attachment.AttachmentType
import com.cmoney.kolfanci.model.attachment.ReSendFile
import com.cmoney.kolfanci.model.attachment.UploadFileItem
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

//TODO: 搬移位置 至共用
/**
 * 聊天室 附加圖片
 *
 * @param quantityLimit 附加圖片數量上限
 */
@Composable
fun ChatRoomAttachImageScreen(
    modifier: Modifier = Modifier,
    imageAttach: List<UploadFileItem>,
    quantityLimit: Int = AttachImageDefault.getQuantityLimit(),
    onDelete: (Uri) -> Unit,
    onAdd: () -> Unit,
    onClick: (Uri) -> Unit,
    onResend: ((ReSendFile) -> Unit)? = null
) {
    val listState = rememberLazyListState()

    if (imageAttach.isNotEmpty()) {
        LaunchedEffect(imageAttach.size) {
            listState.animateScrollToItem(imageAttach.size)
        }

        LazyRow(
            modifier = modifier.padding(start = 10.dp, end = 10.dp),
            state = listState, horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (imageAttach.isNotEmpty()) {
                items(imageAttach) { attach ->
                    AttachImageItem(
                        uploadFileItem = attach,
                        onClick = onClick,
                        onDelete = {
                            onDelete.invoke(it)
                        },
                        onResend = onResend
                    )
                }
                if (imageAttach.size < quantityLimit) {
                    item {
                        Button(
                            onClick = {
                                onAdd.invoke()
                            },
                            modifier = Modifier
                                .size(108.dp, 120.dp)
                                .padding(top = 10.dp, bottom = 10.dp),
                            border = BorderStroke(0.5.dp, LocalColor.current.text.default_100),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(15)
                        ) {
                            Text(
                                text = "新增圖片",
                                color = LocalColor.current.text.default_100
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AttachImageItem(
    uploadFileItem: UploadFileItem,
    onDelete: (Uri) -> Unit,
    onClick: (Uri) -> Unit,
    onResend: ((ReSendFile) -> Unit)? = null
) {
    val context = LocalContext.current
    val uri = uploadFileItem.uri
    val status = uploadFileItem.status
    val request = ImageRequest.Builder(context)
        .data(uri)
        .build()

    Box(
        modifier = Modifier
            .height(120.dp)
            .padding(top = 10.dp, bottom = 10.dp)
            .clickable(
                enabled = (status !is UploadFileItem.Status.Failed)
            ) {
                onClick.invoke(uri)
            },
        contentAlignment = Alignment.TopEnd
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = request,
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )

            if (status is UploadFileItem.Status.Failed) {
                Image(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            onResend?.invoke(
                                ReSendFile(
                                    type = AttachmentType.Image,
                                    file = uploadFileItem,
                                    title = context.getString(R.string.image_upload_fail_title),
                                    description = context.getString(R.string.image_upload_fail_desc)
                                )
                            )
                        },
                    painter = painterResource(id = R.drawable.upload_failed),
                    contentDescription = null
                )
            }
        }

        if (status !is UploadFileItem.Status.Failed) {
            Image(
                modifier = Modifier
                    .padding(10.dp)
                    .clickable {
                        onDelete.invoke(uri)
                    },
                painter = painterResource(id = R.drawable.close), contentDescription = null
            )
        }

    }
}

object AttachImageDefault {
    /**
     * 預設附加圖片上限
     */
    const val DEFAULT_QUANTITY_LIMIT = 10

    /**
     * 附加圖片數量上限
     */
    fun getQuantityLimit(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val limit = MediaStore.getPickImagesMaxLimit()
            if (DEFAULT_QUANTITY_LIMIT > limit) {
                limit
            } else {
                DEFAULT_QUANTITY_LIMIT
            }
        } else {
            DEFAULT_QUANTITY_LIMIT
        }
    }

}


@Preview
@Composable
fun ChatRoomAttachImageScreenPreview() {
    FanciTheme {
        ChatRoomAttachImageScreen(
            modifier = Modifier,
            imageAttach = listOf(
                UploadFileItem(),
                UploadFileItem(),
                UploadFileItem()
            ),
            onDelete = {},
            onAdd = {},
            onClick = {},
            onResend = {}
        )
    }
}