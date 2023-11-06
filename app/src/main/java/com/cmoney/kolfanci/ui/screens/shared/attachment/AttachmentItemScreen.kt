package com.cmoney.kolfanci.ui.screens.shared.attachment

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
import com.cmoney.kolfanci.extension.getDisplayFileSize
import com.cmoney.kolfanci.model.attachment.AttachmentType
import com.cmoney.kolfanci.model.attachment.ReSendFile
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor


/**
 * 附加檔案 item (ex:pdf, txt)
 *
 * @param file 檔案
 * @param fileSize 檔案大小
 * @param displayName 檔名
 * @param isItemClickable 是否可以點擊
 * @param isItemCanDelete 是否可以刪除,是否呈現刪除按鈕
 * @param isShowResend 是否呈現 重新發送 icon
 * @param onClick 點擊 callback
 * @param onDelete 刪除 callback
 * @param onResend 重新發送 callback
 */
@Composable
fun AttachmentFileItem(
    modifier: Modifier = Modifier,
    file: Uri,
    fileSize: Long = 0,
    displayName: String,
    isItemClickable: Boolean,
    isItemCanDelete: Boolean,
    isShowResend: Boolean,
    onClick: (Uri) -> Unit,
    onDelete: ((Uri) -> Unit)? = null,
    onResend: ((ReSendFile) -> Unit)? = null
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(LocalColor.current.background)
            .clickable(
                enabled = isItemClickable
            ) {
                onClick.invoke(file)
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
                        modifier = Modifier.padding(start = 15.dp, top = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.attachment_file),
                            contentDescription = "attachment_file"
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

                    //File size
                    Text(
                        modifier = Modifier.padding(top = 5.dp, start = 15.dp, bottom = 5.dp),
                        text = if (fileSize == 0L) {
                            file.getDisplayFileSize(context)
                        } else {
                            fileSize.getDisplayFileSize()
                        },
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = LocalColor.current.text.default_50
                        )
                    )
                }

                if (isItemCanDelete) {
                    //Close btn
                    Image(
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable {
                                onDelete?.invoke(file)
                            },
                        painter = painterResource(id = R.drawable.close), contentDescription = null
                    )
                }
            }

            //上傳失敗
            if (isShowResend) {
                Image(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            onResend?.invoke(
                                ReSendFile(
                                    type = AttachmentType.Audio,
                                    file = file,
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
fun AttachmentFileItemPreview() {
    FanciTheme {
        AttachmentFileItem(
            modifier = Modifier
                .width(270.dp)
                .height(75.dp),
            file = Uri.EMPTY,
            displayName = "上課教材.pdf",
            isItemClickable = true,
            isItemCanDelete = true,
            isShowResend = false,
            onClick = {},
            onDelete = {},
            onResend = {}
        )
    }
}


/**
 * 音檔附加檔案 item
 *
 * @param file 檔案
 * @param displayName 檔名
 * @param isItemClickable 是否可以點擊
 * @param isItemCanDelete 是否可以刪除,是否呈現刪除按鈕
 * @param isShowResend 是否呈現 重新發送 icon
 * @param onClick 點擊 callback
 * @param onDelete 刪除 callback
 * @param onResend 重新發送 callback
 */
@Composable
fun AttachmentAudioItem(
    modifier: Modifier = Modifier,
    file: Uri,
    displayName: String,
    isItemClickable: Boolean,
    isItemCanDelete: Boolean,
    isShowResend: Boolean,
    onClick: (Uri) -> Unit,
    onDelete: ((Uri) -> Unit)? = null,
    onResend: ((ReSendFile) -> Unit)? = null
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(LocalColor.current.background)
            .clickable(
                enabled = isItemClickable
            ) {
                onClick.invoke(file)
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
                            text = file.getAudioDisplayDuration(context),
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

                if (isItemCanDelete) {
                    //Close btn
                    Image(
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable {
                                onDelete?.invoke(file)
                            },
                        painter = painterResource(id = R.drawable.close), contentDescription = null
                    )
                }
            }

            //上傳失敗
            if (isShowResend) {
                Image(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            onResend?.invoke(
                                ReSendFile(
                                    type = AttachmentType.Audio,
                                    file = file,
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
            displayName = "上課教材.mp3",
            file = Uri.EMPTY,
            isItemClickable = true,
            isItemCanDelete = true,
            isShowResend = false,
            onClick = {},
            onDelete = {},
            onResend = {}
        )
    }
}