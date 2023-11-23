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
import com.cmoney.kolfanci.model.attachment.ReSendFile
import com.cmoney.kolfanci.model.attachment.AttachmentInfoItem
import com.cmoney.kolfanci.ui.screens.shared.attachment.AttachmentAudioItem
import com.cmoney.kolfanci.ui.theme.FanciTheme

@Composable
fun AttachmentAudioScreen(
    modifier: Modifier = Modifier,
    itemModifier: Modifier = Modifier,
    audioList: List<AttachmentInfoItem>,
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
        items(audioList) { file ->
            AttachmentAudioItem(
                modifier = itemModifier,
                file = file.uri,
                duration = file.duration ?: 0,
                isItemClickable = true,
                isItemCanDelete = (file.status == AttachmentInfoItem.Status.Undefined),
                isShowResend = (file.status is AttachmentInfoItem.Status.Failed),
                displayName = file.filename,
                onClick = onClick,
                onDelete = onDelete,
                onResend = onResend
            )
        }
    }
}

@Preview
@Composable
fun AttachmentAudioScreenPreview() {
    FanciTheme {
        AttachmentAudioScreen(
            itemModifier = Modifier
                .width(270.dp)
                .height(75.dp),
            audioList = listOf(
                AttachmentInfoItem(uri = Uri.EMPTY),
                AttachmentInfoItem(uri = Uri.EMPTY),
                AttachmentInfoItem(uri = Uri.EMPTY)
            ),
            onClick = {},
            onDelete = {}
        )
    }
}