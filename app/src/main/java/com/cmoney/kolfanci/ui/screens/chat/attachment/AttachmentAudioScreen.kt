package com.cmoney.kolfanci.ui.screens.chat.attachment

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.getAudioDisplayDuration
import com.cmoney.kolfanci.extension.getFileName
import com.cmoney.kolfanci.model.attachment.AttachmentType
import com.cmoney.kolfanci.model.attachment.ReSendFile
import com.cmoney.kolfanci.model.attachment.UploadFileItem
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

@Composable
fun AttachmentAudioScreen(
    modifier: Modifier = Modifier,
    itemModifier: Modifier = Modifier,
    audioList: List<UploadFileItem>,
    onClick: (Uri) -> Unit,
    onDelete: (Uri) -> Unit
) {
    val listState = rememberLazyListState()
    val context = LocalContext.current

    LazyRow(
        modifier = modifier.padding(start = 10.dp, end = 10.dp),
        state = listState, horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(audioList) { audio ->
            AttachmentAudioItem(
                modifier = itemModifier,
                audio = audio,
                displayName = audio.uri.getFileName(context).orEmpty(),
                onClick = onClick,
                onDelete = onDelete
            )
        }
    }
}

@Composable
fun AttachmentAudioItem(
    modifier: Modifier = Modifier,
    audio: UploadFileItem,
    displayName: String,
    onClick: (Uri) -> Unit,
    onDelete: (Uri) -> Unit,
    onResend: ((ReSendFile) -> Unit)? = null
) {
    val context = LocalContext.current
    val status = audio.status

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(LocalColor.current.background)
            .clickable(
                enabled = audio.status == UploadFileItem.Status.Undefined
            ) {
                onClick.invoke(audio.uri)
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.TopEnd)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {

                    Row(
                        modifier = Modifier.padding(start = 15.dp, top = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.audio_icon),
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            text = displayName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                color = LocalColor.current.text.default_100,
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.padding(start = 15.dp, top = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = audio.uri.getAudioDisplayDuration(context),
                            fontSize = 14.sp,
                            color = LocalColor.current.text.default_50
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Image(
                            modifier = Modifier.size(14.dp),
                            painter = painterResource(id = R.drawable.small_play),
                            contentDescription = null
                        )
                    }
                }

                if (status !is UploadFileItem.Status.Failed) {
                    //Close btn
                    Image(
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable {
                                onDelete.invoke(audio.uri)
                            },
                        painter = painterResource(id = R.drawable.close), contentDescription = null
                    )
                }
            }

            //上傳失敗
            if (status is UploadFileItem.Status.Failed) {
                Image(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            onResend?.invoke(
                                ReSendFile(
                                    type = AttachmentType.Audio,
                                    file = audio,
                                    title = context.getString(R.string.file_upload_fail_title),
                                    description = context.getString(R.string.file_upload_fail_desc)
                                )
                            )
                        },
                    painter = painterResource(id = R.drawable.upload_failed),
                    contentDescription = null
                )
            }
        }
    }
}

@Preview
@Composable
fun AttachmentAudioItemPreview() {
    FanciTheme {
        AttachmentAudioItem(
            modifier = Modifier
                .width(270.dp)
                .height(75.dp),
            audio = UploadFileItem(uri = Uri.EMPTY),
            displayName = "上課教材.mp3",
            onClick = {},
            onDelete = {}
        )
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
                UploadFileItem(uri = Uri.EMPTY),
                UploadFileItem(uri = Uri.EMPTY),
                UploadFileItem(uri = Uri.EMPTY)
            ),
            onClick = {},
            onDelete = {}
        )
    }
}