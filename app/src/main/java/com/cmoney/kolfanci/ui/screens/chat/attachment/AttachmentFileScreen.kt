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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.cmoney.kolfanci.extension.getDisplayFileSize
import com.cmoney.kolfanci.extension.getFileName
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

@Composable
fun AttachmentFileScreen(
    modifier: Modifier = Modifier,
    itemModifier: Modifier = Modifier,
    fileList: List<Uri>,
    onClick: (Uri) -> Unit,
    onDelete: (Uri) -> Unit
) {
    val listState = rememberLazyListState()
    val context = LocalContext.current

    LazyRow(
        modifier = modifier.padding(start = 10.dp, end = 10.dp),
        state = listState, horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(fileList) { file ->
            AttachmentFileItem(
                modifier = itemModifier,
                file = file,
                displayName = context.getFileName(file).orEmpty(),
                onClick = onClick,
                onDelete = onDelete
            )
        }
    }
}

@Composable
fun AttachmentFileItem(
    modifier: Modifier = Modifier,
    file: Uri,
    displayName: String,
    onClick: (Uri) -> Unit,
    onDelete: (Uri) -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(LocalColor.current.background)
            .clickable {
                onClick.invoke(file)
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.TopEnd)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Row(
                    modifier = Modifier.padding(start = 15.dp, top = 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.attachment_file),
                        contentDescription = "attachment_file"
                    )

                    Spacer(modifier = Modifier.width(10.dp))

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
                    modifier = Modifier.padding(top = 5.dp, start = 15.dp, bottom = 10.dp),
                    text = context.getDisplayFileSize(file),
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = LocalColor.current.text.default_50
                    )
                )
            }

            //Close btn
            Image(
                modifier = Modifier
                    .padding(10.dp)
                    .clickable {
                        onDelete.invoke(file)
                    },
                painter = painterResource(id = R.drawable.close), contentDescription = null
            )
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
            displayName = "上課教材.mp3",
            onClick = {},
            onDelete = {}
        )
    }
}

@Preview
@Composable
fun AttachmentFileScreenPreview() {
    FanciTheme {
        AttachmentFileScreen(
            itemModifier = Modifier
                .width(270.dp)
                .height(75.dp),
            fileList = listOf(
                Uri.EMPTY,
                Uri.EMPTY,
                Uri.EMPTY
            ),
            onClick = {},
            onDelete = {}
        )
    }
}