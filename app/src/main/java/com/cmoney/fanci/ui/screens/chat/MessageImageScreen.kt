package com.cmoney.fanci.ui.screens.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.fanci.R
import com.socks.library.KLog

@Composable
fun MessageImageScreen(images: List<Any>, modifier: Modifier = Modifier, isShowLoading: Boolean = false) {
    val TAG = "MessageImageScreen"
    Box(
        modifier = modifier
            .size(205.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                // TODO:  
                KLog.i(TAG, "image click.")
            },
    ) {
        when (images.size) {
            1 -> {
                MessageImage(
                    model = images.first(),
                    modifier = Modifier.fillMaxSize(),
                )
            }
            2 -> {
                Row {
                    MessageImage(
                        model = images.first(),
                        modifier = Modifier.weight(1f),
                    )
                    MessageImage(
                        model = images[1],
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            3 -> {
                Column {
                    Row(modifier = Modifier.weight(1f)) {
                        MessageImage(
                            model = images.first(),
                            modifier = Modifier.weight(1f),
                        )
                        MessageImage(
                            model = images[1],
                            modifier = Modifier.weight(1f),
                        )
                    }
                    MessageImage(
                        model = images[2],
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            4 -> {
                Column {
                    Row(modifier = Modifier.weight(1f)) {
                        MessageImage(
                            model = images.first(),
                            modifier = Modifier.weight(1f),
                        )
                        MessageImage(
                            model = images[1],
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Row(modifier = Modifier.weight(1f)) {
                        MessageImage(
                            model = images[2],
                            modifier = Modifier.weight(1f),
                        )
                        MessageImage(
                            model = images[3],
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
            else -> {
                Column {
                    Row(modifier = Modifier.weight(1f)) {
                        MessageImage(
                            model = images.first(),
                            modifier = Modifier.weight(1f),
                        )
                        MessageImage(
                            model = images[1],
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Row(modifier = Modifier.weight(1f)) {
                        MessageImage(
                            model = images[2],
                            modifier = Modifier.weight(1f),
                        )
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.weight(1f)
                        ) {
                            MessageImage(
                                model = images[3],
                                modifier = Modifier.fillMaxSize(),
                            )
                            val remainder = images.size - 4
                            Text(
                                text = "%d+".format(remainder),
                                fontSize = 40.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        if (isShowLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(45.dp).align(Alignment.Center)
            )
        }
    }
}

@Composable
fun MessageImage(model: Any, modifier: Modifier = Modifier) {
    AsyncImage(
        modifier = modifier,
        model = model,
        contentScale = ContentScale.Crop,
        contentDescription = null,
        placeholder = painterResource(id = R.drawable.resource_default)
    )
}

@Preview(showBackground = true)
@Composable
fun MessageImageScreenPreview() {
    MessageImageScreen(
        listOf(
            "https://picsum.photos/500/500",
            "https://picsum.photos/400/400",
            "https://picsum.photos/300/300",
            "https://picsum.photos/300/300",
            "https://picsum.photos/300/300",
            "https://picsum.photos/300/300",
            "https://picsum.photos/300/300"
        )
    )
}