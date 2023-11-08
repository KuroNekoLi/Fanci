package com.cmoney.kolfanci.ui.screens.chat.attachment

import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.kolfanci.model.attachment.ReSendFile
import com.cmoney.kolfanci.model.attachment.AttachmentInfoItem
import com.cmoney.kolfanci.ui.screens.shared.attachment.AttachImageItem
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 * 聊天室 附加圖片
 *
 * @param quantityLimit 附加圖片數量上限
 */
@Composable
fun ChatRoomAttachImageScreen(
    modifier: Modifier = Modifier,
    imageAttach: List<AttachmentInfoItem>,
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
                        file = attach.uri,
                        isItemClickable = true,
                        isItemCanDelete = (attach.status == AttachmentInfoItem.Status.Undefined),
                        isShowResend = (attach.status is AttachmentInfoItem.Status.Failed),
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
                AttachmentInfoItem(),
                AttachmentInfoItem(),
                AttachmentInfoItem()
            ),
            onDelete = {},
            onAdd = {},
            onClick = {},
            onResend = {}
        )
    }
}