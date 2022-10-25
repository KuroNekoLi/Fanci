package com.cmoney.fanci.ui.screens.chat

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cmoney.fanci.R

/**
 * 聊天室 附加圖片
 */
@Composable
fun MessageAttachImageScreen(viewModel: ChatRoomViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val listState = rememberLazyListState()
    val imageListObserve = viewModel.imageAttach.observeAsState()

    LazyRow(
        modifier = Modifier.padding(start = 40.dp, end = 10.dp),
        state = listState, horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        imageListObserve.value?.apply {
            items(this) { it ->
                AttachImage(it) {
                    viewModel.removeAttach(it)
                }
            }
        }
    }
}

@Composable
private fun AttachImage(uri: Uri, onDelete: (Uri) -> Unit) {
    val context = LocalContext.current

    val request = ImageRequest.Builder(context)
        .data(uri)
        .build()

    Box(
        modifier = Modifier
            .height(135.dp)
            .padding(top = 10.dp, bottom = 10.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        AsyncImage(
            model = request,
            modifier = Modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
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


@Preview(showBackground = true)
@Composable
fun MessageAttachImageScreenPreview() {
    MessageAttachImageScreen()
}